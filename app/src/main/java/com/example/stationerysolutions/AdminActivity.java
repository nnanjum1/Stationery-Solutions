package com.example.stationerysolutions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button btnViewProducts = findViewById(R.id.btn_view_products);
        Button btnManageOrders = findViewById(R.id.btn_manage_orders);
        Button btnLogout = findViewById(R.id.btn_logout);
        sharedPreferences = getSharedPreferences("StationerySolutionsPrefs", MODE_PRIVATE);

        btnViewProducts.setOnClickListener(v->{
            Intent intent = new Intent(AdminActivity.this, AdminViewActivity.class);
            startActivity(intent);

        });

        btnLogout.setOnClickListener(v -> clearSession());

    }

    private void clearSession() {
        // Clear all session data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Show a confirmation message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
