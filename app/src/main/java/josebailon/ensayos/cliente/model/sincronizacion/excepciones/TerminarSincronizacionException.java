package josebailon.ensayos.cliente.model.sincronizacion.excepciones;

/**
 * Excepcion de terminado de sincronizacion
 *
 * @author Jose Javier Bailon Ortiz
 */
public class TerminarSincronizacionException extends Exception{
    public TerminarSincronizacionException(String msg) {
        super(msg);
    }
}
