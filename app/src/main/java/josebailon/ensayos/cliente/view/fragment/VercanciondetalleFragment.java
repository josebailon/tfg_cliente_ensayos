package josebailon.ensayos.cliente.view.fragment;

import android.os.Bundle;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.databinding.FragmentVercancionDetalleBinding;
import josebailon.ensayos.cliente.view.adapter.NotasConAudioAdapter;
import josebailon.ensayos.cliente.view.adapter.UsuariosAdapter;
import josebailon.ensayos.cliente.viewmodel.VercanciondetalleViewModel;

/**
 * Fragment para ver detalles de una cancion
 *
 * @author Jose Javier Bailon Ortiz
 */
public class VercanciondetalleFragment extends Fragment {



    private FragmentVercancionDetalleBinding binding;
    private VercanciondetalleViewModel viewModel;

    private NotasConAudioAdapter adaptadorNotas;
    private UsuariosAdapter adaptadorUsuarios;
    private List<NotaAndAudio> notasActuales=null;
    private List<UsuarioEntity> usuariosActuales =null;

    private UUID idcancion;
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
            if (datos==null){
                NavHostFragment.findNavController(this).popBackStack();
                return;
            }

            //refrescar cancion
            this.cancion=datos;
            binding.lbNombre.setText(datos.getNombre());
            binding.lbDescripcion.setText(datos.getDescripcion());
            binding.lbDuracion.setText(datos.getDuracion());
            viewModel.setIdcancion(datos.getId());
            });
        //recoger notas de la cancion
        viewModel.getNotasDeCancion(idcancion).observe(getViewLifecycleOwner(), datos->{
            if(datos==null) {
                NavHostFragment.findNavController(this).popBackStack();
                return;
            }
            notasActuales=datos.stream().filter(notaConAudio -> !notaConAudio.nota.isBorrado()).collect(Collectors.toList());
            adaptadorNotas.setData(notasActuales);
        });
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAgregarNota.setOnClickListener(v -> mostrarFragmentCrearEditarNota(null));
        //Eliminar menu de accinoes
        mostrarMenuAcciones();

    }


    /**
     * Lanzar el fragment para crear y editar notas de una cancion
     * @param uuid id de la nota. POner a null para crear o pasar la id para editar
     */
    private void mostrarFragmentCrearEditarNota(String uuid) {
        Bundle bundle = new Bundle();
        bundle.putString("idnota", uuid);
        bundle.putString("idcancion", idcancion.toString());
        NavHostFragment.findNavController(VercanciondetalleFragment.this)
                .navigate(R.id.action_vercanciondetalleFragment_to_crearEditarNota,bundle);
    }


    /**
     * Toast de mensaje
     * @param msg
     */
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

    /**
     * Muestra el menu contextual de una nota
     * @param position Posicion en la lista
     * @return True si se ha manejado
     */
    public boolean mostrarMenuNota(int position) {
        PopupMenu popupMenu = new PopupMenu(getContext() , binding.verNotasRecycleView.getChildAt(position).findViewById(R.id.nombre));
        popupMenu.inflate(R.menu.contextmenu);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId()==R.id.itemEditar){
                mostrarFragmentCrearEditarNota(notasActuales.get(position).nota.getId().toString());
            }
            else if (item.getItemId()==R.id.itemEliminar){
                borrarNota(notasActuales.get(position).nota);
            }
            return true;
        });
        popupMenu.show();
        return true;
    }


    /**
     * Borra una nota
     * @param nota La nota
     */
    private void borrarNota(NotaEntity nota) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminación")
                .setMessage("¿Quieres borrar la nota "+nota.getNombre()+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("SI",(dialog, which) -> {
                    viewModel.borrarNota(nota);
                })
                .setNegativeButton("NO", null)
                .show();
    }

    /**
     * Navega hacia el fragmento de vista de detalle de una nota
     * @param id La id de la nota
     */
    public void verNota(UUID id) {
        Bundle bundle = new Bundle();
        bundle.putString("idnota", id.toString());
        NavHostFragment.findNavController(VercanciondetalleFragment.this)
                .navigate(R.id.action_vercanciondetalleFragment_to_verNotaFragment,bundle);


    }

    /**
     * Muestra el menu de acciones
     */
    private void mostrarMenuAcciones() {
        getActivity().addMenuProvider( new MenuProvider(){

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}