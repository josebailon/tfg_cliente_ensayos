package josebailon.ensayos.cliente.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.data.database.entity.AudioEntity;
import josebailon.ensayos.cliente.data.database.entity.NotaEntity;
import josebailon.ensayos.cliente.data.database.relaciones.NotaAndAudio;


@Dao
public interface AudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertAudio(AudioEntity audioEntity);

    @Update
    int updateAudio(AudioEntity audioEntity);

    @Delete
    void deleteAudio(AudioEntity audioEntity);

    @Query("SELECT * from audio where nota_id=:id")
    LiveData<AudioEntity> getAudioById(UUID id);



}
