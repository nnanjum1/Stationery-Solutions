package com.example.stationerysolutions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InsertProductActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText productNameInput, productPriceInput, productQuantityInput;
    private ImageView productImageView;
    private Button uploadImageButton, saveProductButton;
    private DatabaseHelper databaseHelper;

    private byte[] imageByteArray;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        // Initialize the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Initialize views
        productNameInput = findViewById(R.id.productNameInput);
        productPriceInput = findViewById(R.id.productPriceInput);
        productQuantityInput = findViewById(R.id.productQuantityInput);
        productImageView = findViewById(R.id.productImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        saveProductButton = findViewById(R.id.saveProductButton);

        // Set up ActivityResultLauncher for image selection
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    productImageView.setImageBitmap(imageBitmap);
                    imageByteArray = bitmapToByteArray(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Set onClickListeners for buttons
        uploadImageButton.setOnClickListener(v -> openFileChooser());
        saveProductButton.setOnClickListener(v -> saveProduct());
    }

    // Open file chooser to pick an image
    private void openFileChooser() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        imagePickerLauncher.launch(pickIntent);
    }

    // Convert Bitmap to byte array (Blob)
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // Save product to the SQLite database
    private void saveProduct() {
        String productName = productNameInput.getText().toString().trim();
        String productPriceStr = productPriceInput.getText().toString().trim();
        String productQuantityStr = productQuantityInput.getText().toString().trim();

        // Validate input fields
        if (productName.isEmpty()) {
            productNameInput.setError("Product name is required.");
            return;
        }
        if (productPriceStr.isEmpty()) {
            productPriceInput.setError("Product price is required.");
            return;
        }
        if (productQuantityStr.isEmpty()) {
            productQuantityInput.setError("Product quantity is required.");
            return;
        }
        if (imageByteArray == null) {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        double productPrice;
        int productQuantity;
        try {
            productPrice = Double.parseDouble(productPriceStr);
            productQuantity = Integer.parseInt(productQuantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or quantity.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productPrice <= 0 || productQuantity <= 0) {
            Toast.makeText(this, "Price and Quantity must be greater than zero.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save product data to SQLite
        boolean isInserted = databaseHelper.insertProduct(productName, productPrice, productQuantity, imageByteArray);

        if (isInserted) {
            Toast.makeText(this, "Product added successfully.", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity
        } else {
            Toast.makeText(this, "Failed to add product.", Toast.LENGTH_SHORT).show();
        }
    }
}
