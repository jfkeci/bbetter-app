package com.example.bbetterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
