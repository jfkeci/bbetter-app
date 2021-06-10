package com.example.bbetterapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.bbetterapp.Adapters.NotesRecyclerAdapter;
import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.Models.User;
import com.example.bbetterapp.NoteArchiveActivity;
import com.example.bbetterapp.R;
import com.example.bbetterapp.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesFragment extends Fragment {

    private Utils utils;
    private MyDbHelper dbHelper;
    private Notes noteUtils;

    private RecyclerView recyclerView;
    private NotesRecyclerAdapter notesAdapter;

    private EditText etNoteTitle, etNoteContent;
    private Button btnAddNote, btnOpenArchive;

    private String uid = "";
    private ArrayList<Notes> notesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        utils = new Utils(getActivity());
        dbHelper = new MyDbHelper(getActivity());
        noteUtils = new Notes(getActivity());

        recyclerView = v.findViewById(R.id.recyclerViewNotes);

        etNoteTitle = v.findViewById(R.id.etNoteTitle);
        etNoteContent = v.findViewById(R.id.etNoteContent);

        btnAddNote = v.findViewById(R.id.btnAddNote);
        btnOpenArchive = v.findViewById(R.id.btnOpenArchive);

        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        btnOpenArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoteArchiveActivity.class);
                startActivity(intent);
            }
        });

        notesList = noteUtils.allNotesList(0);

        for(Notes mynote : notesList){
            Utils.makeMyLog("noteee title: ", ""+ mynote.getNoteTitle());
            Utils.makeMyLog("noteee content: ", ""+ mynote.getNoteContent());
            Utils.makeMyLog("noteee datetime: ", ""+ mynote.getNoteCreatedAt());
        }

        notesAdapter = new NotesRecyclerAdapter(getActivity(), notesList);
        recyclerView.setAdapter(notesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        notesAdapter.notifyDataSetChanged();

        notesAdapter.setOnLongItemClickListener(new NotesRecyclerAdapter.OnLongNoteClickedListener() {
            @Override
            public void onNoteClicked(int position) {
                notesAdapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    private void saveNote(){
        Notes newNote = new Notes();

        if(etNoteTitle.getText().toString().isEmpty() && etNoteContent.getText().toString().isEmpty()){
            Utils.makeMyToast("Write something!", getActivity());
        }else{

            newNote.setUserId(utils.getMyUserId());
            newNote.setNoteTitle(etNoteTitle.getText().toString());
            newNote.setNoteContent(etNoteContent.getText().toString());
            newNote.setNoteArchived(false);


            if(utils.isNetworkAvailable()){

                newNote.setSynced(1);

                Call<Notes> call = ApiClient.getInstance().getApi().saveNewNote(newNote);

                call.enqueue(new Callback<Notes>() {
                    @Override
                    public void onResponse(Call<Notes> call, Response<Notes> response) {
                        if(!response.isSuccessful()){
                            Utils.makeMyToast("Something went wrong\ntry again...", getActivity());
                        }else{

                            Notes savedNote = response.body();

                            boolean isInserted = dbHelper.addNewNote(savedNote);

                            if(isInserted){
                                dbHelper.setNoteSynced(savedNote.getNoteId(), 1);

                                etNoteTitle.setText("");
                                etNoteContent.setText("");
                                getData();
                                closeKeyboard();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<Notes> call, Throwable t) {

                    }
                });


            }else{
                newNote.setNoteId("nt"+utils.getDateNow(4));
                newNote.setNoteCreatedAt(utils.getDateNow(1));
                newNote.setNoteUpdatedAt(utils.getDateNow(1));
                newNote.setSynced(0);

                boolean isInserted = dbHelper.addNewNote(newNote);
                if(isInserted){
                    etNoteTitle.setText("");
                    etNoteContent.setText("");
                    getData();
                    closeKeyboard();
                }else{
                    Utils.makeMyToast("Try again!", getActivity());
                }
            }


        }
    }

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData()
    {
        notesList = noteUtils.allNotesList(0);
        notesAdapter.setData(notesList);
    }

}