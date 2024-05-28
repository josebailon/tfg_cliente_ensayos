package josebailon.ensayos.cliente.model.network.model.entidades;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Entidad RetroFit para la recepcion de Usuario
 *
 * @author Jose Javier Bailon Ortiz
 */
public class UsuarioApiEnt {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("role")
    @Expose
    private String role;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}