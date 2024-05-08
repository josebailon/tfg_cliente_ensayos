package josebailon.ensayos.cliente.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.data.repository.GrupoRepo;
import josebailon.ensayos.cliente.data.repository.SharedPreferencesRepo;
import josebailon.ensayos.cliente.data.repository.UsuarioRepo;

public class VergruposViewModel extends ViewModel {
    GrupoRepo grupoRepo = GrupoRepo.getInstance(App.getContext());
    UsuarioRepo usuarioRepo = UsuarioRepo.getInstance(App.getContext());
    SharedPreferencesRepo sharedRepo = SharedPreferencesRepo.getInstance();
    public void crear(String nombre, String descripcion) {
        GrupoEntity g = new GrupoEntity();
        g.setId(UUID.randomUUID());
        g.setNombre(nombre);
        g.setDescripcion(descripcion);
        g.setVersion(0);

        grupoRepo.insertGrupo(g);
        UsuarioEntity u = new UsuarioEntity();

        u.setEmail(sharedRepo.readLogin().getEmail());
        u.setGrupo(g.getId());
        usuarioRepo.insertUsuario(u);
    }
    public LiveData<List<GrupoEntity>> getGrupos() {
        return grupoRepo.getAllGrupos();
    }

    public void actualizar(GrupoEntity grupo, String nombre, String descripcion) {
        grupo.setNombre(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setEditado(true);
        grupoRepo.updateGrupo(grupo);
    }

    public void borrar(GrupoEntity grupo) {
        grupo.setBorrado(true);
        grupo.setEditado(true);
        grupoRepo.borrardoLogico(grupo);
    }
}