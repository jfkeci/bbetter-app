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

    Button btnSaveEdit;
    EditText etTitle, etContent;

    MyDbHelper dbHelper;

    Intent intent;

    ArrayList<Notes> dbNotes;

    Notes selectedNote;

    Utils utils;

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

        /*dbNotes = new ArrayList<>();

        dbNotes.clear();

        Cursor res = dbHelper.getNoteById(noteID);

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
        }*/

        /*selectedNote = dbNotes.get(0);*/

        selectedNote = utils.getNoteById(noteId);

        etTitle.setText(selectedNote.getNoteTitle());
        etContent.setText(selectedNote.getNoteContent());

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date = Calendar.getInstance().getTime();
                selectedNote.setNoteUpdatedAt(sdf.format(date));
                selectedNote.setNoteUpdatedAt(etContent.getText().toString());
                selectedNote.setNoteUpdatedAt(etTitle.getText().toString());


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