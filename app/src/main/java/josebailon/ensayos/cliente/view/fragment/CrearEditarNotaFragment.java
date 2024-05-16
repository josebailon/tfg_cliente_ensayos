package josebailon.ensayos.cliente.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;
import java.util.UUID;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.databinding.FragmentCrearEditarNotaBinding;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.grabacion.Reproductor;
import josebailon.ensayos.cliente.model.grabacion.ReproductorImpl;
import josebailon.ensayos.cliente.viewmodel.CrearEditarNotaViewModel;

public class CrearEditarNotaFragment extends Fragment {

    private FragmentCrearEditarNotaBinding binding;
    private CrearEditarNotaViewModel viewModel;
    private NotaAndAudio notaAndAudio;

    private Reproductor reproductor;

    //Escucha de edicion de texto
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {      }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            viewModel.actualizarTexto(binding.inputNombre.getText().toString(),binding.inputTexto.getText().toString());
        }
    };



    private ActivityResultLauncher<Intent> lanzadorIntentGrabacion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //lanzador de intents
        lanzadorIntentGrabacion = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),resultado -> {
                    if(resultado.getResultCode()==RESULT_OK){
                        manejarSeleccionAudio(resultado.getData());
                    }
                });
    }

     @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);



        binding = FragmentCrearEditarNotaBinding.inflate(inflater, container, false);
        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(CrearEditarNotaViewModel.class);

        //recoger id cancion
        viewModel.setIdcancion(UUID.fromString(getArguments().getString("idcancion")));
        //recoger id nota
        viewModel.setIdNota(getArguments().getString("idnota"));

        // Callback manejo boton atras
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                manejarBotonAtras();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        //escuchar mensajes
        viewModel.getMensaje().observe(getViewLifecycleOwner(),mensaje -> toast(mensaje.toString()));

        //escucha de cambios para actualizar botones
        viewModel.getHaCambiado().observe(getViewLifecycleOwner(),aBoolean -> {
            notaAndAudio=viewModel.getNotaAndAudioObj();
            actualizarBotones();
        });

        //escucha de descarga
         viewModel.getDescargando().observe(getViewLifecycleOwner(),descargando -> {
             binding.descargandoBar.setVisibility(descargando?View.VISIBLE:View.INVISIBLE);
             boolean necesitaDescargar = notaAndAudio!=null//hay nota/audio
                             && notaAndAudio.audio!=null//tiene audio
                             &&!viewModel.existeArchivo()//no existe el archivo
                             &&!descargando;//no se esta descargando
             binding.btnDescargaAudio.setVisibility((necesitaDescargar)?View.VISIBLE:View.INVISIBLE);
             if (!descargando)
                actualizarBotones();
         });

        //recoger nota de bd
        viewModel.getNotaAndAudio().observe(getViewLifecycleOwner(), datos -> {
                binding.inputNombre.setText(datos.nota.getNombre());
                binding.inputTexto.setText(datos.nota.getTexto());
            notaAndAudio=datos;
            //listeners texto
            binding.inputNombre.addTextChangedListener(textWatcher);
            binding.inputTexto.addTextChangedListener(textWatcher);
            //refrescar botones
            actualizarBotones();
        });

        viewModel.getOcupado().observe(getViewLifecycleOwner(),ocupado -> {

            binding.btnQuitarAudio.setEnabled(!ocupado);
            binding.btnEscuchaAudio.setEnabled(!ocupado);
            binding.btnElegirAudio.setEnabled(!ocupado);
            binding.btnGuardarNota.setEnabled(!ocupado);
            binding.btnCancelarNota.setEnabled(!ocupado);
            binding.ocupadoBar.setVisibility(ocupado?View.VISIBLE:View.INVISIBLE);

        });
        return binding.getRoot();
    }

    private void actualizarBotones() {
        if (notaAndAudio==null)
            return;
        if (notaAndAudio.audio == null || notaAndAudio.audio.isBorrado()){
            binding.lbSinAudio.setVisibility(View.VISIBLE);
            binding.btnEscuchaAudio.setVisibility(View.INVISIBLE);
            binding.btnQuitarAudio.setVisibility(View.INVISIBLE);
            //con audio
        }else{
            binding.lbSinAudio.setVisibility(View.INVISIBLE);
            binding.btnEscuchaAudio.setVisibility(viewModel.existeArchivo()?View.VISIBLE:View.INVISIBLE);
            binding.btnDescargaAudio.setVisibility(!viewModel.existeArchivo()?View.VISIBLE:View.INVISIBLE);
            binding.btnQuitarAudio.setVisibility(View.VISIBLE);
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ocultarMenuAcciones();

        //LISTENERS
        //guardar
        binding.btnGuardarNota.setOnClickListener(v -> {
            if(reproductor!=null)
                reproductor.parar();
            viewModel.guardarNota();
            NavHostFragment.findNavController(this).popBackStack();
        });
        //cancelar
        binding.btnCancelarNota.setOnClickListener(v -> {
            if(reproductor!=null)
                reproductor.parar();
            NavHostFragment.findNavController(this).popBackStack();
        });
        //elegir audio
        binding.btnElegirAudio.setOnClickListener(v -> {
            seleccionarAudio();
        });
        //quitar audio
        binding.btnQuitarAudio.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminación")
                    .setMessage("¿Quieres quitar el audio de la nota?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("SÍ",(dialog, which) -> {
                        viewModel.quitarAudio();
                        actualizarBotones();
                    })
                    .setNegativeButton("NO", null)
                    .show();
        });
        //reproducir audio
        binding.btnEscuchaAudio.setOnClickListener(v -> {
            escucharAudio();
        });
        //parar audio
        binding.btnStop.setOnClickListener(v -> {
            pararAudio();
        });
        //descarga audio
        binding.btnDescargaAudio.setOnClickListener(v -> viewModel.descargarAudio());

        //cerrar teclado
        binding.frameLayout.setOnClickListener(v -> {
            //ocultar teclado
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            View vista = requireActivity().getCurrentFocus();
            if (v!=null)
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });


        //titulo
            if(((AppCompatActivity) getActivity()).getSupportActionBar()!=null) {
                if (viewModel.getModo() == viewModel.MODO_EDICION)
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Editar Nota");
                else
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Agregar Nota");
            }
    }

    private void escucharAudio() {
        String ruta = viewModel.getRutaAudio();
        if (!viewModel.existeArchivo()){
            toast("El archivo de sonido no se encuentra");
            return;
        }
        reproductor = new ReproductorImpl();
        reproductor.definirVistaParaMc(getContext(),binding.mediaControl,() -> {
            pararAudio();
        });
        try {
            reproductor.iniciar(new File(ruta));
        binding.btnQuitarAudio.setVisibility(View.INVISIBLE);
        binding.btnEscuchaAudio.setVisibility(View.INVISIBLE);
        binding.btnElegirAudio.setVisibility(View.INVISIBLE);
        binding.btnStop.setVisibility(View.VISIBLE);
        }catch (Exception ex){
            toast(ex.getMessage());
        }


    }

    private void pararAudio(){
        if (reproductor!=null) {
            reproductor.parar();
            binding.btnQuitarAudio.setVisibility(View.VISIBLE);
            binding.btnEscuchaAudio.setVisibility(View.VISIBLE);
            binding.btnElegirAudio.setVisibility(View.VISIBLE);
            binding.btnStop.setVisibility(View.INVISIBLE);
        }
    }

    private void manejarBotonAtras() {
        if (viewModel.isHaCambiado()) {

            new AlertDialog.Builder(getContext())
                    .setTitle("Cambios sin guardar")
                    .setMessage("¿Quieres descartar los cambios?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Descartar cambios", (dialog, which) -> {
                        if(reproductor!=null)
                            reproductor.parar();
                        NavHostFragment.findNavController(this).popBackStack();
                    })
                    .setNegativeButton("Seguir editando", null)
                    .show();
        }
        else {
            if(reproductor!=null)
                reproductor.parar();
            NavHostFragment.findNavController(this).popBackStack();
        }
    }



    private void seleccionarAudio() {

        Intent selectIntent = new Intent();
        //String mime ="audio/3gp|audio/AMR|audio/mpeg|audio/wav|audio/m4a";
        String mime ="audio/mpeg";
        selectIntent.setType(mime);
        selectIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent grabarIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        Intent aplicacionIntent = new Intent("josebailon.ensayos.grabador.GRABAR");

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, selectIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Selecionar o grabar audio");
        Intent[] intentArray = { aplicacionIntent, grabarIntent };
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        lanzadorIntentGrabacion.launch(chooser);
    }
    private void manejarSeleccionAudio(Intent data) {
        Uri uri = data.getData();
        viewModel.definirAudio(uri);
    }



    private void toast(String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) requireActivity()).getSupportActionBar() != null) {
            ((MainActivity) requireActivity()).getSupportActionBar().show();
        }
    }

    private void ocultarMenuAcciones() {
        getActivity().addMenuProvider( new MenuProvider(){
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {}
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}