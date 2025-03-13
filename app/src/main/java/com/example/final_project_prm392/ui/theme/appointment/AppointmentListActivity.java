package com.example.final_project_prm392.ui.theme.appointment;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.final_project_prm392.Repository.AppointmentRepository;
import com.example.final_project_prm392.databinding.ActivityAppointmentListBinding;
import com.example.final_project_prm392.model.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {

    private ActivityAppointmentListBinding binding;
    private AppointmentRepository appointmentRepository;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appointmentRepository = new AppointmentRepository();

        setupUI();
        setupRecyclerView();
        loadAppointments();
    }

    private void setupUI() {
        binding.backButton.setOnClickListener(v -> finish());

        binding.swipeRefreshLayout.setOnRefreshListener(this::loadAppointments);
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(
                appointment -> cancelAppointment(appointment),
                appointment -> {
                    // Navigate to reschedule screen
                    // This would be implemented in a future update
                    Toast.makeText(this, "Reschedule feature coming soon", Toast.LENGTH_SHORT).show();
                }
        );

        binding.appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.appointmentsRecyclerView.setAdapter(adapter);
    }

    private void loadAppointments() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please login to view appointments", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyStateLayout.setVisibility(View.GONE);

        appointmentRepository.getAppointmentsForUser(currentUser.getUid(), new AppointmentRepository.AppointmentsCallback() {
            @Override
            public void onSuccess(List<Appointment> appointments) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);

                if (appointments.isEmpty()) {
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                } else {
                    adapter.submitList(appointments);
                }
            }

            @Override
            public void onFailure(Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(AppointmentListActivity.this, "Failed to load appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelAppointment(Appointment appointment) {
        binding.progressBar.setVisibility(View.VISIBLE);

        appointmentRepository.updateAppointmentStatus(appointment.getId(), "cancelled", new AppointmentRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AppointmentListActivity.this, "Appointment cancelled", Toast.LENGTH_SHORT).show();
                loadAppointments();
            }

            @Override
            public void onFailure(Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AppointmentListActivity.this, "Failed to cancel appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

