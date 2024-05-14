package josebailon.ensayos.cliente.view.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.databinding.FragmentInitBinding;
import josebailon.ensayos.cliente.databinding.FragmentSincronizadoBinding;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.viewmodel.InitViewModel;
import josebailon.ensayos.cliente.viewmodel.SincronizadoViewModel;

public class SincronizadoFragment extends Fragment {

    private FragmentSincronizadoBinding binding;
    private SincronizadoViewModel viewModel;

    private Handler handler;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSincronizadoBinding.inflate(inflater, container, false);
        viewModel= new ViewModelProvider(this).get(SincronizadoViewModel.class);
        return binding.getRoot();

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler= new Handler(Looper.getMainLooper());

        binding.button.setOnClickListener(v -> {
            viewModel.iniciar();

        });

        viewModel._s.observe(getViewLifecycleOwner(),semaphore -> {
            Log.i("JJBO"," inicio de observe");
            binding.button.setEnabled(false);
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Eliminación")
                                .setMessage("Estas pausado por el semaforo ?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("SÍ",(dialog, which) -> {
                                    semaphore.release();

                                })
                                .setNegativeButton("NO", (dialog, which) -> {
                                    semaphore.release();
                                })
                                .show();
                    }
            );
//        // Inicialización del ViewModel
//        InitViewModel viewModel = new ViewModelProvider(this).get(InitViewModel.class);
//        // Observar cambios en el estado
//        viewModel.comprobar().observe(getViewLifecycleOwner(), integer -> {
//            switch (integer) {
//                case InitViewModel.LOGINOK:
//                    // ir a grupos
//                    NavHostFragment.findNavController(SincronizadoFragment.this)
//                            .navigate(R.id.action_InitFragment_to_vergruposFragment);
//                    break;
//                case InitViewModel.NO_INTERNET:
//                    //mostrar toast de no internet e ir a grupos
//                    Toast.makeText(getContext(), "No se puede conectar con el servidor", Toast.LENGTH_SHORT).show();
//                    if (viewModel.usuarioInicializado()) {
//                        NavHostFragment.findNavController(SincronizadoFragment.this)
//                                .navigate(R.id.action_InitFragment_to_vergruposFragment);
//                        Toast.makeText(getContext(), "Trabajando en modo sin conexión", Toast.LENGTH_SHORT).show();
//                    } else
//                        NavHostFragment.findNavController(SincronizadoFragment.this)
//                                .navigate(R.id.action_InitFragment_to_LoginRegistroFragment);
//                    break;
//                case InitViewModel.NEEDLOGIN:
//                    //ir a login
//                    NavHostFragment.findNavController(SincronizadoFragment.this)
//                            .navigate(R.id.action_InitFragment_to_LoginRegistroFragment);
//                    break;
//
//            }
//        });

//        viewModel.grupos.observe(getViewLifecycleOwner(), grupoEntities -> {
//            for (GrupoEntity g : grupoEntities
//            ) {
//                Log.i("JJBO", g.getNombre());
//                Log.i("JJBO", "" + g.isBorrado());
//            }
//        });
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
            ((MainActivity) requireActivity()).getSupportActionBar().hide();
        }
    }


}