package com.example.fashionshopmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshopmobile.MainActivity;
import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.User;
import com.example.fashionshopmobile.request.UserSyncRequest;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        initViews();

        // Quan trọng:
        // Nếu Firebase vẫn còn user đang đăng nhập,
        // gọi lại backend để lấy role mới nhất trong DB.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (sessionManager.isLoggedIn() && currentUser != null) {
            showLoading(true);
            syncUserToBackend(currentUser);
            return;
        }

        // Nếu session còn nhưng Firebase không còn user thì xóa session cũ.
        if (sessionManager.isLoggedIn() && currentUser == null) {
            sessionManager.logout();
        }

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }

        showLoading(true);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            syncUserToBackend(firebaseUser);
                        } else {
                            showLoading(false);
                            Toast.makeText(this, "Không lấy được thông tin Firebase user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showLoading(false);
                        Toast.makeText(
                                this,
                                "Đăng nhập thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void syncUserToBackend(FirebaseUser firebaseUser) {
        String firebaseUid = firebaseUser.getUid();
        String email = firebaseUser.getEmail();

        String fullName = firebaseUser.getDisplayName();
        if (fullName == null || fullName.isEmpty()) {
            fullName = getNameFromEmail(email);
        }

        String avatarUrl = null;
        if (firebaseUser.getPhotoUrl() != null) {
            avatarUrl = firebaseUser.getPhotoUrl().toString();
        }

        UserSyncRequest request = new UserSyncRequest(firebaseUid, email, fullName, avatarUrl);

        ApiClient.getApiService().syncUser(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    sessionManager.saveUser(user);

                    Toast.makeText(
                            LoginActivity.this,
                            "Role backend = " + user.getRole()
                                    + " | Role session = " + sessionManager.getRole(),
                            Toast.LENGTH_LONG
                    ).show();

                    openHomeByRole();
                } else {
                    firebaseAuth.signOut();
                    sessionManager.logout();

                    Toast.makeText(
                            LoginActivity.this,
                            "Firebase đăng nhập được nhưng sync user thất bại",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                firebaseAuth.signOut();
                sessionManager.logout();

                Toast.makeText(
                        LoginActivity.this,
                        "Không gọi được API sync user: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private String getNameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "Khách hàng";
        }

        return email.substring(0, email.indexOf("@"));
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
    }

    private void openHomeByRole() {
        Intent intent;

        if (sessionManager.isAdmin()) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}