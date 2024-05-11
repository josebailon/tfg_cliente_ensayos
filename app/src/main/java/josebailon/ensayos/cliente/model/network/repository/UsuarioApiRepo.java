package josebailon.ensayos.cliente.model.network.repository;

import android.util.Log;

import java.io.IOException;

import josebailon.ensayos.cliente.model.network.APIBuilder;
import josebailon.ensayos.cliente.model.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import retrofit2.Response;

public class UsuarioApiRepo {
    private static volatile UsuarioApiRepo instancia = null;
    APIservice servicio;
    private UsuarioApiRepo() {
        servicio = APIBuilder.getBuilder().create(APIservice.class);
    }

    public static UsuarioApiRepo getInstance() {
        if (instancia == null) {
            synchronized (UsuarioApiRepo.class) {
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
