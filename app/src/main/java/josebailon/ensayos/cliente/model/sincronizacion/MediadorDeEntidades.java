package josebailon.ensayos.cliente.model.sincronizacion;

import java.util.UUID;

import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;

public class MediadorDeEntidades {

    public static GrupoEntity grupoApiEntToGrupoEntity(GrupoApiEnt grupoApi){
            GrupoEntity g = new GrupoEntity();
            g.setId(UUID.fromString(grupoApi.getId()));
            g.setNombre(grupoApi.getNombre());
            g.setDescripcion(grupoApi.getDescripcion());
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

}
