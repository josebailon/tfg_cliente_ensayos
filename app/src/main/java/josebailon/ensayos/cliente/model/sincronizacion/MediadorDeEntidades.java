package josebailon.ensayos.cliente.model.sincronizacion;

import java.util.UUID;

import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.network.model.entidades.AudioApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.NotaApiEnt;

public class MediadorDeEntidades {

    public static GrupoEntity grupoApiEntToGrupoEntity(GrupoApiEnt grupoApi){
            GrupoEntity g = new GrupoEntity();
            g.setId(UUID.fromString(grupoApi.getId()));
            g.setNombre(grupoApi.getNombre());
            g.setDescripcion(grupoApi.getDescripcion());
            g.setVersion(grupoApi.getVersion());
            g.setBorrado(false);
            g.setEditado(false);
            g.setAbandonado(false);
            return g;
    }
    public static UsuarioEntity crearUsuarioEntityParaGrupo(String idGrupo, String email){
        UsuarioEntity u = new UsuarioEntity();
        u.setGrupo(UUID.fromString(idGrupo));
        u.setEmail(email);
        return u;
    }

    public static CancionEntity cancionApiEntToCancionEntity(String idGrupo, CancionApiEnt cancionRemota) {
        CancionEntity c = new CancionEntity();
        c.setId(UUID.fromString(cancionRemota.getId()));
        c.setNombre(cancionRemota.getNombre());
        c.setDescripcion(cancionRemota.getDescripcion());
        c.setDuracion(cancionRemota.getDuracion());
        c.setVersion(cancionRemota.getVersion());
        c.setBorrado(false);
        c.setEditado(false);
        c.setGrupo(UUID.fromString(idGrupo));
        return c;
    }

    public static NotaEntity notaApiEntToNotaEntity(String idCancion, NotaApiEnt notaRemota) {
        NotaEntity n = new NotaEntity();
        n.setId(UUID.fromString(notaRemota.getId()));
        n.setNombre(notaRemota.getNombre());
        n.setTexto(notaRemota.getTexto());
        n.setVersion(notaRemota.getVersion());
        n.setBorrado(false);
        n.setEditado(false);
        n.setCancion(UUID.fromString(idCancion));
        return n;
    }

    public static AudioEntity audioApiEntToAudioEntity(AudioApiEnt audioRemoto) {
        AudioEntity a = new AudioEntity();
        a.setNota_id(UUID.fromString(audioRemoto.getId()));
        a.setArchivo(audioRemoto.getNombreArchivo());
        a.setVersion(audioRemoto.getVersion());
        a.setBorrado(false);
        a.setEditado(false);
        return a;
    }
}
