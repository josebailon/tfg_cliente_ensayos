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
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.data.database.relaciones.GrupoAndUsuariosAndCanciones;


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

    @Query("SELECT * from cancion where id=:id")
    GrupoEntity getCancionById(UUID id);
//
//    @Transaction
//    @Query("SELECT * FROM grupo WHERE id = :id")
//    LiveData<List<GrupoAndUsuariosAndCanciones>> getWithUsuariosAndCanciones(UUID id);
//
//


}
