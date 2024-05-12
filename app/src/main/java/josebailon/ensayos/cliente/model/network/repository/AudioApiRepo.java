package josebailon.ensayos.cliente.model.network.repository;

import josebailon.ensayos.cliente.model.network.APIBuilder;
import josebailon.ensayos.cliente.model.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioApiRepo {
    private static volatile AudioApiRepo instancia = null;
    APIservice servicio;
    private AudioApiRepo() {
        servicio = APIBuilder.getBuilder().create(APIservice.class);
    }

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



    public Response<ResponseBody> descarga(String idNota, String token, IDescargaResponse callback){
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
        return null;
    }



    public interface IDescargaResponse{
        void onResponse(Response<ResponseBody> response);
        void onFailure(Throwable t);
    }

}
