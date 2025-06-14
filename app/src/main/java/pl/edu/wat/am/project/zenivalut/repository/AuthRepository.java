package pl.edu.wat.am.project.zenivalut.repository;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import pl.edu.wat.am.project.zenivalut.MainActivity;
import pl.edu.wat.am.project.zenivalut.model.LoginData;
import pl.edu.wat.am.project.zenivalut.model.RegisterData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final Context context;

    public AuthRepository(Context context) {
        this.context = context;
    }
    public void loginUser(LoginData loginData, Callback<ResponseBody> callback){
        ApiService apiService = ApiInstance.getInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.loginUser(loginData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null){
                    try {
                        String token = response.body().string();
                        SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token", token);
                        editor.apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void registerUser(RegisterData registerData, Callback<ResponseBody> callback) {
        ApiService apiService = ApiInstance.getInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.registerUser(registerData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String token = response.body().string();
                        SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token", token);
                        editor.apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }
}
