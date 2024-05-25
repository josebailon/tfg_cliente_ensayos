package josebailon.ensayos.cliente.model.archivos;

import android.net.Uri;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import josebailon.ensayos.cliente.App;

public class ArchivosRepo {
    public final String CARPETA_AUDIO="audio";
    private final String CARPETA_TEMP="tmp";
    private static ArchivosRepo instancia;
    private ArchivosRepo(){
        inicializarCarpetas();
    }

    private void inicializarCarpetas() {
        File directorioApp = App.getContext().getFilesDir();
        File directorioAudio=new File(directorioApp,CARPETA_AUDIO);
        File directorioTmp = new File(directorioApp,CARPETA_TEMP);
        if (!directorioAudio.exists())
            directorioAudio.mkdir();
        if (!directorioTmp.exists())
            directorioTmp.mkdir();
    }

    public static ArchivosRepo getInstance() {
        if (instancia == null) {
            synchronized (ArchivosRepo.class) {
                if (instancia == null) {
                    instancia = new ArchivosRepo();
                }
            }
        }
        return instancia;
    }

    public String guardarUri(Uri uri) {
        try {
            InputStream inputStream = App.getContext().getContentResolver().openInputStream(uri);
            String extension = Utiles.getExtensionDeUri(uri);
            File directorio = App.getContext().getFilesDir();
            String nuevoNombre = UUID.randomUUID().toString() + "." + extension;
            File destino = new File(directorio, CARPETA_AUDIO+"/"+nuevoNombre);

            FileOutputStream outputStream = new FileOutputStream(destino);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();
            return nuevoNombre;
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public String getPath(String archivo) {
        File directorio = App.getContext().getFilesDir();
        File destino = new File(directorio, CARPETA_AUDIO+"/"+archivo);
        if (destino.exists())
            return destino.getAbsolutePath();
        else
            return null;
    }


    public void guardarBytes(InputStream inputStream, String nombre) throws IOException {
        File directorio = App.getContext().getFilesDir();
        File destino = new File(directorio,CARPETA_AUDIO+"/"+nombre);

        OutputStream output = null;
        try {
            output = new FileOutputStream(destino);

            byte[] buffer = new byte[1024]; // or other buffer size
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (output != null)
                        output.close();
            }catch (Exception e){}
        }
    }

    public File getTempPath(String s) {
        File directorio = App.getContext().getFilesDir();
        File destino = new File(directorio,"otro/"+s);
        return destino;
    }

    public File getTempPathNuevo() {
        return new File(App.getContext().getFilesDir(),CARPETA_TEMP+"/"+UUID.randomUUID()+".mp3");
    }

    public Uri generarUri(String archivo) {

        File file = new File(App.getContext().getFilesDir(),CARPETA_AUDIO+"/"+archivo);
        if (!file.exists())
            return null;
        return  FileProvider.getUriForFile(App.getContext(), "josebailon.ensayos.cliente.provider", file);
    }

    public void renombrar(String origen, String destino) {
        if (existeAudio(origen)) {
            File orig = new File(App.getContext().getFilesDir(), CARPETA_AUDIO + "/" + origen);
            File dest = new File(App.getContext().getFilesDir(), CARPETA_AUDIO + "/" + destino);
            orig.renameTo(dest);
        }
    }






    public void guardarUri(Uri uri, ArchivosRepo.CallbackGuardado callback){

        String resultado = this.guardarUri(uri);

        if (resultado==null){
            callback.fracaso("No se pudo guardar el audio");
        }else {
            callback.exito(resultado);
        }
    }

    public String getAudio(String archivo) {
        return this.getPath(archivo);
    }

    public boolean existeAudio(String archivo) {
        if (TextUtils.isEmpty(archivo))
            return false;
        File destino = new File(App.getContext().getFilesDir(), CARPETA_AUDIO+"/"+archivo);
        return destino.exists();

    }



    public interface CallbackGuardado{
        public void exito(String nombre);
        public void fracaso(String mensaje);
    }

    public List<String> getAllAudioFiles(){
        File folder =   new File(App.getContext().getFilesDir(),CARPETA_AUDIO);
        List<File> listaArchivos = Arrays.asList(folder.listFiles());

        return listaArchivos.stream()
                .filter(file -> file.isFile() && !file.isDirectory())
                .map(file -> file.getName())
                .collect(Collectors.toList());
    }

    public void borrarAudio(String archivo){
        if (TextUtils.isEmpty(archivo))
            return;
        File directorio = App.getContext().getFilesDir();
        File destino = new File(directorio, CARPETA_AUDIO+"/"+archivo);
        if (destino.isFile()&&!destino.isDirectory())
            destino.delete();
    }
}
