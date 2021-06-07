package com.example.bbetterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.Models.Sessions;
import com.example.bbetterapp.Models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

    public static String parseDate(String dbDate){ //2021-05-20T07:12:03.611Z
        String date ="";




        return date;//"dd.MM.yyyy 'at' HH:mm"
    }

    public static String convertDate(String dbDate ){//"dd.MM.yyyy 'at' HH:mm"
        String convertedDate ="";

        return convertedDate;//2021-05-20T07:12:03.611Z
    }

    public ArrayList<Notes> allNotesList(int archived_yn){
        MyDbHelper dbHelper;
        dbHelper = new MyDbHelper(context);

        ArrayList<Notes> dbNotes = new ArrayList<>();

        Cursor res = dbHelper.getAllNotesArchiveYN(archived_yn);

        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            boolean archived = false;

            if(archived_yn == 0){
                archived = false;
            }else if(archived_yn == 1){
                archived = true;
            }

            Notes note = new Notes(res.getString(0),
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4),
                    res.getString(5),
                    archived);

            dbNotes.add(0, note);
        }

        return dbNotes;
    }

    public Notes getNoteById(String id){
        MyDbHelper dbHelper;
        dbHelper = new MyDbHelper(context);
        Notes selectedNote;

        ArrayList<Notes> dbNotes = new ArrayList<>();

        Cursor res = dbHelper.getNoteById(id);

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
                    archived);

            dbNotes.add(0, note);
        }

        selectedNote = dbNotes.get(0);

        return selectedNote;
    }

    public String getMyUserId(){
        MyDbHelper dbHelper = new MyDbHelper(context);
        User user = dbHelper.getUser();
        return user.getUserId();
    }

    public String getDateNow(int type){
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdf3 = new SimpleDateFormat("dd.MM.yyyy");

        Date date = Calendar.getInstance().getTime();

        String dateTime = "";

        if(type == 1){
            dateTime = sdf1.format(date);
        }else if(type == 2){
            dateTime = sdf2.format(date);
        }else if(type == 3){
            dateTime = sdf3.format(date);
        }

        return dateTime;
    }

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

            mySessions.add(0, session);
        }

        return mySessions;
    }

    public ArrayList<Events> allEventsList(int checkedYN){
        ArrayList<Events> myEvents = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);

        Cursor res = dbHelper.getAllEventsByCheck(checkedYN);

        while(res.moveToNext()){
            boolean finished = false;

            if(checkedYN == 1){
                finished = true;
            }if(checkedYN == 0){
                finished = false;
            }

            Events event = new Events(
                    res.getString(0),
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4),
                    res.getInt(5),
                    finished,
                    res.getString(7)
            );

            myEvents.add(0, event);
        }

        return myEvents;
    }

    /*        Call<ArrayList<Notes>> call = ApiClient.getInstance().getApi().getNotes();

        call.enqueue(new Callback<ArrayList<Notes>>() {
            @Override
            public void onResponse(Call<ArrayList<Notes>> call, Response<ArrayList<Notes>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyLog("List of user response was not successful: ", ""+response.code());
                    return;
                }

                ArrayList<Notes> notes = response.body();
                notesAdapter = new NotesRecyclerAdapter(getActivity(), notes);
                recyclerView.setAdapter(notesAdapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onFailure(Call<ArrayList<Notes>> call, Throwable t) {

            }
        });*/

    /*    public void updateSessionsList(){

        sessionList.clear();

        Call<ArrayList<Sessions>> call = ApiClient.getInstance().getApi().getSessions();

        call.enqueue(new Callback<ArrayList<Sessions>>() {
            @Override
            public void onResponse(Call<ArrayList<Sessions>> call, Response<ArrayList<Sessions>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyToast("code: "+ response.code(), getActivity().getBaseContext());
                    return;
                }

                sessionList = response.body();
            }

            @Override
            public void onFailure(Call<ArrayList<Sessions>> call, Throwable t) {
                Utils.makeMyToast("message: "+t.getMessage(), getActivity().getBaseContext());
            }
        });
    }*/

}
