package josebailon.ensayos.cliente.model.sincronizacion;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.archivos.ArchivosRepo;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesSincronos;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.dto.LoginDto;
import josebailon.ensayos.cliente.model.network.APIBuilder;
import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import josebailon.ensayos.cliente.model.sincronizacion.comprobadores.ComprobadorModificacionesGrupos;
import josebailon.ensayos.cliente.model.sincronizacion.comprobadores.ComprobadorNuevosRemotos;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.CredencialesErroneasException;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.TerminarSincronizacionException;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Servicio de sincronizacion.  Se encarga de orquestar la sincronizacion en un hilo propio y avisar
 * al callback e los progresos y mensajes.
 *
 * @author Jose Javier Bailon Ortiz
 */
public class SincronizadorService  {


    /**
     * REspuesta ok
     */
    public static int R_OK = 0;

    /**
     * REspuesta de credenciales erroneas
     */
    public static int R_CREDENCIALES_ERRONEAS = 100;

    /**
     * Servicio web de acceso a la API
     */
    private APIservice apIservice = APIBuilder.getBuilder().create(APIservice.class);

    /**
     * Repositorio de acceso a las Shared preferences
     */
    private SharedPreferencesRepo sharedPreferencesRepo = SharedPreferencesRepo.getInstance();

    /**
     * Executor que se encarga de realizar el trabajo de sincronizacion
     */
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Handler de escucha a los eventos del sincronizador
     */
    private ISincronizadorFeedbackHandler handler;

    /**
     * Token de acceso
     */
    private String token;

    /**
     * Estado de la sincronizacion
     */
    private int estado = 0;

    /**
     * Repositorio de acceso a los archivos
     */
    private ArchivosRepo archivosRepo = ArchivosRepo.getInstance();

    /**
     * Servicio de acceso sincrono a la base de datos
     */
    private DatosLocalesSincronos servicioDatosLocales = DatosLocalesSincronos.getInstance(App.getContext());


    /**
     * Constructor del servicio de sincronizacion
     * @param handler Handler de escucha
     */
    public SincronizadorService(ISincronizadorFeedbackHandler handler) {
        this.handler = handler;

    }


    /**
     * Devuelve el servicio de acceso a la web API
     * @return El APIservice
     */
    public APIservice getApIservice() {
        return apIservice;
    }

    /**
     * Devuelve el handler de escucha de la sincronizacion
     * @return El handler
     */
    public ISincronizadorFeedbackHandler getHandler() {
        return handler;
    }

    /**
     * Devuelve el token de acceso
     * @return
     */
    public String getToken() {
        return token;
    }


    /**
     * Inicia la sincronizacion en un hilo independiente
     */
    public void iniciar() {
        executor.execute(() -> {
            handler.onIniciado();
            try {
                recogerToken();
                List<GrupoApiEnt> datos = recogerDatosDeServidor();
                comprobarNuevasEntidadesRemotas(datos);
                comprobarModificaciones(datos);
                handler.onFinalizado();
            } catch (TerminarSincronizacionException ex) {
                handler.onSendMessage(ex.getMessage());
                handler.onFinalizado();
            } catch (CredencialesErroneasException ex) {
                setEstado(R_CREDENCIALES_ERRONEAS);
                handler.onFinalizado();
            } catch (IOException e) {
                handler.onSendMessage("Hay problemas contactando con el servidor");
                handler.onFinalizado();
            }

            //limpieza
            limpiarArchivos();
        });
    }

    /**
     * LImpia los archivos de audio almacenados tras la sincronizacion
     */
    private void limpiarArchivos() {
        List<String> archivos = archivosRepo.getAllAudioFiles();
        List<AudioEntity> audios =  servicioDatosLocales.getAllAudios();
        for (String archivo: archivos) {
            if (!audios.stream().filter(a -> a.getArchivo().equals(archivo)).findFirst().isPresent()){
                archivosRepo.borrarAudio(archivo);
            }
        }
    }


    /**
     * Hace login y recoge el token de acceso que se usara durante la sincronizacion
     * @throws TerminarSincronizacionException Si se ha producido un error de conexion
     * @throws CredencialesErroneasException Si las credenciales no son validas
     */
    private void recogerToken() throws TerminarSincronizacionException, CredencialesErroneasException {
        LoginDto login = sharedPreferencesRepo.readLogin();
        try {
            Response<LoginResponse> lr = apIservice.login(new LoginRequest(login.getEmail(), login.getPassword())).execute();
            switch (lr.code()) {
                case 200:
                    token = lr.body().getBearer();
                    break;
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }


    /**
     * Recoge los datos de grupos que pertenecen al usuario que hay en el servidor
     * @return La lista de grupos
     * @throws TerminarSincronizacionException Si no hay conexion con el servidor
     */
    private List<GrupoApiEnt> recogerDatosDeServidor() throws TerminarSincronizacionException {
        try {
            handler.onSendStatus("Recogiendo datos del servidor");
            Call<List<GrupoApiEnt>> llamada = apIservice.getDatoscompletos(token);
            Response<List<GrupoApiEnt>> datos = llamada.execute();
            handler.onSendStatus("datos del servidor recogidos");
            return datos.body();
        } catch (IOException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }

    /**
     * Lanza el comprobador de nuevas entidades remotas
     * @param gruposRemotos Lista de grupos remotos
     */
    private void comprobarNuevasEntidadesRemotas(List<GrupoApiEnt> gruposRemotos) {
        ComprobadorNuevosRemotos comprobadorNuevosRemotos = new ComprobadorNuevosRemotos(this, gruposRemotos);
        comprobadorNuevosRemotos.iniciar();
    }

    /**
     * Lanza el comprobador de modificaciones de grupos iniciando la cadena de comprobaciones de modificaciones de entidades
     * @param gruposRemotos Grupos remotos
     * @throws CredencialesErroneasException Si nlas credenciales son erroneas
     * @throws TerminarSincronizacionException Si se pide terminar sincronizacion
     * @throws IOException Si se produce un error durantie la sincronizacion
     */
    private void comprobarModificaciones(List<GrupoApiEnt> gruposRemotos) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        ComprobadorModificacionesGrupos comprobadorModificacionesGrupos = new ComprobadorModificacionesGrupos(this);
        comprobadorModificacionesGrupos.comprobarGrupos(gruposRemotos);
    }

    /**
     * Define el estado del sincronizador
     * @param estado
     */
    public void setEstado(int estado) {
        this.estado = estado;
    }


}
