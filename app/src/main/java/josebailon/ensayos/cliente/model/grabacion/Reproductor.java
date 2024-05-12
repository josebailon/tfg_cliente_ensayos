package josebailon.ensayos.cliente.model.grabacion;

import java.io.File;

public interface Reproductor {
    public void iniciar (File archivo);
    public void parar();
}
