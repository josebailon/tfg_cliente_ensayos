package josebailon.ensayos.cliente.view.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.databinding.FragmentSincronizadoBinding;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.relation.GrupoAndUsuariosAndCanciones;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;
import josebailon.ensayos.cliente.view.dialogos.DialogoConflictoCancion;
import josebailon.ensayos.cliente.view.dialogos.DialogoConflictoGrupo;
import josebailon.ensayos.cliente.viewmodel.SincronizadorViewModel;

public class SincronizadoFragment extends Fragment {

    private FragmentSincronizadoBinding binding;
    private SincronizadorViewModel viewModel;

    private Handler handler;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSincronizadoBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SincronizadorViewModel.class);
        return binding.getRoot();

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler(Looper.getMainLooper());

        viewModel.getMensaje().observe(getViewLifecycleOwner(), s -> {
            toast(s);
        });

        viewModel.getMensajeEstado().observe(getViewLifecycleOwner(), s -> {
            binding.lbEstado.setText(s);
        });
        viewModel.getSincronizando().observe(getViewLifecycleOwner(), sincronizando -> {
            if (sincronizando) {
                binding.lbEstado.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.lbEstado.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        viewModel.getConflicto().observe(getViewLifecycleOwner(), conflicto -> {
            switch (conflicto.getTipo()) {
                case Conflicto.T_GRUPO:
                    new DialogoConflictoGrupo(getContext(), (Conflicto<GrupoAndUsuariosAndCanciones, GrupoApiEnt>) conflicto).show();
                    break;
                case Conflicto.T_CANCION:
                    new DialogoConflictoCancion(getContext(), (Conflicto<CancionEntity, CancionApiEnt>) conflicto).show();
                    break;
            }
        });
        viewModel.iniciar();

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


}