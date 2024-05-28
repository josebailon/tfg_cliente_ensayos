package josebailon.ensayos.cliente.model.sincronizacion;

import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.network.model.entidades.AudioApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.NotaApiEnt;


/**
 * Calcular el estado de dos entidades, remota y local en funcion de los siguientes parametros
 * • S: Sin cambios locales
 * • E: Editado localmente
 * • B: Borrado localmente (borrado lógico)
 * • A: Abandonado (solo entidad grupo)
 * • X: No existente
 * • V0: Version 0
 * • VN: Versión N (Diferente de 0)
 * • VQ: Versión Q (Diferente de 0 y N)
 *
 *  @author Jose Javier Bailon Ortiz
 * */
public class CalculadoraEstados {
    public static final int X_X=-1; // NO EXISTENTE - NO EXISTENTE
    public static final int X_VN=0; // NO EXISTENTE - IDn
    public static final int V0_X=1;// ID0 - NO EXISTENTE
    public static final int VN_X=2;//  IDn - NO EXISTENTE
    public static final int SVN_VN=3;// SIN CAMBIOS IDn - IDn
    public static final int SVN_VQ=4;// SIN CAMBIOS IDn - IDq
    public static final int EVN_VN=5;// EDITADO IDn - IDn
    public static final int EVN_VQ=6;// EDITADO IDn - IDq
    public static final int B_X=7;// BORRADO - NO EXISTENTE
    public static final int B_VN=8;// BORRADO - IDn
    public static final int A_X=9;// ABANDONADO - NO EXISTENTE
    public static final int A_VN=10;// ABANDONADO - IDn

    /**
     * Estado de grupos
     * @param grupoLocal
     * @param grupoRemoto
     * @return
     */
    public static int estadoCanciones(GrupoEntity grupoLocal, GrupoApiEnt grupoRemoto) {
        Object objLocal=grupoLocal;
        boolean abandonadoLocal=(grupoLocal!=null && grupoLocal.isAbandonado());
        int versionLocal =(grupoLocal!=null) ?  grupoLocal.getVersion():-1;
        boolean borradoLocal=(grupoLocal!=null && grupoLocal.isBorrado());
        boolean editadoLocal=(grupoLocal!=null && grupoLocal.isEditado());
        Object objRemoto=grupoRemoto;
        int versionRemoto=(grupoRemoto!=null) ?  grupoRemoto.getVersion():-1;
        return calcularEstado(objLocal,abandonadoLocal,versionLocal,borradoLocal,editadoLocal,objRemoto,versionRemoto);
    }


    /**
     * Estado de canciones
     * @param cancionLocal
     * @param cancionRemota
     * @return
     */
    public static int estadoCanciones(CancionEntity cancionLocal, CancionApiEnt cancionRemota) {
        Object objLocal=cancionLocal;
        boolean abandonadoLocal=false;
        int versionLocal =(cancionLocal!=null) ?  cancionLocal.getVersion():-1;
        boolean borradoLocal=(cancionLocal!=null && cancionLocal.isBorrado());
        boolean editadoLocal=(cancionLocal!=null && cancionLocal.isEditado());
        Object objRemoto=cancionRemota;
        int versionRemoto=(cancionRemota!=null) ?  cancionRemota.getVersion():-1;
        return calcularEstado(objLocal,abandonadoLocal,versionLocal,borradoLocal,editadoLocal,objRemoto,versionRemoto);
    }

    /**
     * Estado de notas
     * @param notaLocal
     * @param notaRemota
     * @return
     */
    public static int estadoNotas(NotaEntity notaLocal, NotaApiEnt notaRemota) {
        Object objLocal=notaLocal;
        boolean abandonadoLocal=false;
        int versionLocal =(notaLocal!=null) ?  notaLocal.getVersion():-1;
        boolean borradoLocal=(notaLocal!=null && notaLocal.isBorrado());
        boolean editadoLocal=(notaLocal!=null && notaLocal.isEditado());
        Object objRemoto=notaRemota;
        int versionRemoto=(notaRemota!=null) ?  notaRemota.getVersion():-1;
        return calcularEstado(objLocal,abandonadoLocal,versionLocal,borradoLocal,editadoLocal,objRemoto,versionRemoto);
    }


    /**
     * Estado de audios
     * @param audioLocal
     * @param audioRemoto
     * @return
     */
    public static int estadoAudios(AudioEntity audioLocal, AudioApiEnt audioRemoto) {
        Object objLocal=audioLocal;
        boolean abandonadoLocal=false;
        int versionLocal =(audioLocal!=null) ?  audioLocal.getVersion():-1;
        boolean borradoLocal=(audioLocal!=null && audioLocal.isBorrado());
        boolean editadoLocal=(audioLocal!=null && audioLocal.isEditado());
        Object objRemoto=audioRemoto;
        int versionRemoto=(audioRemoto!=null) ?  audioRemoto.getVersion():-1;
        return calcularEstado(objLocal,abandonadoLocal,versionLocal,borradoLocal,editadoLocal,objRemoto,versionRemoto);
    }


    /**
     * Calculador generico de estado segun los parametros
     * @param objLocal
     * @param abandonadoLocal
     * @param versionLocal
     * @param borradoLocal
     * @param editadoLocal
     * @param objRemoto
     * @param versionRemoto
     * @return
     */
    private static int calcularEstado(
            Object objLocal,
            boolean abandonadoLocal,
            int versionLocal,
            boolean borradoLocal,
            boolean editadoLocal,
            Object objRemoto,
            int versionRemoto
            ){
        if (objLocal==null && objRemoto==null) return X_X;
        if (objLocal==null && objRemoto!=null) return X_VN;
        if (objLocal!=null && abandonadoLocal && objRemoto==null) return  A_X;
        if (objLocal!=null && abandonadoLocal && objRemoto!=null) return  A_VN;
        if (objLocal!=null && borradoLocal && objRemoto==null) return  B_X;
        if (objLocal!=null && borradoLocal && objRemoto!=null) return  B_VN;
        if (objLocal!=null && versionLocal==0 && objRemoto==null) return V0_X;
        if (objLocal!=null && objRemoto==null) return  VN_X;
        //edicion
        if (objLocal!=null &&  objRemoto!=null && editadoLocal && versionLocal==versionRemoto) return  EVN_VN;
        if (objLocal!=null &&  objRemoto!=null && editadoLocal && versionLocal!=versionRemoto) return  EVN_VQ;
        //versiones
        if (objLocal!=null &&  objRemoto!=null && versionLocal==versionRemoto) return  SVN_VN;
        if (objLocal!=null &&  objRemoto!=null && versionLocal!=versionRemoto) return  SVN_VQ;
        return 0;
    }
}
