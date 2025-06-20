package pl.edu.wat.am.project.zenivalut.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pl.edu.wat.am.project.zenivalut.model.DailyExpensesDTO;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.AccountApi;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpensesViewModel extends ViewModel {

    private final MutableLiveData<List<DailyExpensesDTO>> weeklyExpenses = new MutableLiveData<>();

    public LiveData<List<DailyExpensesDTO>> getWeeklyExpenses() {
        return weeklyExpenses;
    }

    public void loadWeeklyExpenses(String token) {
        AccountApi apiService = ApiInstance.getInstance().create(AccountApi.class);
        Call<List<DailyExpensesDTO>> call = apiService.getWeeklyExpenses("Bearer " + token);
        call.enqueue(new Callback<List<DailyExpensesDTO>>() {
            @Override
            public void onResponse(Call<List<DailyExpensesDTO>> call, Response<List<DailyExpensesDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weeklyExpenses.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<DailyExpensesDTO>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
