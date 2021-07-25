package com.example.uasshabri.frontpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uasshabri.R;
import com.example.uasshabri.dashboard.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView btnClickHere;
    private EditText inptEmail, inptPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnClickHere = findViewById(R.id.clickText);
        inptEmail = findViewById(R.id.email);
        inptPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBarLogin);

        firebaseAuth = FirebaseAuth.getInstance();

        btnClickHere.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clickText:
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
                break;
            case R.id.loginButton:
                progressBar.setVisibility(View.VISIBLE);
                String email = inptEmail.getText().toString().trim();
                String password = inptPassword.getText().toString().trim();

                if (checkRule(email, password)) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Email or password is wrong", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Something went wrong, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                    break;
                }
        }
    }

    protected boolean checkRule(String email,String password) {
        if (TextUtils.isEmpty(email)) {
            inptEmail.setError("Email is required");
            inptEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            inptPassword.setError("Password is required");
            inptPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inptEmail.setError("Please provide valid email");
            inptEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        return true;
    }
}