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
import com.example.bbetterapp.Models.Notes;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    Button startButton;

    MyDbHelper dbHelper;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDbHelper(this);
        utils = new Utils(this);

        startButton = findViewById(R.id.startButton);

        boolean isUserSet = dbHelper.userIsSet();

        if(isUserSet){
            startActivity(new Intent(MainActivity.this, FragmentHolderActivity.class));

            /*if(isNetworkAvailable()){
                saveNonSyncedNotesAPI();
            }*/
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUserSet){

                    /*if(isNetworkAvailable()){
                        saveNonSyncedNotesAPI();
                    }*/

                    startActivity(new Intent(MainActivity.this, FragmentHolderActivity.class));
                }else{
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });
    }

    private void saveNonSyncedNotesAPI(){
        Call<ArrayList<Notes>> call = ApiClient.getInstance().getApi().getAllUserNotes(utils.getMyUserId());

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
                Utils.makeMyToast("Couldn't manage to sync notes ", getApplicationContext());
            }
        });
    }
    private void saveNonSyncedNotesDB(){
        ArrayList<Notes> dbNotes = allNotesListSync(0);

        for (Notes note : dbNotes) {
            Call<Notes> call = ApiClient.getInstance().getApi().createNote(note);
            call.enqueue(new Callback<Notes>() {
                @Override
                public void onResponse(Call<Notes> call, Response<Notes> response) {
                    if(!response.isSuccessful()){
                        Utils.makeMyLog("Couldn't manage to sync notes: ", ""+response.code());
                        return;
                    }

                    dbHelper.deleteNote(note.getNoteId());
                }

                @Override
                public void onFailure(Call<Notes> call, Throwable t) {
                    Utils.makeMyToast("Couldn't manage to sync notes ", getApplicationContext());
                }
            });
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isAvalible = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if(!isAvalible){
            Utils.makeMyToast("Can't sync\nNo internet connection", getApplicationContext());
        }

        return isAvalible;
    }

    public ArrayList<Notes> allNotesListSync(int syncNum){

        ArrayList<Notes> dbNotes = new ArrayList<>();

        Cursor res = dbHelper.getAllNotesSyncedYN(syncNum);
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            boolean archived = false;
            if(res.getInt(6) == 0){
                archived = false;
            }else if(res.getInt(6) == 1){
                archived = true;
            }
            Notes note = new Notes(res.getString(0),
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4),
                    res.getString(5),
                    archived
            );
            note.setSynced(res.getInt(7));
            dbNotes.add(0, note);
        }

        return dbNotes;
    }
}