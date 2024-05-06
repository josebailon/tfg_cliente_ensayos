package josebailon.ensayos.cliente.data.network;

import android.content.res.Resources;

import josebailon.ensayos.cliente.App;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import josebailon.ensayos.cliente.R;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIBuilder {
    public static Retrofit getBuilder(){
        return new Retrofit.Builder()
                .baseUrl(App.getContext().getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();
    }
}
