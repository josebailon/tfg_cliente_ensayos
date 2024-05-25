package josebailon.ensayos.cliente.view.dialogos;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.grabacion.Reproductor;
import josebailon.ensayos.cliente.model.grabacion.ReproductorImpl;
import josebailon.ensayos.cliente.model.network.model.entidades.NotaApiEnt;
import josebailon.ensayos.cliente.model.sincronizacion.MediadorDeEntidades;
import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;
import josebailon.ensayos.cliente.viewmodel.SincronizadorViewModel;

public class DialogoConflictoNota extends Dialog {
    Conflicto<NotaAndAudio, NotaApiEnt> conflicto;
    NotaAndAudio local;
    NotaApiEnt remoto;
    CancionEntity resultado;
    Spinner spinnerTitulo;
    Spinner spinnerTexto;
    Spinner spinnerAudio;
    TextView lbNombre;
    TextView lbTexto;

    TextView lbFecha;

    Button btnEscuchaAudio;
    ImageButton btnStop;
    String archivoAudio;
    String fechaAudio;
    boolean reproduciendo;

    private Reproductor reproductor;

    SincronizadorViewModel viewModel;
    View zonaReproductor;

    public DialogoConflictoNota(@NonNull Context context, Conflicto<NotaAndAudio, NotaApiEnt> conflicto, SincronizadorViewModel viewModel) {
        super(context);
        this.conflicto = conflicto;
        this.viewModel = viewModel;
        local = conflicto.getLocal();
        remoto = conflicto.getRemoto();
//        prepararFusion();
        prepararInterface();
        this.setCanceledOnTouchOutside(false);
    }

//    private void prepararFusion() {
//        fusion = new CancionEntity();
//        fusion.setId(local.getId());
//        fusion.setDescripcion("");
//        fusion.setNombre("");
//        fusion.setDuracion("");
//        fusion.setFecha(new Date(System.currentTimeMillis()));
//        fusion.setEditado(true);
//        fusion.setBorrado(false);
//        fusion.setVersion(remoto.getVersion());
//        //nombre
//        if (local.getNombre().equals(remoto.getNombre()))
//            fusion.setNombre(remoto.getNombre());
//        else
//            fusion.setNombre(local.getNombre()+" - "+remoto.getNombre());
//        //descripcion
//        if (local.getDescripcion().equals(remoto.getDescripcion()))
//            fusion.setDescripcion(remoto.getDescripcion());
//        else
//            fusion.setDescripcion(local.getDescripcion()+" \n "+remoto.getDescripcion());
//        //duracion
//        if (local.getDuracion().equals(remoto.getDescripcion()))
//            fusion.setDuracion(remoto.getDuracion());
//        else
//            fusion.setDuracion(local.getDuracion()+" / "+remoto.getDuracion());
//    }

    private void prepararInterface() {
        this.setContentView(R.layout.dialogo_conflicto_nota);
        Window window = this.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        spinnerTitulo = ((Spinner) this.findViewById(R.id.spinnerTitulo));
        spinnerTexto = ((Spinner) this.findViewById(R.id.spinnerTexto));
        spinnerAudio = ((Spinner) this.findViewById(R.id.spinnerAudio));
        lbNombre = ((TextView) this.findViewById(R.id.lbNombre));
        lbTexto = ((TextView) this.findViewById(R.id.lbTexto));
        lbFecha = ((TextView) this.findViewById(R.id.fechaAudio));
        btnEscuchaAudio = ((Button) this.findViewById(R.id.btnEscuchaAudio));
        btnStop = ((ImageButton) this.findViewById(R.id.btnStop));
        zonaReproductor = this.findViewById(R.id.mediaControl);

        //rellenar spinners
        //spinner titulo
        List<String> opcionesTitulo = Arrays.asList("Título local", "Título remoto", "Título fusionado");
        ArrayAdapter<String> adapterTit = new ArrayAdapter<String>(getContext(), R.layout.elemento_spinner_simple, opcionesTitulo);

        spinnerTitulo.setAdapter(adapterTit);
        //spinner texto
        List<String> opcionesTexto = Arrays.asList("Texto local", "Texto remoto", "Texto fusionado");
        ArrayAdapter<String> adapterTex = new ArrayAdapter<String>(getContext(), R.layout.elemento_spinner_simple, opcionesTexto);
        spinnerTexto.setAdapter(adapterTex);
        //spinner audio
        List<String> opcionesAudio = new ArrayList<>();
        if (local.audio == null && remoto.getAudio() == null)
            opcionesAudio.add("Sin audio");
        else {
            if (local.audio != null && !local.audio.isBorrado())
                opcionesAudio.add("Audio local");
            else
                opcionesAudio.add("Sin audio");
            if (remoto.getAudio() != null)
                opcionesAudio.add("Audio remoto");
            else
                opcionesAudio.add("Sin audio");
        }
        ArrayAdapter<String> adapterAudio = new ArrayAdapter<String>(getContext(), R.layout.elemento_spinner_simple, opcionesAudio);
        spinnerAudio.setAdapter(adapterAudio);

        //onclick guardar
        ((Button) (this.findViewById(R.id.btnGuardarVersion))).setOnClickListener(v -> {
            guardar();
        });

        //onclick guardar
        ((ImageButton) (this.findViewById(R.id.btnStop))).setOnClickListener(v -> {
            pararReproduccion();
        });
        ((Button) (this.findViewById(R.id.btnEscuchaAudio))).setOnClickListener(v -> {
            reproducir();
        });
        //escucha spinners
        AdapterView.OnItemSelectedListener adapterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarCampos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };


