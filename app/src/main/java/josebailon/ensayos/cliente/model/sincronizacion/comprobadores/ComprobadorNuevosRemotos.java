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


/**
 * Comprueba si hay nuevas entidades remotas que haya que crear de manera local
 *
 * @author Jose Javier Bailon Ortiz
 */
public class ComprobadorNuevosRemotos {

    /**
     * Sevicio de acceso a la web API
     */
    private APIservice apIservice;

    /**
     * Repositioro de Shared preferences
     */
    private SharedPreferencesRepo sharedPreferencesRepo;

    /**
     * Handler de escucha a la sincronizacion
     */
    private ISincronizadorFeedbackHandler handler;

    /**
     * Token de acceso
     */
    private String token;

    /**
     * Servicio de sincronizacion
     */
    private SincronizadorService sincronizadorService;

    /**
     * Servicio de acceso sincrono a la base de datos
     */
    private DatosLocalesSincronos servicioLocal;

    /**
     * Grupos remotos
     */
    private List<GrupoApiEnt> gruposRemotos;

    /**
     * Nombre de usuario
     */
    private String nombreUsuario;


    /**
     * Constructor
     * @param sincronizadorService Servicio de sincronizacion
     * @param gruposRemotos Lista de grupos remotos
     */
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

    /**
     * Inicia el proceso de comprobacion de nuevas entidades
     */
    public void iniciar() {
        insertarGrupos();
    }


    /**
     * Inserta los nuevos grupos encontrados en  remoto
     */
    private void insertarGrupos() {
        handler.onSendStatus("Agregando nuevos grupos");
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

    /**
     * Inserta los nuevos usuarios definidos en cada nuevo grupo remoto
     * @param grupoRemoto
     */
    private void insertarUsuarios(GrupoApiEnt grupoRemoto) {
        for (UsuarioApiEnt usuarioRemoto : grupoRemoto.getUsuarios()) {
                UsuarioEntity nuevoUsuario = MediadorDeEntidades.crearUsuarioEntityParaGrupo(grupoRemoto.getId(), usuarioRemoto.getEmail());
                servicioLocal.insertUsuario(nuevoUsuario);
        }
    }

    /**
     * Inserta la nuevas canciones encontradas en remoto
     * @param grupoRemoto El grupo del que analizar las canciones
     * @param comprobar True si debe comprobar si las canciones son nuevas. False para que las inserte directametne
     */
    private void insertarCanciones(GrupoApiEnt grupoRemoto,boolean comprobar) {
        handler.onSendStatus("Agregando nuevas canciones");
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

    /**
     * Inserta la nuevas notas encontradas en remoto
     * @param cancionRemota La cancion de la que analizar las notas
     * @param comprobar True si debe comprobar si las notas son nuevas. False para que las inserte directametne
     */
    private void insertarNotas(CancionApiEnt cancionRemota, boolean comprobar) {
        handler.onSendStatus("Agregando nuevas notas");
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

    /**
     * Inserta la nuevos audios encontrados en remoto
     * @param notaRemota La nota del que analizar las canciones
     * @param comprobar True si debe comprobar si los audios son nuevos. False para que las inserte directametne
     */
    private void insertarAudio(NotaApiEnt notaRemota, boolean comprobar) {
        handler.onSendStatus("Agregando nuevos audios");
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
