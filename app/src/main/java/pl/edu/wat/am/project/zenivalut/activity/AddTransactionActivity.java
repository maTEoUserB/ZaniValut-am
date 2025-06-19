package pl.edu.wat.am.project.zenivalut.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.edu.wat.am.project.zenivalut.MyApp;
import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.CreateTransactionData;
import pl.edu.wat.am.project.zenivalut.model.TransactionData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.TransactionApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTransactionActivity extends BaseActivity {

    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";

    private EditText titleEditText, amountEditText, descriptionEditText;
    private Spinner categorySpinner, typeSpinner;
    private Button dateButton, submitButton;

    private Map<String, Long> categoryMap = new HashMap<>();
    private String isoDateTimeString = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        setupToolbar();

        titleEditText = findViewById(R.id.titleEditText);
        amountEditText = findViewById(R.id.amountEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        typeSpinner = findViewById(R.id.typeSpinner);
        dateButton = findViewById(R.id.dateButton);
        submitButton = findViewById(R.id.submitButton);

        setupDatePicker();
        setupTypeSpinner();
        setupCategorySpinner();

        submitButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String type = typeSpinner.getSelectedItem().toString();
            String selectedCategory = categorySpinner.getSelectedItem().toString();

            if (title.isEmpty() || amountStr.isEmpty() || type.isEmpty() || isoDateTimeString == null || !categoryMap.containsKey(selectedCategory)) {
                Toast.makeText(this, "Wypełnij wymagane pola.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Kwota musi być liczbą (00.0)", Toast.LENGTH_SHORT).show();
                return;
            }

            long categoryId = categoryMap.get(selectedCategory);

            CreateTransactionData dto = new CreateTransactionData(
                    title, amount, description, categoryId, type, isoDateTimeString
            );

            SharedPreferences prefs = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String token = prefs.getString(TOKEN_KEY, null);

            if (token == null) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                return;
            }

            TransactionApi transactionApi = ApiInstance.getInstance().create(TransactionApi.class);
            Call<TransactionData> call = transactionApi.createTransaction("Bearer " + token, dto);

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<TransactionData> call, Response<TransactionData> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddTransactionActivity.this, "Transaction added!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddTransactionActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TransactionData> call, Throwable t) {
                    Toast.makeText(AddTransactionActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupDatePicker() {
        dateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    isoDateTimeString = sdf.format(calendar.getTime());
                    dateButton.setText(isoDateTimeString);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupTypeSpinner() {
        List<String> types = new ArrayList<>();
        types.add("income");
        types.add("expense");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void setupCategorySpinner() {
        List<String> dummyCategoryNames = new ArrayList<>();
        dummyCategoryNames.add("Rachunki/opłaty");
        dummyCategoryNames.add("Żywność");
        dummyCategoryNames.add("Transport");
        dummyCategoryNames.add("Zdrowie/higiena");
        dummyCategoryNames.add("Edukacja");
        dummyCategoryNames.add("Rodzina");
        dummyCategoryNames.add("Rozrywka");
        dummyCategoryNames.add("Inne");

        categoryMap.put("Rachunki/opłaty", 8L);
        categoryMap.put("Żywność", 9L);
        categoryMap.put("Transport", 10L);
        categoryMap.put("Zdrowie/higiena", 11L);
        categoryMap.put("Edukacja", 12L);
        categoryMap.put("Rodzina", 13L);
        categoryMap.put("Rozrywka", 14L);
        categoryMap.put("Inne", 15L);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dummyCategoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }
}
