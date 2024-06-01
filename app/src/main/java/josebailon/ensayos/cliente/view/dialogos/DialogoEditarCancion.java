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
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;

public class DialogoEditarCancion extends Dialog {
    GrupoEntity grupo;
    public DialogoEditarCancion(@NonNull Context context, CancionEntity cancion) {
        super(context);
        this.setContentView(R.layout.dialogo_crear_cancion);
        ((EditText)this.findViewById(R.id.inputEmail)).setText(cancion.getNombre());
        ((EditText)this.findViewById(R.id.inputDescripcion)).setText(cancion.getDescripcion());
        ((EditText)this.findViewById(R.id.inputDuracion)).setText(cancion.getDuracion());
    }

    public String  getNombre (){
        return ((EditText) (this.findViewById(R.id.inputEmail))).getText().toString();
    }
    public String getDescripcion(){
        return ((EditText) (this.findViewById(R.id.inputDescripcion))).getText().toString();    }
    public String getDuracion() {
        return ((EditText) (this.findViewById(R.id.inputDuracion))).getText().toString();
    }

    public void mostrar(View.OnClickListener listenerOk) {
        this.show();
        Window window = this.getWindow();
        ((TextView)this.findViewById(R.id.tituloventana)).setText("Editar Canción");
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (this.findViewById(R.id.btnAceptar))).setOnClickListener(listenerOk);
        ((Button) (this.findViewById(R.id.btnCancelar))).setOnClickListener(v -> this.dismiss());
    }

}
