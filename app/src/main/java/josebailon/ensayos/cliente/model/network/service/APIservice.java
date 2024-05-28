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

/**
 * Servicio Retrofit de llamas a la api
 *
 * @author Jose Javier Bailon Ortiz
 */
public interface APIservice {
    /**
     * Hacer login
     * @param loginRequest Peticion
     * @return Respuesta
     */
    @POST("/auth/login")
    public Call<LoginResponse> login(@Body LoginRequest loginRequest);

    /**
     * Hacer registro
     * @param loginRequest Peticion
     * @return Respuesta
     */
    @POST("/auth/registrar")
    public Call<RegistroResponse> registro(@Body LoginRequest loginRequest);

    /**
     * Comprobar existencia de usuario
     * @param email Email del usuario
     * @param authHeader Token de acceso
     * @return
     */
    @GET("/usuario/existe/{email}")
    public Call<UsuarioResponse> existe(@Path("email")String email, @Header("Authorization") String authHeader);

    /**
     * Descargar un audio
     * @param uuidnota UUID de la nota a la que pertence el audio
     * @param authHeader Token de acceso
     * @return Respuesta
     */
    @GET("/audio/{uuidnota}")
    public Call<ResponseBody> descarga(@Path("uuidnota")String uuidnota, @Header("Authorization") String authHeader);

    /**
     * Descagar todos los datos del usuario
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @GET("/usuario/grupos")
    public Call<List<GrupoApiEnt>> getDatoscompletos(@Header("Authorization") String authHeader);

    /**
     * Insertar un grupo
     * @param grupo El grupo
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @POST("/grupo")
    public Call<GrupoApiEnt> insertGrupo(@Body GrupoEntity grupo, @Header("Authorization") String authHeader);

    /**
     * Agregar un usuario a un grupo
     * @param email El email del usuario
     * @param idgrupo UUID del grupo
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @POST("/grupo/{idgrupo}/{email}")
    public Call<Object> agregarUsarioAGrupo(@Path("email")String email, @Path("idgrupo")String idgrupo, @Header("Authorization") String authHeader);

    /**
     * Eliminar un usuario de un grupo
     * @param email El email del usuario
     * @param idgrupo UUID del grupo
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @DELETE("/grupo/{idgrupo}/{email}")
    public Call<ResponseBody> eliminarUsuarioDeGrupo(@Path("email")String email, @Path("idgrupo")String idgrupo, @Header("Authorization") String authHeader);

    /**
     * Borrar un grupo
     * @param idgrupo Uuid del grupo
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @DELETE("/grupo/{idgrupo}")
    public Call<ResponseBody> deleteGrupo(@Path("idgrupo")String idgrupo, @Header("Authorization") String authHeader);

    /**
     * Actualizar un grupo
     * @param grupo El grupo
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @PUT("/grupo")
    public Call<GrupoApiEnt> updateGrupo(@Body GrupoEntity grupo, @Header("Authorization") String authHeader);

    /**
     * Insertar una cancion
     * @param idgrupo UUID del grupo
     * @param cancion Cancion
     * @param authHeader Token de acceso
     * @return La repsuesta
     */
    @POST("/cancion/{idgrupo}")
    public Call<CancionApiEnt> insertCancion(@Path("idgrupo")String idgrupo, @Body CancionEntity cancion, @Header("Authorization") String authHeader);

    /**
     * Actualizar una cacnion
     * @param cancion La cancion
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @PUT("/cancion")
    public Call<CancionApiEnt> updateCancion(@Body CancionEntity cancion, @Header("Authorization") String authHeader);


    /**
     * Borrar la cancion
     * @param idcancion UUID de la cancion
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @DELETE("/cancion/{idcancion}")
    public Call<ResponseBody> deleteCancion(@Path("idcancion")String idcancion, @Header("Authorization") String authHeader);

    /**
     * Insertar una nota
     * @param idcancion UUID de la cancion a la que asignar la nota
     * @param nota La nota
     * @param authHeader El token de acdeso
     * @return La respuesta
     */
    @POST("/nota/{idcancion}")
    public Call<NotaApiEnt> insertNota(@Path("idcancion")String idcancion, @Body NotaEntity nota, @Header("Authorization") String authHeader);


    /**
     * Insertar un audio
     *
     * @param archivoAudio Archivo de audio
     * @param audio Entidad audio
     * @param authHeader Token de acceso
     * @return Respusta
     */
    @POST("/audio")
    @Multipart
    Call<AudioApiEnt> insertAudio(@Part MultipartBody.Part archivoAudio, @Part("datos") AudioEntity audio, @Header("Authorization") String authHeader);

    /**
     * Borrar una nota
     * @param idnota UUID de la nota
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @DELETE("/nota/{idnota}")
    public Call<ResponseBody> deleteNota(@Path("idnota")String idnota, @Header("Authorization") String authHeader);

    /**
     * Actualizar una nota
     * @param nota La nota
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @PUT("/nota")
    public Call<NotaApiEnt> updateNota(@Body NotaEntity nota, @Header("Authorization") String authHeader);

    /**
     * Borrar un audio
     * @param idaudio UUID del audio
     * @param authHeader Token de acceso
     * @return La respuesta
     */
    @DELETE("/audio/{idaudio}")
    public Call<ResponseBody> deleteAudio(@Path("idaudio")String idaudio, @Header("Authorization") String authHeader);

    /**
     * Actualizar un audio
     * @param archivoAudio Archivo de audio
     * @param audio Entidad audio
     * @param authHeader Token de acceso
     * @return La repsuesta
     */
    @PUT("/audio")
    @Multipart
    Call<AudioApiEnt> updateAudio(@Part MultipartBody.Part archivoAudio, @Part("datos") AudioEntity audio, @Header("Authorization") String authHeader);

}
