package com.example.bbetterapp.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.Models.Sessions;
import com.example.bbetterapp.Models.User;

public class MyDbHelper extends SQLiteOpenHelper {

    //database name
    private static final String DATABASE_NAME = "bbetter.db";

    //table user
    private static final String USER_TABLE = "USER_TABLE";
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
    //table apps
    private static final String APPS_TABLE = "APPS_TABLE";
    private static final String  COL21 = "userId";
    private static final String  COL22 = "appName";
    private static final String  COL23 = "appStatus";
    private static final String  COL24 = "packageName";
    private static final String  COL25 = "synced";

    //table events
    private static final String EVENTS_TABLE = "EVENTS_TABLE";
    private static final String  COL31 = "_id";
    private static final String  COL32 = "userId";
    private static final String  COL33 = "eventTitle";
    private static final String  COL34 = "eventDetails";
    private static final String  COL35 = "eventDate";
    private static final String  COL36 = "eventType";
    private static final String  COL37 = "eventChecked";
    private static final String  COL38 = "eventCreatedAt";
    private static final String  COL39 = "synced";

    //table notes
    private static final String NOTES_TABLE = "NOTES_TABLE";
    private static final String  COL41 = "_id";
    private static final String  COL42 = "userId";
    private static final String  COL43 = "noteTitle";
    private static final String  COL44 = "noteContent";
    private static final String  COL45 = "noteCreatedAt";
    private static final String  COL46 = "noteUpdatedAt";
    private static final String  COL47 = "noteArchived";
    private static final String  COL48 = "synced";

    //table sessions
    private static final String SESSIONS_TABLE = "SESSIONS_TABLE";
    private static final String  COL51 = "_id";
    private static final String  COL52 = "userId";
    private static final String  COL53 = "sessionLength";
    private static final String  COL54 = "sessionPoints";
    private static final String  COL55 = "sessionFinished";
    private static final String  COL56 = "sessionCreatedAt";
    private static final String  COL57 = "synced";

    public MyDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create users table
        db.execSQL("create table " + USER_TABLE +
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

        //create events table
        db.execSQL("create table " + EVENTS_TABLE +
                " ("+COL31+" TEXT PRIMARY KEY, " +
                COL32+" TEXT, " +
                COL33+" TEXT, " +
                COL34+" TEXT, " +
                COL35+" TEXT, " +
                COL36+" INTEGER, " +
                COL37+" INTEGER DEFAULT 0, " +
                COL38+" TEXT, " +
                COL39+" INTEGER DEFAULT 0);");

        //create notes table
        db.execSQL("create table " + NOTES_TABLE +
                " ("+COL41+" TEXT PRIMARY KEY, " +
                COL42+" TEXT, " +
                COL43+" TEXT, " +
                COL44+" TEXT, " +
                COL45+" TEXT, " +
                COL46+" TEXT, " +
                COL47+" INTEGER DEFAULT 0, " +
                COL48+" INTEGER DEFAULT 0);");

        //create sessions table
        db.execSQL("create table " + SESSIONS_TABLE +
                " ("+COL51+" TEXT PRIMARY KEY, " +
                COL52+" TEXT, " +
                COL53+" INTEGER, " +
                COL54+" INTEGER, " +
                COL55+" INTEGER DEFAULT 0, " +
                COL56+" TEXT, " +
                COL57+" INTEGER DEFAULT 0);");

        //create apps table
        db.execSQL("create table " + APPS_TABLE +
                " ("+COL21+" TEXT PRIMARY KEY, " +
                COL22+" TEXT, " +
                COL23+" INTEGER DEFAULT 0, " +
                COL24+" TEXT, " +
                COL25+" INTEGER DEFAULT 0);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + APPS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SESSIONS_TABLE);
        onCreate(db);
    }

    public boolean setCurrentUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+USER_TABLE);

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

        long isSet = db.insert(USER_TABLE, null, contentValues);

