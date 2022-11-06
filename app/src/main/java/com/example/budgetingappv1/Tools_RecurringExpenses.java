package com.example.budgetingappv1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class Tools_RecurringExpenses extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView rv_transactions;
    private RecyclerViewAdapter_Transactions mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseHelper databaseHelper;
    private List<Data_Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_expenses);

        FloatingActionButton close_btn = findViewById(R.id.recurring_close_btn);
        rv_transactions = findViewById(R.id.rv_recurringlist);
        databaseHelper = new DatabaseHelper(Tools_RecurringExpenses.this);
        searchView = findViewById(R.id.search_bar_recurring);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // rv_transactions.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(Tools_RecurringExpenses.this);
        rv_transactions.setLayoutManager(layoutManager);

        // specify an adapter
        transactions = databaseHelper.getRecurringTransactionsLike(null);
        mAdapter = new RecyclerViewAdapter_Transactions(transactions);
        rv_transactions.setAdapter(mAdapter);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query != null){
                    // update recycler view every time the query text changes
                    mAdapter.updateAdapter(databaseHelper.getRecurringTransactionsLike(query));
                }
                return true;
            }
        });
    }
}