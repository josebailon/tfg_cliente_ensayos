package josebailon.ensayos.cliente.model.grabacion;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;

import java.io.File;

import josebailon.ensayos.cliente.App;

public class ReproductorImpl implements Reproductor{
    private MediaPlayer player;
    private MediaController mediaController;
    private View vista;
    private OnMedioTerminado mt;


    @Override
    public void iniciar(File archivo) throws Exception {
        player = MediaPlayer.create(App.getContext(), Uri.fromFile(archivo));
        if (player==null) {
            throw new Exception("No se puede reproducir el archivo");
        }
        Handler h;
        h=new Handler();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        mediaController.show(0);
                        start();
                    }
                });
            }
        });
        player.setOnCompletionListener(mp -> {
            if (mt!=null)
                mt.terminado();
        });

    }

    @Override
    public void parar() {
        if (player!=null&&mediaController!=null) {
            player.stop();
            player.release();
            player=null;
            mediaController.hide();
        }
    }

    @Override
    public void definirVistaParaMc(Context contexto, View v, OnMedioTerminado mt) {
        vista=v;
        this.mt =mt;
        mediaController=  new MediaController(contexto){
            @Override
            public void show(int timeout) {
                super.show(0);
            }
        };
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(v);


    }

    @Override
    public void start() {
        if(!player.isPlaying())
            player.start();
    }
    @Override
    public void pause() {
        if(player.isPlaying())
            player.pause();
    }
    @Override
    public int getDuration() {
        if (player!=null)
        return player.getDuration();
        else return 0;
    }
    @Override
    public int getCurrentPosition() {
        if (player!=null && player.isPlaying())
            return player.getCurrentPosition();
        else
            return 0;
    }
    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }
    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }
    @Override
    public int getBufferPercentage() {
        return 0;
    }
    @Override
    public boolean canPause() {
        return true;
    }
    @Override
    public boolean canSeekBackward() {
        return true;
    }
    @Override
    public boolean canSeekForward() {
        return true;
    }
    @Override
    public int getAudioSessionId() {
        return player.getAudioSessionId();
    }



}
