package josebailon.ensayos.cliente.model.grabacion;

import android.media.MediaRecorder;
import android.os.Build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import josebailon.ensayos.cliente.App;

public class GrabadorImpl implements Grabador {

    private MediaRecorder recorder;

    private MediaRecorder crearRecorder(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            return new MediaRecorder(App.getContext());
        else
            return new MediaRecorder();

    }

    @Override
    public void iniciar(File destino) throws IOException {
        recorder = crearRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(new FileOutputStream(destino).getFD());
        recorder.prepare();;
        recorder.start();
    }

    @Override
    public void parar() {
        recorder.stop();
        recorder.reset();
    }

}
