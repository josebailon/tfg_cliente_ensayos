package josebailon.ensayos.cliente.model.sincronizacion.comprobadores;

import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.*;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesSincronos;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.UsuarioApiEnt;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados;
import josebailon.ensayos.cliente.model.sincronizacion.ISincronizadorFeedbackHandler;
import josebailon.ensayos.cliente.model.sincronizacion.MediadorDeEntidades;
import josebailon.ensayos.cliente.model.sincronizacion.SincronizadorService;
import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.CredencialesErroneasException;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.TerminarSincronizacionException;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Comprueba si hay modificaciones en los grupos y maneja la sincronizacion adecuada
 *
 * @author Jose Javier Bailon Ortiz
 */
public class ComprobadorModificacionesGrupos {
    /**
     * Sevicio de acceso a la web API
     */
    private APIservice apIservice;
    /**
     * Repositorio de shared preferences
     */
    private SharedPreferencesRepo sharedPreferencesRepo;

    /**
     * Handler de escucha a la sincronizacion
     */
    private ISincronizadorFeedbackHandler handler;

    /**
     * token de acceso
     */
    private String token;

    /**
     * Servicio de sincronizacion
     */
    private SincronizadorService sincronizadorService;

    /**
     * Servicio de acceso sincrono a la base de datos local
     */
    private DatosLocalesSincronos servicioLocal;

    /**
     * Nombre de usuario local
     */
    private String nombreUsuario;


    /**
     * Conscturctor
     *
     * @param sincronizadorService Servicio de sincronizacion
     */
    public ComprobadorModificacionesGrupos(SincronizadorService sincronizadorService) {
        this.sincronizadorService = sincronizadorService;
        this.apIservice = sincronizadorService.getApIservice();
        this.handler = sincronizadorService.getHandler();
        this.token = sincronizadorService.getToken();
        this.servicioLocal = DatosLocalesSincronos.getInstance(App.getContext());
        this.sharedPreferencesRepo = SharedPreferencesRepo.getInstance();
        this.nombreUsuario = sharedPreferencesRepo.readLogin().getEmail();
    }


