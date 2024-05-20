package josebailon.ensayos.cliente.model.network.service;

import java.util.List;

import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.model.RegistroResponse;
import josebailon.ensayos.cliente.model.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.model.network.model.entidades.AudioApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.NotaApiEnt;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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
    public Call<ResponseBody> eliminarUsuarioDeGrupo(@Path("email")String email, @Path("idgrupo")String idgrupo, @Header("Authorization") String authHeader);

    @DELETE("/grupo/{idgrupo}")
    public Call<ResponseBody> deleteGrupo(@Path("idgrupo")String idgrupo, @Header("Authorization") String authHeader);

    @PUT("/grupo")
    public Call<GrupoApiEnt> updateGrupo(@Body GrupoEntity grupo, @Header("Authorization") String authHeader);

    @POST("/cancion/{idgrupo}")
    public Call<CancionApiEnt> insertCancion(@Path("idgrupo")String idgrupo, @Body CancionEntity cancion, @Header("Authorization") String authHeader);

    @PUT("/cancion")
    public Call<CancionApiEnt> updateCancion(@Body CancionEntity cancion, @Header("Authorization") String authHeader);

    @DELETE("/cancion/{idcancion}")
    public Call<ResponseBody> deleteCancion(@Path("idcancion")String idcancion, @Header("Authorization") String authHeader);

    @POST("/nota/{idcancion}")
    public Call<NotaApiEnt> insertNota(@Path("idcancion")String idcancion, @Body NotaEntity nota, @Header("Authorization") String authHeader);

    @POST("/audio")
    @Multipart
    Call<AudioApiEnt> insertAudio(@Part MultipartBody.Part imageFile, @Part("datos") AudioEntity audio, @Header("Authorization") String authHeader);

    @DELETE("/nota/{idnota}")
    public Call<ResponseBody> deleteNota(@Path("idnota")String idnota, @Header("Authorization") String authHeader);

    @PUT("/nota")
    public Call<NotaApiEnt> updateNota(@Body NotaEntity nota, @Header("Authorization") String authHeader);
    @DELETE("/audio/{idaudio}")
    public Call<ResponseBody> deleteAudio(@Path("idaudio")String idaudio, @Header("Authorization") String authHeader);

    @PUT("/audio")
    @Multipart
    Call<AudioApiEnt> updateAudio(@Part MultipartBody.Part imageFile, @Part("datos") AudioEntity audio, @Header("Authorization") String authHeader);


//    @GET("/auth/login/{email}")
//    public LoginResponse login(String email, String password);
}
