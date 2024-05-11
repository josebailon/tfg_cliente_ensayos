package josebailon.ensayos.cliente.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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


@Database(entities = {UsuarioEntity.class,GrupoEntity.class, CancionEntity.class, NotaEntity.class, AudioEntity.class}, exportSchema = false, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "app_database.db";
    private static AppDatabase instance;
    private static final Object LOCK = new Object();

    public abstract GrupoDao grupoDao();
    public abstract CancionDao cancionDao();
    public abstract UsuarioDao usuarioDao();
    public abstract NotaDao notaDao();
    public abstract AudioDao audioDao();

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
