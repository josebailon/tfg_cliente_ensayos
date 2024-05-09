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

import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.data.database.relaciones.GrupoAndUsuariosAndCanciones;


@Dao
public interface GrupoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertGrupo(GrupoEntity grupoEntity);

    @Update
    int updateGrupo(GrupoEntity grupoEntity);

    @Delete
    void deleteGrupo(GrupoEntity grupoEntity);

    @Query("SELECT * from grupo ORDER BY nombre ASC")
    List<GrupoEntity> getAllGrupos();
    @Query("SELECT * from grupo WHERE borrado=0 AND abandonado=0 ORDER BY nombre ASC")
    LiveData<List<GrupoEntity>> getAllGruposNoBorrados();

    @Query("SELECT * from grupo where id=:id")
    GrupoEntity getGrupoById(UUID id);

    @Transaction
    @Query("SELECT * FROM grupo WHERE id = :id")
    LiveData<GrupoAndUsuariosAndCanciones> getWithUsuariosAndCanciones(UUID id);




}
