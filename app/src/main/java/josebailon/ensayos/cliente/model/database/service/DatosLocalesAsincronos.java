package josebailon.ensayos.cliente.model.database.service;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.sql.Date;
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

/**
 * Servicio de acceso a la base de datos de modo asincrono
 *
 * @author Jose Javier Bailon Ortiz
 */
public class DatosLocalesAsincronos{
    /**
     * Executor encargado de hacer las tareas asincronas
     */
    private Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Objeto Room de la base de datos
     */
    private AppDatabase DB;

    /**
     * Intancia singleton
     */
    private static DatosLocalesAsincronos instancia;

    /**
     * Constructor privado para singleton
     * @param context
     */
    private DatosLocalesAsincronos(Context context){
        DB=AppDatabase.getInstance(context);
    }

    /**
     * Devuelve una instancia singleton
     * @param context El contexto a usar
     * @return La instancia
     */
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

    /**
     * Insertar una relacion GrupoUsuario
     * @param grupo El grupo
     * @param usuario El usuario
     */
    public void insertGrupoUsuario(GrupoEntity grupo, UsuarioEntity usuario) {
        executor.execute(() ->{
            grupo.setFecha(new Date(System.currentTimeMillis()));
            DB.grupoDao().insertGrupo(grupo);
            DB.usuarioDao().insertUsuario(usuario);
        });
    }

    /**
     * Devuelve un livedata con una lista de todos los grupos que no se han borrado
     *
     * @return La lista
     */
    public LiveData<List<GrupoEntity>> getAllGruposNoBorrados() {
        return DB.grupoDao().getAllGruposNoBorrados();

    }

    /**
     * Actualiza un Grupo
     * @param grupo El grupo
     */
    public void updateGrupo(GrupoEntity grupo) {
        executor.execute(() -> {
            grupo.setFecha(new Date(System.currentTimeMillis()));
            DB.grupoDao().updateGrupo(grupo);
        });
    }

    /**
     * Borra un grupo
     * @param grupo El grupo
     */
    public void deleteGrupo(GrupoEntity grupo) {
        executor.execute(() -> {
            DB.grupoDao().deleteGrupo(grupo);
        });
    }

    /**
     * Borrado logico de un grupo
     * @param grupo El grupo
     */
    public void borrardoLogicoGrupo(GrupoEntity grupo) {
        executor.execute(() -> {
            grupo.setBorrado(true);
            DB.grupoDao().updateGrupo(grupo);
        });
    }

    /**
     * Inserta una cancion
     * @param cancion La cancion
     */
    public void insertCancion(CancionEntity cancion) {
        executor.execute(() ->{
            cancion.setFecha(new Date(System.currentTimeMillis()));
            DB.cancionDao().insertCancion(cancion);
        });
    }


    /**
     * Devuelve una relacion grupoWithUsuariosAndCanciones
     * @param idgrupo La id del grupo
     * @return El objeto relacion
     */
    public LiveData<GrupoAndUsuariosAndCanciones> getGrupoWithUsuariosAndCanciones(UUID idgrupo) {

            return DB.grupoDao().getGrupoWithUsuariosAndCanciones(idgrupo);
    }


    /**
     * Borra una cancion
     * @param cancion La cancion
     */
    public void deleteCancion(CancionEntity cancion) {
        executor.execute(() ->{
            DB.cancionDao().deleteCancion(cancion);
        });
    }

    /**
     * Borrado logico de una cancion
     * @param cancion Cancion
     */
    public void borrardoLogicoCancion(CancionEntity cancion) {
        executor.execute(() ->{
            cancion.setBorrado(true);
            DB.cancionDao().updateCancion(cancion);
        });
    }

    /**
     * Actualiza una cancion
     * @param cancion La cancion
     */
    public void updateCancion(CancionEntity cancion) {
        executor.execute(() ->{
            cancion.setFecha(new Date(System.currentTimeMillis()));
            DB.cancionDao().updateCancion(cancion);
        });
    }

