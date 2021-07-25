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
import com.example.uasshabri.dashboard.fragment.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView btnClickHere;
    private EditText inptName, inptEmail, inptUsername, inptPassword, inptConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnClickHere = findViewById(R.id.clickText);
        btnRegister = findViewById(R.id.registerButton);
        inptName = findViewById(R.id.name);
        inptEmail = findViewById(R.id.email);
        inptUsername = findViewById(R.id.username);
        inptPassword = findViewById(R.id.password);
        inptConfirmPassword = findViewById(R.id.confirmPassword);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        btnClickHere.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clickText:
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;
            case R.id.registerButton:
                progressBar.setVisibility(View.VISIBLE);
                String name = inptName.getText().toString().trim();
                String email = inptEmail.getText().toString().trim();
                String username = inptUsername.getText().toString().trim();
                String password = inptPassword.getText().toString().trim();
                String confirmPassword = inptConfirmPassword.getText().toString().trim();

                if (checkRule(name, email, username, password, confirmPassword)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                FirebaseUser uid = firebaseAuth.getCurrentUser();

                                if (uid != null) {
                                    String documentId = uid.getUid();
                                    User user = new User(email, name, username);

                                    db.collection("users")
                                            .document(documentId)
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(RegisterActivity.this, "User created, login success", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterActivity.this, "Something went wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "UID is null, try again or contact your administrator", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error found, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }

    protected boolean checkRule(String name, String email, String username, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            inptName.setError("Name is required");
            inptName.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            inptEmail.setError("Email is required");
            inptEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(username)) {
            inptUsername.setError("Username is required");
            inptUsername.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            inptPassword.setError("Password is required");
            inptPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            inptConfirmPassword.setError("Confirm password is required");
            inptConfirmPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (password.length() < 8) {
            inptPassword.setError("Password must be at least 8 character or more");
            inptPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            inptConfirmPassword.setError("Confirm password is not same with password");
            inptConfirmPassword.requestFocus();
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