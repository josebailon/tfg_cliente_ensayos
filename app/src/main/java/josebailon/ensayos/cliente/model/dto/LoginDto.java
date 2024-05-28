package josebailon.ensayos.cliente.model.dto;


/**
 * DTO para envio de peticiones login al servicio web
 *
 * @author Jose Javier Bailon Ortiz
 */
public class LoginDto {
    private String email;
    private String password;


    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
