package josebailon.ensayos.cliente.view.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.databinding.FragmentCrearEditarNotaBinding;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.view.adapter.NotasConAudioAdapter;
import josebailon.ensayos.cliente.view.adapter.UsuariosAdapter;
import josebailon.ensayos.cliente.viewmodel.CrearEditarNotaViewModel;
import josebailon.ensayos.cliente.viewmodel.VercanciondetalleViewModel;

public class CrearEditarNotaFragment extends Fragment {

    private FragmentCrearEditarNotaBinding binding;
    private CrearEditarNotaViewModel viewModel;


    private NotaEntity nota;
    private AudioEntity audio;

    private ActivityResultLauncher<Intent> lanzadorIntentGrabacion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lanzadorIntentGrabacion = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),resultado -> {
                    if(resultado.getResultCode()==RESULT_OK){
                        manejarResultadoAudio(resultado.getData());
                    }
                });
    }

    private void manejarResultadoAudio(Intent data) {

        Log.i("JJBO","Recibido el intent");
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



        //recoger nota
        viewModel.getNotaAndAudio().observe(getViewLifecycleOwner(), datos -> {
            binding.inputNombre.setText(datos.nota.getNombre());
            binding.inputTexto.setText(datos.nota.getTexto());
            //sin audio
            if (datos.audio == null || datos.audio.isBorrado()){
                binding.lbSinAudio.setVisibility(View.VISIBLE);
                binding.btnEscuchaAudio.setVisibility(View.INVISIBLE);
                binding.btnQuitarAudio.setVisibility(View.INVISIBLE);
            //con audio
            }else{
                binding.lbSinAudio.setVisibility(View.INVISIBLE);
                binding.btnEscuchaAudio.setVisibility(View.VISIBLE);
                binding.btnQuitarAudio.setVisibility(View.VISIBLE);
            }
            agregarListenersTexto();
            nota = datos.nota;
            audio = datos.audio;
        });

        return binding.getRoot();
    }

    private void agregarListenersTexto() {
        binding.inputNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {      }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setHaCambiado(true);
                Log.i("JJBO","ha cambiado");
            }
        });
        binding.inputTexto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {      }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setHaCambiado(true);
                nota.setNombre(binding.inputNombre.getText().toString());
                nota.setTexto(binding.inputTexto.getText().toString());
            }
        });
    }


    private void manejarBotonAtras() {
        if (viewModel.isHaCambiado()) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cambios sin guardar")
                    .setMessage("¿Quieres descartar los cambios?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Descartar cambios", (dialog, which) -> {
                        NavHostFragment.findNavController(this).popBackStack();
                    })
                    .setNegativeButton("Seguir editando", null)
                    .show();
        }
        else
            NavHostFragment.findNavController(this).popBackStack();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ocultarMenuAcciones();
        binding.btnGuardarNota.setOnClickListener(v -> {
            viewModel.guardarNota();
            NavHostFragment.findNavController(this).popBackStack();
        });
        binding.btnCancelarNota.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
        binding.btnElegirAudio.setOnClickListener(v -> {
            elegirAudio();
        });

        //titulo
        if (viewModel.getModo()== viewModel.MODO_EDICION)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Editar Nota");
        else
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Crear Nota");
    }

    private void elegirAudio() {


        Intent selectIntent = new Intent();
        String mime ="audio/3gp|audio/AMR|audio/mp3|audio/wav|audio/m4a";
        selectIntent.setType(mime);
        selectIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent grabarIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, selectIntent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Selecionar o grabar audio");

        Intent[] intentArray = { grabarIntent };
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        lanzadorIntentGrabacion.launch(chooser);
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