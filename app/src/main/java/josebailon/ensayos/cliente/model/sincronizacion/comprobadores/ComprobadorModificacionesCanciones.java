package josebailon.ensayos.cliente.model.sincronizacion.comprobadores;

import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.A_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.A_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.B_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.B_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.EVN_VN;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.EVN_VQ;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.SVN_VQ;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.V0_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.VN_X;
import static josebailon.ensayos.cliente.model.sincronizacion.CalculadoraEstados.estadoCanciones;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.database.repository.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesSincronos;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.UsuarioApiEnt;
import josebailon.ensayos.cliente.model.network.service.APIservice;
import josebailon.ensayos.cliente.model.sincronizacion.ISincronizadorFeedbackHandler;
import josebailon.ensayos.cliente.model.sincronizacion.MediadorDeEntidades;
import josebailon.ensayos.cliente.model.sincronizacion.SincronizadorService;
import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.CredencialesErroneasException;
import josebailon.ensayos.cliente.model.sincronizacion.excepciones.TerminarSincronizacionException;
import retrofit2.Response;

public class ComprobadorModificacionesCanciones {
    APIservice apIservice;
    private SharedPreferencesRepo sharedPreferencesRepo;

    ISincronizadorFeedbackHandler handler;
    String token;
    SincronizadorService sincronizadorService;
    DatosLocalesSincronos servicioLocal;
    UUID idGrupo;
    String nombreUsuario;


    public ComprobadorModificacionesCanciones(SincronizadorService sincronizadorService) {
        this.sincronizadorService = sincronizadorService;
        this.apIservice = sincronizadorService.getApIservice();
        this.handler = sincronizadorService.getHandler();
        this.token = sincronizadorService.getToken();
        this.servicioLocal = DatosLocalesSincronos.getInstance(App.getContext());
        this.sharedPreferencesRepo = SharedPreferencesRepo.getInstance();
        this.nombreUsuario = sharedPreferencesRepo.readLogin().getEmail();
    }



    public void comprobarCanciones(UUID idGrupo, List<CancionApiEnt> cancionesRemotas) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        this.idGrupo=idGrupo;
        List<CancionEntity> cancionesLocales = servicioLocal.getGrupoWithUsuariosAndCanciones(idGrupo).canciones;
        for (CancionEntity cancionLocal : cancionesLocales) {
                CancionApiEnt cancionRemota = cancionesRemotas.stream().filter(cancion -> UUID.fromString(cancion.getId()).equals(cancionLocal.getId())).findFirst().orElse(null);
                comprobarCancion(cancionLocal,cancionRemota);
                }
    }

    public void comprobarCancion(CancionEntity cancionLocal, CancionApiEnt cancionRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {
        int estado = estadoCanciones(cancionLocal,cancionRemota);
        switch (estado){
            case V0_X:
                agregarAlServidor(cancionLocal);
                comprobarNotas(cancionLocal,cancionRemota);
                break;
            case VN_X:
                eliminarLocal(cancionLocal);
                break;
            case SVN_VQ:
                actualizarLocal(cancionLocal,cancionRemota);
                comprobarNotas(cancionLocal,cancionRemota);
                break;
            case EVN_VN:
                //actualizar servidor
                Conflicto<CancionEntity, CancionApiEnt> conflicto = actualizarServidorConDatosLocales(cancionLocal,cancionRemota);
                //si hay conflicto resolverlo
                if (conflicto!=null){
                    resolverConflicto(conflicto);
                }else{
                    //si no hay conflicto comprobar las canciones
                    comprobarNotas(cancionLocal,cancionRemota);
                }
                break;
            case EVN_VQ:
                    Conflicto conflictoDirecto = new Conflicto<CancionEntity, CancionApiEnt>(Conflicto.T_CANCION,cancionLocal,cancionRemota);
                    resolverConflicto(conflictoDirecto);
                break;
            case B_VN:
                eliminarLocal(cancionLocal);
                eliminarDeServidor(cancionLocal);
                break;
            case B_X:
                eliminarLocal(cancionLocal);
                break;
            case A_X:
                eliminarLocal(cancionLocal);
                break;
            case A_VN:
                eliminarLocal(cancionLocal);
                break;

        }

}

    private Conflicto<CancionEntity,CancionApiEnt> actualizarServidorConDatosLocales(CancionEntity cancionLocal, CancionApiEnt cancionRemota) throws CredencialesErroneasException, TerminarSincronizacionException, IOException {

        Response<CancionApiEnt> lr=null;

        try {
            lr = apIservice.updateCancion(cancionLocal, token).execute();
            switch (lr.code()) {
                case 200:
                    servicioLocal.updateCancion(MediadorDeEntidades.cancionApiEntToCancionEntity(idGrupo.toString(),lr.body()));
                    break;
                case 409:
                    //si hay conflicto devolverlo
                    return new Conflicto<CancionEntity, CancionApiEnt>(Conflicto.T_CANCION,cancionLocal,lr.body());
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

    private void resolverConflicto(Conflicto<CancionEntity, CancionApiEnt> conflicto) throws  TerminarSincronizacionException {
        //mandar conflicto
        sincronizadorService.getHandler().onConflicto(conflicto);
        try {
            //esperar resolucion
            conflicto.esperar();
            //recoger solucion
            CancionEntity solucion =conflicto.getResuelto();
            //actualizar en local y en remoto con la eleccion de solucion
            servicioLocal.updateCancion(solucion);
            comprobarCancion(solucion,conflicto.getRemoto());
        } catch (InterruptedException | CredencialesErroneasException |
                 TerminarSincronizacionException | IOException e) {
            throw new TerminarSincronizacionException("Sincronización terminada");
        }
    }



    private void eliminarDeServidor(CancionEntity cancionLocal) throws TerminarSincronizacionException {

        try {
            Response<Object> lr = apIservice.deleteCancion(cancionLocal.getId().toString(),token).execute();
            switch (lr.code()) {
                case 401:
                    throw new CredencialesErroneasException("");
            }
        } catch (IOException | CredencialesErroneasException e) {
            throw new TerminarSincronizacionException("Sin conexión con el servidor");
        }
    }

    private void actualizarLocal(CancionEntity cancionLocal, CancionApiEnt cancionRemota) {
        cancionLocal.setNombre(cancionRemota.getNombre());
        cancionLocal.setDescripcion(cancionRemota.getDescripcion());
        cancionLocal.setDuracion(cancionRemota.getDuracion());
        cancionLocal.setVersion(cancionRemota.getVersion());
        cancionLocal.setBorrado(false);
        cancionLocal.setEditado(false);
        servicioLocal.updateCancion(cancionLocal);
    }

    private void eliminarLocal(CancionEntity cancionLocal) {
        servicioLocal.deleteCancion(cancionLocal);
    }




        private void agregarAlServidor(CancionEntity cancionLocal) throws CredencialesErroneasException, TerminarSincronizacionException {
            try {
                Response<CancionApiEnt> lr = apIservice.insertCancion(idGrupo.toString(),cancionLocal, token).execute();
                switch (lr.code()) {
                    case 200:
                        servicioLocal.updateCancion(MediadorDeEntidades.cancionApiEntToCancionEntity(idGrupo.toString(),lr.body()));
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


    private void comprobarNotas(CancionEntity cancionLocal, CancionApiEnt cancionRemota) {
        //new ComprobadorModificacionesNotas(cancionLocal,cancionRemota);
    }
}
