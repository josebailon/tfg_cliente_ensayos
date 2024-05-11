package josebailon.ensayos.cliente.model.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.model.database.AppDatabase;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;

public class UsuarioRepo {

    private AppDatabase appDatabase;
    private Executor executor = Executors.newSingleThreadExecutor();
    private static UsuarioRepo instancia;
    public UsuarioRepo(Context context){
        appDatabase=AppDatabase.getInstance(context);
    }



    public static UsuarioRepo getInstance(Context context) {
        if (instancia == null) {
            synchronized (UsuarioRepo.class) {
                if (instancia == null) {
                    instancia = new UsuarioRepo(context);
                }
            }
        }
        return instancia;
    }

    public void insertUsuario(UsuarioEntity usuarioEntity){
            appDatabase.usuarioDao().insertUsuario(usuarioEntity);
    }

    public void updateUsuario(UsuarioEntity usuarioEntity){
            appDatabase.usuarioDao().updateUsuario(usuarioEntity);
    }

    public void deleteUsuario(UsuarioEntity usuarioEntity){
            appDatabase.usuarioDao().deleteUsuario(usuarioEntity);
    }
    public LiveData<List<UsuarioEntity>> getAllUsuarios(){
            return appDatabase.usuarioDao().getAllUsuarios();
    }

    public LiveData<UsuarioEntity> getUsuarioByEmailGrupo(String email, UUID grupo){
            return appDatabase.usuarioDao().getUsuarioByEmailGrupo(email,grupo);
    }

}
