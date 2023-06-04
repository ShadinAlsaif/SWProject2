package com.example.lab5login;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "Login.db";

    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(username TEXT PRIMARY KEY, password TEXT)");
        MyDB.execSQL("create Table products(productId INTEGER PRIMARY KEY AUTOINCREMENT," +
                " productName TEXT , productPrice REAL, productDescription TEXT," +
                " productImage TEXT, startDate TEXT, endDate TEXT)");
//        MyDB.execSQL("create Table rents(rentId INTEGER PRIMARY KEY AUTOINCREMENT," +
//                " startDate TEXT , endDate TEXT,productId INTEGER," +
//                "FOREIGN KEY (productId) REFERENCES products (productId))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
        MyDB.execSQL("drop Table if exists products");
//        MyDB.execSQL("drop Table if exists rents");
    }

    public Boolean insertData(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = MyDB.insert("users", null, contentValues);
        if (result == -1) return false;
        else
            return true;
    }

    public Boolean insertProduct(Product product) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("productName", product.getProductName());
        contentValues.put("productPrice", product.getProductPrice());
        contentValues.put("productDescription", product.getProductDescription());
        contentValues.put("productImage", product.getProductImage());
        contentValues.put("startDate", product.getStartDate());
        contentValues.put("endDate", product.getEndDate());
        long result = MyDB.insert("products", null, contentValues);
        Log.d("TAG", "insertTask: " + result);
        return result != -1;
    }

    public Boolean deleteProduct(String id) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        String[] args = {id};
        int result = MyDB.delete("products", "productId=?", args);
        Log.d("TAG", "deleteProduct: result" + result);
        return result > 0;
    }

    @SuppressLint("Range")
    public ArrayList<Product> getAllProducts() {
        SQLiteDatabase dataBase = this.getReadableDatabase();
        ArrayList<Product> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM products";
            Cursor cursor = dataBase.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int productId = cursor.getInt(cursor.getColumnIndex("productId"));
                String name = cursor.getString(cursor.getColumnIndex("productName"));
                double price = cursor.getDouble(cursor.getColumnIndex("productPrice"));
                String description = cursor.getString(cursor.getColumnIndex("productDescription"));
                String image = cursor.getString(cursor.getColumnIndex("productImage"));
                String startDate = cursor.getString(cursor.getColumnIndex("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndex("endDate"));
                Product product = new Product(productId, name, price, description, image, startDate, endDate);
                list.add(product);
                cursor.moveToNext();
            }

            cursor.close();
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }

    @SuppressLint("Range")
    public Product getProductDetails(String productName) {
        Product product = new Product();
        SQLiteDatabase MyDB = this.getReadableDatabase();
        try {
            Cursor cursor = MyDB.rawQuery("Select * from products where productName = ?", new String[]{productName});
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int productId = cursor.getInt(cursor.getColumnIndex("productId"));
                String name = cursor.getString(cursor.getColumnIndex("productName"));
                double price = cursor.getDouble(cursor.getColumnIndex("productPrice"));
                String description = cursor.getString(cursor.getColumnIndex("productDescription"));
                String image = cursor.getString(cursor.getColumnIndex("productImage"));
                String startDate = cursor.getString(cursor.getColumnIndex("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndex("endDate"));
                product = new Product(productId, name, price, description, image, startDate, endDate);
                cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception e) {
            product = new Product();
        }
        return product;
    }

    public Boolean addRent(Product product) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("startDate", product.getStartDate());
        values.put("endDate", product.getEndDate());
        String whereClause = "productId = ?";
        String[] whereArgs = {String.valueOf(product.getProductId())};
        long result = database.update("products", values, whereClause, whereArgs);
        return result != -1;
    }

    public Boolean removeRent(Product product) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("startDate", product.getStartDate());
        values.put("endDate", product.getEndDate());
        String whereClause = "productId = ?";
        String[] whereArgs = {String.valueOf(product.getProductId())};
        long result = database.update("products", values, whereClause, whereArgs);
        return result != -1;
    }

    public Boolean checkusername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ?", new String[]{username});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public Boolean checkusernamepassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ? and password = ?", new String[]{username, password});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }
}

