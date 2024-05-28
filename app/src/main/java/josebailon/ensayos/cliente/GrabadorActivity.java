package josebailon.ensayos.cliente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import josebailon.ensayos.cliente.databinding.ActivityGrabadorBinding;
import josebailon.ensayos.cliente.model.archivos.ArchivosRepo;
import josebailon.ensayos.cliente.model.grabacion.Grabador;
import josebailon.ensayos.cliente.model.grabacion.GrabadorImpl;
import josebailon.ensayos.cliente.model.grabacion.Reproductor;
import josebailon.ensayos.cliente.model.grabacion.ReproductorImpl;

/**
 * Actividad de grabacion
 */
public class GrabadorActivity extends AppCompatActivity {

    /**
     * Objeto grabador
     */
    private Grabador grabador;

    /**
     * Objeto repropductor
     */
    private Reproductor reproductor;

    /**
     * Estado grabando
     */

    private boolean grabando = false;

    /**
     * Estado ya grabado
     */
    private boolean grabado = false;

    /**
     * Estado reproduciendo
     */
    private boolean reproduciendo = false;

    private ActivityGrabadorBinding binding;
    ArchivosRepo archivosRepo = ArchivosRepo.getInstance();


    /**
     * audio actual
     */
    private File rutaActual;

    /**
     * Handler de escucha de cronometro
     */
    private final Handler handler = new Handler();

    /**
     * Hilo de cronometro
     */
    private  Runnable contador;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGrabadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //inicializar reproductor y grabador
        grabador = new GrabadorImpl();
        reproductor = new ReproductorImpl();
        reproductor.definirVistaParaMc(this,binding.mediaControl,() -> {
            pararReproduccion();
        });

        actualizaBotones();
        //EVENTOS
        //guardar
        binding.btnGuardar.setOnClickListener(v -> {
            Intent data = new Intent();
            ArchivosRepo repo = ArchivosRepo.getInstance();
            data.setData(Uri.fromFile(rutaActual));
            setResult(RESULT_OK, data);
            if (reproduciendo)
                reproductor.parar();

            finish();
        });
        //cancelar
        binding.btnCancelar.setOnClickListener(v->{
            if(reproduciendo)
                reproductor.parar();
            finish();
        });

        //grabar
        binding.btnRecordStart.setOnClickListener(v -> {

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);

            } else {

                    grabando=true;
                    rutaActual=archivosRepo.getTempPathNuevo();
                    try {
                        grabador.iniciar(rutaActual);
                        iniciarTiempo();
                    } catch (IOException e) {
                        grabando=false;
                        grabado=false;
                        pararTiempo();
                    }finally {
                        actualizaBotones();
                    }

            }

        });
        //parar grabacion
        binding.btnRecordStop.setOnClickListener(v -> {
            grabador.parar();
            grabando=false;
            grabado=true;
            actualizaBotones();
            pararTiempo();
        });
        //iniciar reproduccion
        binding.btnPlayStart.setOnClickListener(v -> {
            try {
                reproductor.iniciar(rutaActual);
                reproduciendo = true;
                actualizaBotones();
                iniciarTiempo();
            }catch (Exception ex){
                toast(ex.getMessage());
            }
        });
        //parar reproduccion
        binding.btnPlayStop.setOnClickListener(v -> {
            pararReproduccion();
        });

    }

    /**
     * Para la reproduccion
     */
    private void pararReproduccion() {
        reproductor.parar();
        reproduciendo=false;
        actualizaBotones();
        pararTiempo();
    }

    /**
     * Resetea el cronometro
     */
    private void resetContador() {
        binding.contador.setText("00:00");
    }


    /**
     * Actualiza los botones segun lso estados de grabando, reproduciendo y grabado
     */
    private void actualizaBotones(){
        binding.btnGuardar.setEnabled((!grabando&&grabado));
        binding.btnCancelar.setEnabled((!grabando));
        binding.btnPlayStart.setVisibility((!grabando&&grabado&&!reproduciendo)?View.VISIBLE:View.INVISIBLE);
        binding.btnPlayStop.setVisibility((reproduciendo)?View.VISIBLE:View.INVISIBLE);
        binding.btnRecordStart.setVisibility((!reproduciendo&&!grabando)?View.VISIBLE:View.INVISIBLE);
        binding.btnRecordStop.setVisibility((grabando)?View.VISIBLE:View.INVISIBLE);

    }

    /**
     * Inicia el cronometro
     */
    private void iniciarTiempo(){
        resetContador();
        contador=new Runnable()
        {
            private long tiempo = 0;

            @Override
            public void run()
            {
                tiempo += 1;

                int segundos =(int) (tiempo % 60);
                int minutos = (int ) (tiempo / 60);
                binding.contador.setText(String.format("%d:%02d", minutos, segundos));
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(contador, 1000); // 1 second delay (takes millis)
    }

    /**
     * Para el cronometro
     */
    private void pararTiempo(){
        handler.removeCallbacks(contador);
        resetContador();
    }

    /**
     * Toast de mensajes
     * @param msg
     */
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
