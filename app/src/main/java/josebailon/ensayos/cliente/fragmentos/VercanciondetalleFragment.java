package josebailon.ensayos.cliente.fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.entity.NotaEntity;
import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.data.database.relaciones.NotaAndAudio;
import josebailon.ensayos.cliente.databinding.FragmentVercancionDetalleBinding;
import josebailon.ensayos.cliente.ui.adapter.NotasConAudioAdapter;
import josebailon.ensayos.cliente.ui.adapter.UsuariosAdapter;
import josebailon.ensayos.cliente.viewmodel.VercanciondetalleViewModel;

public class VercanciondetalleFragment extends Fragment {



    private FragmentVercancionDetalleBinding binding;
    private VercanciondetalleViewModel viewModel;

    private NotasConAudioAdapter adaptadorNotas;
    private UsuariosAdapter adaptadorUsuarios;
    List<NotaAndAudio> notasActuales=null;
    List<UsuarioEntity> usuariosActuales =null;

    UUID idcancion;
    private CancionEntity cancion;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentVercancionDetalleBinding.inflate(inflater, container, false);
        //recoger id cancion
        idcancion = UUID.fromString(getArguments().getString("idcancion"));

        //recycler de notas
        RecyclerView notasRecyclerView = binding.verNotasRecycleView;
        notasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorNotas = new NotasConAudioAdapter(new ArrayList<NotaAndAudio>(),this);
        notasRecyclerView.setAdapter(adaptadorNotas);



        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(VercanciondetalleViewModel.class);
        //escuchar mensajes
        viewModel.getMensaje().observe(getViewLifecycleOwner(),mensaje -> toast(mensaje.toString()));

        //recoger cancion
        viewModel.getCancion(idcancion).observe(getViewLifecycleOwner(), datos ->{
            //refrescar cancion
            this.cancion=datos;
            binding.lbNombre.setText(datos.getNombre());
            binding.lbDescripcion.setText(datos.getDescripcion());
            binding.lbDuracion.setText(datos.getDuracion());
            viewModel.setIdcancion(datos.getId());
            }
        );
        //recoger nota cancion
        viewModel.getNotasDeCancion(idcancion).observe(getViewLifecycleOwner(), datos->{
            notasActuales=datos.stream().filter(notaConAudio -> !notaConAudio.nota.isBorrado()).collect(Collectors.toList());
            adaptadorNotas.setData(notasActuales);
        });
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAgregarNota.setOnClickListener(v -> mostrarFragmentCrearNota());



        getActivity().addMenuProvider( new MenuProvider(){
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }


    private void mostrarFragmentCrearNota() {
//        Dialog dialog = new Dialog(getContext());
//        dialog.setContentView(R.layout.dialogo_crear_cancion);
//        dialog.show();
//        Window window = dialog.getWindow();
//        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
//            String nombre = ((EditText) (dialog.findViewById(R.id.inputEmail))).getText().toString();
//            String descripcion = ((EditText) (dialog.findViewById(R.id.inputDescripcion))).getText().toString();
//            String duracion = ((EditText) (dialog.findViewById(R.id.inputDuracion))).getText().toString();
//
//            if (TextUtils.isEmpty(nombre))
//                toast("El nombre no puede estar vacío");
//            else {
//                //guardar Cancion
//                viewModel.crearCancion(nombre,  descripcion, duracion, idcancion);
//                dialog.dismiss();
//            }
//        });
//        ((Button) (dialog.findViewById(R.id.btnCancelar))).setOnClickListener(v -> dialog.dismiss());

    }

    private void mostrarDialogoEdicionNota(NotaEntity nota) {
//        Dialog dialog = new Dialog(getContext());
//        dialog.setContentView(R.layout.dialogo_crear_cancion);
//        ((EditText)dialog.findViewById(R.id.inputEmail)).setText(cancion.getNombre());
//        ((EditText)dialog.findViewById(R.id.inputDescripcion)).setText(cancion.getDescripcion());
//        ((EditText)dialog.findViewById(R.id.inputDuracion)).setText(cancion.getDuracion());
//        dialog.show();
//        Window window = dialog.getWindow();
//        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
//            String nombre = ((EditText) (dialog.findViewById(R.id.inputEmail))).getText().toString();
//            String descripcion = ((EditText) (dialog.findViewById(R.id.inputDescripcion))).getText().toString();
//            String duracion = ((EditText) (dialog.findViewById(R.id.inputDuracion))).getText().toString();
//
//            if (TextUtils.isEmpty(nombre))
//                toast("El nombre no puede estar vacío");
//            else {
//                //guardar CANCION
//                //viewModel.actualizarCancion(cancion,nombre,descripcion,duracion);
//                dialog.dismiss();
//            }
//        });
//        ((Button) (dialog.findViewById(R.id.btnCancelar))).setOnClickListener(v -> dialog.dismiss());
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

    public boolean mostrarMenuNota(int position) {
        PopupMenu popupMenu = new PopupMenu(getContext() , binding.verNotasRecycleView.getChildAt(position).findViewById(R.id.nombre));
        // add the menu
        popupMenu.inflate(R.menu.contextmenu);
        // implement on menu item click Listener
        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId()==R.id.itemEditar){
                mostrarDialogoEdicionNota(notasActuales.get(position).nota);
            }
            else if (item.getItemId()==R.id.itemEliminar){
                borrarNota(notasActuales.get(position).nota);
            }
            return true;
        });
        popupMenu.show();
        return true;
    }
 

    private void borrarNota(NotaEntity nota) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminación")
                .setMessage("¿Quieres borrar la nota "+nota.getNombre()+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("SÍ",(dialog, which) -> {
                    viewModel.borrarNota(nota);
                })
                .setNegativeButton("NO", null)
                .show();
    }

    public void verNota(UUID id) {
        Log.i("JJBO", id.toString());
    }
}