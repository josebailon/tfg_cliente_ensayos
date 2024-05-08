package josebailon.ensayos.cliente.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.data.database.relaciones.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.data.network.model.LoginRequest;
import josebailon.ensayos.cliente.data.network.model.LoginResponse;
import josebailon.ensayos.cliente.data.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.data.repository.AuthApiRepo;
import josebailon.ensayos.cliente.data.repository.CancionRepo;
import josebailon.ensayos.cliente.data.repository.GrupoRepo;
import josebailon.ensayos.cliente.data.repository.SharedPreferencesRepo;
import josebailon.ensayos.cliente.data.repository.UsuarioApiRepo;
import josebailon.ensayos.cliente.data.repository.UsuarioRepo;
import josebailon.ensayos.cliente.data.sharedpref.LoginDto;
import retrofit2.Response;

public class VergrupodetalleViewModel extends ViewModel {
    GrupoRepo grupoRepo = GrupoRepo.getInstance(App.getContext());
    CancionRepo cancionRepo = CancionRepo.getInstance(App.getContext());
    UsuarioRepo usuarioRepo = UsuarioRepo.getInstance(App.getContext());
    SharedPreferencesRepo sharedReqpo = SharedPreferencesRepo.getInstance();
    AuthApiRepo authApiRepo = AuthApiRepo.getInstance();
    UsuarioApiRepo usuarioApiRepo = UsuarioApiRepo.getInstance();

    MutableLiveData<String> mensaje = new MutableLiveData<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private UUID grupoId;

    public void setGrupoId(UUID grupoId) {
        this.grupoId = grupoId;
    }

    public VergrupodetalleViewModel() {
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public void crearCancion(String nombre, String descripcion, String duracion, UUID grupo) {
        CancionEntity c = new CancionEntity();
        c.setId(UUID.randomUUID());
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setDuracion(duracion);
        c.setGrupo(grupo);
        c.setVersion(0);

        cancionRepo.insertCancion(c);
    }

    public LiveData<GrupoAndUsuariosAndCanciones> getGrupo(UUID idgrupo) {
        return grupoRepo.getGrupoWithUsuariosAndCanciones(idgrupo);
    }


    public void borrarCancion(CancionEntity cancion) {
        cancion.setBorrado(true);
        cancion.setEditado(true);
        cancionRepo.borrardoLogico(cancion);
    }

    public void actualizarCancion(CancionEntity cancion, String nombre, String descripcion, String duracion) {
        cancion.setNombre(nombre);
        cancion.setDescripcion(descripcion);
        cancion.setDuracion(duracion);
        cancion.setEditado(true);
        cancionRepo.updateCancion(cancion);
    }


    public void agregarUsuario(String email) {
        LoginDto l = sharedReqpo.readLogin();
        if (TextUtils.isEmpty(l.getEmail())) {
            this.mensaje.postValue("Debe hacer login antes de agregar un usuario");
            return;
        }
        authApiRepo.login(new LoginRequest(l.getEmail(), l.getPassword()), new AuthApiRepo.ILoginResponse() {
            @Override
            public void onResponse(Response<LoginResponse> loginResponse) {

                if (loginResponse.code() == 200) {

                    executor.execute(() -> {
                        //hacer llamada
                        try {
                            Response<UsuarioResponse> response = usuarioApiRepo.existe(email,loginResponse.body().getBearer());
                            switch (response.code()){
                                case 200:
                                    UsuarioEntity u = new UsuarioEntity();
                                    u.setEmail(email);
                                    u.setGrupo(grupoId);
                                    usuarioRepo.insertUsuario(u);
                                    break;
                                case 404:
                                    mensaje.postValue("El usuario no existe");
                                    break;
                                default:
                                    mensaje.postValue("No se ha podido agregar el usuario");
                            }
                        } catch (IOException e) {
                            mensaje.postValue("No se ha podido agregar el usuario");
                            throw new RuntimeException(e);
                        }
                    });
                }
                else
                    mensaje.postValue("No se ha podido agregar el usuario");
            }
            @Override
            public void onFailure(Throwable t) {
                mensaje.postValue("Sin conexión con el servidor");
            }
        });
    }
}