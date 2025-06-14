package pl.edu.wat.am.project.zenivalut.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.RegisterData;
import pl.edu.wat.am.project.zenivalut.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText username;
    TextInputEditText password;
    TextInputEditText confirmPassword;
    EditText balance;
    Button registerButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.nick_register);
        password = findViewById(R.id.password2);
        confirmPassword = findViewById(R.id.password3);
        balance = findViewById(R.id.balance);
        registerButton = findViewById(R.id.register2);

        registerButton.setOnClickListener(v -> {
            String nick = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();
            String balanceText = balance.getText().toString().trim();

            if (nick.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() || balanceText.isEmpty()) {
                Toast.makeText(this, "Wszystkie pola są wymagane.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Hasła się nie zgadzają.", Toast.LENGTH_SHORT).show();
                return;
            }

            double balanceValue;
            try {
                balanceValue = Double.parseDouble(balanceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Nieprawidłowy balans.", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterData registerData = new RegisterData(nick, pass, balanceValue);

            AuthRepository.getInstance().registerUser(registerData, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Konto utworzone pomyślnie. Zaloguj się.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Rejestracja nie powiodła się.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Błąd: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
