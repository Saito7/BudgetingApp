package com.example.budgetingappv1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class Tools_Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences preferences = getSharedPreferences(AccountCreation.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Float income = preferences.getFloat(AccountCreation.ACCOUNT_INCOME, 0);
        Float budget = preferences.getFloat(AccountCreation.ACCOUNT_BUDGET, 0);

        SwitchCompat tipsSwitch = findViewById(R.id.tipsSwitch);
        SwitchCompat notificationsSwitch = findViewById(R.id.notificationsSwitch);
        FloatingActionButton close_btn = findViewById(R.id.settings_close_btn);
        Button clear_data_btn = findViewById(R.id.clear_data_btn);
        Button contact_us_btn = findViewById(R.id.contact_us_btn);
        DatabaseHelper databaseHelper = new DatabaseHelper(Tools_Settings.this);
        EditText income_text = findViewById(R.id.income_text);
        EditText budget_num_text = findViewById(R.id.budget_num_text);

        income_text.setText(income.toString());
        budget_num_text.setText(budget.toString());

        tipsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something
            }
        });

        notificationsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something
            }
        });

        income_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try {
                    editor.putFloat(AccountCreation.ACCOUNT_INCOME, Float.parseFloat(String.format(Locale.ROOT,"%.2f",Float.parseFloat(income_text.getText().toString()))));
                }
                catch(Exception e){
                    Toast.makeText(Tools_Settings.this, "Something went wrong (" + e.getMessage() + ")", Toast.LENGTH_SHORT).show();
                }

                editor.apply();
                return true;
            }
        });

        budget_num_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try {
                    editor.putFloat(AccountCreation.ACCOUNT_BUDGET, Float.parseFloat(String.format(Locale.ROOT,"%.2f",Float.parseFloat(budget_num_text.getText().toString()))));

                }
                catch(Exception e){
                    Toast.makeText(Tools_Settings.this, "Something went wrong (" + e.getMessage() + ")", Toast.LENGTH_SHORT).show();
                }

                editor.apply();
                return true;
            }
        });

        clear_data_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.clearData();
            }
        });

        contact_us_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open small window with details
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}