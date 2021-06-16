package com.example.bbetterapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.bbetterapp.Adapters.ArchivedNotesRecyclerAdapter;
import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Notes;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteArchiveActivity extends AppCompatActivity {

    private Utils utils;
    private MyDbHelper dbHelper;
    private Notes noteUtils;

    private ImageButton btnBack;
    private RecyclerView recyclerViewArchive;

    private ArchivedNotesRecyclerAdapter notesArchiveAdapter;

    private ArrayList<Notes> archivedNotesList;

    private Notes deletedNote;
    private Notes unarchivedNote;

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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                if(direction == ItemTouchHelper.RIGHT){
                    unarchivedNote = archivedNotesList.get(position);

                    if(utils.isNetworkAvailable()){

                        unarchivedNote.setSynced(1);
                        unarchivedNote.setNoteArchived(false);
                        unarchivedNote.setNoteUpdatedAt(null);

                        Call<Notes> call = ApiClient.getInstance().getApi().updateNote(unarchivedNote.getNoteId(), unarchivedNote);

                        call.enqueue(new Callback<Notes>() {
                            @Override
                            public void onResponse(Call<Notes> call, Response<Notes> response) {
                                if(!response.isSuccessful()){
                                    Utils.makeMyToast("Something went wrong\nplease try again", getApplicationContext());
                                }else{

                                    Notes updatedNote = response.body();

                                    updatedNote.setSynced(1);

                                    dbHelper.updateNote(updatedNote);
                                    archivedNotesList.remove(position);
                                    notesArchiveAdapter.notifyItemRemoved(position);
                                }
                            }

                            @Override
                            public void onFailure(Call<Notes> call, Throwable t) {
                                Utils.makeMyToast("Something went wrong\nplease try again", getApplicationContext());
                            }
                        });
                    }else{
                        archivedNotesList.remove(position);
                        notesArchiveAdapter.notifyItemRemoved(position);
                        unarchivedNote.setSynced(2);
                        unarchivedNote.setNoteArchived(false);
                        dbHelper.updateNote(unarchivedNote);
                    }
                }
                if(direction == ItemTouchHelper.LEFT){
                    deletedNote = archivedNotesList.get(position);

                    if(utils.isNetworkAvailable()){
                        Call<Notes> call = ApiClient.getInstance().getApi().deleteNote(deletedNote.getNoteId());

                        call.enqueue(new Callback<Notes>() {
                            @Override
                            public void onResponse(Call<Notes> call, Response<Notes> response) {
                                if(!response.isSuccessful()){
                                    Utils.makeMyToast("Something went wrong\n please try again", getApplicationContext());
                                }else{
                                    dbHelper.deleteNote(deletedNote.getNoteId());
                                    archivedNotesList.remove(position);
                                    notesArchiveAdapter.notifyItemRemoved(position);
                                }
                            }
                            @Override
                            public void onFailure(Call<Notes> call, Throwable t) {
                                Utils.makeMyToast("Something went wrong\n please try again", getApplicationContext());
                            }
                        });
                    }else{
                        deletedNote.setSynced(3);
                        dbHelper.updateNote(deletedNote);
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.myPink))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep_white)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.myGoodGreen))
                        .addSwipeRightActionIcon(R.drawable.ic_archive_white)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewArchive);
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