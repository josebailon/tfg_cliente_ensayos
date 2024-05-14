package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import josebailon.ensayos.cliente.model.sincronizacion.TestHilo;

public class SincronizadoViewModel extends AndroidViewModel {


    MutableLiveData<Integer> _estado = new MutableLiveData<>();

    public MutableLiveData<Semaphore> _s = new MutableLiveData<>();
    TestHilo t;
    public SincronizadoViewModel(@NonNull Application application) {
        super(application);
        _estado.postValue(0);
         t = new TestHilo();
         _s = t.getSemaforo();
    }


    public void iniciar(){
        ExecutorService e = Executors.newSingleThreadExecutor();
        e.execute(t);
    }

    /**
     * Devuelve si el usuario esta inicializado
     * @return
     */
}
