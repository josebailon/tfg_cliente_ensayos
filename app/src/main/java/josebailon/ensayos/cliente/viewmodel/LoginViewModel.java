package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.repository.AuthApiRepo;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import retrofit2.Response;


/**
 * ViewModel de vista login
 *
 * @author Jose Javier Bailon Ortiz
 */
public class LoginViewModel extends AndroidViewModel {
    public static final int LOGINKO =1;
    public static final int LOGINOK =2;
    public static final int NO_INTERNET =-1;

    /**
     * True si se esta en proceso de login
     */
    private MutableLiveData<Boolean> loging=new MutableLiveData<>();

    /**
     * Resultado de intento de login
     */
    private MutableLiveData<Integer> resultado=new MutableLiveData<>();

    private AuthApiRepo authApiRepo;
    private SharedPreferencesRepo sharedRepo;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authApiRepo = AuthApiRepo.getInstance();
        sharedRepo= SharedPreferencesRepo.getInstance();
        loging.postValue(false);
        resultado.postValue(0);
    }

    public LiveData<Boolean> getLoging() {
        return loging;
    }

    public LiveData<Integer> getResultado() {
        return resultado;
    }

    /**
     * Efectuar login
     * @param email Email a usar
     * @param password password a usar
     */
    public void login(String email, String password){
        loging.setValue(true);
        authApiRepo.login(new LoginRequest(email, password), new AuthApiRepo.ILoginResponse() {
            @Override
            public void onResponse(Response<LoginResponse> loginResponse) {
                loging.setValue(false);
                if (loginResponse.code()==200) {
                    resultado.postValue(LOGINOK);
                    sharedRepo.saveLogin(email, password);
                }
                else
                    resultado.postValue(LOGINKO);
            }

            @Override
            public void onFailure(Throwable t) {
                loging.setValue(false);
                resultado.postValue(NO_INTERNET);
            }
        });
    }
}
