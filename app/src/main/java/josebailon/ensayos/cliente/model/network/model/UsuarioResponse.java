package josebailon.ensayos.cliente.model.network.model;

import com.google.gson.annotations.SerializedName;

public class UsuarioResponse {
    @SerializedName("id")
    public int id;
    @SerializedName("email")
    public String email;
    @SerializedName("role")
    public String role;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
