package josebailon.ensayos.cliente.data.network.service;

import josebailon.ensayos.cliente.data.network.model.LoginRequest;
import josebailon.ensayos.cliente.data.network.model.LoginResponse;
import josebailon.ensayos.cliente.data.network.model.RegistroResponse;
import josebailon.ensayos.cliente.data.network.model.UsuarioResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIservice {
    @POST("/auth/login")
    public Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("/auth/registrar")
    public Call<RegistroResponse> registro(@Body LoginRequest loginRequest);

    @GET("/usuario/existe/{email}")
    public Call<UsuarioResponse> existe(@Path("email")String email, @Header("Authorization") String authHeader);


//    @GET("/auth/login/{email}")
//    public LoginResponse login(String email, String password);
}
