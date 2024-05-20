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
import josebailon.ensayos.cliente.model.archivos.service.ArchivosServicio;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesSincronos;
import josebailon.ensayos.cliente.model.network.model.entidades.AudioApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
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

public class ComprobadorModificacionesNotas {
    APIservice apIservice;
    private SharedPreferencesRepo sharedPreferencesRepo;

    ISincronizadorFeedbackHandler handler;
    String token;
    SincronizadorService sincronizadorService;
    DatosLocalesSincronos servicioLocal;
    ArchivosServicio archivosServicio = new ArchivosServicio();
    UUID idCancion;
    String nombreUsuario;


    public ComprobadorModificacionesNotas(SincronizadorService sincronizadorService) {
        this.sincronizadorService = sincronizadorService;
        this.apIservice = sincronizadorService.getApIservice();
        this.handler = sincronizadorService.getHandler();
        this.token = sincronizadorService.getToken();
        this.servicioLocal = DatosLocalesSincronos.getInstance(App.getContext());
        this.sharedPreferencesRepo = SharedPreferencesRepo.getInstance();
        this.nombreUsuario = sharedPreferencesRepo.readLogin().getEmail();
    }



    public void comprobarNotas(UUID idCancion, List<NotaApiEnt> notasRemotas) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        this.idCancion = idCancion;
        List<NotaAndAudio> notasLocales = servicioLocal.getNotasWithAudioByCancionId(this.idCancion);
        if (notasRemotas==null)
            notasRemotas=new ArrayList<>();
        for (NotaAndAudio notaLocal : notasLocales) {
                NotaApiEnt notaRemota = notasRemotas.stream().filter(nota -> UUID.fromString(nota.getId()).equals(notaLocal.nota.getId())).findFirst().orElse(null);
                comprobarNota(notaLocal,notaRemota);
                }
    }

    public void comprobarNota(NotaAndAudio notaLocal, NotaApiEnt notaRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        int estadoNota = estadoNotas(notaLocal.nota,notaRemota);

        switch (estadoNota){
            case V0_X:
                agregarAlServidor(notaLocal.nota);
                agregarAudioAlServidor(notaLocal.audio);
                break;
            case VN_X:
                eliminarLocal(notaLocal.nota);
                break;
            case SVN_VQ:
                actualizarLocal(notaLocal,notaRemota);
                actualizarAudioLocal(notaLocal.audio,notaRemota.getAudio());
                break;
            case EVN_VN:
                //actualizar servidor
                Conflicto<NotaAndAudio, NotaApiEnt> conflicto = actualizarServidorConDatosLocales(notaLocal,notaRemota);
                //si hay conflicto resolverlo
                if (conflicto!=null){
                    resolverConflicto(conflicto);
                }
                break;
            case EVN_VQ:
                    Conflicto conflictoDirecto = new Conflicto<NotaAndAudio, NotaApiEnt>(Conflicto.T_NOTA,notaLocal,notaRemota);
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


    private Conflicto<NotaAndAudio,NotaApiEnt> actualizarServidorConDatosLocales(NotaAndAudio notaLocal, NotaApiEnt notaRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {

        Response<NotaApiEnt> lr=null;

        try {
            lr = apIservice.updateNota(notaLocal.nota, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateNota(MediadorDeEntidades.notaApiEntToNotaEntity(notaLocal.nota.getCancion().toString(),lr.body()));
                    return actualizarServidorConAudioLocal(notaLocal,notaRemota);
                case 409:
                    //si hay conflicto devolverlo
                    NotaApiEnt remotoModificado = new GsonBuilder().create().fromJson(lr.errorBody().string(),NotaApiEnt.class);
                    return new Conflicto<NotaAndAudio, NotaApiEnt>(Conflicto.T_NOTA,notaLocal,remotoModificado);
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

    private Conflicto<NotaAndAudio, NotaApiEnt> actualizarServidorConAudioLocal(NotaAndAudio notaLocal, NotaApiEnt notaRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {

        String fileLocal = (notaLocal.audio!=null)?notaLocal.audio.getArchivo():"";
        String fileRemoto = (notaRemota.getAudio()!=null)?notaRemota.getAudio().getNombreArchivo():"";

        if (TextUtils.isEmpty(fileLocal))
            return null;

        int estadoAudio = estadoAudios(notaLocal.audio,(notaRemota!=null)?notaRemota.getAudio():null);
        switch (estadoAudio){
            case B_VN:
            case X_VN:
                eliminarAudioDelServidor(notaLocal.audio);
                servicioLocal.deleteAudio(notaLocal.audio);
                break;
            case VN_X:
            case V0_X:
                agregarAudioAlServidor(notaLocal.audio);
            case EVN_VN:
            case SVN_VQ:
            case EVN_VQ:
                if(!fileLocal.equals(fileRemoto)&&notaRemota.getAudio()!=null)
                    notaLocal.audio.setVersion(notaRemota.getAudio().getVersion());
                return actualizarAudioAlServidor(notaLocal,notaRemota);
        }
        return null;
    }

    private Conflicto<NotaAndAudio, NotaApiEnt> actualizarAudioAlServidor(NotaAndAudio notaLocal, NotaApiEnt notaRemota) throws CredencialesErroneasException, TerminarSincronizacionException {
        if (notaLocal.audio==null)
            return null;
        try {
            if (!archivosServicio.existeAudio(notaLocal.audio.getArchivo()))
                return null;
            File archivo = new File(archivosServicio.getAudio(notaLocal.audio.getArchivo()));
            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("audio/mpeg"), archivo);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("archivo", archivo.getName(), fileRequestBody);


            Response<AudioApiEnt> lr = apIservice.updateAudio(filePart, notaLocal.audio, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateAudio(MediadorDeEntidades.audioApiEntToAudioEntity(lr.body()));
                    archivosServicio.renombrar(notaLocal.audio.getArchivo(),lr.body().getNombreArchivo());
                    break;
                case 409:
                    return new Conflicto<NotaAndAudio, NotaApiEnt>(Conflicto.T_NOTA,notaLocal,notaRemota);
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

    private void eliminarAudioDelServidor(AudioEntity audio) throws IOException, TerminarSincronizacionException {

        try {
            Response<ResponseBody> lr =  apIservice.deleteAudio(audio.getNota_id().toString(),token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }

    private void resolverConflicto(Conflicto<NotaAndAudio, NotaApiEnt> conflicto) throws  TerminarSincronizacionException {
        handler.onSendMessage("HAY CONFLICTO");
//        //mandar conflicto
//        sincronizadorService.getHandler().onConflicto(conflicto);
//        try {
//            //esperar resolucion
//            conflicto.esperar();
//            //recoger solucion
//            CancionEntity solucion =conflicto.getResuelto();
//            //actualizar en local y en remoto con la eleccion de solucion
//            servicioLocal.updateCancion(solucion);
//            comprobarNota(solucion,conflicto.getRemoto());
//        } catch (InterruptedException | CredencialesErroneasException |
//                 TerminarSincronizacionException | IOException e) {
//            throw new TerminarSincronizacionException("Sincronización terminada");
//        }
    }



    private void eliminarDeServidor(NotaEntity notaLocal) throws TerminarSincronizacionException {

        try {
            Response<ResponseBody> lr = apIservice.deleteNota(notaLocal.getId().toString(),token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }

    private void actualizarLocal(NotaAndAudio notaLocal, NotaApiEnt notaRemota) {
        NotaEntity nuevaNota = MediadorDeEntidades.notaApiEntToNotaEntity(notaLocal.nota.getCancion().toString(),notaRemota);
        notaLocal.nota.setNombre(nuevaNota.getNombre());
        notaLocal.nota.setTexto(nuevaNota.getTexto());
        notaLocal.nota.setVersion(nuevaNota.getVersion());
        notaLocal.nota.setFecha(nuevaNota.getFecha());
        notaLocal.nota.setBorrado(false);
        notaLocal.nota.setEditado(false);
        servicioLocal.updateNota(notaLocal.nota);
    }

    private void eliminarLocal(NotaEntity notaLocal) {
        servicioLocal.deleteNota(notaLocal);
    }




        private void agregarAlServidor(NotaEntity notaLocal) throws CredencialesErroneasException, TerminarSincronizacionException {
            try {
                Response<NotaApiEnt> lr = apIservice.insertNota(idCancion.toString(),notaLocal, token).execute();
                switch (lr.code()) {
                    case 200:
                        servicioLocal.updateNota(MediadorDeEntidades.notaApiEntToNotaEntity(idCancion.toString(),lr.body()));
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

    private void agregarAudioAlServidor(AudioEntity audioLocal) throws CredencialesErroneasException, TerminarSincronizacionException {
        if (audioLocal==null)
            return;
        try {
            if (!archivosServicio.existeAudio(audioLocal.getArchivo()))
                return;
            File archivo = new File( archivosServicio.getAudio(audioLocal.getArchivo()));
            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("audio/mpeg"), archivo);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("archivo", archivo.getName(), fileRequestBody);


            Response<AudioApiEnt> lr = apIservice.insertAudio(filePart, audioLocal, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateAudio(MediadorDeEntidades.audioApiEntToAudioEntity(lr.body()));
                    archivosServicio.renombrar(audioLocal.getArchivo(),lr.body().getNombreArchivo());
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

    private void actualizarAudioLocal(AudioEntity audioLocal, AudioApiEnt audioRemoto) {
        int estadoAudio = estadoAudios(audioLocal,audioRemoto);
        switch (estadoAudio){
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
