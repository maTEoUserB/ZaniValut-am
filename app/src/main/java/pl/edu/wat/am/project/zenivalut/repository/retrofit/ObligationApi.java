package pl.edu.wat.am.project.zenivalut.repository.retrofit;

import java.util.List;
import java.util.Map;

import pl.edu.wat.am.project.zenivalut.model.CreateObligationData;
import pl.edu.wat.am.project.zenivalut.model.CreateSavingsData;
import pl.edu.wat.am.project.zenivalut.model.ObligationData;
import pl.edu.wat.am.project.zenivalut.model.ObligationsListData;
import pl.edu.wat.am.project.zenivalut.model.SavingsData;
import pl.edu.wat.am.project.zenivalut.model.SavingsListData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ObligationApi {
    @Headers("Content-Type: application/json")
    @POST("/new/obligation")
    Call<ObligationData> createObligation(@Header("Authorization") String token, @Body CreateObligationData dto);
    @GET("/obligations")
    Call<ObligationsListData> getObligations(@Header("Authorization") String token);

    @POST("/update/obligation/{id}")
    Call<ObligationData> updateObligation(
            @Header("Authorization") String token,
            @Path("id") long id
    );
}
