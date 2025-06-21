package pl.edu.wat.am.project.zenivalut.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.adapter.SavingsAdapter;
import pl.edu.wat.am.project.zenivalut.model.SavingsData;
import pl.edu.wat.am.project.zenivalut.model.SavingsListData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.SavingsApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavingsListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SavingsAdapter adapter;
    private List<SavingsListData> savings = new ArrayList<>();
    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";

    SwitchCompat themeSwitch;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_list);

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

        recyclerView = findViewById(R.id.savingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavingsAdapter(this, savings, this::updateSaving);
        recyclerView.setAdapter(adapter);

        loadSavings();
    }

    private void loadSavings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null) return;

        SavingsApi api = ApiInstance.getInstance().create(SavingsApi.class);
        api.getSavings("Bearer " + token).enqueue(new Callback<List<SavingsListData>>() {
            @Override
            public void onResponse(Call<List<SavingsListData>> call, Response<List<SavingsListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    savings.clear();
                    savings.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<SavingsListData>> call, Throwable t) {
                Toast.makeText(SavingsListActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSaving(SavingsListData saving) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_savings));

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint(getString(R.string.current_amount) + " " + saving.getCurrentAmount());
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.update_text) + " ", (dialog, which) -> {
            String inputValue = input.getText().toString();
            if(inputValue.isEmpty()){
                Toast.makeText(this, R.string.amount_not_blank, Toast.LENGTH_SHORT).show();
                return;
            }
            Double newAmount;
            try {
                newAmount = Double.parseDouble(inputValue);
            } catch (NumberFormatException e){
                Toast.makeText(this, R.string.wrong_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("currentAmount", newAmount);

            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            String token = prefs.getString("token", null);
            if (token == null) {
                Toast.makeText(this, R.string.token_error, Toast.LENGTH_SHORT).show();
                return;
            }

            SavingsApi api = ApiInstance.getInstance().create(SavingsApi.class);

            Call<SavingsData> call = api.updateSavingGoal("Bearer " + token, saving.getId(), updates);
            call.enqueue(new Callback<SavingsData>() {
                @Override
                public void onResponse(Call<SavingsData> call, Response<SavingsData> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getApplicationContext(), R.string.updated_savings, Toast.LENGTH_SHORT).show();
                        loadSavings();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SavingsData> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.connection_error + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton(R.string.text_cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

}
