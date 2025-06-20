package pl.edu.wat.am.project.zenivalut.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pl.edu.wat.am.project.zenivalut.MyApp;
import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.CreateSavingsData;
import pl.edu.wat.am.project.zenivalut.model.CreateTransactionData;
import pl.edu.wat.am.project.zenivalut.model.SavingsData;
import pl.edu.wat.am.project.zenivalut.model.TransactionData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.SavingsApi;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.TransactionApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSavingsActivity extends BaseActivity {

    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";
    private EditText titleEditText, currentAmountEditText, finalAmountEditText;
    private Button dateButton, submitButton;

    private String isoDateTimeString = null;

    SwitchCompat themeSwitch;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_savings);

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

        titleEditText = findViewById(R.id.titleEditText);
        currentAmountEditText = findViewById(R.id.currentAmountEditText);
        finalAmountEditText = findViewById(R.id.finalAmountEditText);
        dateButton = findViewById(R.id.dateButton);
        submitButton = findViewById(R.id.submitButton);

        setupDatePicker();

        submitButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String curAmountStr = currentAmountEditText.getText().toString().trim();
            String finAmountStr = finalAmountEditText.getText().toString().trim();

            if (title.isEmpty() || curAmountStr.isEmpty() || finAmountStr.isEmpty() || isoDateTimeString == null) {
                Toast.makeText(this, getString(R.string.required_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            double curAmount, finAmount;
            try {
                curAmount = Double.parseDouble(curAmountStr);
                finAmount = Double.parseDouble(finAmountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.amount_must), Toast.LENGTH_SHORT).show();
                return;
            }

            CreateSavingsData dto = new CreateSavingsData(
                    title, curAmount, finAmount, isoDateTimeString
            );

            SharedPreferences prefs = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String token = prefs.getString(TOKEN_KEY, null);

            if (token == null) {
                Toast.makeText(this, getString(R.string.not_authenticated), Toast.LENGTH_SHORT).show();
                return;
            }

            SavingsApi transactionApi = ApiInstance.getInstance().create(SavingsApi.class);
            Call<SavingsData> call = transactionApi.createSavings("Bearer " + token, dto);

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<SavingsData> call, Response<SavingsData> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddSavingsActivity.this, getString(R.string.savings_added), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddSavingsActivity.this, getString(R.string.error) + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SavingsData> call, Throwable t) {
                    Toast.makeText(AddSavingsActivity.this, getString(R.string.faild) + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupDatePicker() {
        dateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                isoDateTimeString = sdf.format(calendar.getTime());
                dateButton.setText(isoDateTimeString);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

}
