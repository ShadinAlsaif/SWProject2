package com.example.lab5login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProductDetailsActivity extends AppCompatActivity {

    ImageView imgBack;
    ImageView imgProduct;
    TextView productName;
    TextView productPrice;

    TextView productDescription;
    Button btnRent;
    Button btnReturn;
    Product product;
    int productId;
    DBHelper db;
    ArrayList<Rent> list;

    String endDate = "";
    String startDate = "";

    String rentDate = "";
    String returnDate = "";


    AlertDialog dialog;
    TextView txtRentDate;
    TextView txtReturnDate;
//    Button btnAddRent;

    SimpleDateFormat df;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        imgBack = findViewById(R.id.imgBack);
        imgProduct = findViewById(R.id.imgProductImage);
        productName = findViewById(R.id.txtProductName);
        productPrice = findViewById(R.id.txtProductPrice);
        productDescription = findViewById(R.id.txtProductDescription);
        btnRent = findViewById(R.id.btnRent);
        btnReturn = findViewById(R.id.btnReturn);
        product = new Product();
        db = new DBHelper(this);
        list = new ArrayList<>();
        df = new SimpleDateFormat("d-M-yyyy", Locale.ENGLISH);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (getIntent().getStringExtra("productName") != null) {

            String name = getIntent().getStringExtra("productName");
            product = db.getProductDetails(name);
            productId = product.getProductId();
            byte[] decodedString = Base64.decode(product.getProductImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgProduct.setImageBitmap(decodedByte);
            productName.setText(product.getProductName());
            productPrice.setText(String.valueOf(product.getProductPrice()));
            productDescription.setText(product.getProductDescription());

            buildDialog();

            btnRent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!product.getStartDate().isEmpty() && !product.getEndDate().isEmpty()) {
                        Date c = Calendar.getInstance().getTime();
                        String currentDate = df.format(c);
                        if (getDifference(currentDate, product.getEndDate()) <= 0) {
                            dialog.show();
                        } else {
                            Toast.makeText(getBaseContext(), "This product already rent", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog.show();
                    }
                }
            });
            btnReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "onClick: " + product.getStartDate());
                    Log.d("TAG", "onClick: " + product.getEndDate());
                    if (!product.getStartDate().isEmpty() && !product.getEndDate().isEmpty()) {
                        showAlertDialog();
                    } else {
                        Toast.makeText(getBaseContext(), "The product is not currently rented", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showRentDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        // Setting the selected date to the text view.
                        rentDate = selectedDayOfMonth + "-" + (selectedMonth + 1) + "-" + selectedYear;
                        Log.d("TAG", "onDateSet:rentDate " + rentDate);
                        Log.d("TAG", "onDateSet:endDate " + endDate);
                        txtRentDate.setText(rentDate);
                    }
                },
                year,
                month,
                day
        );

        datePickerDialog.setTitle("Select Return date");
        datePickerDialog.show();
    }

    private void showReturnDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        // Setting the selected date to the text view.
                        returnDate = selectedDayOfMonth + "-" + (selectedMonth + 1) + "-" + selectedYear;
                        Log.d("TAG", "onDateSet:rentDate " + rentDate);
                        Log.d("TAG", "onDateSet:endDate " + endDate);
                        txtReturnDate.setText(returnDate);
                    }
                },
                year,
                month,
                day
        );

        datePickerDialog.setTitle("Select Return date");
        datePickerDialog.show();
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
        builder.setTitle("Confirmation Message");
        builder.setMessage("Are you sure you want to return this product");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date c = Calendar.getInstance().getTime();
                String currentDate = df.format(c);
                product.setEndDate(currentDate);
                Log.d("TAG", "onClick: return getStartDate " + product.getStartDate());
                Log.d("TAG", "onClick: return getEndDate " + product.getEndDate());
                if (db.removeRent(product)) {
                    Toast.makeText(getBaseContext(), "This product has been successfully returned", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }


    public double getDifference(String currentDate, String rentDate) {
        try {
            Date date1 = df.parse(currentDate);
            Date date2 = df.parse(rentDate);

            long differenceInMillis = date2.getTime() - date1.getTime();
            long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);
            Log.d("TAG", "printDifference:differenceInDays " + differenceInDays);
            return (double) differenceInDays;

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.rent_dialog, null);

        txtRentDate = view.findViewById(R.id.txtRentDate);
        txtReturnDate = view.findViewById(R.id.txtReturnDate);

        txtRentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRentDatePicker();
            }
        });
        txtReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReturnDatePicker();
            }
        });


        builder.setView(view);
        builder.setTitle("Enter Rent details")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("TAG", "onClick: OK rentDate " + rentDate);
                        Log.d("TAG", "onClick: OK returnDate " + returnDate);
                        Date c = Calendar.getInstance().getTime();
                        String currentDate = df.format(c);
                        if (getDifference(rentDate, returnDate) > 0) {
                            if (getDifference(currentDate, rentDate) > 0) {
                                product.setStartDate(rentDate);
                                product.setEndDate(returnDate);
                                if (db.addRent(product)) {
                                    Toast.makeText(getBaseContext(), "This product has been successfully rented", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "The rent date must be after current date", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "The return date must be after rental date", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        db.close();

        dialog = builder.create();
    }

}