package josebailon.ensayos.cliente.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(tableName = "audio", foreignKeys = {@ForeignKey(entity = NotaEntity.class, parentColumns = {"id"}, childColumns = {"nota_id"}, onDelete= ForeignKey.CASCADE)})
public class AudioEntity {
    @PrimaryKey(autoGenerate = false)
    @NotNull
    private UUID nota_id;
    private String archivo;
    private int version;
    @ColumnInfo(name = "borrado", defaultValue = "0")
    private boolean borrado;
    @ColumnInfo(name = "destacado", defaultValue = "0")
    private boolean editado;

    public UUID getNota_id() {
        return nota_id;
    }

    public void setNota_id(UUID nota_id) {
        this.nota_id = nota_id;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isBorrado() {
        return borrado;
    }

    public void setBorrado(boolean borrado) {
        this.borrado = borrado;
    }

    public boolean isEditado() {
        return editado;
    }

    public void setEditado(boolean editado) {
        this.editado = editado;
    }

//    data class UserAndLibrary(
//            @Embedded val user: User,
//            @Relation(
//                    parentColumn = "userId",
//                    entityColumn = "userOwnerId"
//            )
//            val library: Library
//    )
}