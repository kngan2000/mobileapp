package com.Duong.Expense.Login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Duong.Expense.R;
import com.Duong.Expense.TripActivity.TripActivity;

public class LoginActivity extends AppCompatActivity {

    EditText EditUserName, EditPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditUserName = findViewById(R.id.UserName);
        EditPassword = findViewById(R.id.Password);
        btnLogin = findViewById(R.id.buttonLogin);

        btnLogin.setOnClickListener(v -> checkCredentials());
    }
    private void checkCredentials() {
        String username = EditUserName.getText().toString().trim();
        String password = EditPassword.getText().toString().trim();

        if (username.isEmpty()) {
            showError(EditUserName);
        } else if (password.isEmpty()) {
            showError(EditPassword);
        } else {
            Login();
        }
    }

    private void Login() {
        if (EditUserName.getText().toString().equals("employee")&& EditPassword.getText().toString().equals("employee")){
            Toast.makeText(LoginActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, TripActivity.class);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(LoginActivity.this, "Login Fail!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(EditText input) {
        input.setError("This is a required field!");
        input.requestFocus();
    }
}