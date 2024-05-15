package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import josebailon.ensayos.cliente.model.sincronizacion.ISincronizadoFeedbackHandler;
import josebailon.ensayos.cliente.model.sincronizacion.SincronizadorService;
import josebailon.ensayos.cliente.model.sincronizacion.TestHilo;

public class SincronizadoViewModel extends AndroidViewModel implements ISincronizadoFeedbackHandler {


    MutableLiveData<String> mensaje = new MutableLiveData<>();
    MutableLiveData<String> mensajeEstado = new MutableLiveData<>();

    SincronizadorService sincronizadorService;

    MutableLiveData<Boolean> sincronizando =new MutableLiveData<>(false);

    public MutableLiveData<Semaphore> _s = new MutableLiveData<>();
    TestHilo t;

    public SincronizadoViewModel(@NonNull Application application) {
        super(application);

//         t = new TestHilo();
//         _s = t.getSemaforo();
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

    public void iniciar(){
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

    /**
     * Devuelve si el usuario esta inicializado
     * @return
     */
}
