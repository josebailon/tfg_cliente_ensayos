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

import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.relation.CancionAndNotas;

/**
 * Dao Room de la entidad Cancion
 *
 * @author Jose Javier Bailon Ortiz
 */
@Dao
public interface CancionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertCancion(CancionEntity cancionEntity);

    @Update
    int updateCancion(CancionEntity cancionEntity);

    @Delete
    void deleteCancion(CancionEntity cancionEntity);
    @Query("SELECT * from cancion ORDER BY nombre ASC")
    LiveData<List<CancionEntity>> getAllCanciones();
    @Query("SELECT * from cancion ORDER BY nombre ASC")
    List<CancionEntity> getAllCancionesSinc();

    @Query("SELECT * from cancion where id=:id")
    LiveData<CancionEntity> getCancionById(UUID id);
    @Query("SELECT * from cancion where id=:id")
    CancionEntity getCancionByIdSinc(UUID id);

    @Transaction
    @Query("SELECT * FROM cancion WHERE id = :id")
    LiveData<CancionAndNotas> getCancionWithNotas(UUID id);
    @Transaction
    @Query("SELECT * FROM cancion WHERE id = :id")
    CancionAndNotas getCancionWithNotasSinc(UUID id);


    @Transaction
    @Query("SELECT C.id,C.nombre,C.descripcion,C.duracion,C.borrado,C.editado,C.fecha,C.grupo,C.version FROM cancion AS C LEFT JOIN nota AS N WHERE N.cancion = C.id AND N.id=:idnota")
    LiveData<CancionEntity> getCancionByIdNota(UUID idnota);
}
