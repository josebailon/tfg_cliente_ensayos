package josebailon.ensayos.cliente.view.util;

import android.text.TextUtils;

public class Validators {
    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password)&&password.length()>3 && password.length()<9;
    }
}
