package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import josebailon.ensayos.cliente.model.archivos.ArchivosRepo;
import josebailon.ensayos.cliente.model.sincronizacion.ISincronizadorFeedbackHandler;
import josebailon.ensayos.cliente.model.sincronizacion.SincronizadorService;
import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;

/**
 * ViewModel de vista de sincronizacion. Se encarga de iniciar la sincronizacion y escuchar
 * los eventos de la misma para trasladarlos a la vista
 *
 * @author Jose Javier Bailon Ortiz
 */
public class SincronizadorViewModel extends AndroidViewModel implements ISincronizadorFeedbackHandler {


    private MutableLiveData<String> mensaje = new MutableLiveData<>();
    private MutableLiveData<String> mensajeEstado = new MutableLiveData<>();

    /**
     * Cuando se produzca un conflicto de sincronizacion el conflicto se establecera en esta variable
     * para que sea resuelto
     * @return
     */
    private MutableLiveData<Conflicto<?,?>> conflicto = new MutableLiveData<>();

    /**
     * Servicio de sincronizacion
     */
    private SincronizadorService sincronizadorService;
    private ArchivosRepo archivosRepo;
    private MutableLiveData<Boolean> sincronizando =new MutableLiveData<>(false);


    public SincronizadorViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public LiveData<String> getMensajeEstado() {
        return mensajeEstado;
    }

    public LiveData<Boolean> getSincronizando() {
        return sincronizando;
    }


    public LiveData<Conflicto<?, ?>> getConflicto() {
        return conflicto;
    }

    /**
     * Iniciar sincronizacion estableciendo el viewModel como handler de escucha de la
     * sincronizacion
     */
    public void iniciar(){
        archivosRepo =ArchivosRepo.getInstance();
        sincronizando.setValue(true);
        sincronizadorService=new SincronizadorService(this);
        sincronizadorService.iniciar();

    }

    @Override
    public void onSendMessage(String msg) {
        mensaje.postValue(msg);
    }

    @Override
    public void onSendStatus(String msg) {
        mensajeEstado.postValue(msg);
    }

    @Override
    public void onIniciado() {
        sincronizando.postValue(true);
    }

    @Override
    public void onFinalizado() {
        sincronizando.postValue(false);
    }

    @Override
    public void onConflicto(Conflicto<?, ?> conflicto) {
        this.conflicto.postValue(conflicto);
    }


    public String getRutaAudio(String archivo) {
        return archivosRepo.getAudio(archivo);
    }

    public boolean existeArchivo(String archivoAudio) {
        return archivosRepo.existeAudio(archivoAudio);
    }
}
