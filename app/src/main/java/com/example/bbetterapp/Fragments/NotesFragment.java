package com.example.bbetterapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

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

    private Notes archivedNote;
    private Notes deletedNote;

    boolean deleted = false;

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

        notesAdapter = new NotesRecyclerAdapter(getActivity(), notesList);
        recyclerView.setAdapter(notesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        notesAdapter.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.UP) {
                    archivedNote = notesList.get(position);
                    archivedNote.setNoteArchived(true);

                    if (utils.isNetworkAvailable()) {
                        archivedNote.setSynced(1);
                        archivedNote.setNoteUpdatedAt(null);

                        Call<Notes> call = ApiClient.getInstance().getApi().updateNote(archivedNote.getNoteId(), archivedNote);

                        call.enqueue(new Callback<Notes>() {
                            @Override
                            public void onResponse(Call<Notes> call, Response<Notes> response) {
                                if (!response.isSuccessful()) {
                                    Utils.makeMyToast("Something went wrong\ntry again...", getActivity());
                                } else {
                                    Notes updatedNote = response.body();

                                    /*updatedNote.setNoteUpdatedAt(utils.parseDateApiToDb(updatedNote.getNoteUpdatedAt()));*/
                                    /*updatedNote.setNoteCreatedAt(utils.parseDateApiToDb(updatedNote.getNoteCreatedAt()));*/

                                    boolean isUpdated = dbHelper.updateNote(updatedNote);

                                    if (isUpdated) {
                                        removeData(position);
                                    } else {
                                        Utils.makeMyToast("Something went wrong!", getActivity());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Notes> call, Throwable t) {
                                Utils.makeMyToast("Something went wrong!", getActivity());
                                Utils.makeMyLog("failed to update: NOTE, message: ", "" + t.getMessage());
                            }
                        });
                    } else {
                        archivedNote.setSynced(2);
                        archivedNote.setNoteUpdatedAt(utils.getDateNow(1));
                        archivedNote.setNoteArchived(true);

                        boolean isArchived = dbHelper.updateNote(archivedNote);

                        if (isArchived) {
                            removeData(position);
                        } else {
                            Utils.makeMyToast("Something went wrong!", getActivity());
                        }
                    }
                }
                if (direction == ItemTouchHelper.DOWN) {
                    deletedNote = notesList.get(position);

                    Notes undoNote = deletedNote;

                    if (deletedNote.isSynced() != 0) {
                        if (utils.isNetworkAvailable()) {
                            deleted = true;
                            Call<Notes> call = ApiClient.getInstance().getApi().deleteNote(deletedNote.getNoteId());

                            call.enqueue(new Callback<Notes>() {
                                @Override
                                public void onResponse(Call<Notes> call, Response<Notes> response) {
                                    if (!response.isSuccessful()) {
                                        Utils.makeMyToast("Something went wrong\ntry again...", getActivity());
                                    } else {
                                        dbHelper.deleteNote(deletedNote.getNoteId());

                                        removeData(position);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Notes> call, Throwable t) {
                                    Utils.makeMyToast("Something went wrong!", getActivity());
                                    Utils.makeMyLog("failed to delete: NOTE, message: ", "" + t.getMessage());
                                }
                            });
                        } else {
                            deleted = false;
                            deletedNote.setSynced(3);

                            boolean isDeleted = dbHelper.updateNote(deletedNote);

                            if (isDeleted) {
                                removeData(position);
                            }
                        }
                    } else {
                        deleted = true;
                        int isDeleted = dbHelper.deleteNote(deletedNote.getNoteId());

                        if (isDeleted == 1) {
                            removeData(position);
                        }
                    }

                    Snackbar.make(recyclerView, Utils.cutString(deletedNote.getNoteTitle(), 8), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (deleted) {
                                undoNote.setSynced(1);
                                Call<Notes> undoCall = ApiClient.getInstance().getApi().saveNewNote(undoNote);
                                undoCall.enqueue(new Callback<Notes>() {
                                    @Override
                                    public void onResponse(Call<Notes> call, Response<Notes> response) {
                                        if (!response.isSuccessful()) {
                                            Utils.makeMyLog("Failed to undo", "");
                                        } else {
                                            Notes note = response.body();

                                            dbHelper.addNewNote(note);
                                            notesList.add(position, note);
                                            notesAdapter.notifyItemInserted(position);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Notes> call, Throwable t) {
                                        Utils.makeMyLog("Failed to undo", "");
                                    }
                                });
                            } else {
                                dbHelper.updateNote(undoNote);
                            }
                        }
                    }).show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        notesAdapter.setOnLongItemClickListener(new NotesRecyclerAdapter.OnLongNoteClickedListener() {
            @Override
            public void onNoteClicked(int position) {
                notesAdapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    private void saveNote() {
        Notes newNote = new Notes();

        if (etNoteTitle.getText().toString().isEmpty() && etNoteContent.getText().toString().isEmpty()) {
            Utils.makeMyToast("Write something!", getActivity());
        } else {

            newNote.setUserId(utils.getMyUserId());
            newNote.setNoteTitle(etNoteTitle.getText().toString());
            newNote.setNoteContent(etNoteContent.getText().toString());
            newNote.setNoteArchived(false);


            if (utils.isNetworkAvailable()) {

                newNote.setSynced(1);

                Call<Notes> call = ApiClient.getInstance().getApi().saveNewNote(newNote);

                call.enqueue(new Callback<Notes>() {
                    @Override
                    public void onResponse(Call<Notes> call, Response<Notes> response) {
                        if (!response.isSuccessful()) {
                            Utils.makeMyToast("Something went wrong\ntry again...", getActivity());
                        } else {

                            Notes savedNote = response.body();

                            boolean isInserted = dbHelper.addNewNote(savedNote);

                            if (isInserted) {
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
                        Utils.makeMyToast("Something went wrong\ntry again...", getActivity());
                    }
                });

            } else {
                newNote.setNoteId("nt" + utils.getDateNow(4));
                newNote.setNoteCreatedAt(utils.getDateNow(1));
                newNote.setNoteUpdatedAt(utils.getDateNow(1));
                newNote.setSynced(0);

                boolean isInserted = dbHelper.addNewNote(newNote);
                if (isInserted) {
                    etNoteTitle.setText("");
                    etNoteContent.setText("");
                    getData();
                    closeKeyboard();
                } else {
                    Utils.makeMyToast("Try again!", getActivity());
                }
            }
        }
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        notesList = noteUtils.allNotesList(0);
        notesAdapter.setData(notesList);
    }

    private void removeData(int position) {
        notesList.remove(position);
        notesAdapter.notifyItemRemoved(position);
    }

}