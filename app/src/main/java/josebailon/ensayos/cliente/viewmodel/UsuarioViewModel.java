package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.data.repository.UsuarioRepo;

public class UsuarioViewModel extends AndroidViewModel {

    private UsuarioRepo usuarioRepository;
    public UsuarioViewModel(@NonNull Application application) {
        super(application);
        usuarioRepository = new UsuarioRepo(application);
    }

    public void insertUsuario(UsuarioEntity usuarioEntity){
        usuarioRepository.insertUsuario(usuarioEntity);
    }
    public void updateUsuario(UsuarioEntity usuarioEntity){
        usuarioRepository.updateUsuario(usuarioEntity);
    }
    public void deleteUsuario(UsuarioEntity usuarioEntity){
        usuarioRepository.deleteUsuario(usuarioEntity);
    }

    public LiveData<List<UsuarioEntity>> getAllUsuarios(){
        return usuarioRepository.getAllUsuarios();
    }
}
