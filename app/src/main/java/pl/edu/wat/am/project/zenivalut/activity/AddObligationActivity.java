package pl.edu.wat.am.project.zenivalut.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.edu.wat.am.project.zenivalut.MyApp;
import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.CreateObligationData;
import pl.edu.wat.am.project.zenivalut.model.ObligationData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ObligationApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddObligationActivity extends BaseActivity {
    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";
    private EditText titleEditText, amountEditText;
    private Spinner categorySpinner;
    private Button dateButton, submitButton;
    private String isoDateTimeString = null;
    private Map<String, Long> categoryMap = new HashMap<>();
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
        setContentView(R.layout.activity_add_obligation);

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
        amountEditText = findViewById(R.id.amountEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        dateButton = findViewById(R.id.dateButton);
        submitButton = findViewById(R.id.submitButton);

        setupDatePicker();
        setupCategorySpinner();

        submitButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();
            String selectedCategory = categorySpinner.getSelectedItem().toString();

            if (title.isEmpty() || amountStr.isEmpty() || isoDateTimeString == null || !categoryMap.containsKey(selectedCategory)) {
                Toast.makeText(this, getString(R.string.required_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.amount_must), Toast.LENGTH_SHORT).show();
                return;
            }

            long categoryId = categoryMap.get(selectedCategory);

            CreateObligationData dto = new CreateObligationData(
                    title, amount, isoDateTimeString, categoryId
            );

            SharedPreferences prefs = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String token = prefs.getString(TOKEN_KEY, null);

            if (token == null) {
                Toast.makeText(this, getString(R.string.not_authenticated), Toast.LENGTH_SHORT).show();
                return;
            }

            ObligationApi transactionApi = ApiInstance.getInstance().create(ObligationApi.class);
            Call<ObligationData> call = transactionApi.createObligation("Bearer " + token, dto);

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ObligationData> call, Response<ObligationData> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddObligationActivity.this, getString(R.string.savings_added), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddObligationActivity.this, getString(R.string.error) + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ObligationData> call, Throwable t) {
                    Toast.makeText(AddObligationActivity.this, getString(R.string.faild) + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void setupCategorySpinner() {
        List<String> dummyCategoryNames = new ArrayList<>();
        dummyCategoryNames.add(getString(R.string.bills));
        dummyCategoryNames.add(getString(R.string.food));
        dummyCategoryNames.add(getString(R.string.transport));
        dummyCategoryNames.add(getString(R.string.health));
        dummyCategoryNames.add(getString(R.string.education));
        dummyCategoryNames.add(getString(R.string.family));
        dummyCategoryNames.add(getString(R.string.entertaiment));
        dummyCategoryNames.add(getString(R.string.other));

        categoryMap.put("Rachunki/opłaty", 8L);
        categoryMap.put("Żywność", 9L);
        categoryMap.put("Transport", 10L);
        categoryMap.put("Zdrowie/higiena", 11L);
        categoryMap.put("Edukacja", 12L);
        categoryMap.put("Rodzina", 13L);
        categoryMap.put("Rozrywka", 14L);
        categoryMap.put("Inne", 15L);

        categoryMap.put("Bills", 8L);
        categoryMap.put("Food", 9L);
        categoryMap.put("Transport", 10L);
        categoryMap.put("Health", 11L);
        categoryMap.put("Education", 12L);
        categoryMap.put("Family", 13L);
        categoryMap.put("Entertaiment", 14L);
        categoryMap.put("Other", 15L);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dummyCategoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }
}
