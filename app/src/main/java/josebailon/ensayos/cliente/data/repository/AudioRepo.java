package josebailon.ensayos.cliente.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.data.database.AppDatabase;
import josebailon.ensayos.cliente.data.database.entity.AudioEntity;
import josebailon.ensayos.cliente.data.database.entity.NotaEntity;
import josebailon.ensayos.cliente.data.database.relaciones.NotaAndAudio;

public class AudioRepo {

    private AppDatabase appDatabase;
    private Executor executor = Executors.newSingleThreadExecutor();

    private static AudioRepo instancia;
    private AudioRepo(Context context){
        appDatabase=AppDatabase.getInstance(context);
    }

    public static AudioRepo getInstance(Context context) {
        if (instancia == null) {
            synchronized (AudioRepo.class) {
                if (instancia == null) {
                    instancia = new AudioRepo(context);
                }
            }
        }
        return instancia;
    }

    public void insertAudio(AudioEntity audio){
        executor.execute(() ->{
            appDatabase.audioDao().insertAudio(audio);
        } );
    }

    public void updateAudio(AudioEntity audio){
        executor.execute(() ->{
            appDatabase.audioDao().updateAudio(audio);
        } );
    }

    public void deleteAudio(AudioEntity audio){
        executor.execute(() ->{
            appDatabase.audioDao().deleteAudio(audio);
        } );
    }


    public LiveData<AudioEntity> getAudioById(UUID id){
        return appDatabase.audioDao().getAudioById(id);
    }

    public void borrardoLogico(AudioEntity audio) {
        audio.setBorrado(true);
        this.updateAudio(audio);
    }



}
