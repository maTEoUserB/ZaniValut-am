package pl.edu.wat.am.project.zenivalut.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import pl.edu.wat.am.project.zenivalut.MyApp;
import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.BalanceData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.AuthApi;
import pl.edu.wat.am.project.zenivalut.viewModel.BalanceViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";
    private BalanceViewModel balanceViewModel;
    TextView balanceTextView;
    TextView euroTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        balanceTextView = findViewById(R.id.balanceTextView);
        euroTextView = findViewById(R.id.euroTextView);

        balanceViewModel = new ViewModelProvider(this).get(BalanceViewModel.class);
        balanceViewModel.getBalance().observe(this, newBalance -> {
            balanceTextView.setText("Saldo: " + newBalance.getBalance() + " PLN");
            euroTextView.setText("-> " + newBalance.getEuroBalance() + " EU");
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.item3) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences prefs = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        AuthApi apiService = ApiInstance.getInstance().create(AuthApi.class);
        Call<BalanceData> call = apiService.getBalance("Bearer " + token);

        call.enqueue(new Callback<BalanceData>() {
            @Override
            public void onResponse(Call<BalanceData> call, Response<BalanceData> response) {
                if (response.body() != null) {
                        String balance = response.body().getBalance().toString();
                        String euroBalance = response.body().getEuroBalance().toString();
                        runOnUiThread(() -> balanceTextView.setText("SALDO: " + balance + " PLN"));
                        runOnUiThread(() -> euroTextView.setText("-> " + euroBalance + " EU"));
                }
            }

            @Override
            public void onFailure(Call<BalanceData> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        balanceViewModel.loadBalance();
    }
}
