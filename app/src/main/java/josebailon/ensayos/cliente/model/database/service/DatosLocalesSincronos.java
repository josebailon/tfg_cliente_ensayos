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
import josebailon.ensayos.cliente.model.database.relation.CancionAndNotas;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;


/**
 * Servicio de acceso a la base de datos de modo sincrono
 *
 * @author Jose Javier Bailon Ortiz
 */
public class DatosLocalesSincronos {

    /**
     * Referencia al objeto Room de la base de datos
     */
    private AppDatabase DB;

    /**
     * Instancia singleton
     */
    private static DatosLocalesSincronos instancia;

    /**
     * Constructor singleton
     * @param context El contexto a usar
     */
    private DatosLocalesSincronos(Context context) {
        DB = AppDatabase.getInstance(context);
    }

    /**
     * Devuelve la instancia singleton
     * @param context El contexto a usar
     * @return La instancia
     */
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


    /**
     * Inserta un grupo
     * @param grupo El grupo
     */
    public void insertGrupo(GrupoEntity grupo) {
        DB.grupoDao().insertGrupo(grupo);
    }


    /**
     * Inserta un grupo y un usuario
     * @param grupo El grupo
     * @param usuario El usuario
     */
    public void insertGrupoUsuario(GrupoEntity grupo, UsuarioEntity usuario) {
        DB.grupoDao().insertGrupo(grupo);
        DB.usuarioDao().insertUsuario(usuario);
    }

    /**
     * Devuelve todos los grupos
     *
     * @return Lista con todos los grupos
     */
    public List<GrupoEntity> getAllGrupos() {
        return DB.grupoDao().getAllGruposSinc();

    }

    /**
     * Actualiza un grupo
     * @param grupo El grupo
     */
    public void updateGrupo(GrupoEntity grupo) {
        DB.grupoDao().updateGrupo(grupo);
    }

    /**
     * Borra un grupo
     * @param grupo El grupo
     */
    public void deleteGrupo(GrupoEntity grupo) {
        DB.grupoDao().deleteGrupo(grupo);
    }

    /**
     * Borrado logico de un grupo
     * @param grupo El grupo
     */
    public void borrardoLogicoGrupo(GrupoEntity grupo) {
        grupo.setBorrado(true);
        DB.grupoDao().updateGrupo(grupo);
    }


    /**
     * Inserta una cancion
     * @param cancion La cancion
     */
    public void insertCancion(CancionEntity cancion) {
        DB.cancionDao().insertCancion(cancion);
    }

    /**
     * Devuelve una relacion grupoWithUsuariosAndCanciones dada la id del grupo
     * @param idgrupo UUID del grupo
     * @return La relacion
     */
    public GrupoAndUsuariosAndCanciones getGrupoWithUsuariosAndCanciones(UUID idgrupo) {

        return DB.grupoDao().getGrupoWithUsuariosAndCancionesSinc(idgrupo);
    }


    /**
     * Devuelve un listado contodos los grupos, sus usuarios y sus canciones
     * @return El listado de grupos
     */
    public List<GrupoAndUsuariosAndCanciones> getAllGruposWithUsuariosAndCanciones() {
        return DB.grupoDao().getAllGruposWithUsuariosAndCancionesSinc();

    }

    /**
     * Borra un cancion
     * @param cancion La cancion
     */
    public void deleteCancion(CancionEntity cancion) {
        DB.cancionDao().deleteCancion(cancion);
    }

    /**
     * Borrado logico de una cancion
     * @param cancion La cancion
     */
    public void borrardoLogicoCancion(CancionEntity cancion) {
        cancion.setBorrado(true);
        DB.cancionDao().updateCancion(cancion);
    }

    /**
     * Actualiza una cancion
     * @param cancion La cancion
     */
    public void updateCancion(CancionEntity cancion) {
        DB.cancionDao().updateCancion(cancion);
    }

    /**
     * Inserta un usuario
     * @param usuario El usuario
     */
    public void insertUsuario(UsuarioEntity usuario) {
        DB.usuarioDao().insertUsuario(usuario);
    }

