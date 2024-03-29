package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.se.omapi.Session;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bbetterapp.Adapters.SessionsRecyclerAdapter;
import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.Models.Sessions;
import com.example.bbetterapp.Models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountDownActivity extends AppCompatActivity {

    public long START_TIME_IN_MILLIS;
    public long TimeLeftInMillis;

    private TextView TextViewCountDown, TextViewCredits;
    private Button ButtonStartPause;
    ImageView ivClock, ivArrow;

    private CountDownTimer countDownTimer;
    private boolean TimerRunning;
    private boolean TimerPaused = false;

    private ObjectAnimator anim;

    private SessionsRecyclerAdapter sessionsAdapter;
    RecyclerView recycleViewTimer;

    private MyDbHelper dbHelper;
    private Utils utils;
    private Sessions sessionUtils;

    private String uid;

    public SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
    public SimpleDateFormat sdfTime = new SimpleDateFormat("'at' HH:mm");

    Sessions newSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_count_down);

        TextViewCredits = findViewById(R.id.twCredits);
        TextViewCountDown = findViewById(R.id.twCountdown);
        ButtonStartPause = findViewById(R.id.btnStartPauseCoundown);
        ivClock = findViewById(R.id.ivTimerCircle);
        ivArrow = findViewById(R.id.ivTimerArrow);
        recycleViewTimer = findViewById(R.id.rwTimer);

        dbHelper = new MyDbHelper(this);
        utils = new Utils(this);
        sessionUtils = new Sessions(this);

        final Intent intent = getIntent();
        final String length_string = intent.getStringExtra("session_length");
        final long session_length = getSessionLength(length_string);
        START_TIME_IN_MILLIS = session_length;
        //START_TIME_IN_MILLIS = 2000;
        TimeLeftInMillis = START_TIME_IN_MILLIS;
        //int minutes = (int)START_TIME_IN_MILLIS / 1000 / 60;
        int seconds = (int) START_TIME_IN_MILLIS / 1000;

        User user = dbHelper.getUser();
        uid = user.getUserId();

        newSession = new Sessions();
        newSession.setUserId(utils.getMyUserId());
        newSession.setSessionLength(Integer.parseInt(getSessionPoints(length_string)));
        newSession.setSessionPoints(Integer.parseInt(getSessionPoints(length_string)));

        anim = ObjectAnimator.ofFloat(ivArrow, "rotation", 0, 360);
        anim.setDuration(1000);
        anim.setRepeatCount(seconds);
        anim.setRepeatMode(ObjectAnimator.RESTART);

        String creditCount = CountCredits();
        TextViewCredits.setText(creditCount);

        InitRecycleViewSessions();

        ButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimerRunning) {
                    pauseTimer();
                    anim.pause();
                    showMessage();
                } else {
                    ButtonStartPause.setBackgroundResource(R.drawable.button_green_square_bg);
                    ButtonStartPause.setText("Stop");
                    anim.start();
                    startTimer();
                }
            }
        });
        updateCountDownText();
    }

    public void saveNewSession(Sessions session) {
        if (utils.isNetworkAvailable()) {

            Call<Sessions> call = ApiClient.getInstance().getApi().saveNewSession(newSession);

            newSession.setSynced(1);

            call.enqueue(new Callback<Sessions>() {
                @Override
                public void onResponse(Call<Sessions> call, Response<Sessions> response) {
                    if (!response.isSuccessful()) {
                        Utils.makeMyToast("Something went wrong\nTry again...", getApplicationContext());
                    } else {

                        Sessions savedSession = response.body();

                        savedSession.setSynced(1);

                        dbHelper.addNewSession(savedSession);
                    }
                }

                @Override
                public void onFailure(Call<Sessions> call, Throwable t) {

                }
            });

        } else {
            newSession.setSessionId("ss" + utils.getDateNow(4));
            newSession.setSynced(0);
            newSession.setSessionCreatedAt(utils.getDateNow(1));
            dbHelper.addNewSession(newSession);
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                TimerRunning = false;
                ButtonStartPause.setText("Start");
                ButtonStartPause.setBackgroundResource(R.drawable.button_green_square_bg);
                TimeLeftInMillis = START_TIME_IN_MILLIS;
                updateCountDownText();
                newSession.setSessionFinished(true);

                saveNewSession(newSession);

                CountDownActivity.super.onBackPressed();
            }
        }.start();
        TimerRunning = true;
    }

    private void pauseTimer() {
        TimerPaused = true;
        countDownTimer.cancel();
        TimerRunning = false;
        ButtonStartPause.setText("Continue");
        ButtonStartPause.setBackgroundResource(R.drawable.button_green_square_bg);
    }

    private void updateCountDownText() {
        int minutes = (int) (TimeLeftInMillis / 1000) / 60;
        int seconds = (int) (TimeLeftInMillis / 1000) % 60;

        String TimeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        TextViewCountDown.setText(TimeLeftFormatted);
    }

    private void showMessage() {
        final AlertDialog areYouSure = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You can do it, just continue")
                .setPositiveButton("Quit", null)
                .setNegativeButton("Continue", null)
                .show();

        Button buttonQuit = areYouSure.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimerPaused) {
                    newSession.setSessionFinished(false);
                    saveNewSession(newSession);
                    CountDownActivity.super.onBackPressed();
                    areYouSure.dismiss();
                } else {
                    CountDownActivity.super.onBackPressed();
                    areYouSure.dismiss();
                }
            }
        });
        Button buttonCancel = areYouSure.getButton(AlertDialog.BUTTON_NEGATIVE);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonStartPause.setBackgroundResource(R.drawable.button_red_square_bg);
                ButtonStartPause.setText("Stop");
                anim.resume();
                startTimer();
                areYouSure.dismiss();
            }
        });
    }

    public void InitRecycleViewSessions() {
        sessionsAdapter = new SessionsRecyclerAdapter(this, sessionUtils.allSessionsList());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recycleViewTimer.setLayoutManager(gridLayoutManager);
        recycleViewTimer.setAdapter(sessionsAdapter);

        sessionsAdapter.notifyDataSetChanged();
    }

    public String CountCredits() {
        int credits = 0;
        String sCredits = "";
        ArrayList<Sessions> mySessions = new ArrayList<>();

        for (Sessions session : mySessions) {
            int points = session.getSessionPoints();
            credits += points;
        }
        if (credits > 0) {
            sCredits = "You have: " + String.valueOf(credits) + " credits";
        } else {
            sCredits = "Try, you can do it";
        }
        return sCredits;
    }

    public long getSessionLength(String sessionLength) {
        if (sessionLength.contains("10")) {
            return 600000;
        }
        if (sessionLength.contains("15")) {
            return 900000;
        }
        if (sessionLength.contains("20")) {
            return 1200000;
        }
        if (sessionLength.contains("25")) {
            return 1500000;
        }
        if (sessionLength.contains("30")) {
            return 1800000;
        }
        if (sessionLength.contains("35")) {
            return 2100000;
        }
        if (sessionLength.contains("40")) {
            return 2400000;
        }
        return 0;
    }

    public String getSessionPoints(String sessionLength) {
        if (sessionLength.contains("10")) {
            return "10";
        }
        if (sessionLength.contains("15")) {
            return "15";
        }
        if (sessionLength.contains("20")) {
            return "20";
        }
        if (sessionLength.contains("25")) {
            return "25";
        }
        if (sessionLength.contains("30")) {
            return "30";
        }
        if (sessionLength.contains("35")) {
            return "35";
        }
        if (sessionLength.contains("40")) {
            return "40";
        }
        return "0";
    }

    @Override
    public void onBackPressed() {
        showMessage();
    }

}