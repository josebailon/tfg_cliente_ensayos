package josebailon.ensayos.cliente;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import josebailon.ensayos.cliente.databinding.ActivityMainBinding;

import android.util.Log;
import android.view.MenuItem;

/**
 * Actividad principal. Contiene un fragment un host fragment donde se van cargando las diferentes
 * vistas de la apliacion.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Configuracion de barra superior
     */
    private AppBarConfiguration appBarConfiguration;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        appBarConfiguration=new AppBarConfiguration.Builder(R.id.InitFragment, R.id.LoginregistroFragment, R.id.vergruposFragment, R.id.sincronizadoFragment).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }





    /**
     * Manejo de items de menu y flecha de retorno
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();
        //derivar flecha de retorno a callback de fragment
        if (id== android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        if (id == R.id.action_sincronizar) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.sincronizadoFragment);
            return true;
        }
        int actual=Navigation.findNavController(this,R.id.nav_host_fragment_content_main).getCurrentDestination().getId();
        if (actual==R.id.verNotaFragment) {
            return false;
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


}