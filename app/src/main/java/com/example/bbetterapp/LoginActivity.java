package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.Models.Sessions;
import com.example.bbetterapp.Models.User;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Utils utils;

    private TextView twRegister;
    private EditText etEmail, etPassword;
    private Button btnLogin;

    private static MyDbHelper dbHelper;
    private Events eventUtils;
    private Notes noteUtils;
    private Sessions sessionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        twRegister = findViewById(R.id.twRegister);

        dbHelper = new MyDbHelper(getApplicationContext());
        utils = new Utils(this);

        eventUtils = new Events(getApplicationContext());
        noteUtils = new Notes(getApplicationContext());
        sessionUtils = new Sessions(getApplicationContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(utils.isNetworkAvailable()){
                    loginUser();
                }else{
                    Utils.makeMyToast("Please turn on your network!", getApplicationContext());
                }
            }
        });

        twRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void loginUser(){

        boolean fieldsFilled = true;

        final String password[] = new String[1];
        final String email[] = new String[1];

        if(Utils.isEmpty(etEmail)){
            fieldsFilled = Utils.etValidation(etEmail, "Email field required", getApplicationContext());
        }else{
            email[0] = etEmail.getText().toString();
            fieldsFilled = true;
        }

        if((etPassword.getText().toString().length()) < 5){
            if(Utils.isEmpty(etPassword)){
                fieldsFilled = Utils.etValidation(etPassword, "Password field required", getApplicationContext());
            }else{
                fieldsFilled = Utils.etValidation(etPassword, "Password should have at least 8 characters", getApplicationContext());
            }
        }else if((etPassword.getText().toString().length()) >= 5){
            password[0] = etPassword.getText().toString();
            fieldsFilled = true;
        }

        if(isNetworkAvailable()){
            Call<List<User>> call = ApiClient.getInstance().getApi().loginUser(email[0], password[0]);

            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if(!response.isSuccessful()){
                        Utils.makeMyToast("code: " +response.code(), getApplicationContext());
                        return;
                    }

                    List<User> users = response.body();

                    if(users.get(0).getEmail().equals(email[0]) && users.get(0).getPassword().equals(password[0])){
                        dbHelper.setCurrentUser(users.get(0));

                        syncNotesApiDb(users.get(0).getUserId());
                        syncEventsApiDb(users.get(0).getUserId());
                        syncSessionsApiDb(users.get(0).getUserId());

                        Utils.makeMyToast("Login successfull!", getApplicationContext());

                        startActivity(new Intent(getApplicationContext(), FragmentHolderActivity.class));
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Utils.makeMyLog("message: ", ""+t.getMessage());
                }
            });
        }
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
                    event.setSynced(1);
                    dbHelper.addNewEvent(event);
                    dbHelper.setEventSynced(event.get_id(), 1);

                    Call<Events> call1 = ApiClient.getInstance().getApi().updateEvent(event.get_id(), event);
                    call1.enqueue(new Callback<Events>() {
                        @Override
                        public void onResponse(Call<Events> call, Response<Events> response) {
                            if(!response.isSuccessful()){
                                Utils.makeMyLog("Failed to sync", "");
                            }
                        }

                        @Override
                        public void onFailure(Call<Events> call, Throwable t) {
                            Utils.makeMyLog("Failed to sync", "");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {
                Utils.makeMyToast("Didn't manage to sync events ", getApplicationContext());
            }
        });
    }
    private void syncSessionsApiDb(String userId){
        Call<ArrayList<Sessions>> call = ApiClient.getInstance().getApi().getAllUserSessions(userId);

        call.enqueue(new Callback<ArrayList<Sessions>>() {
            @Override
            public void onResponse(Call<ArrayList<Sessions>> call, Response<ArrayList<Sessions>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyLog("Couldn't manage to sync notes: ", ""+response.code());
                    return;
                }

                ArrayList<Sessions> apiSessions = response.body();

                for(Sessions session : apiSessions){
                    dbHelper.addNewSession(session);
                    dbHelper.setSessionSynced(session.getSessionId(), 1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Sessions>> call, Throwable t) {

            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isAvalible = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if(!isAvalible){
            Utils.makeMyToast("Can't login\nNo internet connection", getApplicationContext());
        }

        return isAvalible;
    }
}