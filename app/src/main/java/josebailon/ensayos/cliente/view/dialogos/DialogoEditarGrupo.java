package josebailon.ensayos.cliente.view.dialogos;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;

public class DialogoEditarGrupo extends Dialog {
    GrupoEntity grupo;
    public DialogoEditarGrupo(@NonNull Context context, GrupoEntity grupo) {
        super(context);
        this.grupo=grupo;
        this.setContentView(R.layout.dialogo_crear_grupo);
        ((EditText) this.findViewById(R.id.inputEmail)).setText(grupo.getNombre());
        ((EditText) this.findViewById(R.id.inputDescripcion)).setText(grupo.getDescripcion());
    }

    public String  getNombre (){
        return ((EditText) (this.findViewById(R.id.inputEmail))).getText().toString();
    }
    public String getDescripcion(){
        return ((EditText) (this.findViewById(R.id.inputDescripcion))).getText().toString();
    }

    public void mostrar(View.OnClickListener listenerOk) {
        this.show();
        Window window = this.getWindow();
        ((TextView) window.findViewById(R.id.tituloventana)).setText("Editar Grupo");
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (this.findViewById(R.id.btnAceptar))).setOnClickListener(listenerOk);
        ((Button) (this.findViewById(R.id.btnCancelar))).setOnClickListener(v -> this.dismiss());
    }
}
