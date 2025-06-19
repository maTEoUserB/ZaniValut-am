package pl.edu.wat.am.project.zenivalut.repository.retrofit;

import java.util.List;

import pl.edu.wat.am.project.zenivalut.model.BalanceData;
import pl.edu.wat.am.project.zenivalut.model.DailyExpensesDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface AccountApi {
    @GET("/balance")
    Call<BalanceData> getBalance(@Header("Authorization") String token);

    @GET("/weekly/expenses")
    Call<List<DailyExpensesDTO>> getWeeklyExpenses(@Header("Authorization") String token);
}
