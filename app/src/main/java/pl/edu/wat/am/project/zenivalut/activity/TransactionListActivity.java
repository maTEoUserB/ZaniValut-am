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
import pl.edu.wat.am.project.zenivalut.model.LastTransactionsDTO;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.TransactionApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<LastTransactionsDTO> transactions = new ArrayList<>();
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
        api.getTransactions("Bearer " + token).enqueue(new Callback<List<LastTransactionsDTO>>() {
            @Override
            public void onResponse(Call<List<LastTransactionsDTO>> call, Response<List<LastTransactionsDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transactions.clear();
                    transactions.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<LastTransactionsDTO>> call, Throwable t) {
                Toast.makeText(TransactionListActivity.this, "Błąd połączenia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteTransaction(long id) {
        // Dodaj endpoint DELETE w backendzie (np. /transactions/{id})
        Toast.makeText(this, "Usunięcie transakcji ID: " + id, Toast.LENGTH_SHORT).show();
        // Wywołaj backend i po sukcesie odśwież listę
    }
}
