package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.Models.Sessions;
import com.example.bbetterapp.Models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    Button startButton;

    MyDbHelper dbHelper;
    Utils utils;
    Notes noteUtils;
    Events eventUtils;
    Sessions sessionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDbHelper(this);
        utils = new Utils(this);
        noteUtils = new Notes(this);
        eventUtils = new Events(this);
        sessionUtils = new Sessions(this);

        startButton = findViewById(R.id.startButton);

        if(dbHelper.userIsSet()){

            if(utils.isNetworkAvailable()){
                syncSessionsApiDb(utils.getMyUserId());
                /*syncEventsApiDb(utils.getMyUserId());
                syncNotesApiDb(utils.getMyUserId());*/
            }else{
                Utils.makeMyToast("Couldn't sync, no network", this);
            }

            startActivity(new Intent(MainActivity.this, FragmentHolderActivity.class));
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbHelper.userIsSet()){
                    if(utils.isNetworkAvailable()){
                        syncSessionsApiDb(utils.getMyUserId());
                        /*syncEventsApiDb(utils.getMyUserId());
                        syncNotesApiDb(utils.getMyUserId());*/
                    }else{
                        Utils.makeMyToast("Couldn't sync, no network", this);
                    }
                    startActivity(new Intent(MainActivity.this, FragmentHolderActivity.class));
                }else{
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });
    }

    private void syncNotesApiDb(String userId){
        Call<ArrayList<Notes>> call = ApiClient.getInstance().getApi().getAllUserNotes(userId);

        call.enqueue(new Callback<ArrayList<Notes>>() {
            @Override
            public void onResponse(Call<ArrayList<Notes>> call, Response<ArrayList<Notes>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyLog("Couldn't manage to sync notes: ", ""+response.code());
                    return;
                }

                ArrayList<Notes> apiNotes = response.body();

                for(Notes note : apiNotes){
                    dbHelper.addNewNote(note);
                    dbHelper.setNoteSynced(note.getNoteId(), 1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Notes>> call, Throwable t) {
                Utils.makeMyToast("Didn't manage to sync notes ", getApplicationContext());
            }
        });
    }
    private void syncEventsApiDb(String userId){
        Call<ArrayList<Events>> call = ApiClient.getInstance().getApi().getAllUserEvents(userId);

        call.enqueue(new Callback<ArrayList<Events>>() {
            @Override
            public void onResponse(Call<ArrayList<Events>> call, Response<ArrayList<Events>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyLog("Couldn't manage to sync notes: ", ""+response.code());
                    return;
                }

                ArrayList<Events> apiEvents = response.body();

                for(Events event : apiEvents){
                    dbHelper.addNewEvent(event);
                    dbHelper.setEventSynced(event.get_id(), 1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {
                Utils.makeMyToast("Didn't manage to sync events ", getApplicationContext());
            }
        });
    }
    private void syncSessionsApiDb(String userId){

        ArrayList<Sessions> dbSessions = sessionUtils.allNonSyncedSessions();

        for(Sessions session : dbSessions){

            session.setSynced(1);

            Call<Sessions> call = ApiClient.getInstance().getApi().saveNewSession(session);

            call.enqueue(new Callback<Sessions>() {
                @Override
                public void onResponse(Call<Sessions> call, Response<Sessions> response) {
                    if(!response.isSuccessful()){
                        Utils.makeMyLog("Couldn't manage to sync sessions: ", ""+response.code());
                        return;
                    }
                }

                @Override
                public void onFailure(Call<Sessions> call, Throwable t) {

                }
            });

        }

        Call<ArrayList<Sessions>> call = ApiClient.getInstance().getApi().getAllUserSessions(userId);

        call.enqueue(new Callback<ArrayList<Sessions>>() {
            @Override
            public void onResponse(Call<ArrayList<Sessions>> call, Response<ArrayList<Sessions>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyLog("Couldn't manage to sync sessions: ", ""+response.code());
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

            }
        });
    }
}