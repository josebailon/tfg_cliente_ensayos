package josebailon.ensayos.cliente.model.network.model.entidades;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public String fechaFormateada(){
        //Formato inicial.
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = formato.parse(fecha);
        } catch (ParseException e) {
            return fecha;
        }

        //Aplica formato requerido.
        formato.applyPattern("dd-MM-yyyy HH:mm:ss");
        return formato.format(d);
    }
}