package josebailon.ensayos.cliente.viewmodel;

import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.archivos.ArchivosRepo;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesAsincronos;
import josebailon.ensayos.cliente.model.dto.LoginDto;
import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.repository.AudioApiRepo;
import josebailon.ensayos.cliente.model.network.repository.AuthApiRepo;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class VerNotaViewModel extends ViewModel {

    public final int MODO_CREACION=0;
    public final int MODO_EDICION=1;

    private DatosLocalesAsincronos servicioDb = DatosLocalesAsincronos.getInstance(App.getContext());
    private ArchivosRepo archivosRepo = ArchivosRepo.getInstance();
    private AudioApiRepo audioApiRepo = AudioApiRepo.getInstance();
    private AuthApiRepo authApiRepo = AuthApiRepo.getInstance();

    private SharedPreferencesRepo sharedRepo = SharedPreferencesRepo.getInstance();
    private int modo=0;
    private MutableLiveData<Boolean> haCambiado= new MutableLiveData<>(false);

    private MutableLiveData<Boolean> descargando = new MutableLiveData<>(false);
    private Executor executor = Executors.newSingleThreadExecutor();
    private UUID idnota;


    MutableLiveData<String> mensaje = new MutableLiveData<>();


    public MutableLiveData<Boolean> getDescargando() {
        return descargando;
    }

    private LiveData<NotaAndAudio> notaAndAudio;

    private MutableLiveData<Boolean> ocupado = new MutableLiveData<>(false);

    public LiveData<String> getMensaje() {
        return mensaje;
    }

//    public UUID getIdcancion() {
//        return idcancion;
//    }
//
//    public void setIdcancion(UUID idcancion) {
//        this.idcancion = idcancion;
//    }



    public LiveData<NotaAndAudio> getNotaAndAudio() {
        return notaAndAudio;
    }



    /**
     * Almacena la id de la nota a editar y en caso de ser nula es que se está en modo de creacion
     * @param id
     */
    public void setIdNota(String id) {
            idnota=UUID.fromString(id);
            notaAndAudio= servicioDb.getNotaWithAudioById(idnota);
    }




      public String getRutaAudio() {
        return archivosRepo.getAudio(notaAndAudio.getValue().audio.getArchivo());
    }

    public boolean existeArchivo() {
        if (notaAndAudio.getValue()==null ||notaAndAudio.getValue().audio==null)
            return false;
        boolean r= archivosRepo.existeAudio(notaAndAudio.getValue().audio.getArchivo());
        return r;
    }

    public void descargarAudio() {
        descargando.setValue(true);
        LoginDto l = sharedRepo.readLogin();
        if (TextUtils.isEmpty(l.getEmail())) {
            this.mensaje.postValue("Debe hacer login antes de agregar un usuario");
            descargando.setValue(false);
            return;
        }
        authApiRepo.login(new LoginRequest(l.getEmail(), l.getPassword()), new AuthApiRepo.ILoginResponse() {
            @Override
            public void onResponse(Response<LoginResponse> loginResponse) {

                if (loginResponse.code() == 200) {

                    executor.execute(() -> {
                        //hacer llamada
                        audioApiRepo.descarga(idnota.toString(), loginResponse.body().getBearer(),
                                new AudioApiRepo.IDescargaResponse() {
                                    @Override
                                    public void onResponse(Response<ResponseBody> response) {
                                        if (response.code()==200) {
                                            String header = response.headers().get("Content-Disposition");
                                            String nombre = header.replace("attachment; filename=", "");
                                            //guardar descarga

                                            try {
                                                archivosRepo.guardarBytes(response.body().byteStream(), nombre);
                                            } catch (IOException e) {
                                                mensaje.setValue("No se ha podido descargar el archivo");
                                            }
                                                descargando.setValue(false);
                                        }
                                        else{
                                            mensaje.setValue("No se ha podido descargar el archivo");
                                        }
                                            descargando.setValue(false);
                                    }
                                    @Override
                                    public void onFailure(Throwable t) {
                                        mensaje.setValue("No se ha podido descargar el archivo");
                                    }
                                });
                    });
                }
                else
                    mensaje.postValue("No se ha podido descargar el archivo");
            }
            @Override
            public void onFailure(Throwable t) {
                mensaje.postValue("Sin conexión con el servidor");
            }
        });
    }

    public Uri getUriDeAudio(String archivo){
        return archivosRepo.generarUri(archivo);
    }

    public LiveData<CancionEntity> getCancion() {
        return servicioDb.getCancionByIdNota(idnota);
    }
}