package josebailon.ensayos.cliente.model.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Entidad RetroFit para la recepcion de la respuesta de registro
 *
 * @author Jose Javier Bailon Ortiz
 */
public class RegistroResponse {
    @SerializedName("accessToken")
    public String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
