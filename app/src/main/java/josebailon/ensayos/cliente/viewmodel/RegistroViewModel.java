package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import josebailon.ensayos.cliente.data.network.model.LoginRequest;
import josebailon.ensayos.cliente.data.network.model.LoginResponse;
import josebailon.ensayos.cliente.data.network.model.RegistroResponse;
import josebailon.ensayos.cliente.data.repository.AuthRepo;
import josebailon.ensayos.cliente.data.repository.SharedPreferencesRepo;
import retrofit2.Response;

public class RegistroViewModel extends AndroidViewModel {
    public static final int REGISTROOK =1;
    public static final int REGISTROKO =2;
    public static final int NO_INTERNET =-1;

    private MutableLiveData<Boolean> registrando =new MutableLiveData<>();
    private MutableLiveData<Integer> resultado=new MutableLiveData<>();

    private AuthRepo authRepo;
    private SharedPreferencesRepo sharedRepo;

    public RegistroViewModel(@NonNull Application application) {
        super(application);
        authRepo= AuthRepo.getInstance();
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

    public void registro(String email, String password){
        registrando.setValue(true);
        authRepo.registro(new LoginRequest(email, password), new AuthRepo.IRegistroResponse() {
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
