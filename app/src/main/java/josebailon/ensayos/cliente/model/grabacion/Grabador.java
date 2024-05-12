package josebailon.ensayos.cliente.model.grabacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface Grabador {
    public void iniciar(File destino) throws IOException;
    public void parar();
}
