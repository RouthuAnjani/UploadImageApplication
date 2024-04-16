package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "imageDB";
    private static final int DATABASE_VERSION = 1;
    public static final String IMAGE_TABLE = "images";
    public static final String IMAGE_COLUMN = "imageUri";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + IMAGE_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + IMAGE_COLUMN + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE);
        onCreate(db);
    }

    public void insertImageUri(String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE_COLUMN, imageUri);
        db.insert(IMAGE_TABLE, null, contentValues);
    }

    public String getLastImageUri() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + IMAGE_COLUMN + " FROM " + IMAGE_TABLE + " ORDER BY ID DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String lastImageUri = null;
        if (cursor != null && cursor.moveToFirst()) {
            lastImageUri = cursor.getString(cursor.getColumnIndex(IMAGE_COLUMN));
            cursor.close();
        }
        return lastImageUri;
    }
}