    /**
     * Inserta un usuario
     * @param usuario El usuario
     */
    public void insertUsuario(UsuarioEntity usuario) {
        executor.execute(() ->{
            DB.usuarioDao().insertUsuario(usuario);
        });
    }

    /**
     * Borra un usuario
     * @param usuario El usuario
     */
    public void deleteUsuario(UsuarioEntity usuario) {
        executor.execute(() ->{
            DB.usuarioDao().deleteUsuario(usuario);
        });
    }

    /**
     * Devuelve una cancion por su UUID
     * @param idcancion UUID de la cancion
     * @return
     */
    public LiveData<CancionEntity> getCancionById(UUID idcancion) {

            return DB.cancionDao().getCancionById(idcancion);
    }

    /**
     * Devuelve la relacion notaWithAudio segun la id de la cancion
     * @param idcancion UUID de la cancion
     * @return La relacion
     */
    public LiveData<List<NotaAndAudio>> getNotasWithAudioByCancionId(UUID idcancion) {

            return DB.notaDao().getNotasWithAudioByCancionId(idcancion);
    }

    /**
     * Borra una nota
     * @param nota La nota
     */
    public void deleteNota(NotaEntity nota) {
        executor.execute(() ->{
            DB.notaDao().deleteNota(nota);
        });
    }

    /**
     * Borrado logico de una nota
     * @param nota La nota
     */
    public void borrardoLogicoNota(NotaEntity nota) {
        executor.execute(() -> {
            nota.setBorrado(true);
            DB.notaDao().updateNota(nota);
        });
    }

    /**
     * Inserta un audio
     * @param audio El audio
     */
    public void insertAudio(AudioEntity audio) {
        executor.execute(() -> {
            DB.audioDao().insertAudio(audio);
        });
    }

    /**
     * Inserta una nota y un audio
     * @param nota La nota
     * @param audio El audio
     */
    public void insertNotaWithAudio(NotaEntity nota,AudioEntity audio) {
        executor.execute(() -> {
            nota.setFecha(new Date(System.currentTimeMillis()));
            DB.notaDao().insertNota(nota);
            if(audio!=null) {
                audio.setFecha(new Date(System.currentTimeMillis()));
                DB.audioDao().insertAudio(audio);
            }
        });
    }

    /**
     * Devuelve una NotaWithAudio dada la id de la nota
     * @param idnota UUID de la nota
     * @return La relacion
     */
    public LiveData<NotaAndAudio> getNotaWithAudioById(UUID idnota) {
        return DB.notaDao().getNotaWithAudio(idnota);
    }

    /**
     * Actualiza una relacion NotaWithAudio
     * @param nota La nota
     * @param audio El audio
     */
    public void updateNotaWithAudio(NotaEntity nota, AudioEntity audio) {
        executor.execute(() -> {
            nota.setFecha(new Date(System.currentTimeMillis()));
            DB.notaDao().updateNota(nota);
            //poner audio si existe
            if (audio!=null) {
                //si ya existe el audio se actualiza
                if (DB.audioDao().getAudioByIdSinc(audio.getNota_id()) != null)
                    DB.audioDao().updateAudio(audio);
                    //si aun no existe se inserta
                else {
                    audio.setFecha(new Date(System.currentTimeMillis()));
                    DB.audioDao().insertAudio(audio);
                }
            }else{
               AudioEntity a = DB.audioDao().getAudioByIdSinc(nota.getId());
                if (a!=null) {
                    a.setBorrado(true);
                    DB.audioDao().updateAudio(a);
                }
            }
        });
    }

    /**
     * Devuelve una concion dada la ID de una nota
     * @param idnota La UUID de la nota
     * @return La cancion
     */
    public LiveData<CancionEntity> getCancionByIdNota(UUID idnota) {
        return DB.cancionDao().getCancionByIdNota(idnota);
    }
}
