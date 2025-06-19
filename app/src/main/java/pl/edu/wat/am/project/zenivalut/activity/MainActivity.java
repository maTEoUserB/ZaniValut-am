package pl.edu.wat.am.project.zenivalut.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import pl.edu.wat.am.project.zenivalut.R;
import pl.edu.wat.am.project.zenivalut.model.DailyExpensesDTO;

import pl.edu.wat.am.project.zenivalut.viewModel.BalanceViewModel;
import pl.edu.wat.am.project.zenivalut.viewModel.ExpensesViewModel;


import android.content.Intent;

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
    private ExpensesViewModel expensesViewModel;

    private static final String PREFS_NAME = "auth";
    private static final String TOKEN_KEY = "token";

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

        balanceTextView = findViewById(R.id.balanceTextView);
        euroTextView = findViewById(R.id.euroTextView);
        lineChart = findViewById(R.id.lineChart);

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

            if (itemId == R.id.item2) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
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

        LineDataSet dataSet = new LineDataSet(entries, "Wydatki");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setText("Wydatki tygodniowe");

        final String[] labelsArray = labels.toArray(new String[0]);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsArray));
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.invalidate();
    }
}
