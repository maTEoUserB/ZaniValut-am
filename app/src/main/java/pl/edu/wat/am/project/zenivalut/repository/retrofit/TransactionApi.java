package pl.edu.wat.am.project.zenivalut.repository.retrofit;

import pl.edu.wat.am.project.zenivalut.model.CreateTransactionData;
import pl.edu.wat.am.project.zenivalut.model.TransactionData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TransactionApi {
    @Headers("Content-Type: application/json")
    @POST("/new/transaction")
    Call<TransactionData> createTransaction(@Header("Authorization") String token, @Body CreateTransactionData dto);
}
