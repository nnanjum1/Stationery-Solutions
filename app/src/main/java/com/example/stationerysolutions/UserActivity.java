package com.example.stationerysolutions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SearchView searchView;
    private ListView productListView;
    private TextView noProductText;
    private UserProductAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("StationerySolutionsPrefs", MODE_PRIVATE);


        searchView = findViewById(R.id.searchView);
        productListView = findViewById(R.id.productListView);
        noProductText = findViewById(R.id.noProductText);
        db = new DatabaseHelper(this);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Logout button functionality
        btnLogout.setOnClickListener(v -> clearSession());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Don't handle query submission
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Trim spaces from the beginning and end of the query string
                String trimmedText = newText.trim();

                // Fetch filtered products based on the trimmed text
                Cursor filteredCursor = db.getFilteredProducts(trimmedText); // Get filtered products from DB
                if (adapter != null) {
                    adapter.changeCursor(filteredCursor); // Update the adapter with filtered results
                }
                return false;
            }
        });

        fetchProducts();

    }


    private void clearSession() {
        // Clear SharedPreferences to log out the user
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    protected void onResume() {
        super.onResume();
        fetchProducts();
    }

    private void fetchProducts() {
        Cursor cursor = db.getAllProducts();

        // Check if the cursor has any rows
        if (cursor != null && cursor.getCount() > 0) {
            // If there are products, hide the no product text and display the list
            noProductText.setVisibility(View.GONE);
            productListView.setVisibility(View.VISIBLE);

            // Set the adapter with the product list and pass 'this' as the listener
            adapter = new UserProductAdapter(this, cursor, 0) ;
            productListView.setAdapter(adapter);
        } else {
            // If no products, show the no product text and hide the list
            noProductText.setVisibility(View.VISIBLE);
            productListView.setVisibility(View.GONE);
        }
    }







}
