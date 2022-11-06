package com.example.budgetingappv1;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;
import java.util.MissingResourceException;

public class AccountCreation extends AppCompatActivity {

    public final static String FIRST_INSTALL = "FirstTimeInstall";
    public final static String CONFIRM = "Yes";
    public final static String ACCOUNT_NAME = "Account_Username";
    public final static String ACCOUNT_INCOME = "Account_Income";
    public final static String ACCOUNT_BUDGET = "Account_Budget";
    public final static String PREFERENCES = "PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        Button finish_btn = findViewById(R.id.finish_btn);
        EditText account_username = findViewById(R.id.account_username);
        EditText account_income = findViewById(R.id.account_input);
        EditText account_budget = findViewById(R.id.account_budget);

        //Check if applicatioin is opened for the first time
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String FirstTime = preferences.getString(FIRST_INSTALL, "");

        if(FirstTime.equals(CONFIRM)){
            Intent intent = new Intent(AccountCreation.this, MainActivity.class);
            startActivity(intent);
        }


        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(FIRST_INSTALL, CONFIRM);
                editor.apply();

                try {
                    editor.putString(ACCOUNT_NAME, account_username.getText().toString());
                    editor.putFloat(ACCOUNT_INCOME, Float.parseFloat(String.format(Locale.ROOT,"%.2f",Float.parseFloat(account_income.getText().toString()))));
                    editor.putFloat(ACCOUNT_BUDGET, Float.parseFloat(String.format(Locale.ROOT,"%.2f",Float.parseFloat(account_budget.getText().toString()))));

                    if(account_username.getText().toString().isEmpty()){
                        throw new MissingResourceException("Missing text input", "Username", "USERNAME");
                    }

                }
                catch(NumberFormatException | MissingResourceException e){
                    Toast.makeText(AccountCreation.this, "Entry fields are missing data", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Toast.makeText(AccountCreation.this, "Something went wrong (" + e.getMessage() + ")", Toast.LENGTH_SHORT).show();
                }

                editor.apply();

                Intent intent = new Intent(AccountCreation.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}