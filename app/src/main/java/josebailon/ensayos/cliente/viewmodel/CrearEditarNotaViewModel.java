package josebailon.ensayos.cliente.viewmodel;

import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.sql.Date;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.archivos.service.ArchivosServicio;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.database.repository.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesAsincronos;
import josebailon.ensayos.cliente.model.dto.LoginDto;
import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.repository.AudioApiRepo;
import josebailon.ensayos.cliente.model.network.repository.AuthApiRepo;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class CrearEditarNotaViewModel extends ViewModel {

    public final int MODO_CREACION=0;
    public final int MODO_EDICION=1;

    private DatosLocalesAsincronos servicioDb = DatosLocalesAsincronos.getInstance(App.getContext());
    private ArchivosServicio servicioArchivos = new ArchivosServicio();
    private AudioApiRepo audioApiRepo = AudioApiRepo.getInstance();
    private AuthApiRepo authApiRepo = AuthApiRepo.getInstance();

    private SharedPreferencesRepo sharedRepo = SharedPreferencesRepo.getInstance();
    private int modo=0;
    private MutableLiveData<Boolean> haCambiado= new MutableLiveData<>(false);

    private MutableLiveData<Boolean> descargando = new MutableLiveData<>(false);
    private Executor executor = Executors.newSingleThreadExecutor();
    private UUID idnota;
    private UUID idcancion;

    MutableLiveData<String> mensaje = new MutableLiveData<>();

    public LiveData<Boolean> getOcupado() {
        return ocupado;
    }

    public LiveData<Boolean> getHaCambiado() {
        return haCambiado;
    }

    public MutableLiveData<Boolean> getDescargando() {
        return descargando;
    }

    private LiveData<NotaAndAudio> notaAndAudio;

    private MutableLiveData<Boolean> ocupado = new MutableLiveData<>(false);

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public UUID getIdcancion() {
        return idcancion;
    }

    public void setIdcancion(UUID idcancion) {
        this.idcancion = idcancion;
    }

    public int getModo() {
        return modo;
    }

    public boolean isHaCambiado() {
        return haCambiado.getValue();
    }


    public LiveData<NotaAndAudio> getNotaAndAudio() {
        return notaAndAudio;
    }

    public NotaAndAudio getNotaAndAudioObj() {
        return notaAndAudio.getValue();
    }

    /**
     * Almacena la id de la nota a editar y en caso de ser nula es que se está en modo de creacion
     * @param id
     */
    public void setIdNota(String id) {
        if (TextUtils.isEmpty(id)){
            modo=MODO_CREACION;
            idnota=UUID.randomUUID();
            NotaAndAudio na = new NotaAndAudio();
            NotaEntity n = new NotaEntity();
            n.setId(idnota);
            n.setNombre("");
            n.setTexto("");
            n.setVersion(0);
            n.setCancion(idcancion);
            na.nota=n;
            notaAndAudio= new MutableLiveData<>(na);
        }else{
            modo=MODO_EDICION;
            idnota=UUID.fromString(id);
            notaAndAudio= servicioDb.getNotaWithAudioById(idnota);
        }
    }

    public void guardarNota(){
        if (modo==MODO_CREACION){
            servicioDb.insertNotaWithAudio(notaAndAudio.getValue().nota, notaAndAudio.getValue().audio);
        }else{
            notaAndAudio.getValue().nota.setEditado(true);
            NotaEntity n = notaAndAudio.getValue().nota;
            AudioEntity a = notaAndAudio.getValue().audio;
            servicioDb.updateNotaWithAudio(n,a);
        }
    }

    public void actualizarTexto(String nombre, String texto) {
        notaAndAudio.getValue().nota.setNombre(nombre);
        notaAndAudio.getValue().nota.setTexto(texto);
        notaAndAudio.getValue().nota.setEditado(true);
        haCambiado.setValue(true);
    }

    public void quitarAudio() {
        AudioEntity a = notaAndAudio.getValue().audio;
        //si es modo creacion se elimina directamente poniendolo a null
        if (modo==MODO_CREACION) {
            if (a != null) {
                notaAndAudio.getValue().audio = null;
                haCambiado.setValue(true);
            }
        }else{
            //si existe
            if (a!=null){
            // si es version 0 se pone a null
                if (a!=null && a.getVersion()==0){
                    notaAndAudio.getValue().audio=null;
                //si es otra version se marca como borrado
                }else{
                    a.setEditado(true);
                    a.setBorrado(true);
                }
                haCambiado.setValue(true);
            }
        }
    }

    public void definirAudio(Uri uri) {
        ocupado.postValue(true);
        servicioArchivos.guardarUri(uri, new ArchivosServicio.CallbackGuardado() {
            @Override
            public void exito(String nombre) {
                    //si no hay audio se crea
                    if (notaAndAudio.getValue().audio==null){
                        AudioEntity a = new AudioEntity();
                        a.setNota_id(idnota);
                        a.setArchivo(nombre);
                        a.setVersion(0);
                        a.setFecha(new Date(System.currentTimeMillis()));
                        notaAndAudio.getValue().audio=a;
                    //si hay audio se modifica
                    }else{
                        AudioEntity a = notaAndAudio.getValue().audio;
                        a.setArchivo(nombre);
                        a.setFecha(new Date(System.currentTimeMillis()));
                        //si la version no es 0 se marca como editado
                        if (a.getVersion()!=0){
                            a.setEditado(true);
                        }
                    }
                    ocupado.postValue(false);
                    haCambiado.setValue(true);
            }

            @Override
            public void fracaso(String msg) {
                mensaje.setValue(msg);
                ocupado.postValue(false);
            }
        });

    }

    public String getRutaAudio() {
        return servicioArchivos.getAudio(notaAndAudio.getValue().audio.getArchivo());
    }

    public boolean existeArchivo() {
        if (notaAndAudio.getValue()==null ||notaAndAudio.getValue().audio==null)
            return false;
        boolean r= servicioArchivos.existeAudio(notaAndAudio.getValue().audio.getArchivo());
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
                        Response<ResponseBody> response = audioApiRepo.descarga(idnota.toString(), loginResponse.body().getBearer(),
                                new AudioApiRepo.IDescargaResponse() {
                                    @Override
                                    public void onResponse(Response<ResponseBody> response) {
                                        if (response.code()==200) {
                                            String header = response.headers().get("Content-Disposition");
                                            String nombre = header.replace("attachment; filename=", "");
                                            //guardar descarga

                                            try {
                                                servicioArchivos.guardarBytes(response.body().byteStream(), nombre);
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
}