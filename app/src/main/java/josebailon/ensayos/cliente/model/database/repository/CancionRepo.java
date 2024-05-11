package josebailon.ensayos.cliente.model.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.model.database.AppDatabase;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;

public class CancionRepo {

    private AppDatabase appDatabase;
    private Executor executor = Executors.newSingleThreadExecutor();

    private static CancionRepo instancia;
    private CancionRepo(Context context){
        appDatabase=AppDatabase.getInstance(context);
    }

    public static CancionRepo getInstance(Context context) {
        if (instancia == null) {
            synchronized (CancionRepo.class) {
                if (instancia == null) {
                    instancia = new CancionRepo(context);
                }
            }
        }
        return instancia;
    }

    public void insertCancion(CancionEntity cancionEntity){
            appDatabase.cancionDao().insertCancion(cancionEntity);
    }

    public void updateCancion(CancionEntity cancionEntity){
            appDatabase.cancionDao().updateCancion(cancionEntity);
    }

    public void deleteCancion(CancionEntity cancionEntity){
            appDatabase.cancionDao().deleteCancion(cancionEntity);
    }


    public LiveData<CancionEntity> getCancionById(UUID id){
        return appDatabase.cancionDao().getCancionById(id);
    }

    public void borrardoLogico(CancionEntity cancion) {
        cancion.setBorrado(true);
        this.updateCancion(cancion);
    }



}
