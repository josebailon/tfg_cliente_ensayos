package josebailon.ensayos.cliente.model.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Entidad Room Usuario
 *
 * @author Jose Javier Bailon Ortiz
 */
@Entity(tableName = "usuario", primaryKeys = {"email","grupo"}, foreignKeys = {@ForeignKey(entity = GrupoEntity.class, parentColumns = {"id"}, childColumns = {"grupo"}, onDelete= ForeignKey.CASCADE)})
public class UsuarioEntity {

    @NotNull
    private String email;
    @ColumnInfo(name = "grupo", index = true)
    @NotNull
    private UUID grupo;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getGrupo() {
        return grupo;
    }

    public void setGrupo(UUID grupo) {
        this.grupo = grupo;
    }
}
