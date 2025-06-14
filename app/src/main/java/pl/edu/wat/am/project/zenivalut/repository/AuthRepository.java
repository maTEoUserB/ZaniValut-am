package pl.edu.wat.am.project.zenivalut.repository;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import pl.edu.wat.am.project.zenivalut.MyApp;
import pl.edu.wat.am.project.zenivalut.model.LoginData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static AuthRepository instance;

    private AuthRepository(){}

    public static AuthRepository getInstance(){
        if(instance == null){
            instance = new AuthRepository();
            return instance;
        }
        return instance;
    }
    public void loginUser(LoginData loginData, Callback<ResponseBody> callback){
        ApiService apiService = ApiInstance.getInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.loginUser(loginData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null){
                    new Thread(() -> {
                        try {
                            String token = response.body().string();
                            SharedPreferences prefs = MyApp.getContext().getSharedPreferences("auth", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("token", token);
                            editor.apply();

                            // Po zapisaniu tokena, wywołaj callback na głównym wątku
                            runOnMainThread(() -> callback.onResponse(call, response));

                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnMainThread(() -> callback.onFailure(call, e));
                        }
                    }).start();
                } else {
                    // Jeśli odpowiedź nie jest OK, wywołaj callback bezpośrednio
                    callback.onResponse(call, response);
                }
            }

            // Pomocnicza metoda do wywołania kodu na głównym wątku
            private void runOnMainThread(Runnable runnable) {
                android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                handler.post(runnable);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
}
