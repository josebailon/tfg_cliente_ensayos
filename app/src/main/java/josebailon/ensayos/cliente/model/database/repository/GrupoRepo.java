package josebailon.ensayos.cliente.model.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.model.database.AppDatabase;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;

public class GrupoRepo {

    private AppDatabase appDatabase;
    private Executor executor = Executors.newSingleThreadExecutor();

    private static GrupoRepo instancia;
    private GrupoRepo(Context context){
        appDatabase=AppDatabase.getInstance(context);
    }

    public static GrupoRepo getInstance(Context context) {
        if (instancia == null) {
            synchronized (GrupoRepo.class) {
                if (instancia == null) {
                    instancia = new GrupoRepo(context);
                }
            }
        }
        return instancia;
    }

    public void insertGrupo(GrupoEntity grupoEntity){
            appDatabase.grupoDao().insertGrupo(grupoEntity);
    }

    public void insertGrupoUsuario(GrupoEntity grupoEntity, UsuarioEntity usuarioEntity){

            appDatabase.grupoDao().insertGrupo(grupoEntity);
            appDatabase.usuarioDao().insertUsuario(usuarioEntity);
    }
    public void updateGrupo(GrupoEntity grupoEntity){
            appDatabase.grupoDao().updateGrupo(grupoEntity);
    }

    public void deleteGrupo(GrupoEntity grupoEntity){
            appDatabase.grupoDao().deleteGrupo(grupoEntity);
    }
    public LiveData<List<GrupoEntity>> getAllGrupos(){
            return appDatabase.grupoDao().getAllGruposNoBorrados();
    }

    public GrupoEntity getGrupoById(UUID id){
            return appDatabase.grupoDao().getGrupoById(id);
    }


    public LiveData<GrupoAndUsuariosAndCanciones> getGrupoWithUsuariosAndCanciones(UUID id){return appDatabase.grupoDao().getGrupoWithUsuariosAndCanciones(id);}






}
