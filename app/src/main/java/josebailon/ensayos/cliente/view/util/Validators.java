package josebailon.ensayos.cliente.view.util;

import android.text.TextUtils;

/**
 * Validadores
 *
 * @author Jose Javier Bailon Ortiz
 */
public class Validators {

    /**
     * Email valido
     * @param email El email
     * @return True si es valido
     */
    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validador de password
     * @param password El pasword
     * @return True si es valido
     */
    public static boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password)&&password.length()>3 && password.length()<9;
    }
}
