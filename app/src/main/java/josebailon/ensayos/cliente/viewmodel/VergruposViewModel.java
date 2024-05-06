package josebailon.ensayos.cliente.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.data.repository.GrupoRepo;

public class VergruposViewModel extends ViewModel {
    GrupoRepo grupoRepo = GrupoRepo.getInstance(App.getContext());
    public void crear(String nombre, String descripcion) {
        GrupoEntity g = new GrupoEntity();
        g.setId(UUID.randomUUID());
        g.setNombre(nombre);
        g.setDescripcion(descripcion);
        g.setVersion(0);

        grupoRepo.insertGrupo(g);
    }
    public LiveData<List<GrupoEntity>> getGrupos() {
        return grupoRepo.getAllGrupos();
    }

}