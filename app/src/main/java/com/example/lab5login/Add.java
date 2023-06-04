package com.example.lab5login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class Add extends AppCompatActivity {

    Button add;
    TextView logout;
    AlertDialog dialog;
    RecyclerView recyclerView;

    EditText editName;
    EditText editPrice;
    EditText editDescription;
    ImageView imageView;
    DBHelper db;
    ArrayList<Product> list;
    ProductsAdapter adapter;
    String name;
    Double price;
    String description;
    String image;
    Uri imageUri;


    Product product;

    public static final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        add = findViewById(R.id.add);
        logout = findViewById(R.id.logout);
        recyclerView = findViewById(R.id.recyclerView);
        db = new DBHelper(this);
        list = new ArrayList<>();
        list = db.getAllProducts();
        product = new Product();
        adapter = new ProductsAdapter(list, this);
        recyclerView.setAdapter(adapter);

        buildDialog();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.close();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    private boolean isValid() {

        Log.d("TAG", "isValid: image " + image);
        if (editName.getText().toString().isEmpty() ||
                editPrice.getText().toString().isEmpty() ||
                editDescription.getText().toString().isEmpty() ||
                image==null
        ) {
            Toast.makeText(getBaseContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        editName = view.findViewById(R.id.editName);
        editPrice = view.findViewById(R.id.editPrice);
        editDescription = view.findViewById(R.id.editDescription);
        imageView = view.findViewById(R.id.image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        builder.setView(view);
        builder.setTitle("Enter name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isValid()) {
                            Log.d("TAG", "onClick: isValid in");
                            name = editName.getText().toString();
                            price = Double.parseDouble(editPrice.getText().toString());
                            description = editDescription.getText().toString();
                            product = new Product(0,name, price, description, image,"","");
                            if (db.insertProduct(product)) {
                                list = db.getAllProducts();
                                adapter = new ProductsAdapter(list, Add.this);
                                recyclerView.setAdapter(adapter);
                                Log.d("TAG", "onClick:list " + list);
                                Toast.makeText(Add.this, "item added successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Add.this, "Adding item failed", Toast.LENGTH_SHORT).show();
                            }
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


    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    imageUri = data.getData();
                    InputStream imageStream;
                    imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(selectedImage);
                    image = imageToString(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


        } else {
            Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
        }
    }

    private String imageToString(Bitmap bm) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}
