package josebailon.ensayos.cliente.model.sincronizacion.comprobadores;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.CancionAndNotas;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesSincronos;
import josebailon.ensayos.cliente.model.network.model.entidades.AudioApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.NotaApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.UsuarioApiEnt;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import josebailon.ensayos.cliente.model.sincronizacion.ISincronizadorFeedbackHandler;
import josebailon.ensayos.cliente.model.sincronizacion.MediadorDeEntidades;
import josebailon.ensayos.cliente.model.sincronizacion.SincronizadorService;

public class ComprobadorNuevosRemotos {
    APIservice apIservice;
    private SharedPreferencesRepo sharedPreferencesRepo;

    ISincronizadorFeedbackHandler handler;
    String token;
    SincronizadorService sincronizadorService;
    DatosLocalesSincronos servicioLocal;
    List<GrupoApiEnt> gruposRemotos;
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
        insertarGrupos();
    }

    private void insertarGrupos() {
        for (GrupoApiEnt grupoRemoto : gruposRemotos) {
            GrupoAndUsuariosAndCanciones grupoLocal = servicioLocal.getGrupoWithUsuariosAndCanciones(UUID.fromString(grupoRemoto.getId()));
            if (grupoLocal == null) {
                GrupoEntity nuevoLocal = MediadorDeEntidades.grupoApiEntToGrupoEntity(grupoRemoto);
                servicioLocal.insertGrupo(nuevoLocal);
                insertarUsuarios(grupoRemoto);
                insertarCanciones(grupoRemoto,false); //false para que inserte sin comprobar
            }
            insertarCanciones(grupoRemoto,true); //true para que comprueba antes de insertar
        }

    }
    private void insertarUsuarios(GrupoApiEnt grupoRemoto) {
        for (UsuarioApiEnt usuarioRemoto : grupoRemoto.getUsuarios()) {
                UsuarioEntity nuevoUsuario = MediadorDeEntidades.crearUsuarioEntityParaGrupo(grupoRemoto.getId(), usuarioRemoto.getEmail());
                servicioLocal.insertUsuario(nuevoUsuario);
        }
    }

    private void insertarCanciones(GrupoApiEnt grupoRemoto,boolean comprobar) {
        for (CancionApiEnt cancionRemota : grupoRemoto.getCanciones()) {
            boolean existeLaCancionLocal = false;
            if (comprobar) {
                CancionAndNotas cancionLocal = servicioLocal.getCancionWithNotas(UUID.fromString(cancionRemota.getId()));
                existeLaCancionLocal = cancionLocal!=null;
            }
            if (!existeLaCancionLocal) {
                CancionEntity nuevoLocal = MediadorDeEntidades.cancionApiEntToCancionEntity(grupoRemoto.getId(),cancionRemota);
                servicioLocal.insertCancion(nuevoLocal);
            }
            insertarNotas(cancionRemota,existeLaCancionLocal);
        }
    }

    private void insertarNotas(CancionApiEnt cancionRemota, boolean comprobar) {
        for (NotaApiEnt notaRemota : cancionRemota.getNotas()) {
            boolean existeLaNotaLocal = false;
            if (comprobar) {
                NotaAndAudio notaLocal = servicioLocal.getNotaWithAudioById(UUID.fromString(notaRemota.getId()));
                existeLaNotaLocal = notaLocal!=null;
            }
            if (!existeLaNotaLocal) {
                NotaEntity nuevoLocal = MediadorDeEntidades.notaApiEntToNotaEntity(cancionRemota.getId(),notaRemota);
                servicioLocal.insertNota(nuevoLocal);
            }
            insertarAudio(notaRemota,existeLaNotaLocal);
        }
    }

    private void insertarAudio(NotaApiEnt notaRemota, boolean comprobar) {
        AudioApiEnt audioRemoto = notaRemota.getAudio();
        if (audioRemoto==null)
            return;
        boolean existeElAudioLocal = false;
        if (comprobar) {
            AudioEntity audioLocal = servicioLocal.getAudioById(UUID.fromString(audioRemoto.getId()));
            existeElAudioLocal = audioLocal!=null;
        }
        if (!existeElAudioLocal) {
            AudioEntity nuevoLocal = MediadorDeEntidades.audioApiEntToAudioEntity(audioRemoto);
            servicioLocal.insertAudio(nuevoLocal);
        }

    }

}
