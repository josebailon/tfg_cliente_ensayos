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

public class SincronizadorService  {


    public static int R_OK = 0;
    public static int R_CREDENCIALES_ERRONEAS = 100;

    private APIservice apIservice = APIBuilder.getBuilder().create(APIservice.class);
    private SharedPreferencesRepo sharedPreferencesRepo = SharedPreferencesRepo.getInstance();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ISincronizadorFeedbackHandler handler;
    private String token;
    private int estado = 0;
    private ArchivosRepo archivosRepo = ArchivosRepo.getInstance();

    private DatosLocalesSincronos servicioDatosLocales = DatosLocalesSincronos.getInstance(App.getContext());

    MutableLiveData<Semaphore> s;


    public SincronizadorService(ISincronizadorFeedbackHandler handler) {
        this.handler = handler;

    }


    public APIservice getApIservice() {
        return apIservice;
    }

    public ISincronizadorFeedbackHandler getHandler() {
        return handler;
    }

    public String getToken() {
        return token;
    }

    public MutableLiveData<Semaphore> getSemaforo() {
        s = new MutableLiveData<Semaphore>();
        return s;
    }



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

    private void limpiarArchivos() {
        List<String> archivos = archivosRepo.getAllAudioFiles();
        List<AudioEntity> audios =  servicioDatosLocales.getAllAudios();
        for (String archivo: archivos) {
            if (!audios.stream().filter(a -> a.getArchivo().equals(archivo)).findFirst().isPresent()){
                archivosRepo.borrarAudio(archivo);
            }
        }
    }


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

    private void comprobarNuevasEntidadesRemotas(List<GrupoApiEnt> gruposRemotos) {
        ComprobadorNuevosRemotos comprobadorNuevosRemotos = new ComprobadorNuevosRemotos(this, gruposRemotos);
        comprobadorNuevosRemotos.iniciar();

    }

    private void comprobarModificaciones(List<GrupoApiEnt> gruposRemotos) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        ComprobadorModificacionesGrupos comprobadorModificacionesGrupos = new ComprobadorModificacionesGrupos(this);
        comprobadorModificacionesGrupos.comprobarGrupos(gruposRemotos);
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }


}
