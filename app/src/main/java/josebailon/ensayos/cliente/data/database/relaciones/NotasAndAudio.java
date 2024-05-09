package josebailon.ensayos.cliente.data.database.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import josebailon.ensayos.cliente.data.database.entity.AudioEntity;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.data.database.entity.NotaEntity;
import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;

public class NotasAndAudio {
    @Embedded
    public NotaEntity nota;

    @Relation(
            parentColumn = "id",
            entityColumn = "nota_id"
    )
    public AudioEntity audio;
}
