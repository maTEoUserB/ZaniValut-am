package pl.edu.wat.am.project.zenivalut.viewModel;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pl.edu.wat.am.project.zenivalut.MyApp;
import pl.edu.wat.am.project.zenivalut.model.BalanceData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.AccountApi;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.AuthApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BalanceViewModel extends ViewModel {
    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";
    private final MutableLiveData<BalanceData> balance = new MutableLiveData<>();

    public LiveData<BalanceData> getBalance() {
        return balance;
    }

    public void loadBalance() {
        SharedPreferences prefs = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        AccountApi apiService = ApiInstance.getInstance().create(AccountApi.class);
        Call<BalanceData> call = apiService.getBalance("Bearer " + token);

        call.enqueue(new Callback<BalanceData>() {
            @Override
            public void onResponse(Call<BalanceData> call, Response<BalanceData> response) {
                if (response.body() != null) {
                    balance.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<BalanceData> call, Throwable t) {
                Log.e("BalanceViewModel", "Błąd ładowania salda", t);
            }
        });
    }
}