    /**
     * Borra un usuario
     * @param usuario El usuario
     */
    public void deleteUsuario(UsuarioEntity usuario) {
        DB.usuarioDao().deleteUsuario(usuario);
    }

    /**
     * Devuelve una cancion dada su id
     * @param idcancion UUID de la cancion
     * @return La cancion
     */
    public CancionEntity getCancionById(UUID idcancion) {

        return DB.cancionDao().getCancionByIdSinc(idcancion);
    }

    /**
     * Devuelve una cancion con sus notas
     * @param idcancion UUID de la cancion
     * @return La relacion
     */
    public CancionAndNotas getCancionWithNotas(UUID idcancion) {
        return DB.cancionDao().getCancionWithNotasSinc(idcancion);
    }

    /**
     * Devuelve todas las notas de una cancion cno sus audios asignados
     * @param idcancion UUID de la cancion
     * @return El listado de notas
     */
    public List<NotaAndAudio> getNotasWithAudioByCancionId(UUID idcancion) {
        return DB.notaDao().getNotasWithAudioByCancionIdSinc(idcancion);
    }

    /**
     * Devuelve una nota y suadio dada la id de la nota
     * @param idnota UUID de la nota
     * @return La relacion
     */
    public NotaAndAudio getNotaWithAudioById(UUID idnota) {
        return DB.notaDao().getNotaWithAudioSinc(idnota);
    }

    /**
     * Borra una nota
     * @param nota La nota
     */
    public void deleteNota(NotaEntity nota) {
        DB.notaDao().deleteNota(nota);
    }

    /**
     * Borrado logico de una nota
     * @param nota La nota
     */
    public void borrardoLogicoNota(NotaEntity nota) {
        nota.setBorrado(true);
        DB.notaDao().updateNota(nota);
    }

    /**
     * Inserta una nota
     * @param nota La nota
     */
    public void insertNota(NotaEntity nota) {
        DB.notaDao().insertNota(nota);
    }

    /**
     * Devuelve un audio dada su id
     * @param idAudio UUID del audio
     * @return El audio
     */
    public AudioEntity getAudioById(UUID idAudio) {
        return DB.audioDao().getAudioByIdSinc(idAudio);
    }

    /**
     * Inserta un audio
     * @param audio El audio
     */
    public void insertAudio(AudioEntity audio) {
        DB.audioDao().insertAudio(audio);
    }

    /**
     * Inserta una nota y un audio
     * @param nota La nota
     * @param audio El audio
     */
    public void insertNotaWithAudio(NotaEntity nota, AudioEntity audio) {
        DB.notaDao().insertNota(nota);
        if (audio != null)
            DB.audioDao().insertAudio(audio);
    }


    /**
     * Actualiza una nota con audio
     * @param nota La nota
     * @param audio El audio
     */
    public void updateNotaWithAudio(NotaEntity nota, AudioEntity audio) {
        DB.notaDao().updateNota(nota);
        //poner audio si existe
        if (audio != null) {
            //si ya existe el audio se actualiza
            if (DB.audioDao().getAudioById(audio.getNota_id()) != null)
                DB.audioDao().updateAudio(audio);
                //si aun no existe se inserta
            else
                DB.audioDao().insertAudio(audio);
        } else {
            DB.audioDao().deleteAudio(DB.audioDao().getAudioByIdSinc(nota.getId()));
        }
    }


    /**
     * Actualiza una nota
     * @param nota La nota
     */
    public void updateNota(NotaEntity nota) {
        DB.notaDao().updateNota(nota);
    }

    /**
     * Actualiza un audio
     * @param audio El audio
     */
    public void updateAudio(AudioEntity audio) {
        DB.audioDao().updateAudio(audio);
    }

    /**
     * Borra un audio
     * @param audioLocal El audio
     */
    public void deleteAudio(AudioEntity audioLocal) {
        DB.audioDao().deleteAudio(audioLocal);
    }

    /**
     * Devuelve un listado con todos los audios
     * @return El listado
     */
    public List<AudioEntity> getAllAudios(){
        return DB.audioDao().getAllAudioSinc();
    }
}


