package josebailon.ensayos.cliente.fragmentos;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.databinding.FragmentVergrupoDetalleBinding;
import josebailon.ensayos.cliente.ui.adapter.CancionesAdapter;
import josebailon.ensayos.cliente.ui.adapter.UsuariosAdapter;
import josebailon.ensayos.cliente.viewmodel.VergrupodetalleViewModel;

public class VergrupodetalleFragment extends Fragment {



    private FragmentVergrupoDetalleBinding binding;
    private VergrupodetalleViewModel viewModel;

    private CancionesAdapter adaptadorCanciones;
    private UsuariosAdapter adaptadorUsuarios;
    List<CancionEntity> cancionesActuales=null;
    List<UsuarioEntity> usuaariosActuales=null;
    private UUID idgrupo;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentVergrupoDetalleBinding.inflate(inflater, container, false);
        //recoger id grupo
        idgrupo = UUID.fromString(getArguments().getString("idgrupo"));

        //recycler de canciones
        RecyclerView cancionesRecyclerView = binding.verCancionesRecycleView;
        cancionesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorCanciones = new CancionesAdapter(new ArrayList<CancionEntity>(),this);
        cancionesRecyclerView.setAdapter(adaptadorCanciones);
        //recycler de usuarios
        RecyclerView usuariosRecyclerView = binding.verUsuariosRecycleView;
        usuariosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorUsuarios = new UsuariosAdapter(new ArrayList<UsuarioEntity>(),this);
        usuariosRecyclerView.setAdapter(adaptadorUsuarios);


        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(VergrupodetalleViewModel.class);
        //escuchar mensajes
        viewModel.getMensaje().observe(getViewLifecycleOwner(),mensaje -> toast(mensaje.toString()));

        //recoger datos
        viewModel.getGrupo(idgrupo).observe(getViewLifecycleOwner(), grupo ->{
            //refrescar nombre
            binding.lbNombre.setText(grupo.grupo.getNombre());
            binding.lbDescripcion.setText(grupo.grupo.getDescripcion());
            viewModel.setGrupoId(grupo.grupo.getId());
            //refrescar canciones
            cancionesActuales=grupo.getCancionesOrdenadas().stream().filter(cancionEntity -> !cancionEntity.isBorrado()).collect(Collectors.toList());
            adaptadorCanciones.setData(cancionesActuales);
            //refrescar usuarios
            adaptadorUsuarios.setData(grupo.getUsuariosOrdenados());
            usuaariosActuales=grupo.getUsuariosOrdenados();
            }
        );

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAgregarCancion.setOnClickListener(v -> mostrarDialogoCrearCancion());
        binding.btnAgregarUsuario.setOnClickListener(v -> mostrarDialogoAgregarUsuario());



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

    private void mostrarDialogoAgregarUsuario() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogo_agregar_usuario);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
            String email = ((EditText) (dialog.findViewById(R.id.inputEmail))).getText().toString();

            if (TextUtils.isEmpty(email))
                toast("El email no puede estar vacío");
            else {
                //guardar Usuario
                viewModel.agregarUsuario(email);
                dialog.dismiss();
            }
        });
        ((Button) (dialog.findViewById(R.id.btnCancelar))).setOnClickListener(v -> dialog.dismiss());
    }


    private void mostrarDialogoCrearCancion() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogo_crear_cancion);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
            String nombre = ((EditText) (dialog.findViewById(R.id.inputEmail))).getText().toString();
            String descripcion = ((EditText) (dialog.findViewById(R.id.inputDescripcion))).getText().toString();
            String duracion = ((EditText) (dialog.findViewById(R.id.inputDuracion))).getText().toString();

            if (TextUtils.isEmpty(nombre))
                toast("El nombre no puede estar vacío");
            else {
                //guardar Cancion
                viewModel.crearCancion(nombre,  descripcion, duracion, idgrupo);
                dialog.dismiss();
            }
        });
        ((Button) (dialog.findViewById(R.id.btnCancelar))).setOnClickListener(v -> dialog.dismiss());

    }

    private void mostrarDialogoEdicionCancion(CancionEntity cancion) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogo_crear_cancion);
        ((EditText)dialog.findViewById(R.id.inputEmail)).setText(cancion.getNombre());
        ((EditText)dialog.findViewById(R.id.inputDescripcion)).setText(cancion.getDescripcion());
        ((EditText)dialog.findViewById(R.id.inputDuracion)).setText(cancion.getDuracion());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
            String nombre = ((EditText) (dialog.findViewById(R.id.inputEmail))).getText().toString();
            String descripcion = ((EditText) (dialog.findViewById(R.id.inputDescripcion))).getText().toString();
            String duracion = ((EditText) (dialog.findViewById(R.id.inputDuracion))).getText().toString();

            if (TextUtils.isEmpty(nombre))
                toast("El nombre no puede estar vacío");
            else {
                //guardar CANCION
                viewModel.actualizarCancion(cancion,nombre,descripcion,duracion);
                dialog.dismiss();
            }
        });
        ((Button) (dialog.findViewById(R.id.btnCancelar))).setOnClickListener(v -> dialog.dismiss());
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

    public boolean mostrarMenuCancion(int position) {
        PopupMenu popupMenu = new PopupMenu(getContext() , binding.verCancionesRecycleView.getChildAt(position).findViewById(R.id.texto));
        // add the menu
        popupMenu.inflate(R.menu.contextmenu);
        // implement on menu item click Listener
        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId()==R.id.itemEditar){
                mostrarDialogoEdicionCancion(cancionesActuales.get(position));
            }
            else if (item.getItemId()==R.id.itemEliminar){
                borrar(cancionesActuales.get(position));
            }
            return true;
        });
        popupMenu.show();
        return true;
    }

    private void borrar(CancionEntity cancionEntity) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminación")
                .setMessage("¿Quieres borrar la cancion "+cancionEntity.getNombre()+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("SÍ",(dialog, which) -> {
                    viewModel.borrarCancion(cancionEntity);
                })
                .setNegativeButton("NO", null)
                .show();
    }

    public void verCancion(UUID id) {
        Log.i("JJBO", id.toString());
    }
}