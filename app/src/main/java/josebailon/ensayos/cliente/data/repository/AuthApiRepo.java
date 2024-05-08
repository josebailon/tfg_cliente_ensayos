package josebailon.ensayos.cliente.data.repository;

import josebailon.ensayos.cliente.data.network.APIBuilder;
import josebailon.ensayos.cliente.data.network.model.LoginRequest;
import josebailon.ensayos.cliente.data.network.model.LoginResponse;
import josebailon.ensayos.cliente.data.network.model.RegistroResponse;
import josebailon.ensayos.cliente.data.network.service.APIservice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthApiRepo {
    private static volatile AuthApiRepo instancia = null;
    APIservice servicio;
    private AuthApiRepo() {
        servicio = APIBuilder.getBuilder().create(APIservice.class);
    }

    public static AuthApiRepo getInstance() {
        if (instancia == null) {
            synchronized (SharedPreferencesRepo.class) {
                if (instancia == null) {
                    instancia = new AuthApiRepo();
                }
            }
        }
        return instancia;
    }

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


    public interface ILoginResponse{
        void onResponse(Response<LoginResponse> loginResponse);
        void onFailure(Throwable t);
    }

    public interface IRegistroResponse{
        void onResponse(Response<RegistroResponse> registroResponse);
        void onFailure(Throwable t);
    }

//    public ApiResponse fetchDataSynchronously() {
//        Call<ApiResponse> call = apiService.getApiData();
//        try {
//            Response<ApiResponse> response = call.execute();
//            if (response.isSuccessful()) {
//                return response.body();
//            } else {
//                // Maneja errores aquí
//                return null;
//            }
//        } catch (IOException e) {
//            // Maneja excepciones aquí
//            return null;
//        }
//    }



}
