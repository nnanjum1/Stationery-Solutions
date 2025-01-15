package com.example.stationerysolutions;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminViewActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView productListView;
    private TextView noProductText;
    private Button insertProductButton;
    private ProductAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        // Initializing views
        searchView = findViewById(R.id.searchView);
        productListView = findViewById(R.id.productListView);
        noProductText = findViewById(R.id.noProductText);
        insertProductButton = findViewById(R.id.insertProductButton);
        db = new DatabaseHelper(this); // Initialize DatabaseHelper

        // Fetch products from SQLite
        fetchProducts();

        // Insert Product Button
        insertProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminViewActivity.this, InsertProductActivity.class);
            startActivity(intent);
        });

        // Set up search functionality
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


    }

    @Override
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
            adapter = new ProductAdapter(this, cursor, new ProductAdapter.OnProductActionListener() {
                @Override
                public void onEditClicked(int productId) {
                    // Handle the edit click here
                    Intent intent = new Intent(AdminViewActivity.this, EditProductActivity.class);
                    intent.putExtra("productId", productId);  // Pass the product ID
                    startActivity(intent);
                }

                @Override
                public void onDeleteClicked(int productId) {
                    // Handle delete if needed
                    boolean isDeleted = db.deleteProduct(String.valueOf(productId));
                    if (isDeleted) {
                        Toast.makeText(AdminViewActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        fetchProducts();  // Refresh the list after deletion
                    }
                }
            });

            productListView.setAdapter(adapter);
        } else {
            // If no products, show the no product text and hide the list
            noProductText.setVisibility(View.VISIBLE);
            productListView.setVisibility(View.GONE);
        }
    }
}
