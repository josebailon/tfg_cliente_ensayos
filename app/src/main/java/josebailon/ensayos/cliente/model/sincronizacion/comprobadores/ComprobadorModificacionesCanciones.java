package josebailon.ensayos.cliente.model.sincronizacion.comprobadores;

import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.A_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.A_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.B_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.B_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.EVN_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.EVN_VQ;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.SVN_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.SVN_VQ;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.V0_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.VN_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.estadoCanciones;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesSincronos;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import josebailon.ensayos.cliente.model.sincronizacion.ISincronizadorFeedbackHandler;
import josebailon.ensayos.cliente.model.sincronizacion.MediadorDeEntidades;
import josebailon.ensayos.cliente.model.sincronizacion.SincronizadorService;
import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.CredencialesErroneasException;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.TerminarSincronizacionException;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Comprueba si hay modificaciones en una lista de canciones y maneja la sincronizacion adecuada
 *
 * @author Jose Javier Bailon Ortiz
 */
public class ComprobadorModificacionesCanciones {

    /**
     * Sevicio de acceso a la web API
     */
    private APIservice apIservice;

    /**
     * Repositorio de shared preferences
     */
    private SharedPreferencesRepo sharedPreferencesRepo;

    /**
     * Handler de escucha de la sincronizacion
     */
    private ISincronizadorFeedbackHandler handler;

    /**
     * Token de acceso
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
     * UUID del grupo al que pertenecen las canciones
     */
    private UUID idGrupo;

    /**
     * Nombre de usuario local
     */
    private String nombreUsuario;


    /**
     * Constructor
     *
     * @param sincronizadorService Servicio de sincronizacion
     */
    public ComprobadorModificacionesCanciones(SincronizadorService sincronizadorService) {
        this.sincronizadorService = sincronizadorService;
        this.apIservice = sincronizadorService.getApIservice();
        this.handler = sincronizadorService.getHandler();
        this.token = sincronizadorService.getToken();
        this.servicioLocal = DatosLocalesSincronos.getInstance(App.getContext());
        this.sharedPreferencesRepo = SharedPreferencesRepo.getInstance();
        this.nombreUsuario = sharedPreferencesRepo.readLogin().getEmail();
    }


