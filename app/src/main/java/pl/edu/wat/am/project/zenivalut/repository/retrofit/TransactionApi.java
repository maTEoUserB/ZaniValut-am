package pl.edu.wat.am.project.zenivalut.repository.retrofit;

import java.util.List;

import pl.edu.wat.am.project.zenivalut.model.CreateTransactionData;
import pl.edu.wat.am.project.zenivalut.model.TransactionsListData;
import pl.edu.wat.am.project.zenivalut.model.TransactionData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TransactionApi {
    @Headers("Content-Type: application/json")
    @POST("/new/transaction")
    Call<TransactionData> createTransaction(@Header("Authorization") String token, @Body CreateTransactionData dto);

    @GET("/transactions")
    Call<List<TransactionsListData>> getTransactions(@Header("Authorization") String token);

    @DELETE("/transaction/delete/{id}")
    Call<Void> deleteTransaction(@Header("Authorization") String token, @Path("id") long id);
}
