package pl.edu.wat.am.project.zenivalut.repository;

import okhttp3.ResponseBody;
import pl.edu.wat.am.project.zenivalut.model.LoginData;
import pl.edu.wat.am.project.zenivalut.model.RegisterData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/auth/signup")
    Call<ResponseBody> registerUser(@Body RegisterData registerData);

    @POST("/api/auth/signin")
    Call<ResponseBody> loginUser(@Body LoginData loginData);

    @GET("/balance")
    Call<ResponseBody> getBalance(@Header("Authorization") String token);
}
