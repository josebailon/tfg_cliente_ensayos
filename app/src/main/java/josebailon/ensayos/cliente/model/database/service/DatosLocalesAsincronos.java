package josebailon.ensayos.cliente.model.database.service;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.model.database.AppDatabase;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;

public class DatosLocalesAsincronos{
    private Executor executor = Executors.newSingleThreadExecutor();
    private AppDatabase DB;
    private static DatosLocalesAsincronos instancia;
    private DatosLocalesAsincronos(Context context){
        DB=AppDatabase.getInstance(context);
    }

    public static DatosLocalesAsincronos getInstance(Context context) {
        if (instancia == null) {
            synchronized (DatosLocalesAsincronos.class) {
                if (instancia == null) {
                    instancia = new DatosLocalesAsincronos(context);
                }
            }
        }
        return instancia;
    }
    public void insertGrupoUsuario(GrupoEntity grupo, UsuarioEntity usuario) {
        executor.execute(() ->{
            DB.grupoDao().insertGrupo(grupo);
            DB.usuarioDao().insertUsuario(usuario);
        });
    }

    public LiveData<List<GrupoEntity>> getAllGruposNoBorrados() {
        return DB.grupoDao().getAllGruposNoBorrados();

    }

    public void updateGrupo(GrupoEntity grupo) {
        executor.execute(() -> {
            DB.grupoDao().updateGrupo(grupo);
        });
    }

    public void deleteGrupo(GrupoEntity grupo) {
        executor.execute(() -> {
            DB.grupoDao().deleteGrupo(grupo);
        });
    }

    public void borrardoLogicoGrupo(GrupoEntity grupo) {
        executor.execute(() -> {
            grupo.setBorrado(true);
            DB.grupoDao().updateGrupo(grupo);
        });
    }

    public void insertCancion(CancionEntity cancion) {
        executor.execute(() ->{
            DB.cancionDao().insertCancion(cancion);
        });
    }


    public LiveData<GrupoAndUsuariosAndCanciones> getGrupoWithUsuariosAndCanciones(UUID idgrupo) {

            return DB.grupoDao().getGrupoWithUsuariosAndCanciones(idgrupo);
    }


    public void deleteCancion(CancionEntity cancion) {
        executor.execute(() ->{
            DB.cancionDao().deleteCancion(cancion);
        });
    }

    public void borrardoLogicoCancion(CancionEntity cancion) {
        executor.execute(() ->{
            cancion.setBorrado(true);
            DB.cancionDao().updateCancion(cancion);
        });
    }

    public void updateCancion(CancionEntity cancion) {
        executor.execute(() ->{
            DB.cancionDao().updateCancion(cancion);
        });
    }

    public void insertUsuario(UsuarioEntity usuario) {
        executor.execute(() ->{
            DB.usuarioDao().insertUsuario(usuario);
        });
    }

    public void deleteUsuario(UsuarioEntity usuario) {
        executor.execute(() ->{
            DB.usuarioDao().deleteUsuario(usuario);
        });
    }

    public LiveData<CancionEntity> getCancionById(UUID idcancion) {

            return DB.cancionDao().getCancionById(idcancion);
    }

    public LiveData<List<NotaAndAudio>> getNotasWithAudioByCancionId(UUID idcancion) {

            return DB.notaDao().getNotasWithAudioByCancionId(idcancion);
    }

    public void deleteNota(NotaEntity nota) {
        executor.execute(() ->{
            DB.notaDao().deleteNota(nota);
        });
    }

    public void borrardoLogicoNota(NotaEntity nota) {
        executor.execute(() -> {
            nota.setBorrado(true);
            DB.notaDao().updateNota(nota);
        });
    }

    public void insertAudio(AudioEntity audio) {
        executor.execute(() -> {
            DB.audioDao().insertAudio(audio);
        });
    }

    public void insertNotaWithAudio(NotaEntity nota,AudioEntity audio) {
        executor.execute(() -> {
            DB.notaDao().insertNota(nota);
            if(audio!=null)
                DB.audioDao().insertAudio(audio);
        });
    }

    public LiveData<NotaAndAudio> getNotaWithAudioById(UUID idnota) {
        return DB.notaDao().getNotaWithAudio(idnota);
    }

    public void updateNotaWithAudio(NotaEntity nota, AudioEntity audio) {
        executor.execute(() -> {
            DB.notaDao().updateNota(nota);
            //poner audio si existe
            if (audio!=null) {
                //si ya existe el audio se actualiza
                if (DB.audioDao().getAudioById(audio.getNota_id()) != null)
                    DB.audioDao().updateAudio(audio);
                    //si aun no existe se inserta
                else
                    DB.audioDao().insertAudio(audio);
            }else{
                DB.audioDao().deleteAudio(DB.audioDao().getAudioByIdSinc(nota.getId()));
            }
        });
    }
}
