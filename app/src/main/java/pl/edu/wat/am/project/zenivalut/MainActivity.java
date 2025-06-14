package pl.edu.wat.am.project.zenivalut;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;

import okhttp3.ResponseBody;
import pl.edu.wat.am.project.zenivalut.repository.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuZXdFeGFtcGxlVXNlciIsImlhdCI6MTc0OTkwNTkyNSwiZXhwIjoxNzQ5OTA5NTI1fQ.GbJNgpSmyXXgxzj0TZmCjuk6aYulupIs9z0DtaezbMM";
        ApiService apiService = ApiInstance.getInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.getBalance(token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                if (response.body() != null) {
                    try {
                        responseString = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    balanceTextView.setText("SALDO: " + responseString + " PLN");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
