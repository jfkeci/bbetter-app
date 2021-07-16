package com.example.bbetterapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.NoteEditActivity;
import com.example.bbetterapp.R;
import com.example.bbetterapp.Utils;

import java.util.ArrayList;

public class ArchivedNotesRecyclerAdapter extends RecyclerView.Adapter<ArchivedNotesRecyclerAdapter.NotesViewHolder> {
    Context context;
    ArrayList<Notes> notesList = new ArrayList<>();

    private OnLongNoteClickedListener mListener;

    MyDbHelper dbHelper;

    public interface OnLongNoteClickedListener {
        void onNoteClicked(int position);
    }

    public void setOnLongItemClickListener(OnLongNoteClickedListener listener) {
        mListener = listener;
    }

    public ArchivedNotesRecyclerAdapter() {
    }

    public ArchivedNotesRecyclerAdapter(Context context, ArrayList<Notes> notes) {
        this.context = context;
        this.notesList = notes;
        dbHelper = new MyDbHelper(context);
    }

    public void setData(ArrayList<Notes> notesList) {
        this.notesList = notesList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.archived_note_card_layout, parent, false);

        return new NotesViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        if (notesList.size() >= 1) {
            if (notesList.get(position).isSynced() != 3) {
                String mainContent = Utils.cutString(notesList.get(position).getNoteContent(), 200);
                String mainTitle = Utils.cutString(notesList.get(position).getNoteTitle(), 30);

                holder.twTitle.setText(mainTitle);
                holder.twContent.setText(mainContent);
                holder.twDate.setText(notesList.get(position).getNoteCreatedAt());
            }
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {

        private TextView twTitle, twContent, twDate;

        public NotesViewHolder(@NonNull View itemView, final OnLongNoteClickedListener listener) {
            super(itemView);

            twTitle = itemView.findViewById(R.id.twNoteTitleArchive);
            twContent = itemView.findViewById(R.id.twNoteContentArchive);
            twDate = itemView.findViewById(R.id.twNoteDateTimeArchive);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onNoteClicked(position);
                            openNoteEditor(position, v);
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void openNoteEditor(int position, View view) {
        Intent intent = new Intent(context, NoteEditActivity.class);
        intent.putExtra("note_id", String.valueOf(notesList.get(position).getNoteId()));
        view.getContext().startActivity(intent);
    }

    public void filterList(ArrayList<Notes> filteredList) {
        notesList = filteredList;
        notifyDataSetChanged();
    }
}

