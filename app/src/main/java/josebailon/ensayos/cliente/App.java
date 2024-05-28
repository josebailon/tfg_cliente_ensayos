package josebailon.ensayos.cliente;

import android.app.Application;
import android.content.Context;

/**
 * Application object. Contiene un singleton con el contexto para areas donde no son necesarias y no
 * se tiene acceso a ninguna activity.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
