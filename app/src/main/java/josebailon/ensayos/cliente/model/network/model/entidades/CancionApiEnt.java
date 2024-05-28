package josebailon.ensayos.cliente.model.network.model.entidades;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Entidad RetroFit para la recepcion de Cancion
 *
 * @author Jose Javier Bailon Ortiz
 */
public class CancionApiEnt {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("duracion")
    @Expose
    private String duracion;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("fecha")
    @Expose
    private String fecha;
    @SerializedName("notas")
    @Expose
    private List<NotaApiEnt> notas;

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

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
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

    public List<NotaApiEnt> getNotas() {
        return notas;
    }

    public void setNotas(List<NotaApiEnt> notas) {
        this.notas = notas;
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