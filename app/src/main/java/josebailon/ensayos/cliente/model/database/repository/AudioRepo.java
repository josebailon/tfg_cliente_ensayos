package josebailon.ensayos.cliente.model.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.UUID;

import josebailon.ensayos.cliente.model.database.AppDatabase;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;

public class AudioRepo {

    private AppDatabase appDatabase;


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
            appDatabase.audioDao().insertAudio(audio);
    }

    public void updateAudio(AudioEntity audio){
            appDatabase.audioDao().updateAudio(audio);
    }

    public void deleteAudio(AudioEntity audio){
            appDatabase.audioDao().deleteAudio(audio);
    }


    public LiveData<AudioEntity> getAudioById(UUID id){
        return appDatabase.audioDao().getAudioById(id);
    }
    public AudioEntity getAudioByIdSinc(UUID id){
        return appDatabase.audioDao().getAudioByIdSinc(id);
    }

    public void borrardoLogico(AudioEntity audio) {
        audio.setBorrado(true);
        this.updateAudio(audio);
    }



}
