package pl.edu.wat.am.project.zenivalut.repository;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import pl.edu.wat.am.project.zenivalut.MyApp;
import pl.edu.wat.am.project.zenivalut.model.LoginData;
import pl.edu.wat.am.project.zenivalut.model.RegisterData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.AuthApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");

    private static AuthRepository instance;

    private AuthRepository() {}

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public void loginUser(LoginData loginData, Callback<ResponseBody> callback) {
        AuthApi apiService = ApiInstance.getInstance().create(AuthApi.class);
        Call<ResponseBody> call = apiService.loginUser(loginData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String token = response.body().string();
                        saveToken(token);

                        runOnMainThread(() -> callback.onResponse(call,
                                Response.success(ResponseBody.create(MEDIA_TYPE_TEXT, "")))
                        );

                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnMainThread(() -> callback.onFailure(call, e));
                    }
                } else {
                    runOnMainThread(() -> callback.onResponse(call, response));
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnMainThread(() -> callback.onFailure(call, t));
            }
        });
    }

    public void registerUser(RegisterData registerData, Callback<ResponseBody> callback) {
        AuthApi apiService = ApiInstance.getInstance().create(AuthApi.class);
        Call<ResponseBody> call = apiService.registerUser(registerData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String token = response.body().string();
                        saveToken(token);

                        runOnMainThread(() -> callback.onResponse(call,
                                Response.success(ResponseBody.create(MEDIA_TYPE_TEXT, "")))
                        );

                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnMainThread(() -> callback.onFailure(call, e));
                    }
                } else {
                    runOnMainThread(() -> callback.onResponse(call, response));
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnMainThread(() -> callback.onFailure(call, t));
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences prefs = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    private void runOnMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
