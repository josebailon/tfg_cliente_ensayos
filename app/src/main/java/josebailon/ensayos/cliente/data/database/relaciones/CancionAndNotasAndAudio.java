package josebailon.ensayos.cliente.data.database.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;
import java.util.stream.Collectors;

import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;

public class CancionAndNotasAndAudio {
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
