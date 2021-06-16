package com.example.bbetterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.Models.Sessions;
import com.example.bbetterapp.Models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {

    Context context;

    public Utils(Context context) {
        this.context = context;
    }

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

    public static String cutString(String string, int length){
        String main_string = "";

        if(string.length() > length){
            for(int i=0;i<length;i++){
                main_string = main_string+string.charAt(i);
            }
            main_string = main_string+"...";
        }if(string.length()<length){
            main_string = string;
        }

        return main_string;
    }

    public String parseDateApiToDb(String dbDate){ //2021-05-20T07:12:03.611Z

        String date =   dbDate.charAt(8) +
                        dbDate.charAt(9) + "." +
                        dbDate.charAt(5) +
                        dbDate.charAt(6) + "." +
                        dbDate.charAt(0) +
                        dbDate.charAt(1) +
                        dbDate.charAt(2) +
                        dbDate.charAt(3) + ". at " +
                        dbDate.charAt(11) +
                        dbDate.charAt(12) + ":" +
                        dbDate.charAt(14) +
                        dbDate.charAt(15);


        return date;//"dd.MM.yyyy 'at' HH:mm"
    }

    public static String parseDateDbToApi(String dbDate ){//"dd.MM.yyyy 'at' HH:mm"
        String convertedDate ="";



        return convertedDate;//2021-05-20T07:12:03.611Z
    }

    public String getMyUserId(){
        MyDbHelper dbHelper = new MyDbHelper(context);
        User user = dbHelper.getUser();
        return user.getUserId();
    }

    public String getDateNow(int type){
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf4 = new SimpleDateFormat("HH-mm-sss");

        Date date = Calendar.getInstance().getTime();

        String dateTime = "";

        if(type == 1){
            dateTime = sdf1.format(date);
        }else if(type == 2){
            dateTime = sdf2.format(date);
        }else if(type == 3){
            dateTime = sdf3.format(date);
        }else if(type == 4){
            dateTime = sdf4.format(date);
        }

        return dateTime;
    }

    public boolean isNetworkAvailable() {
        boolean isAvailable;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else {
            isAvailable = false;
        }

        return isAvailable;
    }
}