        if(isSet == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean userIsSet(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + USER_TABLE, null);
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

    public User getUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + USER_TABLE, null);
        User user = new User();
        while(res.moveToNext()){
            user.setUserId(res.getString(0));
            user.setFirstName(res.getString(1));
            user.setLastName(res.getString(2));
            user.setUserName(res.getString(3));
            user.setEmail(res.getString(4));
            user.setGender(res.getString(5));
            user.setAge(res.getInt(6));
            user.setUserNotesUrl(res.getString(7));
            user.setUserEventsUrl(res.getString(8));
            user.setUserSessionsUrl(res.getString(9));
        }
        return user;
    }

    /*------------------------------------------------------------------------------------------------------------*/
    /* EVENTS CRUD */
    /*------------------------------------------------------------------------------------------------------------*/
    public boolean addNewEvent(Events event){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL31, event.get_id());
        contentValues.put(COL32, event.getUserId());
        contentValues.put(COL33, event.getEventTitle());
        contentValues.put(COL34, event.getEventDetails());
        contentValues.put(COL35, event.getEventDate());
        contentValues.put(COL36, event.getEventType());
        contentValues.put(COL38, event.getEventCreatedAt());
        contentValues.put(COL39, event.isSynced());

        long result = db.insert(EVENTS_TABLE, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllEventsByCheck(int check){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + EVENTS_TABLE+" WHERE eventChecked='" + check + "'", null);
        return res;
    }

    public boolean eventSetChecked(String eventId, int checkYN){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL39, checkYN);
        db.update(EVENTS_TABLE, contentValues, "EVENT_ID = ?", new String[]{eventId});

        return true;
    }

    public boolean addNewEventWithId(Events event){
        int check = 1;
        SQLiteDatabase db = this.getWritableDatabase();

        if(event.isEventChecked()){
            check = 1;
        }else{
            check = 0;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL31, event.get_id());
        contentValues.put(COL32, event.getUserId());
        contentValues.put(COL33, event.getEventTitle());
        contentValues.put(COL34, event.getEventDetails());
        contentValues.put(COL35, event.getEventDate());
        contentValues.put(COL36, event.getEventType());
        contentValues.put(COL38, event.getEventCreatedAt());

        long result = db.insert(EVENTS_TABLE,null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean setEventSynced(String eventId, int sycned){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL39, sycned);

        db.update(EVENTS_TABLE, contentValues, "_id = ?", new String[]{eventId});

        return true;
    }

    public Cursor getAllEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + EVENTS_TABLE, null);
        return res;
    }

    public boolean updateEventState(String eventId, int eventState){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL31, eventId);
        contentValues.put(COL36, eventState);
        db.update(EVENTS_TABLE, contentValues, "_id = ?", new String[]{eventId});

        return true;
    }

    public Integer deleteEvent(String eventId){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(EVENTS_TABLE, "_id = ?", new String[]{eventId});
    }

    /*------------------------------------------------------------------------------------------------------------*/
    /* NOTES CRUD */
    /*------------------------------------------------------------------------------------------------------------*/
    public boolean addNewNote(Notes note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL41, note.getNoteId());
        contentValues.put(COL42, note.getUserId());
        contentValues.put(COL43, note.getNoteTitle());
        contentValues.put(COL44, note.getNoteContent());
        contentValues.put(COL45, note.getNoteCreatedAt());
        contentValues.put(COL46, note.getNoteUpdatedAt());
        contentValues.put(COL48, note.isSynced());

        long result = db.insert(NOTES_TABLE, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getNoteById(String noteId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + NOTES_TABLE+" WHERE _id='" + noteId + "';", null);
        return res;
    }

    public Cursor getAllNotesArchiveYN(int archived){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + NOTES_TABLE +" WHERE noteArchived='" + archived + "';", null);
        return res;
    }

    public Cursor getAllNotesSyncedYN(int synced){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + NOTES_TABLE +" WHERE synced='" + synced + "';", null);
        return res;
    }

    public Cursor getAllNotes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + NOTES_TABLE +";", null);
        return res;
    }

    public boolean updateNote(Notes note){
        int archived = 0;

        if(note.getNoteArchived()){
            archived = 1;
        }

        String nid = note.getNoteId();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL43, note.getNoteTitle());
        contentValues.put(COL44, note.getNoteContent());
        contentValues.put(COL46, note.getNoteUpdatedAt());
        contentValues.put(COL47, archived);
        contentValues.put(COL48, note.isSynced());

        db.update(NOTES_TABLE, contentValues, "_id = ?", new String[]{nid});

        return true;
    }

    public boolean setNoteSynced(String noteId, int synced){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL48, synced);

        db.update(NOTES_TABLE, contentValues, "_id = ?", new String[]{noteId});

        return true;
    }

    public boolean setNoteArchived(String noteId, int yn){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL47, yn);

        db.update(NOTES_TABLE, contentValues, "_id = ?", new String[]{noteId});

        return true;
    }
    public Integer deleteNote(String noteId){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(NOTES_TABLE, "_id = ?", new String[]{noteId});
    }

    /*------------------------------------------------------------------------------------------------------------*/
    /* SESSIONS CRUD */
    /*------------------------------------------------------------------------------------------------------------*/
    public boolean addNewSession(Sessions session){

        SQLiteDatabase db = this.getWritableDatabase();

        int finished = 0;

        String points = "";

        if(session.isSessionFinished()){
            finished = 1;
            points = String.valueOf(session.getSessionPoints());
        }else{
            finished = 0;
            points = "-"+String.valueOf(session.getSessionPoints());
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL51, session.getSessionId());
        contentValues.put(COL52, session.getUserId());
        contentValues.put(COL53, session.getSessionLength());
        contentValues.put(COL54, points);
        contentValues.put(COL55, finished);
        contentValues.put(COL56, session.getSessionCreatedAt());
        contentValues.put(COL57, session.isSynced());


        long result = db.insert(SESSIONS_TABLE, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean setSessionSynced(String sessionId, int sycned){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL57, sycned);

        db.update(SESSIONS_TABLE, contentValues, "_id = ?", new String[]{sessionId});

        return true;
    }

    public Cursor getAllSessions(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + SESSIONS_TABLE, null);
        return res;
    }

}
