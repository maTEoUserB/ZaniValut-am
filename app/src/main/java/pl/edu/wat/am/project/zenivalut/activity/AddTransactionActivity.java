package pl.edu.wat.am.project.zenivalut.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri photoUri;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    private EditText titleEditText, amountEditText, descriptionEditText;
    private Spinner categorySpinner, typeSpinner;
    private Button dateButton, submitButton, scanReceiptButton;

    private Map<String, Long> categoryMap = new HashMap<>();
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
        setContentView(R.layout.activity_add_transaction);

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
        descriptionEditText = findViewById(R.id.descriptionEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        typeSpinner = findViewById(R.id.typeSpinner);
        dateButton = findViewById(R.id.dateButton);
        submitButton = findViewById(R.id.submitButton);
        scanReceiptButton = findViewById(R.id.scanReceiptButton);

        setupDatePicker();
        setupTypeSpinner();
        setupCategorySpinner();

        //Uprawnienia do kamery
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && photoUri != null) {
                        sendImageToBackend(photoUri);
                    }
                });

        submitButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String amountStr = amountEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String type = typeSpinner.getSelectedItem().toString();

            if(type.equals("income") || type.equals("przychód")){
                type = "income";
            }
            if(type.equals("expense") || type.equals("wydatek")){
                type = "expense";
            }

            String selectedCategory = categorySpinner.getSelectedItem().toString();

            if (title.isEmpty() || amountStr.isEmpty() || type.isEmpty() || isoDateTimeString == null || !categoryMap.containsKey(selectedCategory)) {
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

            CreateTransactionData dto = new CreateTransactionData(
                    title, amount, description, categoryId, type, isoDateTimeString
            );

            SharedPreferences prefs = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String token = prefs.getString(TOKEN_KEY, null);

            if (token == null) {
                Toast.makeText(this, getString(R.string.not_authenticated), Toast.LENGTH_SHORT).show();
                return;
            }

            TransactionApi transactionApi = ApiInstance.getInstance().create(TransactionApi.class);
            Call<TransactionData> call = transactionApi.createTransaction("Bearer " + token, dto);

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<TransactionData> call, Response<TransactionData> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddTransactionActivity.this, getString(R.string.transactoin_added), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddTransactionActivity.this, getString(R.string.error) + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TransactionData> call, Throwable t) {
                    Toast.makeText(AddTransactionActivity.this, getString(R.string.faild) + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        //Skan paragonu
        scanReceiptButton.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Toast.makeText(this, getString(R.string.create_photo_faild), Toast.LENGTH_SHORT).show();
                }

                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, "pl.edu.wat.am.project.zenivalut.provider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    takePictureLauncher.launch(takePictureIntent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoUri != null) {
                sendImageToBackend(photoUri);
            }
        }
    }

    private void sendImageToBackend(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, getString(R.string.get_photo_faild), Toast.LENGTH_SHORT).show();
                return;
            }

            File tempFile = File.createTempFile("upload_", ".jpg", getCacheDir());
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String token = prefs.getString(TOKEN_KEY, null);

            if (token == null) {
                Toast.makeText(this, getString(R.string.not_authorization), Toast.LENGTH_SHORT).show();
                return;
            }

            TransactionApi api = ApiInstance.getInstance().create(TransactionApi.class);
            Call<TransactionData> call = api.uploadReceipt("Bearer " + token, body);

            call.enqueue(new Callback<TransactionData>() {
                @Override
                public void onResponse(Call<TransactionData> call, Response<TransactionData> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(AddTransactionActivity.this, getString(R.string.receipt_sent), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddTransactionActivity.this, getString(R.string.receipt_error), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TransactionData> call, Throwable t) {
                    Toast.makeText(AddTransactionActivity.this, getString(R.string.connection_error) + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error) + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void setupDatePicker() {
        dateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                    SimpleDateFormat sdfBackend = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
                    isoDateTimeString = sdfBackend.format(calendar.getTime());
                    dateButton.setText(sdfDisplay.format(calendar.getTime()));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupTypeSpinner() {
        List<String> types = new ArrayList<>();
        types.add(getString(R.string.income));
        types.add(getString(R.string.expense));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
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
