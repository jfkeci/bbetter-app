package com.example.bbetterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.Models.Session;
import com.example.bbetterapp.Models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {

    private void makeMyToast(String message, Context context){
        Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
    }
    public void makeMyMessage(String title, String message, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void makeMyLog(String title, String message){
        Log.d("" + title, ", message: " + message);
    }

}
