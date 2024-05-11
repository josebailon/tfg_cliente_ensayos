package josebailon.ensayos.cliente.model.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Entity(tableName = "cancion", foreignKeys = {@ForeignKey(entity = GrupoEntity.class, parentColumns = {"id"}, childColumns = {"grupo"}, onDelete= ForeignKey.CASCADE)})
public class CancionEntity {
    @PrimaryKey(autoGenerate = false)
    @NotNull
    private UUID id;
    private String nombre;
    private String descripcion;

    private String duracion;
    private int version;
    @ColumnInfo(name = "borrado", defaultValue = "0")
    private boolean borrado;
    @ColumnInfo(name = "editado", defaultValue = "0")
    private boolean editado;

    @ColumnInfo(name = "grupo", index = true)
    private UUID grupo;


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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
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

    public UUID getGrupo() {
        return grupo;
    }

    public void setGrupo(UUID grupo) {
        this.grupo = grupo;
    }
}