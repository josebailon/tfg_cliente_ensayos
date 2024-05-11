package josebailon.ensayos.cliente.data.database.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;
import java.util.stream.Collectors;

import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.entity.NotaEntity;

public class CancionAndNotas {
    @Embedded
    public CancionEntity cancion;

    @Relation(
            parentColumn = "id",
            entityColumn = "cancion"
    )
    public List<NotaEntity> notas;

    public List<NotaEntity> getNotasOrdenadas(){
        return notas.stream().sorted((o1, o2) -> o1.getNombre().compareTo(o2.getNombre())).collect(Collectors.toList());
    }
}
