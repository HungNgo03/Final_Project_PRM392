// ProfileActivity.java
package com.example.final_project_prm392.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.final_project_prm392.Domain.User;
import com.example.final_project_prm392.R;
import com.example.final_project_prm392.ViewModel.ProfileViewModel;
import com.example.final_project_prm392.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;
    private Uri selectedImageUri;
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.profileImage.setImageURI(uri);
                    binding.changePhotoText.setText("Change Photo");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ProfileViewModel();

        setupUI();
        loadUserProfile();
        observeViewModel();
    }

    private void setupUI() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.profileImageLayout.setOnClickListener(v ->
                getContent.launch("image/*"));

        binding.saveButton.setOnClickListener(v -> saveProfile());

        binding.logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading ->
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                updateUI(user);
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login to view profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        viewModel.loadUserProfile();
    }

    private void updateUI(User user) {
        binding.nameEditText.setText(user.getName());
        binding.emailEditText.setText(user.getEmail());
        binding.phoneEditText.setText(user.getPhone());
        binding.medicalHistoryEditText.setText(user.getMedicalHistory());

        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfilePicture())
                    .placeholder(R.drawable.placeholder_user)
                    .into(binding.profileImage);
            binding.changePhotoText.setText("Change Photo");
        }
    }

    private void saveProfile() {
        String name = binding.nameEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String medicalHistory = binding.medicalHistoryEditText.getText().toString().trim();

        if (name.isEmpty()) {
            binding.nameEditText.setError("Name is required");
            return;
        }

        if (phone.isEmpty()) {
            binding.phoneEditText.setError("Phone is required");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        if (selectedImageUri != null) {
            uploadImageAndSaveProfile(name, phone, medicalHistory);
        } else {
            saveProfileWithoutImage(name, phone, medicalHistory);
        }
    }

    private void uploadImageAndSaveProfile(String name, String phone, String medicalHistory) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("profile_images/" + UUID.randomUUID().toString());

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            viewModel.updateUserProfile(name, phone, medicalHistory, imageUrl);
                        }))
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileWithoutImage(String name, String phone, String medicalHistory) {
        viewModel.updateUserProfile(name, phone, medicalHistory, null);
    }
}