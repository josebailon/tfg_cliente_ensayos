package josebailon.ensayos.cliente.data.database.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import josebailon.ensayos.cliente.data.database.entity.AudioEntity;
import josebailon.ensayos.cliente.data.database.entity.NotaEntity;

public class NotaAndAudio {
    @Embedded
    public NotaEntity nota;

    @Relation(
            parentColumn = "id",
            entityColumn = "nota_id"
    )
    public AudioEntity audio;
}
