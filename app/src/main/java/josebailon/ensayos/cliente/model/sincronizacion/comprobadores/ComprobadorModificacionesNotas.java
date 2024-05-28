package josebailon.ensayos.cliente.model.sincronizacion.comprobadores;

import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.B_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.B_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.EVN_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.EVN_VQ;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.SVN_VQ;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.V0_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.VN_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.X_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.estadoNotas;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.estadoAudios;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.archivos.ArchivosRepo;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesSincronos;
import josebailon.ensayos.cliente.model.network.model.entidades.AudioApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.NotaApiEnt;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import josebailon.ensayos.cliente.model.sharedpreferences.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.sincronizacion.ISincronizadorFeedbackHandler;
import josebailon.ensayos.cliente.model.sincronizacion.MediadorDeEntidades;
import josebailon.ensayos.cliente.model.sincronizacion.SincronizadorService;
import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.CredencialesErroneasException;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.TerminarSincronizacionException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Comprueba si hay modificaciones en los notas y audios y maneja la sincronizacion adecuada
 *
 * @author Jose Javier Bailon Ortiz
 */
public class ComprobadorModificacionesNotas {

    /**
     * Sevicio de acceso a la web API
     */
    APIservice apIservice;
    /**
     * Repositorio de shared preferences
     */
    private SharedPreferencesRepo sharedPreferencesRepo;

    /**
     * Handler que escucha la respuesta de la sincronizacion
     */
    ISincronizadorFeedbackHandler handler;

    /**
     * Token de acceso
     */
    String token;

    /**
     * Servicio de sincronizacion
     */
    SincronizadorService sincronizadorService;

    /**
     * Servicio de acceso sincrono a la base de datos local
     */
    DatosLocalesSincronos servicioLocal;

    /**
     * Repositorio e archivos locales
     */
    ArchivosRepo archivosRepo = ArchivosRepo.getInstance();

    /**
     * UUID de la cancion a la que pertenecen las notas
     */
    UUID idCancion;

    /**
     * Nombre de usuario local
     */
    String nombreUsuario;


    /**
     * Constructor
     *
     * @param sincronizadorService Servicio de sincronizacion
     */
    public ComprobadorModificacionesNotas(SincronizadorService sincronizadorService) {
        this.sincronizadorService = sincronizadorService;
        this.apIservice = sincronizadorService.getApIservice();
        this.handler = sincronizadorService.getHandler();
        this.token = sincronizadorService.getToken();
        this.servicioLocal = DatosLocalesSincronos.getInstance(App.getContext());
        this.sharedPreferencesRepo = SharedPreferencesRepo.getInstance();
        this.nombreUsuario = sharedPreferencesRepo.readLogin().getEmail();
    }


