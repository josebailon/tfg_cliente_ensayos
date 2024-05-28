package josebailon.ensayos.cliente.model.grabacion;

/**
 * Interfaz para el callback de cuando se termina de reproducir un medio
 *
 * @author Jose Javier Bailon Ortiz
 */
public interface OnMedioTerminado{

    /**
     * Se ejecuta al terminarse de reproducir un medio
     */
    public void terminado();
}
