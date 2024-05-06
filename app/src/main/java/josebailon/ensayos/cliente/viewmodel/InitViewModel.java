package josebailon.ensayos.cliente.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.data.network.model.LoginRequest;
import josebailon.ensayos.cliente.data.network.model.LoginResponse;
import josebailon.ensayos.cliente.data.repository.AuthRepo;
import josebailon.ensayos.cliente.data.repository.GrupoRepo;
import josebailon.ensayos.cliente.data.repository.SharedPreferencesRepo;
import josebailon.ensayos.cliente.data.repository.UsuarioRepo;
import josebailon.ensayos.cliente.data.sharedpref.LoginDto;
import retrofit2.Response;

public class InitViewModel extends AndroidViewModel {

    public static final int WORKING =0;
    public static final int NEEDLOGIN =1;

    public static final int LOGINOK =2;
    public static final int NO_INTERNET =-1;

    private AuthRepo authRepo;
    MutableLiveData<Integer> _estado = new MutableLiveData<>();

    public MutableLiveData<List<GrupoEntity>> grupos = new MutableLiveData<>();
    public InitViewModel(@NonNull Application application) {
        super(application);
        authRepo= AuthRepo.getInstance();
        _estado.postValue(WORKING);
    }

    public LiveData<Integer> comprobar(){
         SharedPreferencesRepo repo = SharedPreferencesRepo.getInstance();
        //repo.clear();

        LoginDto l = repo.readLogin();
        Log.i("JJBO",l.getEmail());
        authRepo.login(new LoginRequest(l.getEmail(), l.getPassword()), new AuthRepo.ILoginResponse() {
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
