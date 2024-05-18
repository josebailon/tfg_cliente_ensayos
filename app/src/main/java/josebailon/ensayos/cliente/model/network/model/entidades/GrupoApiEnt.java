package josebailon.ensayos.cliente.model.network.model.entidades;

import java.util.List;
import java.util.UUID;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;

public class GrupoApiEnt {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("fecha")
    @Expose
    private String fecha;
    @SerializedName("usuarios")
    @Expose
    private List<UsuarioApiEnt> usuarios;
    @SerializedName("canciones")
    @Expose
    private List<CancionApiEnt> canciones;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<UsuarioApiEnt> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioApiEnt> usuarios) {
        this.usuarios = usuarios;
    }

    public List<CancionApiEnt> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<CancionApiEnt> canciones) {
        this.canciones = canciones;
    }



}