package josebailon.ensayos.cliente.model.sincronizacion.excepciones;

/**
 * Excepcion de credenciales erroneas
 *
 * @author Jose Javier Bailon Ortiz
 */
public class CredencialesErroneasException extends Exception{
    public CredencialesErroneasException(String msg) {
        super(msg);
    }
}
