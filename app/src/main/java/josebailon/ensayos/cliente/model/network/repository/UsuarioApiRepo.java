package josebailon.ensayos.cliente.model.network.repository;

import android.util.Log;

import java.io.IOException;

import josebailon.ensayos.cliente.model.network.APIBuilder;
import josebailon.ensayos.cliente.model.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import retrofit2.Response;


/**
 * Repositorio de web API sobre usuario
 *
 * @author Jose Javier Bailon Ortiz
 */
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


    /**
     * Peticion para comprobar que un usuario existe o no
     * @param email El email
     * @param token El token de acceso
     * @return El usuario recibido
     * @throws IOException Si no se puede conectar con el servidor
     */
    public Response<UsuarioResponse> existe(String email, String token) throws IOException {
        return servicio.existe(email, token).execute();
    }



}
