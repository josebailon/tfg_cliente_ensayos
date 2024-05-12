package josebailon.ensayos.cliente.model.database.service.impl;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.database.repository.AudioRepo;
import josebailon.ensayos.cliente.model.database.repository.CancionRepo;
import josebailon.ensayos.cliente.model.database.repository.GrupoRepo;
import josebailon.ensayos.cliente.model.database.repository.NotaRepo;
import josebailon.ensayos.cliente.model.database.repository.UsuarioRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesServicio;

public class DatosLocalesAsincronos implements DatosLocalesServicio {
    private Executor executor = Executors.newSingleThreadExecutor();
    private GrupoRepo grupoRepo = GrupoRepo.getInstance(App.getContext());
    private UsuarioRepo usuarioRepo = UsuarioRepo.getInstance(App.getContext());
    private CancionRepo cancionRepo = CancionRepo.getInstance(App.getContext());
    private NotaRepo notaRepo = NotaRepo.getInstance(App.getContext());
    private AudioRepo audioRepo = AudioRepo.getInstance(App.getContext());


    @Override
    public void insertGrupoUsuario(GrupoEntity grupo, UsuarioEntity usuario) {
        executor.execute(() ->{
            grupoRepo.insertGrupo(grupo);
            usuarioRepo.insertUsuario(usuario);
        });
    }

    @Override
    public LiveData<List<GrupoEntity>> getAllGrupos() {
             return grupoRepo.getAllGrupos();
    }

    @Override
    public void updateGrupo(GrupoEntity grupo) {
        executor.execute(() -> {
            grupoRepo.updateGrupo(grupo);
        });
    }

    @Override
    public void deleteGrupo(GrupoEntity grupo) {
        executor.execute(() -> {
            grupoRepo.deleteGrupo(grupo);
        });
    }

    public void borrardoLogicoGrupo(GrupoEntity grupo) {
        grupo.setBorrado(true);
        grupoRepo.updateGrupo(grupo);
    }

    @Override
    public void insertCancion(CancionEntity cancion) {
        executor.execute(() ->{
            cancionRepo.insertCancion(cancion);
        });
    }

    @Override
    public LiveData<GrupoAndUsuariosAndCanciones> getGrupoWithUsuariosAndCanciones(UUID idgrupo) {
        return grupoRepo.getGrupoWithUsuariosAndCanciones(idgrupo);
    }

    @Override
    public void deleteCancion(CancionEntity cancion) {
        executor.execute(() ->{
            cancionRepo.deleteCancion(cancion);
        });
    }

    @Override
    public void borrardoLogicoCancion(CancionEntity cancion) {
        executor.execute(() ->{
            cancionRepo.borrardoLogico(cancion);
        });
    }

    @Override
    public void updateCancion(CancionEntity cancion) {
        executor.execute(() ->{
            cancionRepo.updateCancion(cancion);
        });
    }

    @Override
    public void insertUsuario(UsuarioEntity usuario) {
        executor.execute(() ->{
            usuarioRepo.insertUsuario(usuario);
        });
    }

    @Override
    public void deleteUsuario(UsuarioEntity usuario) {
        executor.execute(() ->{
            usuarioRepo.deleteUsuario(usuario);
        });
    }

    @Override
    public LiveData<CancionEntity> getCancionById(UUID idcancion) {
        return cancionRepo.getCancionById(idcancion);
    }

    @Override
    public LiveData<List<NotaAndAudio>> getNotasWithAudioByCancionId(UUID idcancion) {
        return notaRepo.getNotasWithAudioByCancionId(idcancion);
    }

    @Override
    public void deleteNota(NotaEntity nota) {
        executor.execute(() ->{
            notaRepo.deleteNota(nota);
        });
    }

    @Override
    public void borrardoLogicoNota(NotaEntity nota) {
        nota.setBorrado(true);
        notaRepo.updateCancion(nota);
    }

    @Override
    public void insertAudio(AudioEntity audio) {
        executor.execute(() -> {
            audioRepo.insertAudio(audio);
        });
    }

    @Override
    public void insertNotaWithAudio(NotaEntity nota,AudioEntity audio) {
        executor.execute(() -> {
            notaRepo.insertNota(nota);
            if(audio!=null)
                audioRepo.insertAudio(audio);
        });
    }

    @Override
    public LiveData<NotaAndAudio> getNotaWithAudioById(UUID idnota) {
        return notaRepo.getNotaWithAudio(idnota);
    }

    @Override
    public void updateNotaWithAudio(NotaEntity nota, AudioEntity audio) {
        executor.execute(() -> {
            notaRepo.updateCancion(nota);
            //poner audio si existe
            if (audio!=null) {
                //si ya existe el audio se actualiza
                if (audioRepo.getAudioByIdSinc(audio.getNota_id()) != null)
                    audioRepo.updateAudio(audio);
                    //si aun no existe se inserta
                else
                    audioRepo.insertAudio(audio);
            }
        });

    }
}
