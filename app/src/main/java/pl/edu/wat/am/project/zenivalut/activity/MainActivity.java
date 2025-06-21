package pl.edu.wat.am.project.zenivalut.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.DailyExpensesDTO;

import pl.edu.wat.am.project.zenivalut.viewModel.BalanceViewModel;
import pl.edu.wat.am.project.zenivalut.viewModel.ExpensesViewModel;


import android.content.Intent;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private BalanceViewModel balanceViewModel;
    TextView balanceTextView;
    TextView euroTextView;
    LineChart lineChart;
    ImageButton logoutButton;
    private ExpensesViewModel expensesViewModel;

    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";

    SwitchCompat themeSwitch;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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

        balanceTextView = findViewById(R.id.balanceTextView);
        euroTextView = findViewById(R.id.euroTextView);
        lineChart = findViewById(R.id.lineChart);
        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        balanceViewModel = new ViewModelProvider(this).get(BalanceViewModel.class);
        balanceViewModel.getBalance().observe(this, newBalance -> {
            balanceTextView.setText(getString(R.string.balance_text) + " " + newBalance.getBalance() + " PLN");
            euroTextView.setText("-> " + newBalance.getEuroBalance() + " EU");
        });

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);
        expensesViewModel = new ViewModelProvider(this).get(ExpensesViewModel.class);
        expensesViewModel.getWeeklyExpenses().observe(this, expenses -> {
            if (expenses != null) {
                updateChart(expenses);
            }
        });
        expensesViewModel.loadWeeklyExpenses(token);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.item1) {
                Intent intent = new Intent(MainActivity.this, AddObligationActivity.class);
                startActivity(intent);
                return true;
            }

            if (itemId == R.id.item2) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(intent);
                return true;
            }

            if (itemId == R.id.item3) {
                Intent intent = new Intent(MainActivity.this, AddSavingsActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });

        CardView card3 = findViewById(R.id.card3);
        card3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionListActivity.class);
            startActivity(intent);
        });

        CardView card2 = findViewById(R.id.card2);
        card2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ObligationListActivity.class);
            startActivity(intent);
        });

        CardView card1 = findViewById(R.id.card1);
        card1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavingsListActivity.class);
            startActivity(intent);
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        balanceViewModel.loadBalance();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);
        if (token != null) {
            expensesViewModel.loadWeeklyExpenses(token);
        }
    }

    private void updateChart(List<DailyExpensesDTO> expenses) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());

        for (int i = 0; i < expenses.size(); i++) {
            DailyExpensesDTO item = expenses.get(i);

            String formattedDate;
            try {
                Date date = inputFormat.parse(item.getDateLabel());
                formattedDate = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                formattedDate = item.getDateLabel();
            }

            labels.add(formattedDate);
            entries.add(new Entry(i, item.getTotalAmount().floatValue()));
        }

        int textColor = getResources().getColor(R.color.chart_text_color, getTheme());
        int lineColor = getResources().getColor(R.color.chart_line_color, getTheme());
        int backgroundColor = getResources().getColor(R.color.chart_background, getTheme());

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.expenses));
        dataSet.setColor(lineColor);
        dataSet.setCircleColor(lineColor);
        dataSet.setValueTextColor(textColor);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getDescription().setText(getString(R.string.weekly_expenses));
        lineChart.getDescription().setTextColor(textColor);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(textColor);

        lineChart.getAxisLeft().setTextColor(textColor);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.setBackgroundColor(backgroundColor);

        lineChart.invalidate();
    }
}
