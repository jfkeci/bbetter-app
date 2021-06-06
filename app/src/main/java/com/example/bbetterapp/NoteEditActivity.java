package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Notes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NoteEditActivity extends AppCompatActivity {

    private Button btnSaveEdit;
    private EditText etTitle, etContent;

    private MyDbHelper dbHelper;

    private Intent intent;

    private ArrayList<Notes> dbNotes;

    private Notes selectedNote;

    private Utils utils;

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        utils = new Utils(getApplicationContext());

        btnSaveEdit = findViewById(R.id.btnSaveEdit);
        etContent = findViewById(R.id.etNoteContent);
        etTitle = findViewById(R.id.etNoteTitle);

        dbHelper = new MyDbHelper(this);

        intent = getIntent();
        String noteId = intent.getStringExtra("note_id");

        selectedNote = utils.getNoteById(noteId);

        etTitle.setText(selectedNote.getNoteTitle());
        etContent.setText(selectedNote.getNoteContent());

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date = Calendar.getInstance().getTime();
                selectedNote.setNoteUpdatedAt(sdf.format(date));
                selectedNote.setNoteContent(etContent.getText().toString());
                selectedNote.setNoteTitle(etTitle.getText().toString());

                dbHelper.updateNote(selectedNote);
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}