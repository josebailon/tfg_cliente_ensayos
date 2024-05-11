package josebailon.ensayos.cliente.model.network.model;

import com.google.gson.annotations.SerializedName;

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