        spinnerTitulo.setOnItemSelectedListener(adapterListener);
        spinnerTexto.setOnItemSelectedListener(adapterListener);
        spinnerAudio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actualizarCamposAudio();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        actualizarCampos();
        actualizarCamposAudio();
    }

    private void reproducir() {
        String ruta = viewModel.getRutaAudio(archivoAudio);
        if (!viewModel.existeArchivo(archivoAudio)) {
            return;
        }

        reproductor = new ReproductorImpl();
        reproductor.definirVistaParaMc(getContext(), zonaReproductor, () -> {
            pararReproduccion();
        });
        try {
            reproductor.iniciar(new File(ruta));
            btnEscuchaAudio.setVisibility(View.INVISIBLE);
            btnStop.setVisibility(View.VISIBLE);
        } catch (Exception ex) {

        }

    }

    private void pararReproduccion() {
        if (reproductor != null) {
            reproductor.parar();
            btnEscuchaAudio.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.INVISIBLE);
        }
    }


    private void actualizarCampos() {
        actualizarTitulo();
        actualizarTexto();

    }

    private void actualizarCamposAudio() {
        String audioSeleccionado = spinnerAudio.getSelectedItem().toString();
        switch (audioSeleccionado) {
            case "Audio local":
                archivoAudio = local.audio.getArchivo();
                fechaAudio = local.audio.fechaFormateada();
                break;
            case "Audio remoto":
                archivoAudio = remoto.getAudio().getNombreArchivo();
                fechaAudio = "Fecha del audio: " + remoto.getAudio().fechaFormateada();
                break;
            case "Sin audio":
                archivoAudio = "";
                fechaAudio = "Sin audio";
                break;
        }

        if (archivoAudio.equals("")) {
            lbFecha.setText("Sin audio");
            btnStop.setVisibility(View.GONE);
            btnEscuchaAudio.setVisibility(View.GONE);
        } else if (reproduciendo) {
            lbFecha.setText(fechaAudio);
            btnStop.setVisibility(View.VISIBLE);
            btnEscuchaAudio.setVisibility(View.GONE);
        } else {
            lbFecha.setText(fechaAudio);
            btnStop.setVisibility(View.GONE);
            btnEscuchaAudio.setVisibility(View.VISIBLE);
        }

    }


    private void actualizarTitulo() {
        long id = spinnerTitulo.getSelectedItemId();
        switch ((int) id) {
            case 0:
                lbNombre.setText(local.nota.getNombre());
                break;
            case 1:
                lbNombre.setText(remoto.getNombre());
                break;
            case 2:
                lbNombre.setText(local.nota.getNombre() + "-" + remoto.getNombre());
                break;
        }
    }

    private void actualizarTexto() {
        long id = spinnerTexto.getSelectedItemId();
        switch ((int) id) {
            case 0:
                lbTexto.setText(local.nota.getTexto());
                break;
            case 1:
                lbTexto.setText(remoto.getTexto());
                break;
            case 2:
                lbTexto.setText(local.nota.getTexto() + "\n" + remoto.getTexto());
                break;
        }
    }

    private void guardar() {


        NotaEntity nota = new NotaEntity();
        nota.setId(local.nota.getId());
        nota.setNombre(lbNombre.getText().toString());
        nota.setTexto(lbTexto.getText().toString());
        nota.setEditado(true);
        nota.setBorrado(false);
        nota.setVersion(remoto.getVersion());
        nota.setFecha(new Date(System.currentTimeMillis()));
        nota.setCancion(local.nota.getCancion());
        nota.setDestacado(false);

        NotaAndAudio salida = new NotaAndAudio();
        salida.nota = nota;

        //coger audio local
        if (spinnerAudio.getSelectedItemId() == 0) {
            salida.audio = local.audio;
        }
        //coger audio remoto
        else {
            if (remoto.getAudio() == null)
                salida.audio = null;
            else
                salida.audio = MediadorDeEntidades.audioApiEntToAudioEntity(remoto.getAudio());
        }


        if (salida.audio != null) {
            if (remoto.getAudio() == null) {
                // v0 e0
                salida.audio.setVersion(0);
                salida.audio.setEditado(true);
            } else {
                if (remoto.getAudio().getNombreArchivo().equals(salida.audio.getArchivo())) {
                    //vr e0
                    salida.audio.setVersion(remoto.getAudio().getVersion());
                    salida.audio.setEditado(false);
                } else {
                    //vr e1
                    salida.audio.setVersion(remoto.getAudio().getVersion());
                    salida.audio.setEditado(true);
                }
            }
        }

        conflicto.setResuelto(salida);
        conflicto.liberar();
        this.dismiss();

    }
}
