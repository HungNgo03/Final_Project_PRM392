package com.example.final_project_prm392.ui.theme.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project_prm392.Activity.MainActivity;
import com.example.final_project_prm392.Repository.UserRepository;
import com.example.final_project_prm392.databinding.ActivityLoginBinding;
import com.example.final_project_prm392.model.User;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository();

        setupUI();
    }

    private void setupUI() {
        binding.loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                loginUser();
            }
        });

        binding.registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Password is required");
            return false;
        }

        return true;
    }

    private void loginUser() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.loginButton.setEnabled(false);

        userRepository.loginUser(email, password, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                binding.progressBar.setVisibility(View.GONE);
                binding.loginButton.setEnabled(true);

                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                binding.loginButton.setEnabled(true);

                Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

