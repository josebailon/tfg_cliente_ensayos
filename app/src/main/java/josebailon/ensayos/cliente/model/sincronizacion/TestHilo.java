package josebailon.ensayos.cliente.model.sincronizacion;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.viewmodel.SincronizadoViewModel;

public class TestHilo extends Thread{


    MutableLiveData<Semaphore> s;


    public MutableLiveData<Semaphore> getSemaforo(){
        s=new MutableLiveData<Semaphore>();
        return s;
    }
    @Override
    public void run() {
        super.run();

        for (int i=0; i<15; i++){
            Log.i("JJBO", ""+i);
            if (i==10){
                Semaphore sem = new Semaphore(0);
                ExecutorService e = Executors.newSingleThreadExecutor();

                   s.postValue(sem);


                try {
                    sem.acquire();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setHandler(Handler handler) {
    }
}
