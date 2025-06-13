package pl.edu.wat.am.project.zenivalut;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.ResponseBody;
import pl.edu.wat.am.project.zenivalut.model.LoginData;
import pl.edu.wat.am.project.zenivalut.model.RegisterData;
import pl.edu.wat.am.project.zenivalut.repository.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        text = findViewById(R.id.text);

//        RegisterData registerData = new RegisterData("newExampleUser", "123", 100.0);
        LoginData loginData = new LoginData("newExampleUser", "123");

        ApiService apiService = ApiInstance.getInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.loginUser(loginData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String responseString = "";
                try {
                    if (response.body() != null) {
                        responseString = response.body().string();
                        text.setText(responseString);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("================Token: " + responseString);
                Toast.makeText(MainActivity.this, "Działa!" + responseString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                text.setText(t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}