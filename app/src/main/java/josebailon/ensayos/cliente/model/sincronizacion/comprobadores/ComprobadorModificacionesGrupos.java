package josebailon.ensayos.cliente.model.sincronizacion.comprobadores;

import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.database.repository.SharedPreferencesRepo;
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
import retrofit2.Response;

public class ComprobadorModificacionesGrupos {
    APIservice apIservice;
    private SharedPreferencesRepo sharedPreferencesRepo;

    ISincronizadorFeedbackHandler handler;
    String token;
    SincronizadorService sincronizadorService;
    DatosLocalesSincronos servicioLocal;
    
    String nombreUsuario;


    public ComprobadorModificacionesGrupos(SincronizadorService sincronizadorService) {
        this.sincronizadorService = sincronizadorService;
        this.apIservice = sincronizadorService.getApIservice();
        this.handler = sincronizadorService.getHandler();
        this.token = sincronizadorService.getToken();
        this.servicioLocal = DatosLocalesSincronos.getInstance(App.getContext());
        this.sharedPreferencesRepo = SharedPreferencesRepo.getInstance();
        this.nombreUsuario = sharedPreferencesRepo.readLogin().getEmail();
    }



    public void comprobarGrupos(List<GrupoApiEnt> gruposRemotos) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        List<GrupoAndUsuariosAndCanciones> gruposLocales = servicioLocal.getAllGruposWithUsuariosAndCanciones();
        for (GrupoAndUsuariosAndCanciones grupoLocal : gruposLocales) {
                GrupoApiEnt grupoRemoto = gruposRemotos.stream().filter(grupo -> UUID.fromString(grupo.getId()).equals(grupoLocal.grupo.getId())).findFirst().orElse(null);
                comprobarGrupo(grupoLocal,grupoRemoto);
                }
    }

    public void comprobarGrupo(GrupoAndUsuariosAndCanciones grupoLocal, GrupoApiEnt grupoRemoto) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        int estado = CalculadoraEstados.estadoCanciones(grupoLocal.grupo,grupoRemoto);
        switch (estado){
            case V0_X:
                agregarAlServidor(grupoLocal);
                comprobarCanciones(grupoLocal,grupoRemoto);
                break;
            case VN_X:
                eliminarLocal(grupoLocal);
                break;
            case SVN_VN:VN_X:
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
                eliminarUsuarioDeGrupoDeServidor(nombreUsuario,grupoLocal);
                break;

        }

}

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
                    return new Conflicto<GrupoAndUsuariosAndCanciones, GrupoApiEnt>(Conflicto.T_GRUPO,grupoLocal,lr.body());
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

    private void eliminarUsuarioDeGrupoDeServidor(String nombreUsuario, GrupoAndUsuariosAndCanciones grupoLocal) throws TerminarSincronizacionException {

        try {
            Response<Object> lr = apIservice.eliminarUsuarioDeGrupo(nombreUsuario,grupoLocal.grupo.getId().toString(),token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }


    private void eliminarDeServidor(GrupoAndUsuariosAndCanciones grupoLocal) throws TerminarSincronizacionException {

        try {
            Response<Object> lr = apIservice.deleteGrupo(grupoLocal.grupo.getId().toString(),token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }

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

    private void reemplazarUsuarios(GrupoAndUsuariosAndCanciones local, GrupoApiEnt remoto) {
        for(UsuarioEntity usuario : local.usuarios)
            servicioLocal.deleteUsuario(usuario);
        for (UsuarioApiEnt usuarioApiEnt : remoto.getUsuarios())
            servicioLocal.insertUsuario(MediadorDeEntidades.crearUsuarioEntityParaGrupo(remoto.getId(),usuarioApiEnt.getEmail()));
    }
    private void reemplazarUsuarios(GrupoAndUsuariosAndCanciones local, GrupoAndUsuariosAndCanciones solucion) {
        for(UsuarioEntity usuario : local.usuarios)
            servicioLocal.deleteUsuario(usuario);
        for (UsuarioEntity usuarioSolucion : solucion.usuarios)
            servicioLocal.insertUsuario(usuarioSolucion);
    }

    private void eliminarLocal(GrupoAndUsuariosAndCanciones grupoLocal) {
        servicioLocal.deleteGrupo(grupoLocal.grupo);

    }




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


    private void comprobarCanciones(GrupoAndUsuariosAndCanciones grupoLocal, GrupoApiEnt grupoRemoto) throws CredencialesErroneasException, IOException, TerminarSincronizacionException {
        new ComprobadorModificacionesCanciones(sincronizadorService).comprobarCanciones(grupoLocal.grupo.getId(), grupoRemoto.getCanciones());
    }
}
