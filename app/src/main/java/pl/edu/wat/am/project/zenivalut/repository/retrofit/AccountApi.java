package pl.edu.wat.am.project.zenivalut.repository.retrofit;

import pl.edu.wat.am.project.zenivalut.model.BalanceData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AccountApi {
    @GET("/balance")
    Call<BalanceData> getBalance(@Header("Authorization") String token);
}
