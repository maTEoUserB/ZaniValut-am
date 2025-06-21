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
import java.util.List;

import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.adapter.ObligationAdapter;
import pl.edu.wat.am.project.zenivalut.model.ObligationData;
import pl.edu.wat.am.project.zenivalut.model.ObligationsListData;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ApiInstance;
import pl.edu.wat.am.project.zenivalut.repository.retrofit.ObligationApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObligationListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ObligationAdapter adapter;
    private List<ObligationData> obligations = new ArrayList<>();
    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";

    SwitchCompat themeSwitch;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obligations_list);

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

        recyclerView = findViewById(R.id.obligationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ObligationAdapter(this, obligations, this::updateObligation);
        recyclerView.setAdapter(adapter);

        loadObligations();
    }

    private void loadObligations() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null) return;

        ObligationApi api = ApiInstance.getInstance().create(ObligationApi.class);
        api.getObligations("Bearer " + token).enqueue(new Callback<ObligationsListData>() {
            @Override
            public void onResponse(Call<ObligationsListData> call, Response<ObligationsListData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obligations.clear();
                    obligations.addAll(response.body().getUnpaidObligations());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ObligationsListData> call, Throwable t) {
                Toast.makeText(ObligationListActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateObligation(long id) {
            SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
            String token = prefs.getString("token", null);

            ObligationApi api = ApiInstance.getInstance().create(ObligationApi.class);

            Call<ObligationData> call = api.updateObligation("Bearer " + token, id);
            call.enqueue(new Callback<ObligationData>() {
                @Override
                public void onResponse(Call<ObligationData> call, Response<ObligationData> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getApplicationContext(), R.string.updated_obligation, Toast.LENGTH_SHORT).show();
                        loadObligations();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ObligationData> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.connection_error + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

}
