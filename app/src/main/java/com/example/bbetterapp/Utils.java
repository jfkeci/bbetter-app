package com.example.bbetterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class Utils {

    public static void makeMyToast(String message, Context context){
        Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
    }
    public static void makeMyMessage(String title, String message, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public static void makeMyLog(String title, String message){
        Log.d("" + title, "" + message);
    }

    public static boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0){
            return false;
        }else{
            return true;
        }
    }
    public static boolean etValidation(EditText editText, String message, Context context){
        editText.requestFocus();
        Utils.makeMyToast(""+message, context);
        return false;
    }

}
