package com.example.bbetterapp.Models;

import android.content.Context;
import android.database.Cursor;

import com.example.bbetterapp.Db.MyDbHelper;

import java.util.ArrayList;

public class Sessions {

    String _id, userId;
    int sessionLength, sessionPoints;
    boolean sessionFinished;
    String createdAt;

    int synced;

    Context context;

    public Sessions(String _id, String userId, int sessionLength, int sessionPoints, boolean sessionFinished, String createdAt) {
        this._id = _id;
        this.userId = userId;
        this.sessionLength = sessionLength;
        this.sessionPoints = sessionPoints;
        this.sessionFinished = sessionFinished;
        this.createdAt = createdAt;
    }

    public Sessions(Context context) {
        this.context = context;
    }

    public int isSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public Sessions() { }

    public String getSessionId() {
        return _id;
    }

    public void setSessionId(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(int sessionLength) {
        this.sessionLength = sessionLength;
    }

    public int getSessionPoints() {
        return sessionPoints;
    }

    public void setSessionPoints(int sessionPoints) {
        this.sessionPoints = sessionPoints;
    }

    public boolean isSessionFinished() {
        return sessionFinished;
    }

    public void setSessionFinished(boolean sessionFinished) {
        this.sessionFinished = sessionFinished;
    }

    public String getSessionCreatedAt() {
        return createdAt;
    }

    public void setSessionCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<Sessions> allSessionsList(){
        ArrayList<Sessions> mySessions = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);

        Cursor res = dbHelper.getAllSessions();

        while(res.moveToNext()){

            boolean finished = true;

            if(Integer.parseInt(res.getString(4)) == 1){
                finished = true;
            }if(Integer.parseInt(res.getString(4)) == 0){
                finished = false;
            }

            Sessions session = new Sessions(
                    res.getString(0),
                    res.getString(1),
                    res.getInt(2),
                    res.getInt(3),
                    finished,
                    res.getString(5)
            );

            mySessions.add(0, session);
        }

        return mySessions;
    }
}
