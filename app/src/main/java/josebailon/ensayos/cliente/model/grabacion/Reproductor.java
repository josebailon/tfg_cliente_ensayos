package josebailon.ensayos.cliente.model.grabacion;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;

import java.io.File;

/**
 * Interfaz de reproductor
 *
 * @author Jose Javier Bailon Ortiz
 */
public interface Reproductor extends   MediaController.MediaPlayerControl{

    /**
     * Iniciar la reproduccion
     * @param archivo El archivo a reproducir
     * @throws Exception Si se produce un error de reproduccion
     */
    public void iniciar (File archivo) throws Exception;

    /**
     * Parar la reproduccion
     */
    public void parar();

    /**
     * Devfinir la vista padre en la cual se colocara la vista del media controller
     * @param contexto Contexto a usar
     * @param v Vista padre
     * @param mt Callback de terminacion de la reproduccion
     */
    public void definirVistaParaMc(Context contexto, View v, OnMedioTerminado mt);
}

