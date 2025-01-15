package com.example.stationerysolutions;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.bumptech.glide.Glide;

public class ProductAdapter extends CursorAdapter {

    private OnProductActionListener listener;

    public ProductAdapter(Context context, Cursor cursor, OnProductActionListener listener) {
        super(context, cursor, 0);  // 0 indicates no flags are needed for this case
        this.listener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_products, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productName = view.findViewById(R.id.productName);
        TextView productPrice = view.findViewById(R.id.productPrice);
        TextView productQuantity = view.findViewById(R.id.productQuantity);
        ImageView productImage = view.findViewById(R.id.productImage);
        Button editButton = view.findViewById(R.id.editButton);
        Button deleteButton = view.findViewById(R.id.deleteButton);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_NAME));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_QUANTITY));
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.PRODUCT_IMAGE));

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

        // Set edit button listener
        editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClicked(id);  // Pass the product ID to edit this product
            }
        });

        // Set delete button listener
        deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClicked(id);  // Pass the product ID to delete this product
            }
        });
    }

    public interface OnProductActionListener {
        void onEditClicked(int productId);
        void onDeleteClicked(int productId);
    }


}
