package josebailon.ensayos.cliente.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.data.database.entity.AudioEntity;
import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.entity.NotaEntity;
import josebailon.ensayos.cliente.data.database.relaciones.NotaAndAudio;
import josebailon.ensayos.cliente.data.repository.AudioRepo;
import josebailon.ensayos.cliente.data.repository.CancionRepo;
import josebailon.ensayos.cliente.data.repository.NotaRepo;
import josebailon.ensayos.cliente.data.repository.SharedPreferencesRepo;

public class VercanciondetalleViewModel extends ViewModel {

    private NotaRepo notaRepo=NotaRepo.getInstance(App.getContext());
    private AudioRepo audioRepo=AudioRepo.getInstance(App.getContext());
    private CancionRepo cancionRepo = CancionRepo.getInstance(App.getContext());
    private SharedPreferencesRepo sharedRepo = SharedPreferencesRepo.getInstance();


    MutableLiveData<String> mensaje = new MutableLiveData<>();
    private Executor executor = Executors.newSingleThreadExecutor();
    private UUID idcancion;

    public void setIdcancion(UUID idcancion) {
        this.idcancion = idcancion;
        UUID n = UUID.randomUUID();
        NotaEntity nota=new NotaEntity();
        nota.setId(n);
        nota.setCancion(idcancion);
        nota.setNombre("Mi nota");
        nota.setTexto("");
        nota.setVersion(0);
        UUID n2 = UUID.randomUUID();
        NotaEntity nota2=new NotaEntity();
        nota2.setId(n2);
        nota2.setCancion(idcancion);
        nota2.setNombre("Mi nota");
        nota2.setTexto("Esto es un texto de la nota\n con dos lineas");
        nota2.setVersion(0);
        UUID n3 = UUID.randomUUID();
        NotaEntity nota3=new NotaEntity();
        nota3.setId(n3);
        nota3.setCancion(idcancion);
        nota3.setNombre("Mi nota");
        nota3.setTexto("");
        nota3.setVersion(0);
        AudioEntity audio = new AudioEntity();
        audio.setNota_id(n3);
        audio.setVersion(0);
        audio.setArchivo("miarchivo 1");

        UUID n4 = UUID.randomUUID();
        NotaEntity nota4=new NotaEntity();
        nota4.setId(n4);
        nota4.setCancion(idcancion);
        nota4.setNombre("Mi nota");
        nota4.setTexto("Esto es un texto");
        nota4.setVersion(0);
        AudioEntity audio2 = new AudioEntity();
        audio.setNota_id(n4);
        audio.setVersion(0);
        audio.setArchivo("miarchivo 3");


        notaRepo.insertNota(nota);
        notaRepo.insertNota(nota2);
        notaRepo.insertNota(nota3);
        notaRepo.insertNota(nota4);

        audioRepo.insertAudio(audio);
        audioRepo.insertAudio(audio2);


    }



    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public void crearNota(String nombre, String descripcion, String duracion, UUID grupo) {
        CancionEntity c = new CancionEntity();
        c.setId(UUID.randomUUID());
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setDuracion(duracion);
        c.setGrupo(grupo);
        c.setVersion(0);

        cancionRepo.insertCancion(c);
    }

    public LiveData<CancionEntity> getCancion(UUID idcancion) {
        return cancionRepo.getCancionById(idcancion);
    }


    public LiveData<List<NotaAndAudio>> getNotasDeCancion(UUID idcancion) {
        return notaRepo.getNotasWithAudioByCancionId(idcancion);
    }

    public void borrarNota(NotaEntity nota) {
        if (nota.getVersion()==0)
            notaRepo.deleteNota(nota);
        else {
            nota.setBorrado(true);
            nota.setEditado(true);
            notaRepo.borrardoLogico(nota);
        }
    }

//    public void actualizarNota(NotaEntity nota, String nombre, String descripcion, String duracion) {
//        nota.setNombre(nombre);
//        nota.setDescripcion(descripcion);
//        nota.setDuracion(duracion);
//        nota.setEditado(true);
//        cancionRepo.updateCancion(nota);
//    }


//    public void agregarUsuario(String email, GrupoEntity grupo) {
//        LoginDto l = sharedRepo.readLogin();
//        if (TextUtils.isEmpty(l.getEmail())) {
//            this.mensaje.postValue("Debe hacer login antes de agregar un usuario");
//            return;
//        }
//        authApiRepo.login(new LoginRequest(l.getEmail(), l.getPassword()), new AuthApiRepo.ILoginResponse() {
//            @Override
//            public void onResponse(Response<LoginResponse> loginResponse) {
//
//                if (loginResponse.code() == 200) {
//
//                    executor.execute(() -> {
//                        //hacer llamada
//                        try {
//                            Response<UsuarioResponse> response = usuarioApiRepo.existe(email,loginResponse.body().getBearer());
//                            switch (response.code()){
//                                case 200:
//                                    UsuarioEntity u = new UsuarioEntity();
//                                    u.setEmail(email);
//                                    u.setGrupo(cancionId);
//                                    usuarioRepo.insertUsuario(u);
//                                    grupo.setEditado(true);
//                                    grupoRepo.updateGrupo(grupo);
//                                    break;
//                                case 404:
//                                    mensaje.postValue("El usuario no existe");
//                                    break;
//                                default:
//                                    mensaje.postValue("No se ha podido agregar el usuario");
//                            }
//                        } catch (IOException e) {
//                            mensaje.postValue("No se ha podido agregar el usuario");
//                            throw new RuntimeException(e);
//                        }
//                    });
//                }
//                else
//                    mensaje.postValue("No se ha podido agregar el usuario");
//            }
//            @Override
//            public void onFailure(Throwable t) {
//                mensaje.postValue("Sin conexión con el servidor");
//            }
//        });
//    }

//    public void borrarUsuario(UsuarioEntity usuario, GrupoEntity grupo) {
//        usuarioRepo.deleteUsuario(usuario);
//        grupo.setEditado(true);
//        grupoRepo.updateGrupo(grupo);
//    }
//
//    public void abandonarGrupo(UsuarioEntity usuario, GrupoEntity grupo) {
//        if (grupo.getVersion()==0)
//            grupoRepo.deleteGrupo(grupo);
//        else
//            grupo.setAbandonado(true);
//        usuarioRepo.deleteUsuario(usuario);
//        grupo.setEditado(true);
//        grupoRepo.updateGrupo(grupo);
//    }
}