package josebailon.ensayos.cliente.model.network.repository;

import josebailon.ensayos.cliente.model.network.APIBuilder;
import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.model.RegistroResponse;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Repositorio de web API de autorizaciones y login
 *
 * @author Jose Javier Bailon Ortiz
 */
public class AuthApiRepo {

    /**
     * Instancia singletion
     */
    private static volatile AuthApiRepo instancia = null;

    /**
     * Servicio de acceso a la web API
     */
    APIservice servicio;

    /**
     * Constructor privado para singleton
     */
    private AuthApiRepo() {
        servicio = APIBuilder.getBuilder().create(APIservice.class);
    }

    /**
     * Devuelve una instancia singleton
     * @return La instancia
     */
    public static AuthApiRepo getInstance() {
        if (instancia == null) {
            synchronized (AuthApiRepo.class) {
                if (instancia == null) {
                    instancia = new AuthApiRepo();
                }
            }
        }
        return instancia;
    }


    /**
     * Hace una llamada de login a la web API
     * @param loginRequest Peticion
     * @param callback Callback al que suministrar la respuesta
     */
    public void login(LoginRequest loginRequest, ILoginResponse callback){
        Call<LoginResponse> loginCall = servicio.login(loginRequest);
        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                callback.onResponse(response);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    /**
     * Hace una llamada de registro de usuario a la web API
     * @param loginRequest Peticion
     * @param callback Callaback al que suminsitrar la respuesta
     */
    public void registro(LoginRequest loginRequest, IRegistroResponse callback){
        Call<RegistroResponse> registroCall = servicio.registro(loginRequest);
        registroCall.enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                callback.onResponse(response);
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    /**
     * Interfaz de callback de login
     */
    public interface ILoginResponse{
        void onResponse(Response<LoginResponse> loginResponse);
        void onFailure(Throwable t);
    }

    /**
     * Interfaz de callback de registro
     */
    public interface IRegistroResponse{
        void onResponse(Response<RegistroResponse> registroResponse);
        void onFailure(Throwable t);
    }

}
