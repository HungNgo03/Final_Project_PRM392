package com.example.final_project_prm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.final_project_prm392.Adapter.CategoryAdapter;
import com.example.final_project_prm392.Adapter.TopDoctorsAdapter;
import com.example.final_project_prm392.R;
import com.example.final_project_prm392.ViewModel.MainViewModel;
import com.example.final_project_prm392.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends ComponentActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new MainViewModel();
        setContentView(binding.getRoot());
        initCategory();
        initTopDoctors();
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_appointments) {
                startActivity(new Intent(MainActivity.this,
                        com.example.final_project_prm392.Activity.AppointmentListActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Điều hướng đến màn hình profile (có thể là màn hình đăng nhập nếu chưa đăng nhập)
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    startActivity(new Intent(MainActivity.this,
                            com.example.final_project_prm392.Activity.LoginActivity.class));
                } else {
                    // Điều hướng đến màn hình profile
                }
                return true;
            }
            return true;
        });
    }
    private void initTopDoctors() {
        binding.progressBarDoctor.setVisibility(View.VISIBLE);
        viewModel.loadDoctors().observe(this, doctorsModels -> {
            binding.doctorView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.doctorView.setAdapter(new TopDoctorsAdapter(doctorsModels));
            binding.progressBarDoctor.setVisibility(View.GONE);
        });
    }

    private void initCategory() {
        binding.progressBarCat.setVisibility(View.VISIBLE);
        binding.catView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.catView.setAdapter(new CategoryAdapter(new ArrayList<>()));
        viewModel.loadCategory().observe(this, categoryModels -> {
            if (categoryModels != null) {
                Log.d("MainActivity", "Received " + categoryModels.size() + " categories");
                ((CategoryAdapter) binding.catView.getAdapter()).updateItems(categoryModels); // Giả sử adapter có phương thức updateData
            } else {
                Log.e("MainActivity", "No categories loaded");
            }
            binding.progressBarCat.setVisibility(View.GONE);
        });
    }
}