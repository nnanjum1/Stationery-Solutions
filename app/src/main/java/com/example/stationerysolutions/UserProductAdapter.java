package com.example.stationerysolutions;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserProductAdapter extends CursorAdapter {

    public UserProductAdapter(Context context, Cursor cursor, int i) {
        super(context, cursor, 0);  // Passing 0 as the 'flags' parameter
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate the layout for each item
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_products, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productName = view.findViewById(R.id.productName);
        TextView productPrice = view.findViewById(R.id.productPrice);
        TextView productQuantity = view.findViewById(R.id.productQuantity);
        ImageView productImage = view.findViewById(R.id.productImage);
        Button btnAddToCart = view.findViewById(R.id.addToCartButton);
        Button editButton = view.findViewById(R.id.editButton);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        // Get product details from cursor
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_NAME));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_QUANTITY));
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_IMAGE));

        // Bind product data to views
        productName.setText(name);
        productPrice.setText("Price: BDT " + price);
        productQuantity.setText("Quantity: " + quantity);

        // Handle image loading from byte array (imageBytes)
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            productImage.setImageBitmap(bitmap);
        } else {
            productImage.setImageResource(R.drawable.img_5);  // Default image if no image available
        }

        btnAddToCart.setVisibility(View.VISIBLE);
        // Set the "Add to Cart" button action
        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(context, name + " added to cart", Toast.LENGTH_SHORT).show();
            // Handle cart addition here (e.g., add to a global cart object or local database)
        });
    }
}