    /**
     * Itera una lista de notas analizando cada una
     *
     * @param idCancion    La UUID de la cancion a la que pertenecen
     * @param notasRemotas Lista de notas remotas
     * @throws CredencialesErroneasException   Si hay problema de credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException                     Si se ha producido un error
     */
    public void comprobarNotas(UUID idCancion, List<NotaApiEnt> notasRemotas) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        this.idCancion = idCancion;
        List<NotaAndAudio> notasLocales = servicioLocal.getNotasWithAudioByCancionId(this.idCancion);
        if (notasRemotas == null)
            notasRemotas = new ArrayList<>();
        for (NotaAndAudio notaLocal : notasLocales) {
            NotaApiEnt notaRemota = notasRemotas.stream().filter(nota -> UUID.fromString(nota.getId()).equals(notaLocal.nota.getId())).findFirst().orElse(null);
            comprobarNota(notaLocal, notaRemota);
        }
    }

    /**
     * Comprueba las modificaciones de una nota
     *
     * @param notaLocal  La nota local
     * @param notaRemota La nota remota
     * @throws CredencialesErroneasException   Si hay problema de credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException                     Si se ha producido un error
     */
    public void comprobarNota(NotaAndAudio notaLocal, NotaApiEnt notaRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        handler.onSendStatus("Comprobando midificaciones de nota " + notaLocal.nota.getNombre());
        int estadoNota = estadoNotas(notaLocal.nota, notaRemota);

        switch (estadoNota) {
            case V0_X:
                agregarAlServidor(notaLocal.nota);
                agregarAudioAlServidor(notaLocal.audio);
                break;
            case VN_X:
                eliminarLocal(notaLocal.nota);
                break;
            case SVN_VQ:
                actualizarLocal(notaLocal, notaRemota);
                actualizarAudioLocal(notaLocal.audio, notaRemota.getAudio());
                break;
            case EVN_VN:
                //actualizar servidor
                Conflicto<NotaAndAudio, NotaApiEnt> conflicto = actualizarServidorConDatosLocales(notaLocal, notaRemota);
                //si hay conflicto resolverlo
                if (conflicto != null) {
                    resolverConflicto(conflicto);
                }
                break;
            case EVN_VQ:
                Conflicto conflictoDirecto = new Conflicto<NotaAndAudio, NotaApiEnt>(Conflicto.T_NOTA, notaLocal, notaRemota);
                resolverConflicto(conflictoDirecto);
                break;
            case B_VN:
                eliminarLocal(notaLocal.nota);
                eliminarDeServidor(notaLocal.nota);
                break;
            case B_X:
                eliminarLocal(notaLocal.nota);
                break;


        }

    }

    /**
     * Actualiza el servidor con los datos locales de una cancion. Si se actualiza con exito lanza la actualizacion del audio asociado
     * @param notaLocal Nota local
     * @param notaRemota Nota remota
     * @return Conflicto si hay respuesta 409 o null si no
     * @throws CredencialesErroneasException Si hay problema de credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException Si se ha producido un error
     */
    private Conflicto<NotaAndAudio, NotaApiEnt> actualizarServidorConDatosLocales(NotaAndAudio notaLocal, NotaApiEnt notaRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {

        //actualizar nota
        Response<NotaApiEnt> lr = null;
        try {
            lr = apIservice.updateNota(notaLocal.nota, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateNota(MediadorDeEntidades.notaApiEntToNotaEntity(notaLocal.nota.getCancion().toString(), lr.body()));
                    //ver si hay que acutalizar audio y devolver su resultado que puede ser un conflicto o nulo
                    return actualizarServidorConAudioLocal(notaLocal, notaRemota);
                case 409:
                    //si hay conflicto devolverlo
                    NotaApiEnt remotoModificado = new GsonBuilder().create().fromJson(lr.errorBody().string(), NotaApiEnt.class);
                    return new Conflicto<NotaAndAudio, NotaApiEnt>(Conflicto.T_NOTA, notaLocal, remotoModificado);
                case 401:
                    throw new CredencialesErroneasException("");
                default:
                    handler.onSendMessage("" + lr.code());
            }
        } catch (IOException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
        //SIN CONFLICTO
        return null;
    }


    /**
     * Analiza un audio y lo elimina o actualiza del servidor segun corresponda
     * @param notaLocal La nota local
     * @param notaRemota La nota remota
     * @return Conflicto si hay que actualizar y se genera conflicto. Null en otro caso
     * @throws CredencialesErroneasException Si hay problema de credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     * @throws IOException Si se ha producido un error
     */
    private Conflicto<NotaAndAudio, NotaApiEnt> actualizarServidorConAudioLocal(NotaAndAudio notaLocal, NotaApiEnt notaRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {

        String fileLocal = (notaLocal.audio != null) ? notaLocal.audio.getArchivo() : "";
        String fileRemoto = (notaRemota.getAudio() != null) ? notaRemota.getAudio().getNombreArchivo() : "";

        if (TextUtils.isEmpty(fileLocal))
            return null;

        int estadoAudio = estadoAudios(notaLocal.audio, (notaRemota != null) ? notaRemota.getAudio() : null);
        switch (estadoAudio) {
            case B_VN:
            case X_VN:
                eliminarAudioDelServidor(notaLocal.audio);
                servicioLocal.deleteAudio(notaLocal.audio);
                break;
            case VN_X:
                if (TextUtils.isEmpty(fileLocal))
                    return null;
            case V0_X:
                agregarAudioAlServidor(notaLocal.audio);
            case EVN_VN:
            case SVN_VQ:
            case EVN_VQ:
                if (!fileLocal.equals(fileRemoto) && notaRemota.getAudio() != null)
                    notaLocal.audio.setVersion(notaRemota.getAudio().getVersion());
                return actualizarAudioAlServidor(notaLocal, notaRemota);
        }
        return null;
    }

    /**
     * Actualiza un audio en el servidor
     * @param notaLocal Nota local
     * @param notaRemota Nota remota
     * @return conflicto si la respuesta es 409 o null en otro caso
     * @throws CredencialesErroneasException
     * @throws TerminarSincronizacionException
     */
    private Conflicto<NotaAndAudio, NotaApiEnt> actualizarAudioAlServidor(NotaAndAudio notaLocal, NotaApiEnt notaRemota) throws CredencialesErroneasException, TerminarSincronizacionException {
        if (notaLocal.audio == null)
            return null;
        try {
            if (!archivosRepo.existeAudio(notaLocal.audio.getArchivo()))
                return null;
            File archivo = new File(archivosRepo.getAudio(notaLocal.audio.getArchivo()));
            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("audio/mpeg"), archivo);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("archivo", archivo.getName(), fileRequestBody);


            Response<AudioApiEnt> lr = apIservice.updateAudio(filePart, notaLocal.audio, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateAudio(MediadorDeEntidades.audioApiEntToAudioEntity(lr.body()));
                    archivosRepo.renombrar(notaLocal.audio.getArchivo(), lr.body().getNombreArchivo());
                    break;
                case 409:
                    return new Conflicto<NotaAndAudio, NotaApiEnt>(Conflicto.T_NOTA, notaLocal, notaRemota);
                case 401:
                    throw new CredencialesErroneasException("");
                default:
                    handler.onSendMessage("" + lr.code());
            }
        } catch (IOException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
        return null;
    }

    /**
     * Elimina un audio del servidor
     * @param audio El audio
     * @throws IOException Si ha habido un error
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void eliminarAudioDelServidor(AudioEntity audio) throws IOException, TerminarSincronizacionException {

        try {
            Response<ResponseBody> lr = apIservice.deleteAudio(audio.getNota_id().toString(), token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }

    /**
     * Lanza la resolucion de un conflicto de nota/audio y espera a su resolucion
     * @param conflicto El conflicto
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void resolverConflicto(Conflicto<NotaAndAudio, NotaApiEnt> conflicto) throws TerminarSincronizacionException {

        descargarAudios(conflicto);


        //mandar conflicto
        sincronizadorService.getHandler().onConflicto(conflicto);
        try {
            //esperar resolucion
            conflicto.esperar();
            //recoger solucion
            NotaAndAudio solucion = conflicto.getResuelto();
            //actualizar en local y en remoto con la eleccion de solucion
            servicioLocal.updateNota(solucion.nota);
            if (solucion.audio == null)
                servicioLocal.deleteAudio(servicioLocal.getAudioById(solucion.nota.getId()));
            else
                servicioLocal.updateAudio(solucion.audio);
            comprobarNota(solucion, conflicto.getRemoto());
        } catch (InterruptedException | CredencialesErroneasException |
                 TerminarSincronizacionException | IOException e) {
            throw new TerminarSincronizacionException("Sincronización terminada");
        }
    }

    /**
     * Descarga los audios asociados a un conflicto
     * @param conflicto El conflicto
     */
    private void descargarAudios(Conflicto<NotaAndAudio, NotaApiEnt> conflicto) {
        try {
            //descargar los audios para comparar
            if (conflicto.getLocal().audio != null && !archivosRepo.existeAudio(conflicto.getLocal().audio.getArchivo())) {
                Response<ResponseBody> res = apIservice.descarga(conflicto.getLocal().nota.getId().toString(), token).execute();
                if (res.code() == 200) {
                    String header = res.headers().get("Content-Disposition");
                    String nombre = header.replace("attachment; filename=", "");
                    //guardar descarga

                    try {
                        archivosRepo.guardarBytes(res.body().byteStream(), nombre);
                    } catch (IOException e) {
                    }
                }
            }
            if (conflicto.getRemoto().getAudio() != null && !archivosRepo.existeAudio(conflicto.getRemoto().getAudio().getNombreArchivo())) {
                Response<ResponseBody> res = apIservice.descarga(conflicto.getRemoto().getId(), token).execute();
                if (res.code() == 200) {
                    String header = res.headers().get("Content-Disposition");
                    String nombre = header.replace("attachment; filename=", "");
                    //guardar descarga

                    try {
                        archivosRepo.guardarBytes(res.body().byteStream(), nombre);
                    } catch (IOException e) {
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Elimina una nota del servidor
     * @param notaLocal La nota local
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void eliminarDeServidor(NotaEntity notaLocal) throws TerminarSincronizacionException {

        try {
            Response<ResponseBody> lr = apIservice.deleteNota(notaLocal.getId().toString(), token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }

    /**
     * Actualiza una nota local con los datos remotos
     * @param notaLocal La nota local
     * @param notaRemota La nota remota
     */
    private void actualizarLocal(NotaAndAudio notaLocal, NotaApiEnt notaRemota) {
        NotaEntity nuevaNota = MediadorDeEntidades.notaApiEntToNotaEntity(notaLocal.nota.getCancion().toString(), notaRemota);
        notaLocal.nota.setNombre(nuevaNota.getNombre());
        notaLocal.nota.setTexto(nuevaNota.getTexto());
        notaLocal.nota.setVersion(nuevaNota.getVersion());
        notaLocal.nota.setFecha(nuevaNota.getFecha());
        notaLocal.nota.setBorrado(false);
        notaLocal.nota.setEditado(false);
        servicioLocal.updateNota(notaLocal.nota);
    }

    /**
     * Elimina unanota local
     * @param notaLocal La nota
     */
    private void eliminarLocal(NotaEntity notaLocal) {
        servicioLocal.deleteNota(notaLocal);
    }


    /**
     * Agrega una nota al servidor
     * @param notaLocal La nota local
     * @throws CredencialesErroneasException Si hay problema de credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void agregarAlServidor(NotaEntity notaLocal) throws CredencialesErroneasException, TerminarSincronizacionException {
        try {
            Response<NotaApiEnt> lr = apIservice.insertNota(idCancion.toString(), notaLocal, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateNota(MediadorDeEntidades.notaApiEntToNotaEntity(idCancion.toString(), lr.body()));
                    break;
                case 401:
                    throw new CredencialesErroneasException("");
                default:
                    handler.onSendMessage("" + lr.code());
            }
        } catch (IOException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }

    }

    /**
     * Agrega un audio al servidor
     * @param audioLocal El audio local
     * @throws CredencialesErroneasException Si hay problema de credenciales
     * @throws TerminarSincronizacionException Si hay que terminar la sincronizacion
     */
    private void agregarAudioAlServidor(AudioEntity audioLocal) throws CredencialesErroneasException, TerminarSincronizacionException {
        if (audioLocal == null)
            return;
        try {
            if (!archivosRepo.existeAudio(audioLocal.getArchivo()))
                return;
            File archivo = new File(archivosRepo.getAudio(audioLocal.getArchivo()));
            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("audio/mpeg"), archivo);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("archivo", archivo.getName(), fileRequestBody);


            Response<AudioApiEnt> lr = apIservice.insertAudio(filePart, audioLocal, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateAudio(MediadorDeEntidades.audioApiEntToAudioEntity(lr.body()));
                    archivosRepo.renombrar(audioLocal.getArchivo(), lr.body().getNombreArchivo());
                    break;
                case 401:
                    throw new CredencialesErroneasException("");
                default:
                    handler.onSendMessage("" + lr.code());
            }
        } catch (IOException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }

    }

    /**
     * Actualiza un audio local con los datos remotos bien sea borrandolo o haciendo un update
     * @param audioLocal El audio local
     * @param audioRemoto El audio remoto
     */
    private void actualizarAudioLocal(AudioEntity audioLocal, AudioApiEnt audioRemoto) {
        int estadoAudio = estadoAudios(audioLocal, audioRemoto);
        switch (estadoAudio) {
            case V0_X:
            case VN_X:
            case B_X:
                servicioLocal.deleteAudio(audioLocal);
                break;
            case SVN_VQ:
            case B_VN:
            case EVN_VN:
            case EVN_VQ:
                AudioEntity nuevoLocal = MediadorDeEntidades.audioApiEntToAudioEntity(audioRemoto);
                servicioLocal.updateAudio(nuevoLocal);
                break;
        }
    }
}
