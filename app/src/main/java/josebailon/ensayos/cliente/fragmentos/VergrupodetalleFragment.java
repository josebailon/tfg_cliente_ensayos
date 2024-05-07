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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.UUID;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.data.database.entity.CancionEntity;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.databinding.FragmentVergrupoDetalleBinding;
import josebailon.ensayos.cliente.databinding.FragmentVergruposBinding;
import josebailon.ensayos.cliente.ui.adapter.CancionesAdapter;
import josebailon.ensayos.cliente.ui.adapter.GruposAdapter;
import josebailon.ensayos.cliente.viewmodel.VergrupodetalleViewModel;
import josebailon.ensayos.cliente.viewmodel.VergruposViewModel;

public class VergrupodetalleFragment extends Fragment {



    private FragmentVergrupoDetalleBinding binding;
    private VergrupodetalleViewModel viewModel;

    private CancionesAdapter adaptadorCanciones;

    private UUID idgrupo;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentVergrupoDetalleBinding.inflate(inflater, container, false);
        //recoger id grupo
        idgrupo = UUID.fromString(getArguments().getString("idgrupo"));

        RecyclerView cancionesRecyclerView = binding.verCancionesRecycleView;
        cancionesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorCanciones = new CancionesAdapter(new ArrayList<CancionEntity>(),this);
        cancionesRecyclerView.setAdapter(adaptadorCanciones);
        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(VergrupodetalleViewModel.class);

        //recoger datos
        viewModel.getGrupo(idgrupo).observe(getViewLifecycleOwner(), grupo ->{
            //refrescar nombre
            binding.lbNombre.setText(grupo.grupo.getNombre());
            binding.lbDescripcion.setText(grupo.grupo.getDescripcion());
            //refrescar canciones
            adaptadorCanciones.setData(grupo.getCancionesOrdenadas());
            }
        );

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAgregarCancion.setOnClickListener(v -> mostrarDialogoCrearCancion());




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


    private void mostrarDialogoCrearCancion() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogo_crear_cancion);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
            String nombre = ((EditText) (dialog.findViewById(R.id.inputNombre))).getText().toString();
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

    public void verGrupo(UUID id) {
        Log.i("JJBO", id.toString());
    }

    public void verCancion(UUID id) {
        Log.i("JJBO", id.toString());
    }
}