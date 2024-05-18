package josebailon.ensayos.cliente.model.sincronizacion;

import josebailon.ensayos.cliente.model.sincronizacion.conflictos.Conflicto;

public interface ISincronizadorFeedbackHandler {
    public void onSendMessage(String msg);
    public void onSendStatus(String msg);

    public void onIniciado();
    public void onFinalizado();

    void onConflicto(Conflicto<?,?> conflicto);

}
