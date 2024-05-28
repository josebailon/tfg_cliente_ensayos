package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.RegistroResponse;
import josebailon.ensayos.cliente.model.network.repository.AuthApiRepo;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import retrofit2.Response;

/**
 * ViewModel de vista registro
 *
 * @author Jose Javier Bailon Ortiz
 */
public class RegistroViewModel extends AndroidViewModel {
    public static final int REGISTROOK =1;
    public static final int REGISTROKO =2;
    public static final int NO_INTERNET =-1;

    /**
     * True si se esta en proceso de registro
     */
    private MutableLiveData<Boolean> registrando =new MutableLiveData<>();

    /**
     * REsultado del intento de registro
     */
    private MutableLiveData<Integer> resultado=new MutableLiveData<>();

    private AuthApiRepo authApiRepo;
    private SharedPreferencesRepo sharedRepo;

    public RegistroViewModel(@NonNull Application application) {
        super(application);
        authApiRepo = AuthApiRepo.getInstance();
        sharedRepo= SharedPreferencesRepo.getInstance();
        registrando.postValue(false);
        resultado.postValue(0);
    }

    public LiveData<Boolean> getRegistrando() {
        return registrando;
    }

    public LiveData<Integer> getResultado() {
        return resultado;
    }


    /**
     * Intentar registro
     * @param email Email a usar
     * @param password Password a usar
     */
    public void registro(String email, String password){
        registrando.setValue(true);
        authApiRepo.registro(new LoginRequest(email, password), new AuthApiRepo.IRegistroResponse() {
            @Override
            public void onResponse(Response<RegistroResponse> registroResponse) {
                registrando.setValue(false);
                switch (registroResponse.code()) {
                    case 200:
                        resultado.postValue(REGISTROOK);
                        sharedRepo.saveLogin(email, password);
                        break;
                    case 409:
                        resultado.postValue(REGISTROKO);
                        break;
                    default:
                        resultado.postValue(NO_INTERNET);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                registrando.setValue(false);
                resultado.postValue(NO_INTERNET);
            }
        });
    }
}
