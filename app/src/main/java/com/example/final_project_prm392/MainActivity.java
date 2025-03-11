package com.example.final_project_prm392;

import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.final_project_prm392.Adapter.CategoryAdapter;
import com.example.final_project_prm392.ViewModel.MainViewModel;
import com.example.final_project_prm392.databinding.ActivityMainBinding;

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
    }

    private void initCategory() {
        binding.progressBarCat.setVisibility(View.VISIBLE);
        viewModel.loadCategory().observe(this, categoryModels -> {
            binding.catView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.catView.setAdapter(new CategoryAdapter(categoryModels));
            binding.progressBarCat.setVisibility(View.GONE);
        });
    }
}