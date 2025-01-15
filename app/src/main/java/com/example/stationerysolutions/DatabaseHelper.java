package com.example.stationerysolutions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 1;

    // User table
    private static final String TABLE_REGISTER = "Register";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_PHONE = "phone";
    private static final String COL_ID = "id";

    // Product table
    public static final String TABLE_PRODUCTS = "products";
    public static final String PRODUCT_ID = "_id";
    public static final String PRODUCT_NAME = "name";
    public static final String PRODUCT_PRICE = "price";
    public static final String PRODUCT_QUANTITY = "quantity";
    public static final String PRODUCT_IMAGE = "image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Register table
        db.execSQL("CREATE TABLE " + TABLE_REGISTER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_PHONE + " TEXT)");

        // Create the Products table with the image column
        String createTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PRODUCT_NAME + " TEXT, " +
                PRODUCT_PRICE + " REAL, " +
                PRODUCT_QUANTITY + " INTEGER, " +
                PRODUCT_IMAGE + " BLOB)";
        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Insert user method
    public boolean insertUser(String username, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_PASSWORD, password);
        contentValues.put(COL_PHONE, phone);
        long result = db.insert(TABLE_REGISTER, null, contentValues);
        db.close();
        return result != -1;
    }


    // Insert product
    public boolean insertProduct(String name, double price, int quantity, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, name);
        values.put(PRODUCT_PRICE, price);
        values.put(PRODUCT_QUANTITY, quantity);
        values.put(PRODUCT_IMAGE, image);
        long result = db.insert(TABLE_PRODUCTS, null, values);
        return result != -1;
    }

    // Get all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(" SELECT * FROM " + TABLE_PRODUCTS, null);
    }


    public boolean deleteProduct(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PRODUCTS, PRODUCT_ID + " = ?", new String[]{id});

        return rowsAffected > 0;
    }

    public boolean updateProduct(int productId, String productName, double price, int quantity, byte[] productImageByteArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, productName);
        values.put(PRODUCT_PRICE, price);
        values.put(PRODUCT_QUANTITY, quantity);
        values.put(PRODUCT_IMAGE, productImageByteArray);
        int result = db.update(TABLE_PRODUCTS, values, PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
        return result > 0;
    }

    public Cursor getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PRODUCTS, null, PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)}, null, null, null);
    }

    public Cursor getFilteredProducts(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM products WHERE name LIKE ?";
        String[] args = new String[]{"%" + query + "%"};
        return db.rawQuery(sql, args);
    }

}







