package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteEditActivity extends AppCompatActivity {

    private Button btnSaveEdit;
    private EditText etTitle, etContent;

    private MyDbHelper dbHelper;
    private Notes noteUtils;

    private Intent intent;

    private ArrayList<Notes> dbNotes;

    private Notes selectedNote;

    private Utils utils;

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        dbHelper = new MyDbHelper(this);
        noteUtils = new Notes(this);
        utils = new Utils(this);

        btnSaveEdit = findViewById(R.id.btnSaveEdit);
        etContent = findViewById(R.id.etNoteContent);
        etTitle = findViewById(R.id.etNoteTitle);

        intent = getIntent();
        String noteId = intent.getStringExtra("note_id");

        selectedNote = noteUtils.getNoteById(noteId);

        etTitle.setText(selectedNote.getNoteTitle());
        etContent.setText(selectedNote.getNoteContent());

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date = Calendar.getInstance().getTime();
                selectedNote.setNoteUpdatedAt(sdf.format(date));
                selectedNote.setNoteContent(etContent.getText().toString());
                selectedNote.setNoteTitle(etTitle.getText().toString());

                if (utils.isNetworkAvailable()) {
                    selectedNote.setSynced(1);

                    Call<Notes> call = ApiClient.getInstance().getApi().updateNote(selectedNote.getNoteId(), selectedNote);

                    call.enqueue(new Callback<Notes>() {
                        @Override
                        public void onResponse(Call<Notes> call, Response<Notes> response) {
                            if (!response.isSuccessful()) {
                                Utils.makeMyToast("Something went wrong\ntry again...", getApplicationContext());
                                Utils.makeMyLog("Failed to save note to: ", "API");
                            } else {
                                Notes updatedNote = response.body();

                                updatedNote.setSynced(1);
                                dbHelper.updateNote(updatedNote);
                            }
                        }

                        @Override
                        public void onFailure(Call<Notes> call, Throwable t) {
                            Utils.makeMyLog("Failed to update: NOTE, message: ", "" + t.getMessage());
                        }
                    });
                } else {
                    selectedNote.setNoteUpdatedAt(utils.getDateNow(1));
                    selectedNote.setSynced(2);
                    dbHelper.updateNote(selectedNote);
                }

                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}