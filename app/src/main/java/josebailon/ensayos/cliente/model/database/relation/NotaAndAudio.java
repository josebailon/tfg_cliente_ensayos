package josebailon.ensayos.cliente.model.database.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;

public class NotaAndAudio {
    @Embedded
    public NotaEntity nota;

    @Relation(
            parentColumn = "id",
            entityColumn = "nota_id"
    )
    public AudioEntity audio;
}
