package josebailon.ensayos.cliente.view.dialogos;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.UsuarioApiEnt;
import josebailon.ensayos.cliente.model.sincronizacion.MediadorDeEntidades;
import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;

public class DialogoConflictoCancion extends Dialog {
    Conflicto<CancionEntity,CancionApiEnt> conflicto;
    CancionEntity local;
    CancionApiEnt remoto;
    CancionEntity fusion;

    public DialogoConflictoCancion(@NonNull Context context, Conflicto<CancionEntity,CancionApiEnt> conflicto) {
        super(context);
        this.conflicto=conflicto;
        local = conflicto.getLocal();
        remoto = conflicto.getRemoto();
        prepararFusion();
        prepararInterface();
        this.setCanceledOnTouchOutside(false);
    }

    private void prepararFusion() {
        fusion = new CancionEntity();
        fusion.setId(local.getId());
        fusion.setDescripcion("");
        fusion.setNombre("");
        fusion.setDuracion("");
        fusion.setFecha(new Date(System.currentTimeMillis()));
        fusion.setEditado(true);
        fusion.setBorrado(false);
        fusion.setVersion(remoto.getVersion());
        //nombre
        if (local.getNombre().equals(remoto.getNombre()))
            fusion.setNombre(remoto.getNombre());
        else
            fusion.setNombre(local.getNombre()+" - "+remoto.getNombre());
        //descripcion
        if (local.getDescripcion().equals(remoto.getDescripcion()))
            fusion.setDescripcion(remoto.getDescripcion());
        else
            fusion.setDescripcion(local.getDescripcion()+" \n "+remoto.getDescripcion());
        //duracion
        if (local.getDuracion().equals(remoto.getDescripcion()))
            fusion.setDuracion(remoto.getDuracion());
        else
            fusion.setDuracion(local.getDuracion()+" / "+remoto.getDuracion());
        //grupo
        fusion.setGrupo(local.getGrupo());
    }

    private void prepararInterface() {
        this.setContentView(R.layout.dialogo_conflicto_cancion);
        Window window = this.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //local
        ((TextView)this.findViewById(R.id.fechaLocal)).setText(local.fechaFormateada());
        ((TextView)this.findViewById(R.id.nombreLocal)).setText(local.getNombre());
        ((TextView)this.findViewById(R.id.descripcionLocal)).setText(local.getDescripcion());
        ((TextView)this.findViewById(R.id.duracionLocal)).setText("Duracion: "+local.getDuracion());
        //remoto
        ((TextView)this.findViewById(R.id.fechaRemota)).setText(remoto.fechaFormateada());
        ((TextView)this.findViewById(R.id.nombreRemoto)).setText(remoto.getNombre());
        ((TextView)this.findViewById(R.id.descripcionRemota)).setText(remoto.getDescripcion());
        ((TextView)this.findViewById(R.id.duracionRemota)).setText("Duracion: "+remoto.getDuracion());


        //fusion
        ((TextView)this.findViewById(R.id.fechaFusion)).setText(fusion.fechaFormateada());
        ((TextView)this.findViewById(R.id.nombreFusion)).setText(fusion.getNombre());
        ((TextView)this.findViewById(R.id.descripcionFusion)).setText(fusion.getDescripcion());
        ((TextView)this.findViewById(R.id.duracionFusion)).setText("Duracion: "+fusion.getDuracion());


        //elegir local
        ((Button) (this.findViewById(R.id.btnAceptarLocal))).setOnClickListener(v -> {
            local.setVersion(remoto.getVersion());
            local.setEditado(true);
            conflicto.setResuelto(local);
            conflicto.liberar();
            this.dismiss();
        });

        //elegir remoto
        ((Button) (this.findViewById(R.id.btnAceptarRemoto))).setOnClickListener(v -> {
            conflicto.setResuelto(MediadorDeEntidades.cancionApiEntToCancionEntity(local.getGrupo().toString(),remoto));
            conflicto.liberar();
            this.dismiss();
        });

        //elegir fusion
        ((Button) (this.findViewById(R.id.btnAceptarFusio))).setOnClickListener(v -> {
            conflicto.setResuelto(fusion);
            conflicto.liberar();
            this.dismiss();
        });


    }
}
