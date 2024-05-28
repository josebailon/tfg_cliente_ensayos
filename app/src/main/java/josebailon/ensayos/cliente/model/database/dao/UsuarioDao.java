package josebailon.ensayos.cliente.model.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;


/**
 * Dao Room de la entidad Usuario
 *
 * @author Jose Javier Bailon Ortiz
 */
@Dao
public interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertUsuario(UsuarioEntity usuarioModel);

    @Update
    int updateUsuario(UsuarioEntity usuarioModel);

    @Delete
    void deleteUsuario(UsuarioEntity usuarioModel);

    @Query("SELECT * from usuario ORDER BY email")
    LiveData<List<UsuarioEntity>> getAllUsuarios();
    @Query("SELECT * from usuario ORDER BY email")
    List<UsuarioEntity> getAllUsuariosSinc();

    @Query("SELECT * from usuario where email=:email AND grupo=:grupo ORDER BY email")
    LiveData<UsuarioEntity> getUsuarioByEmailGrupo(String email, UUID grupo);
    @Query("SELECT * from usuario where email=:email AND grupo=:grupo ORDER BY email")
    UsuarioEntity getUsuarioByEmailGrupoSinc(String email, UUID grupo);

    @Query("SELECT * from usuario where grupo=:grupo ORDER BY email")
    LiveData<UsuarioEntity> getUsuarioByGrupo( UUID grupo);
    @Query("SELECT * from usuario where grupo=:grupo ORDER BY email")
    UsuarioEntity getUsuarioByGrupoSinc( UUID grupo);
}
