package josebailon.ensayos.cliente.model.grabacion;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;

import java.io.File;

public interface Reproductor extends   MediaController.MediaPlayerControl{
    public void iniciar (File archivo);
    public void parar();
    public void definirVistaParaMc(Context contexto, View v, OnMedioTerminado mt);
}

