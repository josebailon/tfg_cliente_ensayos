package josebailon.ensayos.cliente.model.network.model.entidades;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotaApiEnt {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("texto")
    @Expose
    private String texto;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("audio")
    @Expose
    private AudioApiEnt audio;

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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public AudioApiEnt getAudio() {
        return audio;
    }

    public void setAudio(AudioApiEnt audio) {
        this.audio = audio;
    }

}