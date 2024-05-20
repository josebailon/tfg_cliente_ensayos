package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.repository.AuthApiRepo;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.dto.LoginDto;
import retrofit2.Response;

public class InitViewModel extends AndroidViewModel {

    public static final int WORKING =0;
    public static final int NEEDLOGIN =1;

    public static final int LOGINOK =2;
    public static final int NO_INTERNET =-1;

    private AuthApiRepo authApiRepo;
    MutableLiveData<Integer> _estado = new MutableLiveData<>();

    public MutableLiveData<List<GrupoEntity>> grupos = new MutableLiveData<>();
    private SharedPreferencesRepo repo = SharedPreferencesRepo.getInstance();
    public InitViewModel(@NonNull Application application) {
        super(application);
        authApiRepo = AuthApiRepo.getInstance();
        _estado.postValue(WORKING);
    }

    /**
     * Devuelve si el usuario esta inicializado
     * @return
     */
    public boolean usuarioInicializado(){
        return !TextUtils.isEmpty(repo.readLogin().getEmail());
    }

    public LiveData<Integer> comprobar(){

        //repo.clear();

        LoginDto l = repo.readLogin();
        authApiRepo.login(new LoginRequest(l.getEmail(), l.getPassword()), new AuthApiRepo.ILoginResponse() {
            @Override
            public void onResponse(Response<LoginResponse> loginResponse) {

                if (loginResponse.code()==200)
                    _estado.postValue(LOGINOK);
                else
                    _estado.postValue(NEEDLOGIN);
            }

            @Override
            public void onFailure(Throwable t) {
                _estado.postValue(NO_INTERNET);
            }
        });
        return _estado;
    }
}
