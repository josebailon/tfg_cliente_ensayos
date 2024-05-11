package josebailon.ensayos.cliente.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.databinding.FragmentRegistroBinding;
import josebailon.ensayos.cliente.view.util.Validators;
import josebailon.ensayos.cliente.viewmodel.RegistroViewModel;


public class RegistroFragment extends Fragment {

    private FragmentRegistroBinding binding;
    private RegistroViewModel viewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentRegistroBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogin.setOnClickListener(v -> {
            intentarRegistro();
        });
        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(RegistroViewModel.class);

        viewModel.getRegistrando().observe(getViewLifecycleOwner(),registrando ->{
            binding.progressBar.setVisibility((registrando)?View.VISIBLE:View.INVISIBLE);
            binding.btnLogin.setEnabled(!registrando);
        });

        // Observar cambios en el estado
        viewModel.getResultado().observe(getViewLifecycleOwner(),integer ->{
            switch (integer){
                case RegistroViewModel.REGISTROOK:
                    // ir a grupos
                    NavHostFragment.findNavController(RegistroFragment.this)
                            .navigate(R.id.action_registroFragment_to_vergruposFragment);
                    break;
                case RegistroViewModel.NO_INTERNET:
                    //mostrar toast de no hay internet
                    toast("No se puede conectar con el servidor");
                    break;
                case RegistroViewModel.REGISTROKO:
                    toast("El email ya está ocupado");
                    break;

            }
        } );

    }

    private void intentarRegistro() {
        String email = binding.inputEmail.getText().toString();
        String password= binding.inputPassword.getText().toString();
        String password_rep= binding.inputPasswordRepetido.getText().toString();
        boolean emailOk= Validators.isEmailValid(email);
        boolean passwordOk= Validators.isPasswordValid(password);
        boolean passwordRepOk= Validators.isPasswordValid(password_rep)&& password.equals(password_rep);
        if (emailOk && passwordOk && passwordRepOk)
            viewModel.registro(email,password);
        else{
            if (!emailOk)
                toast("Email no válido");
            if (!passwordOk)
                toast("La contraseña debe tener entre 4 y 8 caracteres");
            if (!passwordRepOk)
                toast("La repetición de la contraseña no es correcta");
        }

    }


    private void toast (String msg){
        Toast.makeText(this.getContext(),msg,Toast.LENGTH_SHORT).show();
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