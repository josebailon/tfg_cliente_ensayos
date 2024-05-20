package josebailon.ensayos.cliente.model.archivos.service;

import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.archivos.repository.ArchivosRepo;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.network.model.LoginRequest;
import josebailon.ensayos.cliente.model.network.model.LoginResponse;
import josebailon.ensayos.cliente.model.network.model.UsuarioResponse;
import josebailon.ensayos.cliente.model.network.repository.AuthApiRepo;
import retrofit2.Response;

public class ArchivosServicio {
    ArchivosRepo repo = ArchivosRepo.getInstance();
    public void guardarUri(Uri uri,CallbackGuardado callback){

            String resultado = repo.guardarUri(uri);

            if (resultado==null){
                callback.fracaso("No se pudo guardar el audio");
            }else {
                callback.exito(resultado);
            }
    }

    public String getAudio(String archivo) {
        return repo.getPath(archivo);
    }

    public boolean existeAudio(String archivo) {
        if (archivo==null)
            return false;
        else
            return repo.existeArchivo(archivo);
    }

    public void guardarBytes(InputStream byteStream,String nombre) throws IOException {
        repo.guardarBytes(byteStream, nombre);

    }

        public Uri generarUri(String archivo){
            return repo.getUri( archivo);
        }

    public void renombrar(String origen, String destino) {
        if (existeAudio(origen))
            repo.renombrar(origen,destino);
    }

    public interface CallbackGuardado{
            public void exito(String nombre);
            public void fracaso(String mensaje);
        }

}
