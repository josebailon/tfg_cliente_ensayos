package josebailon.ensayos.cliente.model.database.dao;

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

import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;


/**
 * Dao Room de la entidad Nota
 *
 * @author Jose Javier Bailon Ortiz
 */
@Dao
public interface NotaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertNota(NotaEntity notaEntity);

    @Update
    int updateNota(NotaEntity notaEntity);

    @Delete
    void deleteNota(NotaEntity notaEntity);

    @Query("SELECT * from nota ORDER BY nombre ASC")
    LiveData<List<NotaEntity>> getAllNotas();
    @Query("SELECT * from nota ORDER BY nombre ASC")
    LiveData<List<NotaEntity>> getAllNotasSinc();

    @Query("SELECT * from nota where id=:id")
    LiveData<NotaEntity> getNotaById(UUID id);
    @Query("SELECT * from nota where id=:id")
    NotaEntity getNotaByIdSinc(UUID id);

    @Transaction
    @Query("SELECT * FROM nota WHERE id = :id")
    LiveData<NotaAndAudio> getNotaWithAudio(UUID id);
    @Transaction
    @Query("SELECT * FROM nota WHERE id = :id")
    NotaAndAudio getNotaWithAudioSinc(UUID id);
    @Transaction
    @Query("SELECT * FROM nota WHERE cancion = :id ORDER BY nombre")
    LiveData<List<NotaAndAudio>> getNotasWithAudioByCancionId(UUID id);
    @Transaction
    @Query("SELECT * FROM nota WHERE cancion = :id ORDER BY nombre")
    List<NotaAndAudio> getNotasWithAudioByCancionIdSinc(UUID id);




}
