package josebailon.ensayos.cliente.model.database.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;
import java.util.stream.Collectors;

import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;

public class GrupoAndUsuariosAndCanciones {
    @Embedded
    public GrupoEntity grupo;

    @Relation(
            parentColumn = "id",
            entityColumn = "grupo"
    )
    public List<UsuarioEntity> usuarios;
    @Relation(
            parentColumn = "id",
            entityColumn = "grupo"
    )
    public List<CancionEntity> canciones;

    public List<CancionEntity> getCancionesOrdenadas(){
        return canciones.stream().sorted((o1, o2) -> o1.getNombre().compareTo(o2.getNombre())).collect(Collectors.toList());
    }
    public List<UsuarioEntity> getUsuariosOrdenados(){
        return usuarios.stream().sorted((o1, o2) -> o1.getEmail().compareTo(o2.getEmail())).collect(Collectors.toList());
    }
}
