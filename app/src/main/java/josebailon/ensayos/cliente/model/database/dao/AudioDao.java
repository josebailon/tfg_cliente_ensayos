package josebailon.ensayos.cliente.model.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.UUID;

import josebailon.ensayos.cliente.model.database.entity.AudioEntity;


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

    @Query("SELECT * from audio where nota_id=:id")
    AudioEntity getAudioByIdSinc(UUID id);


}
