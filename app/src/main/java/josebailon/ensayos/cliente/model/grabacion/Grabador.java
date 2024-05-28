package josebailon.ensayos.cliente.model.grabacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Interfaz de recorder de audio
 *
 * @author Jose Javier Bailon Ortiz
 */
public interface Grabador {

    /**
     * Iniciar la grabacion
     * @param destino Archivo destino para almacenar la grabacion
     * @throws IOException Cuando se produce un error guardando u obteniendo el audio
     */
    public void iniciar(File destino) throws IOException;

    /**
     * Parar la grabacion
     */
    public void parar();
}
