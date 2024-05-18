package josebailon.ensayos.cliente.model.network.model.entidades;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AudioApiEnt {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nombreArchivo")
    @Expose
    private String nombreArchivo;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("fecha")
    @Expose
    private String fecha;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
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
}