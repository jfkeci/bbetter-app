package com.example.bbetterapp.Models;

import android.content.Context;
import android.database.Cursor;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notes {
    String _id, userId, noteTitle, noteContent, createdAt, updatedAt;
    Boolean noteArchived;

    int synced;

    Context context;

    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------CONSTRUCTORS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    public Notes(String _id, String userId, String noteTitle, String noteContent, String createdAt, String updatedAt, Boolean noteArchived) {
        this._id = _id;
        this.userId = userId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.noteArchived = noteArchived;
    }

    public Notes() {
    }

    public Notes(Context context) {
        this.context = context;
    }

    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------GETTERS/SETTERS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    public int isSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public String getNoteId() {
        return _id;
    }

    public void setNoteId(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteCreatedAt() {
        return createdAt;
    }

    public void setNoteCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getNoteUpdatedAt() {
        return updatedAt;
    }

    public void setNoteUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getNoteArchived() {
        return noteArchived;
    }

    public void setNoteArchived(Boolean noteArchived) {
        this.noteArchived = noteArchived;
    }

    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------DB ACTIONS AND UTILS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/

    public Notes getNoteById(String id) {
        MyDbHelper dbHelper;
        dbHelper = new MyDbHelper(context);
        Notes selectedNote;

        ArrayList<Notes> dbNotes = new ArrayList<>();

        Cursor res = dbHelper.getNoteById(id);

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            boolean archived = false;

            if (res.getInt(6) == 0) {
                archived = false;
            } else if (res.getInt(6) == 1) {
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

    public ArrayList<Notes> allNotesList(int archived_yn) {
        MyDbHelper dbHelper;
        dbHelper = new MyDbHelper(context);

        ArrayList<Notes> dbNotes = new ArrayList<>();

        if (archived_yn == 0 || archived_yn == 1) {
            Cursor res = dbHelper.getAllNotesArchiveYN(archived_yn);
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                if (res.getInt(7) != 3) {
                    boolean archived = false;

                    if (archived_yn == 0) {
                        archived = false;
                    } else if (archived_yn == 1) {
                        archived = true;
                    }

                    Notes note = new Notes(res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            res.getString(3),
                            res.getString(4),
                            res.getString(5),
                            archived
                    );

                    note.setSynced(res.getInt(7));

                    dbNotes.add(0, note);
                }
            }
        } else if (archived_yn == 2) {
            Cursor res = dbHelper.getAllNotes();
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                if (res.getInt(7) != 3) {
                    boolean archived = false;

                    if (res.getInt(6) == 0) {
                        archived = false;
                    } else if (res.getInt(6) == 1) {
                        archived = true;
                    }

                    Notes note = new Notes(res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            res.getString(3),
                            res.getString(4),
                            res.getString(5),
                            archived
                    );

                    note.setSynced(res.getInt(7));

                    dbNotes.add(0, note);
                }
            }
        }

        return dbNotes;
    }

    public ArrayList<Notes> allNotes(int archived_yn) {
        MyDbHelper dbHelper;
        dbHelper = new MyDbHelper(context);

        ArrayList<Notes> dbNotes = new ArrayList<>();

        Cursor res = dbHelper.getAllNotesArchiveYN(archived_yn);
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            if (res.getInt(7) != 3) {
                boolean archived = false;

                if (archived_yn == 0) {
                    archived = false;
                } else if (archived_yn == 1) {
                    archived = true;
                }

                Notes note = new Notes(res.getString(0),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        archived
                );

                note.setSynced(res.getInt(7));

                dbNotes.add(0, note);
            }
        }
        return dbNotes;
    }

    public ArrayList<Notes> allNotesListBySync(int syncNum) {
        MyDbHelper dbHelper;
        dbHelper = new MyDbHelper(context);

        ArrayList<Notes> dbNotes = new ArrayList<>();

        Cursor res = dbHelper.getAllNotesSyncedYN(syncNum);
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            if (res.getInt(7) != 3) {
                boolean archived = false;
                if (res.getInt(6) == 0) {
                    archived = false;
                } else if (res.getInt(6) == 1) {
                    archived = true;
                }
                Notes note = new Notes(res.getString(0),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        archived
                );
                note.setSynced(res.getInt(7));
                dbNotes.add(0, note);
            }
        }

        return dbNotes;
    }

    public void syncNotesApiDb(String userId) {

        MyDbHelper dbHelper = new MyDbHelper(context);

        //SAVING OFFLINE DATA WHERE SYNCED = 0
        //UPDATING OFFLINE DATA WHERE SYNCED = 2
        //DELETING OFFLINE DATA WHERE SYNCED = 3
        //SAVING ONLINE DATA WHERE SYNCED = 0
        //UPDATING ONLINE DATA WHERE SYNCED = 2
        //DELETING ONLINE DATA WHERE SYNCED = 3

        Call<ArrayList<Notes>> call = ApiClient.getInstance().getApi().getAllUserNotes(userId);

        call.enqueue(new Callback<ArrayList<Notes>>() {
            @Override
            public void onResponse(Call<ArrayList<Notes>> call, Response<ArrayList<Notes>> response) {
                if (!response.isSuccessful()) {
                    Utils.makeMyLog("Couldn't manage to sync notes: ", "" + response.code());
                    return;
                }

                ArrayList<Notes> apiNotes = response.body();

                for (Notes note : apiNotes) {
                    dbHelper.addNewNote(note);
                    dbHelper.setNoteSynced(note.getNoteId(), 1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Notes>> call, Throwable t) {
                Utils.makeMyToast("Didn't manage to sync notes ", context);
            }
        });
    }
}
