package josebailon.ensayos.cliente.model.sincronizacion;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import josebailon.ensayos.cliente.model.database.repository.SharedPreferencesRepo;
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

public class SincronizadorService extends Thread {


    public static int R_OK = 0;
    public static int R_CREDENCIALES_ERRONEAS = 100;

    private APIservice apIservice = APIBuilder.getBuilder().create(APIservice.class);
    private SharedPreferencesRepo sharedPreferencesRepo = SharedPreferencesRepo.getInstance();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private ISincronizadoFeedbackHandler handler;
    private String token;

    int estado = 0;


    MutableLiveData<Semaphore> s;


    public SincronizadorService(ISincronizadoFeedbackHandler handler) {
        this.handler = handler;

    }


    public APIservice getApIservice() {
        return apIservice;
    }

    public ISincronizadoFeedbackHandler getHandler() {
        return handler;
    }

    public String getToken() {
        return token;
    }

    public MutableLiveData<Semaphore> getSemaforo() {
        s = new MutableLiveData<Semaphore>();
        return s;
    }


    @Override
    public void run() {
        super.run();

        for (int i = 0; i < 15; i++) {
            Log.i("JJBO", "" + i);
            if (i == 10) {
                Semaphore sem = new Semaphore(0);
                ExecutorService e = Executors.newSingleThreadExecutor();

                s.postValue(sem);


                try {
                    sem.acquire();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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
            }
        });
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

    private void comprobarModificaciones(List<GrupoApiEnt> gruposRemotos) throws CredencialesErroneasException, TerminarSincronizacionException {
        ComprobadorModificacionesGrupos comprobadorModificacionesGrupos = new ComprobadorModificacionesGrupos(this);
        comprobadorModificacionesGrupos.comprobarGrupos(gruposRemotos);
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
