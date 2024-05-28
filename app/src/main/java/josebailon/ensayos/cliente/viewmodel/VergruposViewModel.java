package josebailon.ensayos.cliente.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesAsincronos;

/**
 * ViewModel de vista de lista de grupos
 *
 * @author Jose Javier Bailon Ortiz
 */
public class VergruposViewModel extends ViewModel {

    private DatosLocalesAsincronos servicio = DatosLocalesAsincronos.getInstance(App.getContext());
    private SharedPreferencesRepo sharedRepo = SharedPreferencesRepo.getInstance();
    private MutableLiveData<String> usuario = new MutableLiveData<>();

    public VergruposViewModel(){
        usuario.postValue(sharedRepo.readLogin().getEmail());
    }

    /**
     * Crear un grupo
     * @param nombre
     * @param descripcion
     */
    public void crear(String nombre, String descripcion) {
        GrupoEntity g = new GrupoEntity();
        g.setId(UUID.randomUUID());
        g.setNombre(nombre);
        g.setDescripcion(descripcion);
        g.setVersion(0);


        UsuarioEntity u = new UsuarioEntity();
        u.setEmail(sharedRepo.readLogin().getEmail());
        u.setGrupo(g.getId());
        servicio.insertGrupoUsuario(g,u);
    }
    public LiveData<List<GrupoEntity>> getGrupos() {
        return servicio.getAllGruposNoBorrados();
    }

    /**
     * Actualizar un grupo
     * @param grupo
     * @param nombre
     * @param descripcion
     */
    public void actualizar(GrupoEntity grupo, String nombre, String descripcion) {
        grupo.setNombre(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setEditado(true);
        servicio.updateGrupo(grupo);
    }

    /**
     * Borrar un grupo
     * @param grupo
     */
    public void borrar(GrupoEntity grupo) {
        grupo.setBorrado(true);
        grupo.setEditado(true);
        if(grupo.getVersion()==0)
            servicio.deleteGrupo(grupo);
        else
            servicio.borrardoLogicoGrupo(grupo);
    }

    public LiveData<String> getUsuario() {
        return usuario;
    }
}