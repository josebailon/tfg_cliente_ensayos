package josebailon.ensayos.cliente.model.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.model.database.AppDatabase;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;

public class NotaRepo {

    private AppDatabase appDatabase;
    private Executor executor = Executors.newSingleThreadExecutor();

    private static NotaRepo instancia;
    private NotaRepo(Context context){
        appDatabase=AppDatabase.getInstance(context);
    }

    public static NotaRepo getInstance(Context context) {
        if (instancia == null) {
            synchronized (NotaRepo.class) {
                if (instancia == null) {
                    instancia = new NotaRepo(context);
                }
            }
        }
        return instancia;
    }

    public void insertNota(NotaEntity nota){
            appDatabase.notaDao().insertNota(nota);
    }

    public void updateCancion(NotaEntity nota){
            appDatabase.notaDao().updateNota(nota);
    }

    public void deleteNota(NotaEntity nota){
            appDatabase.notaDao().deleteNota(nota);
    }


    public LiveData<NotaEntity> getNotaById(UUID id){
        return appDatabase.notaDao().getNotaById(id);
    }



    public LiveData<List<NotaAndAudio>> getNotasWithAudioByCancionId(UUID id){
        return appDatabase.notaDao().getNotasWithAudioByCancionId(id);
    }

    public LiveData<NotaAndAudio> getNotaWithAudio(UUID id){return appDatabase.notaDao().getNotaWithAudio(id);}

}
