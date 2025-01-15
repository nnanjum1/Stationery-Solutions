package com.example.stationerysolutions;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;  // Request code for image picker
    private ImageView productImageView;
    private Button chooseImageButton;
    private Button saveProductButton;
    private EditText productNameEditText, productPriceEditText, productQuantityEditText;
    private byte[]  productImageByteArray;
    private int productId;  // To hold the product ID

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Initialize views
        productNameEditText = findViewById(R.id.editProductName);
        productPriceEditText = findViewById(R.id.editProductPrice);
        productQuantityEditText = findViewById(R.id.editProductQuantity);
        productImageView = findViewById(R.id.productImageView);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        saveProductButton = findViewById(R.id.saveProductButton);

        db = new DatabaseHelper(this);  // Initialize the database helper

        // Get the product ID passed from AdminViewActivity
        Intent intent = getIntent();
        productId = intent.getIntExtra("productId", -1);

        // Fetch product data from database and populate fields
        fetchProductData(productId);

        // Set button listeners
        chooseImageButton.setOnClickListener(v -> openImageChooser());
        saveProductButton.setOnClickListener(v -> saveProduct());
    }

    private void fetchProductData(int productId) {
        // Fetch product details using product ID from the database
        Cursor cursor = db.getProductById(productId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_NAME));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_QUANTITY));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_IMAGE));

            // Populate the EditText fields with the product data
            productNameEditText.setText(name);
            productPriceEditText.setText(String.valueOf(price));
            productQuantityEditText.setText(String.valueOf(quantity));

            // Set the product image if available
            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                productImageView.setImageBitmap(bitmap);
                productImageByteArray = imageBytes;
            }

            cursor.close();
        }
    }

    private void openImageChooser() {
        // Intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");  // Only allow image types
        startActivityForResult(intent, PICK_IMAGE_REQUEST);  // Start the activity for result
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                productImageView.setImageBitmap(bitmap);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                productImageByteArray = byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveProduct() {
        // Get data from EditText fields
        String name = productNameEditText.getText().toString();
        String priceStr = productPriceEditText.getText().toString();
        String quantityStr = productQuantityEditText.getText().toString();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);


        // Save the product data to the database
        boolean isUpdated = db.updateProduct(productId, name, price, quantity, productImageByteArray);
        if (isUpdated) {
            Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity and return to the previous screen
        } else {
            Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to convert URI to byte array (optional)
//    private byte[] convertUriToByteArray(Uri uri) {
//        try {
//            InputStream inputStream = getContentResolver().openInputStream(uri);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = inputStream.read(buffer)) != -1) {
//                byteArrayOutputStream.write(buffer, 0, length);
//            }
//            return byteArrayOutputStream.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
