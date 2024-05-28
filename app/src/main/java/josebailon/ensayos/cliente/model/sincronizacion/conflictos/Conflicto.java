package josebailon.ensayos.cliente.model.sincronizacion.conflictos;

import java.util.concurrent.Semaphore;


/**
 * Conflicto generico
 * @param <L> Clase de la entidad local
 * @param <S> Clase de la entidad remota
 *
 * @author Jose Javier Bailon Ortiz
 */
public class Conflicto <L, S>{

    /**
     * Tipo grupo
     */
    public static final int T_GRUPO = 0;

    /**
     * Tipo cancion
     */
    public static final int T_CANCION = 1 ;

    /**
     * Tipo nota
     */
    public static final int T_NOTA = 2;

    /**
     * Semaforo de espera a la resolucion de la sincronizacion
     */
    private Semaphore semaforo;

    /**
     * Tipo de conflicto
     */
    private int tipo;

    /**
     * Entidad local
     */
    private L local;

    /**
     * Entidad remota
     */
    private S remoto;

    /**
     * Entidad resultante de la resolucion del conflicto
     */
    private L resuelto;


    /**
     * Constructor
     * @param tipo   Tipo de conflicto
     * @param local Entidad local
     * @param remoto Entidad remota
     */
    public Conflicto(int tipo, L local, S remoto) {
        this.tipo = tipo;
        this.local = local;
        this.remoto = remoto;
        this.semaforo=new Semaphore(0);
    }

    /**
     * Devuelve el semaforo de espera de resolucion
     * @return El semaforo
     */
    public Semaphore getSemaforo() {
        return semaforo;
    }


    /**
     * Esperar la resolucion
     * @throws InterruptedException Si se interrumpe el hilo
     */
    public void esperar() throws InterruptedException {
        semaforo.acquire();
    }


    /**
     * Liberar espera de la resolucion
     */
    public void liberar(){
        semaforo.release();
    }

    /**
     * Devuelve 3el tipo
     * @return el tipo
     */

    public int getTipo() {
        return tipo;
    }

    /**
     * Devuelve la entidad local
     * @return La entidad localk
     */
    public L getLocal() {
        return local;
    }

    /**
     * Devuelve la entidad remota
     * @return La entidad remota
     */

    public S getRemoto() {
        return remoto;
    }

    /**
     * Devuelve El resultado de la resolucion
     * @return La entidad resultado
     */

    public L getResuelto() {
        return resuelto;
    }

    /**
     * Definir la entidad de resolucion
     * @param resuelto La entidad a definir
     */
    public void setResuelto(L resuelto) {
        this.resuelto = resuelto;
    }
}
