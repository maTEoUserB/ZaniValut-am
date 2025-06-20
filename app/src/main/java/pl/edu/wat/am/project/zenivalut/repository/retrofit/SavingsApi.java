package pl.edu.wat.am.project.zenivalut.repository.retrofit;

import java.util.List;
import java.util.Map;

import pl.edu.wat.am.project.zenivalut.model.CreateSavingsData;
import pl.edu.wat.am.project.zenivalut.model.SavingsData;
import pl.edu.wat.am.project.zenivalut.model.SavingsListData;
import pl.edu.wat.am.project.zenivalut.model.TransactionData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SavingsApi {
    @Headers("Content-Type: application/json")
    @POST("/new/savings_goal")
    Call<SavingsData> createSavings(@Header("Authorization") String token, @Body CreateSavingsData dto);
    @GET("/savings/goals")
    Call<List<SavingsListData>> getSavings(@Header("Authorization") String token);

    @PATCH("saving_goal/update/{id}")
    Call<SavingsData> updateSavingGoal(
            @Header("Authorization") String token,
            @Path("id") long id,
            @Body Map<String, Object> updates
    );
}
