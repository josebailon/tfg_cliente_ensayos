package josebailon.ensayos.cliente.model.sincronizacion;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.database.repository.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesSincronos;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.UsuarioApiEnt;
import josebailon.ensayos.cliente.model.network.service.APIservice;

public class ComprobadorNuevosRemotos {
    APIservice apIservice;
    private SharedPreferencesRepo sharedPreferencesRepo;

    ISincronizadoFeedbackHandler handler;
    String token;
    SincronizadorService sincronizadorService;
    DatosLocalesSincronos servicioLocal;
    List<GrupoApiEnt> gruposRemotos;
    GrupoAndUsuariosAndCanciones grupoLocal;
    String nombreUsuario;


    public ComprobadorNuevosRemotos(SincronizadorService sincronizadorService, List<GrupoApiEnt> gruposRemotos) {
        this.sincronizadorService = sincronizadorService;
        this.apIservice = sincronizadorService.getApIservice();
        this.handler = sincronizadorService.getHandler();
        this.token = sincronizadorService.getToken();
        this.gruposRemotos = gruposRemotos;
        this.servicioLocal = DatosLocalesSincronos.getInstance(App.getContext());
        this.sharedPreferencesRepo = SharedPreferencesRepo.getInstance();
        this.nombreUsuario = sharedPreferencesRepo.readLogin().getEmail();
    }

    public void iniciar() {
        for (GrupoApiEnt grupoRemoto : gruposRemotos) {
            this.grupoLocal = servicioLocal.getGrupoWithUsuariosAndCanciones(UUID.fromString(grupoRemoto.getId()));
            if (this.grupoLocal == null) {
                GrupoEntity nuevoLocal = MediadorDeEntidades.grupoApiEntToGrupoEntity(grupoRemoto);
                servicioLocal.insertGrupo(nuevoLocal);
                insertarUsuarios(grupoRemoto);
            } else {
                //INSERTAR NUEVAS CANCIONES
            }
        }
    }

    private void insertarUsuarios(GrupoApiEnt grupoRemoto) {
        for (UsuarioApiEnt usuarioRemoto : grupoRemoto.getUsuarios()) {
            if (this.grupoLocal == null) {
                UsuarioEntity nuevoUsuario = MediadorDeEntidades.crearUsuarioEntityParaGrupo(grupoRemoto.getId(), usuarioRemoto.getEmail());
                servicioLocal.insertUsuario(nuevoUsuario);
            }
        }
    }
}
