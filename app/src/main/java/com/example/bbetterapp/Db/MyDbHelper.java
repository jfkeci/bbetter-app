package com.example.bbetterapp.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bbetterapp.Models.User;

public class MyDbHelper extends SQLiteOpenHelper {

    //database name
    private static final String DATABASE_NAME = "bbetter.db";

    //table user
    private static final String TABLE_USER = "USER_TABLE";
    private static final String  COL11 = "userId";
    private static final String  COL12 = "firstName";
    private static final String  COL13 = "lastName";
    private static final String  COL14 = "userName";
    private static final String  COL15 = "email";
    private static final String  COL16 = "gender";
    private static final String  COL17 = "age";
    private static final String  COL18 = "userNotesUrl";
    private static final String  COL19 = "userEventsUrl";
    private static final String  COL110 = "userSessionsUrl";

    public MyDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_USER +
                " ("+COL11+" TEXT PRIMARY KEY, " +
                COL12+" TEXT, " +
                COL13+" TEXT, " +
                COL14+" TEXT, " +
                COL15+" TEXT, " +
                COL16+" TEXT, " +
                COL17+" INTEGER, " +
                COL18+" TEXT, " +
                COL19+" TEXT, " +
                COL110+" TEXT);");

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

    public boolean userIsSet(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        int count = 0;
        while(res.moveToNext()){
            count=1;
        }
        if(count==1){
            return true;
        }else{
            return false;
        }
    }
}
