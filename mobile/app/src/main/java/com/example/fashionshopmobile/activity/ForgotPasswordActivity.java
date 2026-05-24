package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnSendReset;
    private TextView tvBackToLogin;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        initViews();

        btnSendReset.setOnClickListener(v -> sendResetEmail());

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void sendResetEmail() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }

        showLoading(true);

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showLoading(false);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Gửi email thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSendReset.setEnabled(!isLoading);
    }
}