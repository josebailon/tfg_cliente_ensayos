package josebailon.ensayos.cliente.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(tableName = "nota", foreignKeys = {@ForeignKey(entity = CancionEntity.class, parentColumns = {"id"}, childColumns = {"cancion"}, onDelete= ForeignKey.CASCADE)})
public class NotaEntity {
    @PrimaryKey(autoGenerate = false)
    @NotNull
    private UUID id;
    private String nombre;
    private String texto;

    private int version;
    @ColumnInfo(name = "borrado", defaultValue = "0")
    private boolean borrado;
    @ColumnInfo(name = "destacado", defaultValue = "0")
    private boolean editado;
    @ColumnInfo(name = "destacado", defaultValue = "0")
    private boolean destacado;

    @ColumnInfo(name = "cancion", index = true)
    private UUID cancion;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
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

    public boolean isDestacado() {
        return destacado;
    }

    public void setDestacado(boolean destacado) {
        this.destacado = destacado;
    }

    public UUID getCancion() {
        return cancion;
    }

    public void setCancion(UUID cancion) {
        this.cancion = cancion;
    }
}