package com.example.bbetterapp.Models;

import android.content.Context;
import android.database.Cursor;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sessions {

    String _id, userId;
    int sessionLength, sessionPoints;
    boolean sessionFinished;
    String createdAt;

    int synced;

    Context context;

    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------CONSTRUCTORS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    public Sessions(String _id, String userId, int sessionLength, int sessionPoints, boolean sessionFinished, String createdAt) {
        this._id = _id;
        this.userId = userId;
        this.sessionLength = sessionLength;
        this.sessionPoints = sessionPoints;
        this.sessionFinished = sessionFinished;
        this.createdAt = createdAt;
    }
    public Sessions() { }
    public Sessions(Context context) {
        this.context = context;
    }

    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------GETTERS/SETTERS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    public int isSynced() {
        return synced;
    }
    public void setSynced(int synced) {
        this.synced = synced;
    }
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


    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------DB ACTIONS AND UTILS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
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

            session.setSynced(res.getInt(6));

            mySessions.add(0, session);
        }

        return mySessions;
    }
    public ArrayList<Sessions> allNonSyncedSessions(){
        ArrayList<Sessions> mySessions = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);

        Cursor res = dbHelper.getAllSessions();

        while(res.moveToNext()){
            if(res.getInt(6) == 0){
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

                session.setSynced(res.getInt(6));

                mySessions.add(0, session);
            }
        }

        return mySessions;
    }

    public void syncSessionsApiDb(String userId){

        MyDbHelper dbHelper = new MyDbHelper(context);

        ArrayList<Sessions> dbSessions = allNonSyncedSessions();

        for(Sessions session : dbSessions){

            session.setSynced(1);

            Call<Sessions> call = ApiClient.getInstance().getApi().saveNewSession(session);

            call.enqueue(new Callback<Sessions>() {
                @Override
                public void onResponse(Call<Sessions> call, Response<Sessions> response) {
                    if(!response.isSuccessful()){
                        Utils.makeMyLog("Failed, Couldn't manage to sync sessions: ", ""+response.code());
                        return;
                    }
                }

                @Override
                public void onFailure(Call<Sessions> call, Throwable t) {
                    Utils.makeMyLog("Failed to sync: SESSION, message: ", ""+t.getMessage());
                }
            });
        }

        Call<ArrayList<Sessions>> call = ApiClient.getInstance().getApi().getAllUserSessions(userId);

        call.enqueue(new Callback<ArrayList<Sessions>>() {
            @Override
            public void onResponse(Call<ArrayList<Sessions>> call, Response<ArrayList<Sessions>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyLog("Failed, Couldn't manage to sync sessions: ", ""+response.code());
                    return;
                }

                ArrayList<Sessions> apiSessions = response.body();

                for(Sessions session : apiSessions){
                    if(session.isSynced() == 0){
                        session.setSynced(1);
                        dbHelper.addNewSession(session);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Sessions>> call, Throwable t) {
                Utils.makeMyLog("Failed to sync: SESSION, message: ", ""+t.getMessage());
            }
        });
    }
}
