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

import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.entity.NotaEntity;
import josebailon.ensayos.cliente.data.database.relaciones.CancionAndNotas;
import josebailon.ensayos.cliente.data.database.relaciones.NotaAndAudio;


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

    @Query("SELECT * from nota where id=:id")
    LiveData<NotaEntity> getNotaById(UUID id);

    @Transaction
    @Query("SELECT * FROM nota WHERE id = :id")
    LiveData<NotaAndAudio> getNotaWithAudio(UUID id);
    @Transaction
    @Query("SELECT * FROM nota WHERE cancion = :id ORDER BY nombre")
    LiveData<List<NotaAndAudio>> getNotasWithAudioByCancionId(UUID id);




}
