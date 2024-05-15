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

public class DatosLocalesSincronos {
    private AppDatabase DB;
    private static DatosLocalesSincronos instancia;
    private DatosLocalesSincronos(Context context){
        DB=AppDatabase.getInstance(context);
    }

    public static DatosLocalesSincronos getInstance(Context context) {
        if (instancia == null) {
            synchronized (DatosLocalesSincronos.class) {
                if (instancia == null) {
                    instancia = new DatosLocalesSincronos(context);
                }
            }
        }
        return instancia;
    }

    public void insertGrupo(GrupoEntity grupo) {
        DB.grupoDao().insertGrupo(grupo);
    }


    public void insertGrupoUsuario(GrupoEntity grupo, UsuarioEntity usuario) {
        DB.grupoDao().insertGrupo(grupo);
        DB.usuarioDao().insertUsuario(usuario);
    }

    public List<GrupoEntity> getAllGruposNoBorrados() {
        return DB.grupoDao().getAllGruposNoBorradosSinc();

    }

    public void updateGrupo(GrupoEntity grupo) {
            DB.grupoDao().updateGrupo(grupo);
    }

    public void deleteGrupo(GrupoEntity grupo) {
            DB.grupoDao().deleteGrupo(grupo);
    }

    public void borrardoLogicoGrupo(GrupoEntity grupo) {
            grupo.setBorrado(true);
            DB.grupoDao().updateGrupo(grupo);
    }

    public void insertCancion(CancionEntity cancion) {
            DB.cancionDao().insertCancion(cancion);
    }

    public GrupoAndUsuariosAndCanciones getGrupoWithUsuariosAndCanciones(UUID idgrupo) {

            return DB.grupoDao().getGrupoWithUsuariosAndCancionesSinc(idgrupo);
    }

    public void deleteCancion(CancionEntity cancion) {
            DB.cancionDao().deleteCancion(cancion);
    }

    public void borrardoLogicoCancion(CancionEntity cancion) {
            cancion.setBorrado(true);
            DB.cancionDao().updateCancion(cancion);
    }

    public void updateCancion(CancionEntity cancion) {
            DB.cancionDao().updateCancion(cancion);
    }

    public void insertUsuario(UsuarioEntity usuario) {
            DB.usuarioDao().insertUsuario(usuario);
    }

    public void deleteUsuario(UsuarioEntity usuario) {
            DB.usuarioDao().deleteUsuario(usuario);
    }

    public CancionEntity getCancionById(UUID idcancion) {

            return DB.cancionDao().getCancionByIdSinc(idcancion);
    }

    public List<NotaAndAudio> getNotasWithAudioByCancionId(UUID idcancion) {

            return DB.notaDao().getNotasWithAudioByCancionIdSinc(idcancion);
    }

    public void deleteNota(NotaEntity nota) {
            DB.notaDao().deleteNota(nota);
    }

    public void borrardoLogicoNota(NotaEntity nota) {
            nota.setBorrado(true);
            DB.notaDao().updateNota(nota);
    }

    public void insertAudio(AudioEntity audio) {
            DB.audioDao().insertAudio(audio);
    }

    public void insertNotaWithAudio(NotaEntity nota,AudioEntity audio) {
            DB.notaDao().insertNota(nota);
            if(audio!=null)
                DB.audioDao().insertAudio(audio);
    }

    public NotaAndAudio getNotaWithAudioById(UUID idnota) {
        return DB.notaDao().getNotaWithAudioSinc(idnota);
    }

    public void updateNotaWithAudio(NotaEntity nota, AudioEntity audio) {
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
    }
}
