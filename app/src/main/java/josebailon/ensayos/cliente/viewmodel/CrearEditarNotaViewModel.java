package josebailon.ensayos.cliente.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesServicio;
import josebailon.ensayos.cliente.model.database.service.impl.DatosLocalesAsincronos;

public class CrearEditarNotaViewModel extends ViewModel {

    public final int MODO_CREACION=0;
    public final int MODO_EDICION=1;


    private DatosLocalesServicio servicio = new DatosLocalesAsincronos();

    private int modo=0;

    private boolean haCambiado=false;
    MutableLiveData<String> mensaje = new MutableLiveData<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private UUID idnota;

    private UUID idcancion;

    private LiveData<NotaAndAudio> notaAndAudio;


    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public UUID getIdcancion() {
        return idcancion;
    }

    public void setIdcancion(UUID idcancion) {
        this.idcancion = idcancion;
    }

    public int getModo() {
        return modo;
    }

    public boolean isHaCambiado() {
        return haCambiado;
    }

    public void setHaCambiado(boolean haCambiado) {
        this.haCambiado = haCambiado;
    }

    public LiveData<NotaAndAudio> getNotaAndAudio() {
        return notaAndAudio;
    }

    /**
     * Almacena la id de la nota a editar y en caso de ser nula es que se está en modo de creacion
     * @param id
     */
    public void setIdNota(String id) {
        if (TextUtils.isEmpty(id)){
            modo=MODO_CREACION;
            idnota=UUID.randomUUID();
            NotaAndAudio na = new NotaAndAudio();
            NotaEntity n = new NotaEntity();
            n.setId(idnota);
            n.setVersion(0);
            n.setCancion(idcancion);
            na.nota=n;
            notaAndAudio= new MutableLiveData<>(na);
        }else{
            modo=MODO_EDICION;
            idnota=UUID.fromString(id);
            notaAndAudio= servicio.getNotaWithAudioById(idnota);
        }
    }

    public void guardarNota(){
        if (modo==MODO_CREACION){
            servicio.insertNotaWithAudio(notaAndAudio.getValue().nota, notaAndAudio.getValue().audio);
        }else{
            notaAndAudio.getValue().nota.setEditado(true);
            NotaEntity n = notaAndAudio.getValue().nota;
            AudioEntity a = notaAndAudio.getValue().audio;
            servicio.updateNotaWithAudio(n,a);
        }
    }
}