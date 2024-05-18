package josebailon.ensayos.cliente.model.sincronizacion.conflictos;

import java.util.concurrent.Semaphore;

public class Conflicto <L, S>{

    public static final int T_GRUPO = 0;
    public static final int T_CANCION = 1 ;

    private Semaphore semaforo;
    int tipo;
    private L local;
    private S remoto;

    private L resuelto;

    public Conflicto(int tipo, L local, S remoto) {
        this.tipo = tipo;
        this.local = local;
        this.remoto = remoto;
        this.semaforo=new Semaphore(0);
    }

    public Semaphore getSemaforo() {
        return semaforo;
    }

    public void esperar() throws InterruptedException {
        semaforo.acquire();
    }

    public void liberar(){
        semaforo.release();
    }

    public int getTipo() {
        return tipo;
    }

    public L getLocal() {
        return local;
    }

    public S getRemoto() {
        return remoto;
    }

    public L getResuelto() {
        return resuelto;
    }

    public void setResuelto(L resuelto) {
        this.resuelto = resuelto;
    }
}
