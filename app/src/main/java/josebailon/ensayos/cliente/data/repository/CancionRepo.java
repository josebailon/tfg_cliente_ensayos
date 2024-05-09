package josebailon.ensayos.cliente.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.data.database.AppDatabase;
import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.data.database.relaciones.GrupoAndUsuariosAndCanciones;

public class CancionRepo {

    private AppDatabase appDatabase;
    private Executor executor = Executors.newSingleThreadExecutor();

    private static CancionRepo instancia;
    private CancionRepo(Context context){
        appDatabase=AppDatabase.getInstance(context);
    }

    public static CancionRepo getInstance(Context context) {
        if (instancia == null) {
            synchronized (SharedPreferencesRepo.class) {
                if (instancia == null) {
                    instancia = new CancionRepo(context);
                }
            }
        }
        return instancia;
    }

    public void insertCancion(CancionEntity cancionEntity){
        executor.execute(() ->{
            appDatabase.cancionDao().insertCancion(cancionEntity);
        } );
    }

    public void updateCancion(CancionEntity cancionEntity){
        executor.execute(() ->{
            appDatabase.cancionDao().updateCancion(cancionEntity);
        } );
    }

    public void deleteCancion(CancionEntity cancionEntity){
        executor.execute(() ->{
            appDatabase.cancionDao().deleteCancion(cancionEntity);
        } );
    }


    public CancionEntity getCancionById(UUID id){
            return appDatabase.cancionDao().getCancionById(id);
    }

    public void borrardoLogico(CancionEntity cancion) {
        cancion.setBorrado(true);
        this.updateCancion(cancion);
    }


    //public LiveData<GrupoAndUsuariosAndCanciones> getGrupoWithUsuariosAndCanciones(UUID id){return appDatabase.grupoDao().getWithUsuariosAndCanciones(id);}

}
