package josebailon.ensayos.cliente.model.sincronizacion;

import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;


/**
 * Interfaz de respuesta de eventos del sincronizador
 *
 * @author Jose Javier Bailon Ortiz
 */
public interface ISincronizadorFeedbackHandler {

    /**
     * Cuando el sincronizador quiere mostrar un mensaje
     * @param msg El mensaje
     */
    public void onSendMessage(String msg);

    /**
     * Cuando cambia el estado
     * @param msg El mensaje del estado
     */
    public void onSendStatus(String msg);

    /**
     * Cuando inicia la sincronizacion
     */
    public void onIniciado();

    /**
     * Cuando finaliza la sincronizacion
     */
    public void onFinalizado();

    /**
     * Cuando se produce un conflicto de sincronizacion
     * @param conflicto El conflicto a resolver
     */
    void onConflicto(Conflicto<?,?> conflicto);

}
