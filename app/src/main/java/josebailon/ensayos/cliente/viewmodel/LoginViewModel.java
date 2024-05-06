package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import josebailon.ensayos.cliente.data.network.model.LoginRequest;
import josebailon.ensayos.cliente.data.network.model.LoginResponse;
import josebailon.ensayos.cliente.data.repository.AuthRepo;
import josebailon.ensayos.cliente.data.repository.SharedPreferencesRepo;
import josebailon.ensayos.cliente.data.sharedpref.LoginDto;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    public static final int LOGINKO =1;
    public static final int LOGINOK =2;
    public static final int NO_INTERNET =-1;

    private MutableLiveData<Boolean> loging=new MutableLiveData<>();
    private MutableLiveData<Integer> resultado=new MutableLiveData<>();

    private AuthRepo authRepo;
    private SharedPreferencesRepo sharedRepo;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepo= AuthRepo.getInstance();
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

    public void login(String email, String password){
        loging.setValue(true);
        authRepo.login(new LoginRequest(email, password), new AuthRepo.ILoginResponse() {
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
