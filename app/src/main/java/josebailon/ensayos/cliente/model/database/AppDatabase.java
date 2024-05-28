package josebailon.ensayos.cliente.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import josebailon.ensayos.cliente.model.database.converter.Converters;
import josebailon.ensayos.cliente.model.database.dao.AudioDao;
import josebailon.ensayos.cliente.model.database.dao.CancionDao;
import josebailon.ensayos.cliente.model.database.dao.GrupoDao;
import josebailon.ensayos.cliente.model.database.dao.NotaDao;
import josebailon.ensayos.cliente.model.database.dao.UsuarioDao;
import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;


/**
 * Objeto Room de la base de datos
 *
 * @author Jose Javier Bailon Ortiz
 */
@Database(entities = {UsuarioEntity.class,GrupoEntity.class, CancionEntity.class, NotaEntity.class, AudioEntity.class}, exportSchema = false, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Nombre de la base de datos
     */
    public static final String DATABASE_NAME = "app_database.db";

    /**
     * Instancia singleton
     */
    private static AppDatabase instance;

    /**
     * Objeto para bloqueo de sincronizado durante la creacion de la instancia
     */
    private static final Object LOCK = new Object();

    /**
     * Dao de grupos
     * @return El dao
     */
    public abstract GrupoDao grupoDao();

    /**
     * Dao de canciones
     * @return El dao
     */
    public abstract CancionDao cancionDao();

    /**
     * Dao de usuarios
     * @return El dao
     */
    public abstract UsuarioDao usuarioDao();

    /**
     * Dao de notas
     * @return El dao
     */
    public abstract NotaDao notaDao();

    /**
     * Dao de audios
     * @return El dao
     */
    public abstract AudioDao audioDao();

    /**
     * Devuelve la instancia singleton
     * @param context El contexto a usar
     * @return La instancia
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null)
            synchronized (LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        return instance;
    }
}
