package com.example.bbetterapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bbetterapp.Adapters.NotesRecyclerAdapter;
import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.R;
import com.example.bbetterapp.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesFragment extends Fragment {

    private RecyclerView recyclerView;
    NotesRecyclerAdapter notesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        recyclerView = v.findViewById(R.id.recyclerViewNotes);

        Call<ArrayList<Notes>> call = ApiClient.getInstance().getApi().getNotes();

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
        });




        return v;
    }
}