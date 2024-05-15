package josebailon.ensayos.cliente.model.sincronizacion;

public interface ISincronizadoFeedbackHandler {
    public void onSendMessage(String msg);
    public void onSendStatus(String msg);

    public void onIniciado();
    public void onFinalizado();

}