    /**
     * Itera una lista de canciones analizando las modicaciones
     *
     * @param idGrupo          El grupo al que pertenecen
     * @param cancionesRemotas La lista de canciones del servidor
     * @throws CredencialesErroneasException   Si hay problema con las credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException                     Si ha ocurrido un error
     */
    public void comprobarCanciones(UUID idGrupo, List<CancionApiEnt> cancionesRemotas) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        this.idGrupo = idGrupo;
        if (cancionesRemotas == null)
            cancionesRemotas = new ArrayList<>();
        List<CancionEntity> cancionesLocales = servicioLocal.getGrupoWithUsuariosAndCanciones(idGrupo).canciones;
        for (CancionEntity cancionLocal : cancionesLocales) {
            CancionApiEnt cancionRemota = cancionesRemotas.stream().filter(cancion -> UUID.fromString(cancion.getId()).equals(cancionLocal.getId())).findFirst().orElse(null);
            comprobarCancion(cancionLocal, cancionRemota);
        }
    }

    /**
     * Comprueba las modificaciones de una cancion actuando segun el estado
     *
     * @param cancionLocal  La cancion local
     * @param cancionRemota La cancion remota
     * @throws CredencialesErroneasException   Si hay problema con las credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException                     Si ha ocurrido un error
     */
    public void comprobarCancion(CancionEntity cancionLocal, CancionApiEnt cancionRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        handler.onSendStatus("Comprobando midificaciones de cancion " + cancionLocal.getNombre());
        int estado = estadoCanciones(cancionLocal, cancionRemota);
        switch (estado) {
            case V0_X:
                agregarAlServidor(cancionLocal);
                comprobarNotas(cancionLocal, cancionRemota);
                break;
            case VN_X:
                eliminarLocal(cancionLocal);
                break;
            case SVN_VN:
                comprobarNotas(cancionLocal, cancionRemota);
                break;
            case SVN_VQ:
                actualizarLocal(cancionLocal, cancionRemota);
                comprobarNotas(cancionLocal, cancionRemota);
                break;
            case EVN_VN:
                //actualizar servidor
                Conflicto<CancionEntity, CancionApiEnt> conflicto = actualizarServidorConDatosLocales(cancionLocal, cancionRemota);
                //si hay conflicto resolverlo
                if (conflicto != null) {
                    resolverConflicto(conflicto);
                } else {
                    //si no hay conflicto comprobar las canciones
                    comprobarNotas(cancionLocal, cancionRemota);
                }
                break;
            case EVN_VQ:
                Conflicto conflictoDirecto = new Conflicto<CancionEntity, CancionApiEnt>(Conflicto.T_CANCION, cancionLocal, cancionRemota);
                resolverConflicto(conflictoDirecto);
                break;
            case B_VN:
                eliminarLocal(cancionLocal);
                eliminarDeServidor(cancionLocal);
                break;
            case B_X:
                eliminarLocal(cancionLocal);
                break;
            case A_X:
                eliminarLocal(cancionLocal);
                break;
            case A_VN:
                eliminarLocal(cancionLocal);
                break;

        }

    }

    /**
     * Actualiza una cancion en el servidor usando los datos locales
     *
     * @param cancionLocal  La cancion local
     * @param cancionRemota La cancion remota
     * @return Un conflicto si la respuesta es 409 o null en otro caso
     * @throws CredencialesErroneasException   Si hay problema con las credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException                     Si ha ocurrido un error
     */
    private Conflicto<CancionEntity, CancionApiEnt> actualizarServidorConDatosLocales(CancionEntity cancionLocal, CancionApiEnt cancionRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {

        Response<CancionApiEnt> lr = null;

        try {
            lr = apIservice.updateCancion(cancionLocal, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateCancion(MediadorDeEntidades.cancionApiEntToCancionEntity(idGrupo.toString(), lr.body()));
                    break;
                case 409:
                    //si hay conflicto devolverlo
                    CancionApiEnt remotoModificado = new GsonBuilder().create().fromJson(lr.errorBody().string(), CancionApiEnt.class);
                    return new Conflicto<CancionEntity, CancionApiEnt>(Conflicto.T_CANCION, cancionLocal, remotoModificado);
                case 401:
                    throw new CredencialesErroneasException("");
                default:
                    handler.onSendMessage("" + lr.code());
            }
        } catch (IOException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
        //SIN CONFLICTO
        return null;
    }

    /**
     * Lanza la resolucion de un conflicto y espera a que sea resuelto
     *
     * @param conflicto El conflicto
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void resolverConflicto(Conflicto<CancionEntity, CancionApiEnt> conflicto) throws TerminarSincronizacionException {
        //mandar conflicto
        sincronizadorService.getHandler().onConflicto(conflicto);
        try {
            //esperar resolucion
            conflicto.esperar();
            //recoger solucion
            CancionEntity solucion = conflicto.getResuelto();
            //actualizar en local y en remoto con la eleccion de solucion
            servicioLocal.updateCancion(solucion);
            comprobarCancion(solucion, conflicto.getRemoto());
        } catch (InterruptedException | CredencialesErroneasException |
                 TerminarSincronizacionException | IOException e) {
            throw new TerminarSincronizacionException("Sincronización terminada");
        }
    }


    /**
     * Eliminar una cancion del servidor
     *
     * @param cancionLocal La cancion local
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void eliminarDeServidor(CancionEntity cancionLocal) throws TerminarSincronizacionException {

        try {
            Response<ResponseBody> lr = apIservice.deleteCancion(cancionLocal.getId().toString(), token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }

    /**
     * Acutalizar una cancion local con los datos remotos
     *
     * @param cancionLocal  Cancion local
     * @param cancionRemota Cancion remota
     */
    private void actualizarLocal(CancionEntity cancionLocal, CancionApiEnt cancionRemota) {
        cancionLocal.setNombre(cancionRemota.getNombre());
        cancionLocal.setDescripcion(cancionRemota.getDescripcion());
        cancionLocal.setDuracion(cancionRemota.getDuracion());
        cancionLocal.setVersion(cancionRemota.getVersion());
        cancionLocal.setBorrado(false);
        cancionLocal.setEditado(false);
        servicioLocal.updateCancion(cancionLocal);
    }

    /**
     * Eliminar una cnacion local
     *
     * @param cancionLocal La cancion local
     */
    private void eliminarLocal(CancionEntity cancionLocal) {
        servicioLocal.deleteCancion(cancionLocal);
    }


    /**
     * Agregar cancion local al servidor
     * @param cancionLocal La cancion local
     * @throws CredencialesErroneasException Si las credenciales son erroneas
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void agregarAlServidor(CancionEntity cancionLocal) throws CredencialesErroneasException, TerminarSincronizacionException {
        try {
            Response<CancionApiEnt> lr = apIservice.insertCancion(idGrupo.toString(), cancionLocal, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateCancion(MediadorDeEntidades.cancionApiEntToCancionEntity(idGrupo.toString(), lr.body()));
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
     * Lanza la comprobacion de notas
     * @param cancionLocal La cancion local
     * @param cancionRemota La cancion remota
     * @throws CredencialesErroneasException Si hay credenciales erroneas
     * @throws IOException Si se ha producido un error
     * @throws TerminarSincronizacionException Si hay quet erminar la sincronizacion
     */
    private void comprobarNotas(CancionEntity cancionLocal, CancionApiEnt cancionRemota) throws CredencialesErroneasException, IOException, TerminarSincronizacionException {
        ComprobadorModificacionesNotas comprobadorNotas = new ComprobadorModificacionesNotas(sincronizadorService);

        comprobadorNotas.comprobarNotas(cancionLocal.getId(), (cancionRemota != null) ? cancionRemota.getNotas() : null);

    }
}
