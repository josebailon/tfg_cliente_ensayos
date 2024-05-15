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

    public void crearNota(String nombre, String descripcion, String duracion, UUID grupo) {
        Log.i("JJBO", "Insertando cancion a "+idcancion.toString());
        UUID n = UUID.randomUUID();
        NotaEntity nota=new NotaEntity();
        nota.setId(n);
        nota.setCancion(idcancion);
        nota.setNombre("Mi nota");
        nota.setTexto("");
        nota.setVersion(0);
        //servicio.insertNotaWithAudio(nota,null);

        AudioEntity audio = new AudioEntity();
        audio.setNota_id(n);
        audio.setVersion(0);
        audio.setArchivo("miarchivo 1");
        servicio.insertNotaWithAudio(nota,audio);
    }

    public LiveData<CancionEntity> getCancion(UUID idcancion) {
        return servicio.getCancionById(idcancion);
    }


    public LiveData<List<NotaAndAudio>> getNotasDeCancion(UUID idcancion) {
        return servicio.getNotasWithAudioByCancionId(idcancion);
    }

    public void borrarNota(NotaEntity nota) {
        if (nota.getVersion()==0)
            servicio.deleteNota(nota);
        else {
            nota.setBorrado(true);
            nota.setEditado(true);
            servicio.borrardoLogicoNota(nota);
        }
    }


}