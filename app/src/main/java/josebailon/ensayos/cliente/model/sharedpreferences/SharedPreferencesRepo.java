package josebailon.ensayos.cliente.model.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.dto.LoginDto;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;


/**
 * Repositorio de acceso a las shared preferences encriptadas
 *
 * @author Jose Javier Bailon Ortiz
 */
public class SharedPreferencesRepo {
    /**
     * Clave de mapa del email
     */
    private final String K_EMAIL="email";

    /**
     * Clave de mapa de Password
     */
    private final String K_PASS="pass";

    /**
     * Objeto de SharedPreferences
     */
    private SharedPreferences sharedPref;

    /**
     * Editor de las SharedPreferences
     */
    private SharedPreferences.Editor editor;

    /**
     * Instancia Singleton
     */
    private static volatile SharedPreferencesRepo instancia = null;

    /**
     * Constructor privado singleton
     * @param contexto
     */
    private SharedPreferencesRepo(Context contexto) {
        MasterKey masterKey = null;
        try {
            masterKey = new MasterKey.Builder(contexto)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        this.sharedPref = EncryptedSharedPreferences.create(
                contexto,
                "sharedprefCRIP",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        this.editor = sharedPref.edit();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Devuelve la instancia singleton
     * @return La instancia
     */
    public static SharedPreferencesRepo getInstance() {
        if (instancia == null) {
            synchronized (SharedPreferencesRepo.class) {
                if (instancia == null) {
                    instancia = new SharedPreferencesRepo(App.getContext());
                }
            }
        }
        return instancia;
    }


    /**
     * Graba los atos de login
     * @param email El email
     * @param pass El password
     */
    public void saveLogin(String email, String pass) {
        this.save(this.K_EMAIL,email);
        this.save(this.K_PASS,pass);
    }

    /**
     * Lee el login
     * @return Los datos de login
     */
    public LoginDto readLogin() {
        String email = this.read(this.K_EMAIL,"");
        String pass = this.read(this.K_PASS, "");
        return new LoginDto(email,pass);
    }


    /**
     * Leer todo
     * @return
     */
    public Map<String, ?> readAll() {
        return sharedPref.getAll();
    }

    /**
     * Guardar String
     * @param clave La clave
     * @param valor El valor
     */
    public void save(String clave, String valor) {
        editor.putString(clave,valor);
        editor.apply();
    }

    /**
     * Leer string
     * @param clave
     * @param defaultValue
     * @return
     */
    public String read(String clave, String defaultValue) {
        return sharedPref.getString(clave, defaultValue);
    }

    /**
     * Guardar int
     * @param clave
     * @param valor
     */
    public void save(String clave, int valor) {
        editor.putInt(clave,valor);
        editor.apply();
    }

    /**
     * Leer int
     * @param clave
     * @param defaultValue
     * @return
     */
    public int read(String clave, int defaultValue) {
        return sharedPref.getInt(clave, defaultValue);
    }

    /**
     * Guardar Long
     * @param clave
     * @param valor
     */
    public void save(String clave, Long valor) {
        editor.putLong(clave,valor);
        editor.apply();
    }

    /**
     * Leer Long
     * @param clave
     * @param defaultValue
     * @return
     */
    public Long read(String clave, Long defaultValue) {
        return sharedPref.getLong(clave, defaultValue);
    }

    /**
     * Limpiar shared preferences
     */
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
