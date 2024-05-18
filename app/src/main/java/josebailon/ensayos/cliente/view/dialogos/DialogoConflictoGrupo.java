package josebailon.ensayos.cliente.view.dialogos;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.sql.Date;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.UsuarioApiEnt;
import josebailon.ensayos.cliente.model.sincronizacion.MediadorDeEntidades;
import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;

public class DialogoConflictoGrupo extends Dialog {
    Conflicto<GrupoAndUsuariosAndCanciones,GrupoApiEnt> conflicto;
    GrupoAndUsuariosAndCanciones local;
    GrupoApiEnt remoto;
    GrupoAndUsuariosAndCanciones fusion;

    public DialogoConflictoGrupo(@NonNull Context context, Conflicto<GrupoAndUsuariosAndCanciones,GrupoApiEnt> conflicto) {
        super(context);
        this.conflicto=conflicto;
        local = conflicto.getLocal();
        remoto = conflicto.getRemoto();
        prepararFusion();
        prepararInterface();
        this.setCanceledOnTouchOutside(false);
    }

    private void prepararFusion() {
        fusion = new GrupoAndUsuariosAndCanciones();
        fusion.grupo=new GrupoEntity();
        fusion.grupo.setId(local.grupo.getId());
        fusion.grupo.setDescripcion("");
        fusion.grupo.setNombre("");
        fusion.grupo.setFecha(new Date(System.currentTimeMillis()));
        fusion.grupo.setEditado(true);
        fusion.grupo.setBorrado(false);
        fusion.grupo.setAbandonado(false);
        fusion.grupo.setVersion(remoto.getVersion());
        fusion.usuarios=new ArrayList<>();
        //nombre
        if (local.grupo.getNombre().equals(remoto.getNombre()))
            fusion.grupo.setNombre(remoto.getNombre());
        else
            fusion.grupo.setNombre(local.grupo.getNombre()+" - "+remoto.getNombre());
        //descripcion
        if (local.grupo.getDescripcion().equals(remoto.getDescripcion()))
            fusion.grupo.setDescripcion(remoto.getDescripcion());
        else
            fusion.grupo.setDescripcion(local.grupo.getDescripcion()+" \n "+remoto.getDescripcion());

        //fecha

        //usuarios
        Set<String> set = new HashSet<String>();
        for (UsuarioEntity u: local.usuarios) {set.add(u.getEmail());}
        for (UsuarioApiEnt ur: remoto.getUsuarios()) {set.add(ur.getEmail());}

        set.stream().forEach(s -> {
            UsuarioEntity uf = new UsuarioEntity();
            uf.setEmail(s);
            uf.setGrupo(local.grupo.getId());
            fusion.usuarios.add(uf);
        });


    }

    private void prepararInterface() {
        this.setContentView(R.layout.dialogo_conflicto_grupo);
        Window window = this.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //local
        ((TextView)this.findViewById(R.id.fechaLocal)).setText(local.grupo.fechaFormateada());
        ((TextView)this.findViewById(R.id.nombreLocal)).setText(local.grupo.getNombre());
        ((TextView)this.findViewById(R.id.descripcionLocal)).setText(local.grupo.getDescripcion());
        TextView lul = (TextView)this.findViewById(R.id.listaUsuariosLocales);
        lul.setText("");
        for (UsuarioEntity u: local.usuarios  ) {
            lul.setText(lul.getText()+u.getEmail()+"\n");
        }
        //remoto
        ((TextView)this.findViewById(R.id.fechaRemota)).setText(remoto.getFecha());
        ((TextView)this.findViewById(R.id.nombreRemoto)).setText(remoto.getNombre());
        ((TextView)this.findViewById(R.id.descripcionRemota)).setText(remoto.getDescripcion());
        TextView lur = (TextView)this.findViewById(R.id.listaUsuariosRemotos);
        lur.setText("");
        for (UsuarioApiEnt u: remoto.getUsuarios()  ) {
            lur.setText(lur.getText()+u.getEmail()+"\n");
        }

        //fusion
        ((TextView)this.findViewById(R.id.fechaFusion)).setText(fusion.grupo.fechaFormateada());
        ((TextView)this.findViewById(R.id.nombreFusion)).setText(fusion.grupo.getNombre());
        ((TextView)this.findViewById(R.id.descripcionFusion)).setText(fusion.grupo.getDescripcion());
        TextView luf = (TextView)this.findViewById(R.id.listaUsuariosFusion);
        luf.setText("");
        for (UsuarioEntity u: fusion.usuarios  ) {
            luf.setText(luf.getText()+u.getEmail()+"\n");
        }



        //elegir local
        ((Button) (this.findViewById(R.id.btnAceptarLocal))).setOnClickListener(v -> {
            local.grupo.setVersion(remoto.getVersion());
            local.grupo.setEditado(true);
            conflicto.setResuelto(local);
            conflicto.liberar();
            this.dismiss();
        });

        //elegir remoto
        ((Button) (this.findViewById(R.id.btnAceptarRemoto))).setOnClickListener(v -> {
            GrupoAndUsuariosAndCanciones salidaRemota = new GrupoAndUsuariosAndCanciones();
            salidaRemota.grupo= MediadorDeEntidades.grupoApiEntToGrupoEntity(remoto);
            salidaRemota.grupo.setEditado(true);
            salidaRemota.usuarios=new ArrayList<>();
            for (UsuarioApiEnt u :remoto.getUsuarios()) {
                UsuarioEntity ue = new UsuarioEntity();
                ue.setGrupo(local.grupo.getId());
                ue.setEmail(u.getEmail());
                salidaRemota.usuarios.add(ue);
            }
            conflicto.setResuelto(salidaRemota);
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
