package josebailon.ensayos.cliente.model.grabacion;

import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;

import josebailon.ensayos.cliente.App;

public class ReproductorImpl implements Reproductor{
    private MediaPlayer player;
    @Override
    public void iniciar(File archivo) {
        player = MediaPlayer.create(App.getContext(), Uri.fromFile(archivo));
        player.start();
    }

    @Override
    public void parar() {
        if (player!=null) {
            player.stop();
            player.release();
            player=null;
        }

    }
}
