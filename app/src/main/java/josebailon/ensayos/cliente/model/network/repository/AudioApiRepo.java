package josebailon.ensayos.cliente.model.network.repository;

import josebailon.ensayos.cliente.model.network.APIBuilder;
import josebailon.ensayos.cliente.model.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Repositorio de web API de audios
 *
 * @author Jose Javier Bailon Ortiz
 */
public class AudioApiRepo {

    /**
     * Instancia singleton del repositorio
     */
    private static volatile AudioApiRepo instancia = null;

    /**
     * Servicio Retrofit
     */
    APIservice servicio;

    /**
     * Constructor privado
     */
    private AudioApiRepo() {
        servicio = APIBuilder.getBuilder().create(APIservice.class);
    }


    /**
     * Devuelve una isntancia singleton
     * @return La instancia
     */
    public static AudioApiRepo getInstance() {
        if (instancia == null) {
            synchronized (AudioApiRepo.class) {
                if (instancia == null) {
                    instancia = new AudioApiRepo();
                }
            }
        }
        return instancia;
    }


    /**
     *  Hace una llamada a descargar un audio de la API
     * @param idNota Id de la nota
     * @param token Token de acceso
     * @param callback Callback al que suministrar el resultado
     */
    public void descarga(String idNota, String token, IDescargaResponse callback){
        Call<ResponseBody> descargaCall = servicio.descarga(idNota,token);
        descargaCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                callback.onResponse(response);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    /**
     * Interfaz de callback para la descarga
     */
    public interface IDescargaResponse{
        /**
         * Cuando se puede acceder al servidor
         * @param response Respuesta obtenida
         */
        void onResponse(Response<ResponseBody> response);

        /**
         * Cuando falla la comunicacion con el servidor
         * @param t Excepcion recogida
         */
        void onFailure(Throwable t);
    }

}
