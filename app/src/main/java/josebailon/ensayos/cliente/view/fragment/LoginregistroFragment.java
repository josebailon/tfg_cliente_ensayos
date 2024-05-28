package josebailon.ensayos.cliente.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.databinding.FragmentLoginregistroBinding;


/**
 * Fragment que muestra un selector de login o registro
 *
 * @author Jose Javier Bailon Ortiz
 */
public class LoginregistroFragment extends Fragment {

    private FragmentLoginregistroBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginregistroBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogin.setOnClickListener(v ->
                NavHostFragment.findNavController(LoginregistroFragment.this)
                        .navigate(R.id.action_LoginRegistroFragment_to_LoginFragment)
        );
        binding.btnRegistro.setOnClickListener(v ->
                NavHostFragment.findNavController(LoginregistroFragment.this)
                        .navigate(R.id.action_LoginregistroFragment_to_RegistroFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}