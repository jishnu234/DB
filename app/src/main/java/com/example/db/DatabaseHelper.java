package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class DatabaseHelper extends SQLiteOpenHelper {


     static final String DB_NAME = "UserDB";
     static final int DB_VERSION = 1;
     static final String TABLE_NAME = "UserData";
     static final String COLUMN_ID = "id";
     static final String COLUMN_NAME = "name";
     static final String COLUMN_PHONE = "phone";


    Context mContext;
    public DatabaseHelper( Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create Table "+TABLE_NAME+"("+COLUMN_ID+" INTEGER " +
                "primary key AUTOINCREMENT, "+COLUMN_NAME+" text, "+COLUMN_PHONE+" text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("Drop Table if exists "+ TABLE_NAME);

    }

    public Boolean insertData(String name, String phone) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PHONE, phone);
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where "+COLUMN_PHONE+" = ?",
                new String[] {phone});
        if(cursor.getCount() == 0) {
            long result = db.insert(TABLE_NAME, null, contentValues);

            if (result == -1) {
                Toast.makeText(mContext, "Error while adding data", Toast.LENGTH_SHORT).show();
                return false;
            } else {

                return true;
            }
        } else {

            Toast.makeText(mContext, "User already found", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public Boolean updateData(String name, String phone, int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PHONE, phone);
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where id = ?",
                new String[]{String.valueOf(id)});
        if(cursor.getCount() > 0) {
            long result = db.update(TABLE_NAME, contentValues,
                    COLUMN_ID+" = ?", new String[] {String.valueOf(id)});

            if (result == -1) {
                Toast.makeText(mContext, "Error while updating data", Toast.LENGTH_SHORT).show();
                return false;
            } else {

                return true;
            }
        } else {

            Toast.makeText(mContext, "No data found", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public Boolean deleteData(int position) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where "+COLUMN_ID+" = ?",
                new String[] {String.valueOf(position)});
        if(cursor.getCount() > 0) {
            long result = db.delete(TABLE_NAME,COLUMN_ID+" = ?",
                    new String[] {String.valueOf(position)});

            if (result == -1) {
                Toast.makeText(mContext, "Error while updating data", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            Toast.makeText(mContext, "No data found", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public Cursor getData () {
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from "+TABLE_NAME, null);
        return cursor;
    }
}
