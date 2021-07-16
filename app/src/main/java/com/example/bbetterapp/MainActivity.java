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

public class MainActivity extends AppCompatActivity {

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

        if (dbHelper.userIsSet()) {

            if (utils.isNetworkAvailable()) {
                /*sessionUtils.syncSessionsApiDb(utils.getMyUserId());
                eventUtils.syncEventsApiDb();*/
                /*noteUtils.syncNotesApiDb(utils.getMyUserId());*/
            } else {
                Utils.makeMyToast("Couldn't sync, no network", this);
            }

            startActivity(new Intent(MainActivity.this, FragmentHolderActivity.class));
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.userIsSet()) {
                    if (utils.isNetworkAvailable()) {
                        /*sessionUtils.syncSessionsApiDb(utils.getMyUserId());
                        eventUtils.syncEventsApiDb();*/
                        /*syncNotesApiDb(utils.getMyUserId());*/
                    } else {
                        Utils.makeMyToast("Couldn't sync, no network", getApplicationContext());
                    }
                    startActivity(new Intent(MainActivity.this, FragmentHolderActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });
    }
}