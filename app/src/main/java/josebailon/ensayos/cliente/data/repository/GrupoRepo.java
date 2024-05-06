package josebailon.ensayos.cliente.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.data.database.AppDatabase;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;

public class GrupoRepo {

    private AppDatabase appDatabase;
    private Executor executor = Executors.newSingleThreadExecutor();

    private static GrupoRepo instancia;
    private GrupoRepo(Context context){
        appDatabase=AppDatabase.getInstance(context);
    }

    public static GrupoRepo getInstance(Context context) {
        if (instancia == null) {
            synchronized (SharedPreferencesRepo.class) {
                if (instancia == null) {
                    instancia = new GrupoRepo(context);
                }
            }
        }
        return instancia;
    }

    public void insertGrupo(GrupoEntity grupoEntity){
        executor.execute(() ->{
            appDatabase.grupoDao().insertGrupo(grupoEntity);
        } );
    }

    public void updateGrupo(GrupoEntity grupoEntity){
        executor.execute(() ->{
            appDatabase.grupoDao().updateGrupo(grupoEntity);
        } );
    }

    public void deleteGrupo(GrupoEntity grupoEntity){
        executor.execute(() ->{
            appDatabase.grupoDao().deleteGrupo(grupoEntity);
        } );
    }
    public LiveData<List<GrupoEntity>> getAllGrupos(){
            return appDatabase.grupoDao().getAllGrupos();
    }

    public GrupoEntity getGrupoById(UUID id){
            return appDatabase.grupoDao().getGrupoById(id);
    }

}
