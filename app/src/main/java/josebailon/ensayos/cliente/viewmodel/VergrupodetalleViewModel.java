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
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.model.network.repository.AuthApiRepo;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.network.repository.UsuarioApiRepo;
import josebailon.ensayos.cliente.model.dto.LoginDto;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesAsincronos;
import retrofit2.Response;

/**
 * ViewModel de vista de detalle de un grupo
 *
 * @author Jose Javier Bailon Ortiz
 */
public class VergrupodetalleViewModel extends ViewModel {

    private SharedPreferencesRepo sharedRepo = SharedPreferencesRepo.getInstance();
    private AuthApiRepo authApiRepo = AuthApiRepo.getInstance();
    private UsuarioApiRepo usuarioApiRepo = UsuarioApiRepo.getInstance();
    private DatosLocalesAsincronos servicio = DatosLocalesAsincronos.getInstance(App.getContext());

    MutableLiveData<String> mensaje = new MutableLiveData<>();

    /**
     * Executor para el manejo de peticiones a la api para comprobacion de usuarios
     */
    private Executor executor = Executors.newSingleThreadExecutor();
    private UUID grupoId;




    public void setGrupoId(UUID grupoId) {
        this.grupoId = grupoId;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    /**
     * Crea una cancion
     * @param nombre
     * @param descripcion
     * @param duracion
     * @param grupo
     */
    public void crearCancion(String nombre, String descripcion, String duracion, UUID grupo) {
        CancionEntity c = new CancionEntity();
        c.setId(UUID.randomUUID());
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setDuracion(duracion);
        c.setGrupo(grupo);
        c.setVersion(0);

        servicio.insertCancion(c);
    }

    public LiveData<GrupoAndUsuariosAndCanciones> getGrupo(UUID idgrupo) {
        return servicio.getGrupoWithUsuariosAndCanciones(idgrupo);
    }


    /**
     * Borra una cancion
     * @param cancion
     */
    public void borrarCancion(CancionEntity cancion) {
        if (cancion.getVersion()==0)
            servicio.deleteCancion(cancion);
        else {
            cancion.setBorrado(true);
            cancion.setEditado(true);
            servicio.borrardoLogicoCancion(cancion);
        }
    }

    /**
     * Actualiza los valores de una cancion
     * @param cancion
     * @param nombre
     * @param descripcion
     * @param duracion
     */
    public void actualizarCancion(CancionEntity cancion, String nombre, String descripcion, String duracion) {
        cancion.setNombre(nombre);
        cancion.setDescripcion(descripcion);
        cancion.setDuracion(duracion);
        cancion.setEditado(true);
        servicio.updateCancion(cancion);
    }


    /**
     * Agrega un usuario a un grupo comprobando si existe
     * @param email
     * @param grupo
     */
    public void agregarUsuario(String email, GrupoEntity grupo) {
        LoginDto l = sharedRepo.readLogin();
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
                                    servicio.insertUsuario(u);
                                    grupo.setEditado(true);
                                    servicio.updateGrupo(grupo);
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

    /**
     * Borra un usuario de un grupo
     * @param usuario
     * @param grupo
     */
    public void borrarUsuario(UsuarioEntity usuario, GrupoEntity grupo) {
        servicio.deleteUsuario(usuario);
        grupo.setEditado(true);
        servicio.updateGrupo(grupo);
    }

    /**
     * Desvincula el usuario local de un grupo
     * @param usuario
     * @param grupo
     */
    public void abandonarGrupo(UsuarioEntity usuario, GrupoEntity grupo) {
        if (grupo.getVersion()==0)
            servicio.deleteGrupo(grupo);
        else {
            grupo.setAbandonado(true);
            servicio.deleteUsuario(usuario);
            grupo.setEditado(true);
            servicio.updateGrupo(grupo);
        }
    }

    public String getUsuario() {
        return sharedRepo.readLogin().getEmail();
    }

    public void lipiarMensaje() {
        this.mensaje.postValue("");
    }
}