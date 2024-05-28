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
import josebailon.ensayos.cliente.databinding.FragmentLoginBinding;
import josebailon.ensayos.cliente.view.util.Validators;
import josebailon.ensayos.cliente.viewmodel.LoginViewModel;


/**
 * Control del fragment de login
 *
 * @author Jose Javier Bailon Ortiz
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogin.setOnClickListener(v -> {
            intentarLogin();
        });
        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        viewModel.getLoging().observe(getViewLifecycleOwner(),loging ->{
            binding.progressBar.setVisibility((loging)?View.VISIBLE:View.INVISIBLE);
            binding.btnLogin.setEnabled(!loging);
        });

        // Observar cambios en el estado
        viewModel.getResultado().observe(getViewLifecycleOwner(),integer ->{
            switch (integer){
                case LoginViewModel.LOGINOK:
                    // ir a grupos
                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_loginFragment_to_vergruposFragment);

                    break;
                case LoginViewModel.NO_INTERNET:
                    //mostrar toast de no hay internet
                    toast("No se puede conectar con el servidor");
                    break;
                case LoginViewModel.LOGINKO:
                    toast("Usuario/contraseña erroneas");
                    break;

            }
        } );

    }

    /**
     * Intenta hacer login validando las entradas previamente
     */
    private void intentarLogin() {
        String email = binding.inputEmail.getText().toString();
        String password= binding.inputPassword.getText().toString();
        boolean emailOk= Validators.isEmailValid(email);
        boolean passwordOk= Validators.isPasswordValid(password);
        if (emailOk && passwordOk)
            viewModel.login(email,password);
        else{
            if (!emailOk)
                toast("Email no válido");
            if (!passwordOk)
                toast("La contraseña debe tener entre 4 y 8 caracteres");
        }

    }


    /**
     * Toast de mensaje
     * @param msg
     */
    private void toast (String msg){
        Toast.makeText(this.getContext(),msg,Toast.LENGTH_LONG).show();
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