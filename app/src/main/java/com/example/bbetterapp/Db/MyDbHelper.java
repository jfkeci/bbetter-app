package com.example.bbetterapp.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bbetterapp.Models.User;

public class MyDbHelper extends SQLiteOpenHelper {

    //database name
    public static final String DATABASE_NAME = "bbetter.db";

    //table user
    public static final String TABLE_USER = "USER_TABLE";
    public static final String  COL11 = "USER_ID";
    public static final String  COL12 = "FIRST_NAME";
    public static final String  COL13 = "LAST_NAME";
    public static final String  COL14 = "USERNAME";
    public static final String  COL15 = "EMAIL";
    public static final String  COL16 = "GENDER";
    public static final String  COL17 = "AGE";
    public static final String  COL18 = "USER_NOTES_URL";
    public static final String  COL19 = "USER_EVENTS_URL";
    public static final String  COL110 = "USER_SESSIONS_URL";

    public MyDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_USER + "(" + "USER_ID TEXT PRIMARY KEY, " +
                "FIRST_NAME TEXT)" +
                "LAST_NAME TEXT)" +
                "USERNAME TEXT)" +
                "EMAIL TEXT)" +
                "GENDER TEXT)" +
                "AGE NUMBER)" +
                "USER_NOTES_URL TEXT)" +
                "USER_EVENTS_URL TEXT)" +
                "USER_SESSIONS_URL TEXT)");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public boolean setCurrentUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_USER);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL11, user.getUserId());
        contentValues.put(COL12, user.getFirstName());
        contentValues.put(COL13, user.getLastName());
        contentValues.put(COL14, user.getUserName());
        contentValues.put(COL15, user.getEmail());
        contentValues.put(COL16, user.getGender());
        contentValues.put(COL17, user.getAge());
        contentValues.put(COL18, user.getUserNotesUrl());
        contentValues.put(COL19, user.getUserEventsUrl());
        contentValues.put(COL110, user.getUserSessionsUrl());

        long isSet = db.insert(TABLE_USER, null, contentValues);

        if(isSet == -1){
            return false;
        }else{
            return true;
        }
    }

}
