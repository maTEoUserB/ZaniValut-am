package pl.edu.wat.am.project.zenivalut.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.adapter.TransactionAdapter;
import pl.edu.wat.am.project.zenivalut.model.TransactionsListData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.TransactionApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<TransactionsListData> transactions = new ArrayList<>();
    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(transactions, this::deleteTransaction);
        recyclerView.setAdapter(adapter);

        loadTransactions();
    }

    private void loadTransactions() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null) return;

        TransactionApi api = ApiInstance.getInstance().create(TransactionApi.class);
        api.getTransactions("Bearer " + token).enqueue(new Callback<List<TransactionsListData>>() {
            @Override
            public void onResponse(Call<List<TransactionsListData>> call, Response<List<TransactionsListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transactions.clear();
                    transactions.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<TransactionsListData>> call, Throwable t) {
                Toast.makeText(TransactionListActivity.this, "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTransaction(long id) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null) return;

        TransactionApi api = ApiInstance.getInstance().create(TransactionApi.class);
        api.deleteTransaction("Bearer " + token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TransactionListActivity.this, "Transakcja usunięta.", Toast.LENGTH_SHORT).show();
                    loadTransactions();
                } else {
                    Toast.makeText(TransactionListActivity.this, "Błąd usuwania: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TransactionListActivity.this, "Nie udało się usunąć transakcji.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
