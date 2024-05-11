package josebailon.ensayos.cliente.model.database.service;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;

public interface DatosLocalesServicio {

    public void insertGrupoUsuario(GrupoEntity grupoEntity, UsuarioEntity usuarioEntity);
    public LiveData<List<GrupoEntity>> getAllGrupos();
    public void updateGrupo(GrupoEntity grupo);
    public void deleteGrupo(GrupoEntity grupo);
    public void borrardoLogicoGrupo(GrupoEntity grupo);

    void insertCancion(CancionEntity cancion);

    LiveData<GrupoAndUsuariosAndCanciones> getGrupoWithUsuariosAndCanciones(UUID idgrupo);

    void deleteCancion(CancionEntity cancion);

    void borrardoLogicoCancion(CancionEntity cancion);

    void updateCancion(CancionEntity cancion);

    void insertUsuario(UsuarioEntity u);

    void deleteUsuario(UsuarioEntity usuario);

    LiveData<CancionEntity> getCancionById(UUID idcancion);

    LiveData<List<NotaAndAudio>> getNotasWithAudioByCancionId(UUID idcancion);

    void deleteNota(NotaEntity nota);

    void borrardoLogicoNota(NotaEntity nota);

    void insertAudio(AudioEntity audio);

    void insertNotaWithAudio(NotaEntity nota,AudioEntity audio);

    LiveData<NotaAndAudio> getNotaWithAudioById(UUID idnota);

    void updateNotaWithAudio(NotaEntity nota, AudioEntity audio);
}
