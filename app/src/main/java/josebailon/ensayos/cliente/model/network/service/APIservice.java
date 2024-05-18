package josebailon.ensayos.cliente.model.network.service;

import java.util.List;

import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.model.RegistroResponse;
import josebailon.ensayos.cliente.model.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIservice {
    //HACER LOGIN
    @POST("/auth/login")
    public Call<LoginResponse> login(@Body LoginRequest loginRequest);

    //HACER REGISTRO
    @POST("/auth/registrar")
    public Call<RegistroResponse> registro(@Body LoginRequest loginRequest);

    //COMPROBAR EXISTENCIA DE USUARIO
    @GET("/usuario/existe/{email}")
    public Call<UsuarioResponse> existe(@Path("email")String email, @Header("Authorization") String authHeader);

    //DESCARGAR AUDIO
    @GET("/audio/{uuidnota}")
    public Call<ResponseBody> descarga(@Path("uuidnota")String uuidnota, @Header("Authorization") String authHeader);

    //DESCARGAR TODOS LOS DATOS DE UN USUARIO
    @GET("/usuario/grupos")
    public Call<List<GrupoApiEnt>> getDatoscompletos(@Header("Authorization") String authHeader);

    @POST("/grupo")
    public Call<GrupoApiEnt> insertGrupo(@Body GrupoEntity grupo, @Header("Authorization") String authHeader);

    @POST("/grupo/{idgrupo}/{email}")
    public Call<Object> agregarUsarioAGrupo(@Path("email")String email, @Path("idgrupo")String idgrupo, @Header("Authorization") String authHeader);
    @DELETE("/grupo/{idgrupo}/{email}")
    public Call<Object> eliminarUsuarioDeGrupo(@Path("email")String email, @Path("idgrupo")String idgrupo, @Header("Authorization") String authHeader);

    @DELETE("/grupo/{idgrupo}")
    public Call<Object> deleteGrupo(@Path("idgrupo")String idgrupo, @Header("Authorization") String authHeader);

    @PUT("/grupo")
    public Call<GrupoApiEnt> updateGrupo(@Body GrupoEntity grupo, @Header("Authorization") String authHeader);

    @POST("/cancion/{idgrupo}")
    public Call<CancionApiEnt> insertCancion(@Path("idgrupo")String idgrupo, @Body CancionEntity cancion, @Header("Authorization") String authHeader);

    @PUT("/cancion")
    public Call<CancionApiEnt> updateCancion(@Body CancionEntity cancion, @Header("Authorization") String authHeader);

    @DELETE("/cancion/{idcancion}")
    public Call<Object> deleteCancion(@Path("idcancion")String idcancion, @Header("Authorization") String authHeader);


//    @GET("/auth/login/{email}")
//    public LoginResponse login(String email, String password);
}
