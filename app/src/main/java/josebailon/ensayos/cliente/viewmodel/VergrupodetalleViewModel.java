package josebailon.ensayos.cliente.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.relaciones.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.data.repository.CancionRepo;
import josebailon.ensayos.cliente.data.repository.GrupoRepo;

public class VergrupodetalleViewModel extends ViewModel {
    GrupoRepo grupoRepo = GrupoRepo.getInstance(App.getContext());
    CancionRepo cancionRepo = CancionRepo.getInstance(App.getContext());
    public void crearCancion(String nombre, String descripcion, String duracion, UUID grupo) {
        CancionEntity c = new CancionEntity();
        c.setId(UUID.randomUUID());
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setDuracion(duracion);
        c.setGrupo(grupo);
        c.setVersion(0);

        cancionRepo.insertCancion(c);
    }
    public LiveData<GrupoAndUsuariosAndCanciones> getGrupo(UUID idgrupo) {
        return grupoRepo.getGrupoWithUsuariosAndCanciones(idgrupo);
    }


}