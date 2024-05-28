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


/**
 * Repositorio de acciones sobre archivos del almacenamiento privado
 *
 * @author Jose Javier Bailon Ortiz
 */
public class ArchivosRepo {
    /**
     * Directorio donde se almacenan los audios
     */
    public final String CARPETA_AUDIO="audio";

    /**
     * Directorio donde se almacenan los temporales
     */
    private final String CARPETA_TEMP="tmp";

    /**
     * Instancia para singleton
     */
    private static ArchivosRepo instancia;

    /**
     * Constructor privado para singleton
     */
    private ArchivosRepo(){
        inicializarDirectorios();
    }


    /**
     * Obtener una instancia singleton
     * @return La instancia
     */
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

    /**
     * Inicializa los directorios creandolas si no existen
     */
    private void inicializarDirectorios() {
        File directorioApp = App.getContext().getFilesDir();
        File directorioAudio=new File(directorioApp,CARPETA_AUDIO);
        File directorioTmp = new File(directorioApp,CARPETA_TEMP);
        if (!directorioAudio.exists())
            directorioAudio.mkdir();
        if (!directorioTmp.exists())
            directorioTmp.mkdir();
    }


    /**
     * Guarda un archivo especificado por una uri al directorio de audio
     * @param uri La urio origen
     * @return El nombre asignado en el almacenamiento privado
     */
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

    /**
     * Devuelve la ruta hacia un archivo de audio dado su nombre
     * @param archivo El nombre del archivo
     * @return La ruta
     */



    /**
     * Guarda un flujo de bytes como archivo en la carpeta de audio
     * @param inputStream El stream de datos
     * @param nombre El nombre del archivo
     * @throws IOException
     */
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


    /**
     * Devuelve una ruta al temporal generada aleatoriamente
     * @return La ruta creada
     */
    public File getTempPathNuevo() {
        return new File(App.getContext().getFilesDir(),CARPETA_TEMP+"/"+UUID.randomUUID()+".mp3");
    }

    /**
     * Genera una Uri para un archivo de la carpeta audio
     * @param archivo El nombre de archivo
     * @return La Uri generada
     */
    public Uri generarUri(String archivo) {

        File file = new File(App.getContext().getFilesDir(),CARPETA_AUDIO+"/"+archivo);
        if (!file.exists())
            return null;
        return  FileProvider.getUriForFile(App.getContext(), "josebailon.ensayos.cliente.provider", file);
    }


    /**
     * Renombra un archivo de la carpeta audio
     * @param origen El nombre original
     * @param destino El nuevo nombre
     */
    public void renombrar(String origen, String destino) {
        if (existeAudio(origen)) {
            File orig = new File(App.getContext().getFilesDir(), CARPETA_AUDIO + "/" + origen);
            File dest = new File(App.getContext().getFilesDir(), CARPETA_AUDIO + "/" + destino);
            orig.renameTo(dest);
        }
    }

    /**
     * Guarda una uri llama a un calback con el resultado
     * @param uri La uri a guardar
     * @param callback El callback a activar
     */
    public void guardarUri(Uri uri, ArchivosRepo.CallbackGuardado callback){

        String resultado = this.guardarUri(uri);

        if (resultado==null){
            callback.fracaso("No se pudo guardar el audio");
        }else {
            callback.exito(resultado);
        }
    }

    /**
     * Devuelve la ruta a un archivo de audio
     * @param archivo El nombre del archivo
     * @return la ruta
     */
    public String getAudio(String archivo) {
        File directorio = App.getContext().getFilesDir();
        File destino = new File(directorio, CARPETA_AUDIO+"/"+archivo);
        if (destino.exists())
            return destino.getAbsolutePath();
        else
            return null;
    }

    /**
     * Devuelve si un audio existe o no
     * @param archivo El nombre del archivo
     * @return True si existe, false si no existe
     */
    public boolean existeAudio(String archivo) {
        if (TextUtils.isEmpty(archivo))
            return false;
        File destino = new File(App.getContext().getFilesDir(), CARPETA_AUDIO+"/"+archivo);
        return destino.exists();

    }




    /**
     * Devuelve una lista con los archivos de audio en el almacenamiento
     * @return La lista
     */
    public List<String> getAllAudioFiles(){
        File folder =   new File(App.getContext().getFilesDir(),CARPETA_AUDIO);
        List<File> listaArchivos = Arrays.asList(folder.listFiles());

        return listaArchivos.stream()
                .filter(file -> file.isFile() && !file.isDirectory())
                .map(file -> file.getName())
                .collect(Collectors.toList());
    }

    /**
     * Elimina un audio dado su nombre
     * @param archivo El nombre
     */
    public void borrarAudio(String archivo){
        if (TextUtils.isEmpty(archivo))
            return;
        File directorio = App.getContext().getFilesDir();
        File destino = new File(directorio, CARPETA_AUDIO+"/"+archivo);
        if (destino.isFile()&&!destino.isDirectory())
            destino.delete();
    }


    /**
     * Intefaz para callback de guardado
     */
    public interface CallbackGuardado{
        public void exito(String nombre);
        public void fracaso(String mensaje);
    }
}
