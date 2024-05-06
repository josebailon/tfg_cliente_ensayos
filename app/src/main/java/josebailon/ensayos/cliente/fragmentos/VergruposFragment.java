package josebailon.ensayos.cliente.fragmentos;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.MyItemRecyclerViewAdapter;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.data.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.databinding.DialogoCrearGrupoBinding;
import josebailon.ensayos.cliente.databinding.FragmentRegistroBinding;
import josebailon.ensayos.cliente.databinding.FragmentVergruposBinding;
import josebailon.ensayos.cliente.placeholder.PlaceholderContent;
import josebailon.ensayos.cliente.ui.adapter.GruposAdapter;
import josebailon.ensayos.cliente.util.Validators;
import josebailon.ensayos.cliente.viewmodel.RegistroViewModel;
import josebailon.ensayos.cliente.viewmodel.VergruposViewModel;

public class VergruposFragment extends Fragment {

    EditText dialogoInputNombre; // user input bar
    EditText dialogoInputDescripcion; // user input bar


    private FragmentVergruposBinding binding;
    private VergruposViewModel viewModel;

    private GruposAdapter adaptador;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentVergruposBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.verGruposRecycleView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new GruposAdapter(new ArrayList<GrupoEntity>(),this);
        recyclerView.setAdapter(adaptador);
        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(VergruposViewModel.class);
        viewModel.getGrupos().observe(getViewLifecycleOwner(), grupos -> adaptador.setData(grupos));

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAgregarGrupo.setOnClickListener(v -> mostrarDialogoCreacion());



    }


    private void mostrarDialogoCreacion() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogo_crear_grupo);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
            String nombre = ((EditText) (dialog.findViewById(R.id.inputNombre))).getText().toString();
            String descripcion = ((EditText) (dialog.findViewById(R.id.inputDescripcion))).getText().toString();

            if (TextUtils.isEmpty(nombre))
                toast("El nombre no puede estar vacío");
            else {
                //guardar GRUPO
                viewModel.crear(nombre,  descripcion);
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


        String uuid = id.toString();
        Bundle bundle = new Bundle();
        bundle.putString("idgrupo", uuid);
        NavHostFragment.findNavController(VergruposFragment.this)
                .navigate(R.id.action_vergruposFragment_to_vergrupodetalleFragment,bundle);


    }
}