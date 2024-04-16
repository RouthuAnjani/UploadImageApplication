package com.example.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;
    private ImageView imageView;
    private SQLiteDatabase database;
    private String lastImageUri; // Variable to store the URI of the last selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        database = openOrCreateDatabase("image_db", MODE_PRIVATE, null);
        createTable();
        loadImageFromDatabase();
    }

    private void loadImageFromDatabase() {
        Cursor cursor = database.rawQuery("SELECT * FROM images", null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("uri");
            if (columnIndex != -1) {
                lastImageUri = cursor.getString(columnIndex); // Save the URI to lastImageUri
                imageView.setImageURI(Uri.parse(lastImageUri));
            } else {
                Toast.makeText(this, "Column index not found", Toast.LENGTH_SHORT).show();
                imageView.setImageResource(R.drawable.ic_launcher_background); // Set placeholder image
            }
        } else {
            // No image found in the database, set a default image
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void createTable() {
        database.execSQL("CREATE TABLE IF NOT EXISTS images(uri TEXT)");
    }

    public void selectImageFromGallery(android.view.View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
            saveImageToDatabase(selectedImageUri.toString());
        } else {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToDatabase(String uri) {
        try {
            database.beginTransaction(); // Begin transaction
            database.execSQL("DELETE FROM images");
            ContentValues values = new ContentValues();
            values.put("uri", uri);
            database.insert("images", null, values);
            database.setTransactionSuccessful(); // Set transaction as successful
            lastImageUri = uri; // Update lastImageUri with the new URI
            Toast.makeText(this, "Image saved to database", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image to database", Toast.LENGTH_SHORT).show();
        } finally {
            database.endTransaction(); // End transaction
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastImageUri != null) {
            imageView.setImageURI(Uri.parse(lastImageUri)); // Load the last image URI
        }
    }
}
