package josebailon.ensayos.cliente.model.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


/**
 * Entidad Room Audio
 *
 * @author Jose Javier Bailon Ortiz
 */
@Entity(tableName = "audio", foreignKeys = {@ForeignKey(entity = NotaEntity.class, parentColumns = {"id"}, childColumns = {"nota_id"}, onDelete= ForeignKey.CASCADE)})
public class AudioEntity {
    @PrimaryKey(autoGenerate = false)
    @NotNull
    @SerializedName("id")
    private UUID nota_id;
    @SerializedName("nombreArchivo")
    private String archivo;
    private int version;
    private Date fecha;
    @ColumnInfo(name = "borrado", defaultValue = "0")
    private boolean borrado;
    @ColumnInfo(name = "editado", defaultValue = "0")
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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
    public String fechaFormateada(){
        return new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(fecha);
    }

}