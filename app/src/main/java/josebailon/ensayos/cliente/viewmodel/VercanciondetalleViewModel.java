package josebailon.ensayos.cliente.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesAsincronos;


/**
 * ViewModel de vista detalle de una cancion
 *
 * @author Jose Javier Bailon Ortiz
 */
public class VercanciondetalleViewModel extends ViewModel {

    private DatosLocalesAsincronos servicio = DatosLocalesAsincronos.getInstance(App.getContext());



    MutableLiveData<String> mensaje = new MutableLiveData<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private UUID idcancion;

    public void setIdcancion(UUID idcancion) {
        this.idcancion = idcancion;
    }



    public LiveData<String> getMensaje() {
        return mensaje;
    }



    public LiveData<CancionEntity> getCancion(UUID idcancion) {
        return servicio.getCancionById(idcancion);
    }


    public LiveData<List<NotaAndAudio>> getNotasDeCancion(UUID idcancion) {
        return servicio.getNotasWithAudioByCancionId(idcancion);
    }

    /**
     * Borra una nota
     * @param nota La nota a borrar
     */
    public void borrarNota(NotaEntity nota) {
        if (nota.getVersion()==0)
            servicio.deleteNota(nota);
        else {
            nota.setBorrado(true);
            nota.setEditado(true);
            servicio.borrardoLogicoNota(nota);
        }
    }


    /**
     * Actualiza los valores de una cancion
     * @param cancion
     * @param nombre
     * @param descripcion
     * @param duracion
     */
    public void actualizarCancion(CancionEntity cancion, String nombre, String descripcion, String duracion) {
        cancion.setNombre(nombre);
        cancion.setDescripcion(descripcion);
        cancion.setDuracion(duracion);
        cancion.setEditado(true);
        servicio.updateCancion(cancion);
    }

}