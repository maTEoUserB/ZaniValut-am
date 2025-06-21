package pl.edu.wat.am.project.zenivalut.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;


import okhttp3.ResponseBody;
import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.LoginData;
import pl.edu.wat.am.project.zenivalut.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    TextInputEditText username;
    TextInputEditText password;
    Button loginButton;
    Button registerButton;
    FrameLayout loadingContainer;

    SwitchCompat themeSwitch;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupToolbar();

        themeSwitch = findViewById(R.id.themeSwitch);
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("nightMode", false);
        if(nightMode){
            themeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        themeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nightMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode", false);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode", true);
                }
                editor.apply();
            }
        });

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadingContainer = findViewById(R.id.loading_container);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v -> {

            String login = username.getText().toString().trim();
            String passwd = password.getText().toString().trim();

            if (login.isEmpty()) {
                Toast.makeText(LoginActivity.this, getString(R.string.nick_required), Toast.LENGTH_SHORT).show();
                username.requestFocus();
                return;
            }

            if (passwd.isEmpty()) {
                Toast.makeText(LoginActivity.this, getString(R.string.password_required), Toast.LENGTH_SHORT).show();
                password.requestFocus();
                return;
            }

            loadingContainer.setVisibility(View.VISIBLE);
            username.setEnabled(false);
            password.setEnabled(false);
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);

            LoginData loginData = new LoginData(login, passwd);

            AuthRepository.getInstance().loginUser(loginData, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        loadingContainer.setVisibility(View.GONE);
                        startActivity(intent);
                        finish();
                    } else {
                        loadingContainer.setVisibility(View.GONE);
                        username.setEnabled(true);
                        password.setEnabled(true);
                        loginButton.setEnabled(true);
                        registerButton.setEnabled(true);
                        Toast.makeText(LoginActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loadingContainer.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(t.getMessage());
                }
            });
        });


        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

}