    /**
     * Itera los grupos lanzando la comprobacion de cada uno
     * @param gruposRemotos La lista de grupos
     * @throws CredencialesErroneasException Si hay problema con las credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException Si se ha producido un error
     */
    public void comprobarGrupos(List<GrupoApiEnt> gruposRemotos) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        List<GrupoAndUsuariosAndCanciones> gruposLocales = servicioLocal.getAllGruposWithUsuariosAndCanciones();
        for (GrupoAndUsuariosAndCanciones grupoLocal : gruposLocales) {
                GrupoApiEnt grupoRemoto = gruposRemotos.stream().filter(grupo -> UUID.fromString(grupo.getId()).equals(grupoLocal.grupo.getId())).findFirst().orElse(null);
                comprobarGrupo(grupoLocal,grupoRemoto);
                }
    }

    /**
     * Hace la cmprobacion apara un grupo
     * @param grupoLocal El grupo local
     * @param grupoRemoto El grupo remoto con el que comparar
     * @throws CredencialesErroneasException Si hay problema con las credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException Si se ha producido un error
     */
    public void comprobarGrupo(GrupoAndUsuariosAndCanciones grupoLocal, GrupoApiEnt grupoRemoto) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        handler.onSendStatus("Comprobando midificaciones de grupo "+grupoLocal.grupo.getNombre());
        int estado = CalculadoraEstados.estadoCanciones(grupoLocal.grupo,grupoRemoto);
        switch (estado){
            case V0_X:
                agregarAlServidor(grupoLocal);
                comprobarCanciones(grupoLocal,grupoRemoto);
                break;
            case VN_X:
                eliminarLocal(grupoLocal);
                break;
            case SVN_VN:
                comprobarCanciones(grupoLocal,grupoRemoto);
                break;
            case SVN_VQ:
                actualizarLocal(grupoLocal,grupoRemoto);
                comprobarCanciones(grupoLocal,grupoRemoto);
                break;
            case EVN_VN:
                //actualizar servidor
                Conflicto<GrupoAndUsuariosAndCanciones, GrupoApiEnt> conflicto = actualizarServidorConDatosLocales(grupoLocal,grupoRemoto);
                //si hay conflicto resolverlo
                if (conflicto!=null){
                    resolverConflicto(conflicto);
                }else{
                    //si no hay conflicto comprobar las canciones
                    comprobarCanciones(grupoLocal,grupoRemoto);
                }
                break;
            case EVN_VQ:
                    Conflicto conflictoDirecto = new Conflicto<GrupoAndUsuariosAndCanciones, GrupoApiEnt>(Conflicto.T_GRUPO,grupoLocal,grupoRemoto);
                    resolverConflicto(conflictoDirecto);
                break;
            case B_VN:
                eliminarLocal(grupoLocal);
                eliminarDeServidor(grupoLocal);
                break;
            case B_X:
                eliminarLocal(grupoLocal);
                break;
            case A_X:
                eliminarLocal(grupoLocal);
                break;
            case A_VN:
                eliminarLocal(grupoLocal);
                //si solo quedo yo lo elimino, si no lo actualizo
                if (grupoRemoto.getUsuarios().size()==1 && grupoRemoto.getUsuarios().get(0).getEmail().equals(nombreUsuario)){
                    eliminarDeServidor(grupoLocal);
                }else {
                    apIservice.updateGrupo(MediadorDeEntidades.grupoApiEntToGrupoEntity(grupoRemoto), token).execute();
                    eliminarUsuarioDeGrupoDeServidor(nombreUsuario, grupoLocal);
                }
                break;

        }

}

    /**
     * Actualiza el servidor con los datos locales
     * @param grupoLocal El grupo local
     * @param grupoRemoto El grupo remoto
     * @return Un conflicto si la respuesta es 409 o null si no hay conflicto
     * @throws CredencialesErroneasException Si hay problema con las credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException Si se ha producido un error
     */
    private Conflicto<GrupoAndUsuariosAndCanciones,GrupoApiEnt> actualizarServidorConDatosLocales(GrupoAndUsuariosAndCanciones grupoLocal, GrupoApiEnt grupoRemoto) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {

        Response<GrupoApiEnt> lr=null;

        try {
            lr = apIservice.updateGrupo(grupoLocal.grupo, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateGrupo(MediadorDeEntidades.grupoApiEntToGrupoEntity(lr.body()));
                    break;
                case 409:
                    //si hay conflicto devolverlo
                    GrupoApiEnt remotoModificado = new GsonBuilder().create().fromJson(lr.errorBody().string(),GrupoApiEnt.class);
                    return new Conflicto<GrupoAndUsuariosAndCanciones, GrupoApiEnt>(Conflicto.T_GRUPO,grupoLocal,remotoModificado);
                case 401:
                    throw new CredencialesErroneasException("");
                default:
                    handler.onSendMessage("" + lr.code());
            }
        } catch (IOException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
        //gestionar usuarios
        List<UsuarioApiEnt> uRemotos=lr.body().getUsuarios();
        List<UsuarioEntity> uLocales = grupoLocal.usuarios;
        //si no hay conflicto resolver usuarios
        //agregar locales a remoto
        for (UsuarioEntity uLocal:uLocales)
            apIservice.agregarUsarioAGrupo(uLocal.getEmail(),grupoLocal.grupo.getId().toString(), token).execute();

        //quitar del servidor no existentes en local
        for (UsuarioApiEnt uRemoto:uRemotos) {
            if (!uLocales.stream().anyMatch(uLocal -> uLocal.getEmail().equals(uRemoto.getEmail()))){
                apIservice.eliminarUsuarioDeGrupo(uRemoto.getEmail(),grupoLocal.grupo.getId().toString(),token).execute();
            }
        }
        return null;
    }

    /**
     * Lanza la resolucion de un conflicto y espera a que sea resuelto
     * @param conflicto El conflicto
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void resolverConflicto(Conflicto<GrupoAndUsuariosAndCanciones, GrupoApiEnt> conflicto) throws  TerminarSincronizacionException {
        //mandar conflicto
        sincronizadorService.getHandler().onConflicto(conflicto);
        try {
            //esperar resolucion
            conflicto.esperar();
            //recoger solucion
            GrupoAndUsuariosAndCanciones solucion =conflicto.getResuelto();
            solucion.canciones=conflicto.getLocal().canciones;
            //actualizar en local y en remoto con la eleccion de solucion
            servicioLocal.updateGrupo( solucion.grupo);
            reemplazarUsuarios(conflicto.getLocal(),solucion);
            comprobarGrupo(solucion,conflicto.getRemoto());
        } catch (InterruptedException | CredencialesErroneasException |
                 TerminarSincronizacionException | IOException e) {
            throw new TerminarSincronizacionException("Sincronización terminada");
        }
    }

    /**
     * Elimina un usuario de un grupo en el servidor
     * @param nombreUsuario El nombre de usuario
     * @param grupoLocal El grupo local
     * @throws TerminarSincronizacionException
     */
    private void eliminarUsuarioDeGrupoDeServidor(String nombreUsuario, GrupoAndUsuariosAndCanciones grupoLocal) throws TerminarSincronizacionException {

        try {
            Response<ResponseBody> lr = apIservice.eliminarUsuarioDeGrupo(nombreUsuario,grupoLocal.grupo.getId().toString(),token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }


    /**
     * Elimina un grupo del servidor
     * @param grupoLocal El grupo local
     * @throws TerminarSincronizacionException
     */
    private void eliminarDeServidor(GrupoAndUsuariosAndCanciones grupoLocal) throws TerminarSincronizacionException {

        try {
            Response<ResponseBody> lr = apIservice.deleteGrupo(grupoLocal.grupo.getId().toString(),token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }

    /**
     * Actualiza un grupo local con los datos del servidor
     * @param grupoLocal El grupo local
     * @param remoto El grupo remoto
     */
    private void actualizarLocal(GrupoAndUsuariosAndCanciones grupoLocal, GrupoApiEnt remoto) {
        GrupoEntity local = grupoLocal.grupo;
        local.setNombre(remoto.getNombre());
        local.setDescripcion(remoto.getDescripcion());
        local.setVersion(remoto.getVersion());
        local.setBorrado(false);
        local.setEditado(false);
        local.setAbandonado(false);
        servicioLocal.updateGrupo(local);
        reemplazarUsuarios(grupoLocal,remoto);
    }

    /**
     * Reemplaza los usuarios asignados a un grupo localmente cogiendolos de un grupo remoto
     * @param local El grupo local
     * @param remoto El grupo remoto
     */
    private void reemplazarUsuarios(GrupoAndUsuariosAndCanciones local, GrupoApiEnt remoto) {
        for(UsuarioEntity usuario : local.usuarios)
            servicioLocal.deleteUsuario(usuario);
        for (UsuarioApiEnt usuarioApiEnt : remoto.getUsuarios())
            servicioLocal.insertUsuario(MediadorDeEntidades.crearUsuarioEntityParaGrupo(remoto.getId(),usuarioApiEnt.getEmail()));
    }

    /**
     * Reemplaza los usuarios asignados a un grupo localmente cogiendolos del resultado de la resolucion de un conflicto
     * @param local El grupo local
     * @param solucion El grupo remoto
     */
    private void reemplazarUsuarios(GrupoAndUsuariosAndCanciones local, GrupoAndUsuariosAndCanciones solucion) {
        for(UsuarioEntity usuario : local.usuarios)
            servicioLocal.deleteUsuario(usuario);
        for (UsuarioEntity usuarioSolucion : solucion.usuarios)
            servicioLocal.insertUsuario(usuarioSolucion);
    }

    /**
     * Elimina un grupo local
     * @param grupoLocal El grupo
     */
    private void eliminarLocal(GrupoAndUsuariosAndCanciones grupoLocal) {
        servicioLocal.deleteGrupo(grupoLocal.grupo);

    }

    /**
     * Agrega un grupo al servidor
     * @param grupoLocal El grupo local
     * @throws CredencialesErroneasException Si hay problema con las credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
        private void agregarAlServidor(GrupoAndUsuariosAndCanciones grupoLocal) throws CredencialesErroneasException, TerminarSincronizacionException {
            try {
                Response<GrupoApiEnt> lr = apIservice.insertGrupo(grupoLocal.grupo, token).execute();
                switch (lr.code()) {
                    case 200:
                        servicioLocal.updateGrupo(MediadorDeEntidades.grupoApiEntToGrupoEntity(lr.body()));
                        for (UsuarioEntity usuario : grupoLocal.usuarios){
                            Response<Object> r = apIservice.agregarUsarioAGrupo(usuario.getEmail(),grupoLocal.grupo.getId().toString(), token).execute();
                        }
                        break;
                    case 401:
                        throw new CredencialesErroneasException("");
                    default:
                        handler.onSendMessage("" + lr.code());
                }
            } catch (IOException e) {
                throw new TerminarSincronizacionException("Sin conexión con el servidor");
            }

        }


    /**
     * Lanza la comprobacion de canciones de un grupo
     * @param grupoLocal El grupo local
     * @param grupoRemoto El grupo remoto
     * @throws CredencialesErroneasException Si hay problema con las credenciales
     * @throws IOException Si ha habido algun problema
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void comprobarCanciones(GrupoAndUsuariosAndCanciones grupoLocal, GrupoApiEnt grupoRemoto) throws CredencialesErroneasException, IOException, TerminarSincronizacionException {
        if(grupoRemoto!=null)
            new ComprobadorModificacionesCanciones(sincronizadorService).comprobarCanciones(grupoLocal.grupo.getId(), grupoRemoto.getCanciones());
    }
}
