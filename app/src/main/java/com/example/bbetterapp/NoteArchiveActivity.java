package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.bbetterapp.Adapters.ArchivedNotesRecyclerAdapter;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Notes;

import java.util.ArrayList;

public class NoteArchiveActivity extends AppCompatActivity {

    private Utils utils;
    private MyDbHelper dbHelper;
    private Notes noteUtils;

    private ImageButton btnBack;
    private RecyclerView recyclerViewArchive;

    private ArchivedNotesRecyclerAdapter notesArchiveAdapter;

    private ArrayList<Notes> archivedNotesList;

    private Notes deletedNote = new Notes();
    private Notes unarchivedNote = new Notes();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_archive);

        utils = new Utils(this);
        dbHelper = new MyDbHelper(this);
        noteUtils = new Notes(this);

        btnBack = findViewById(R.id.btnBack);
        recyclerViewArchive = findViewById(R.id.recycleViewArchive);

        archivedNotesList = noteUtils.allNotesList(1);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initRecyclerViewArchivedNotes();
    }

    private void initRecyclerViewArchivedNotes() {
        notesArchiveAdapter = new ArchivedNotesRecyclerAdapter(this, archivedNotesList);
        recyclerViewArchive.setAdapter(notesArchiveAdapter);
        RecyclerView.LayoutManager checkedLayoutManager = new LinearLayoutManager(this);
        recyclerViewArchive.setLayoutManager(checkedLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData()
    {
        archivedNotesList = noteUtils.allNotesList(1);
        notesArchiveAdapter.setData(archivedNotesList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}