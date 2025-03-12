package com.example.final_project_prm392.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.final_project_prm392.Adapter.CategoryAdapter;
import com.example.final_project_prm392.Adapter.TopDoctorsAdapter;
import com.example.final_project_prm392.ViewModel.MainViewModel;
import com.example.final_project_prm392.databinding.ActivityMainBinding;

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