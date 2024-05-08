package josebailon.ensayos.cliente.data.repository;

import android.util.Log;

import java.io.IOException;

import josebailon.ensayos.cliente.data.network.APIBuilder;
import josebailon.ensayos.cliente.data.network.model.LoginRequest;
import josebailon.ensayos.cliente.data.network.model.LoginResponse;
import josebailon.ensayos.cliente.data.network.model.RegistroResponse;
import josebailon.ensayos.cliente.data.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.data.network.service.APIservice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioApiRepo {
    private static volatile UsuarioApiRepo instancia = null;
    APIservice servicio;
    private UsuarioApiRepo() {
        servicio = APIBuilder.getBuilder().create(APIservice.class);
    }

    public static UsuarioApiRepo getInstance() {
        if (instancia == null) {
            synchronized (SharedPreferencesRepo.class) {
                if (instancia == null) {
                    instancia = new UsuarioApiRepo();
                }
            }
        }
        return instancia;
    }





    public Response<UsuarioResponse> existe(String email, String token) throws IOException {
        Log.i("JJBO",token);
        return servicio.existe(email, token).execute();
    }

    public interface IExisteResponse{
        void onResponse(Response<UsuarioResponse> usuarioResponse);
        void onFailure(Throwable t);
    }

}
