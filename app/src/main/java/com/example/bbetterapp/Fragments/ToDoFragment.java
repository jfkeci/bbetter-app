package com.example.bbetterapp.Fragments;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbetterapp.Adapters.EventsRecyclerAdapter;
import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.Models.User;
import com.example.bbetterapp.R;
import com.example.bbetterapp.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToDoFragment extends Fragment {


    private RecyclerView recyclerViewToDo;
    private RecyclerView recyclerViewChecked;

    private ConstraintLayout constraintLayoutToDo, constraintLayoutChecked;
    private TextView btnPending, btnDone;

    private long atTime;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");

    private MyDbHelper dbHelper;
    private EventsRecyclerAdapter todoAdapter;
    private EventsRecyclerAdapter checkedAdapter;

    public ArrayList<Events> eventsList;
    public ArrayList<Events> eventsCheckedList;

    private Events deletedEvent = new Events();
    private Events checkedEvent = new Events();

    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View todoView = inflater.inflate(R.layout.fragment_to_do, container, false);

        dbHelper = new MyDbHelper(getActivity());

        eventsList = new ArrayList<>();
        eventsCheckedList = new ArrayList<>();

        recyclerViewToDo = todoView.findViewById(R.id.recyclerViewToDo);
        recyclerViewChecked = todoView.findViewById(R.id.recyclerViewToDoDone);
        constraintLayoutToDo = todoView.findViewById(R.id.constraintLayoutToDo);
        constraintLayoutChecked = todoView.findViewById(R.id.constraintLayoutChecked);
        btnPending = todoView.findViewById(R.id.btnPending);
        btnDone = todoView.findViewById(R.id.btnDone);

        User user = dbHelper.getUser();

        /*uid = user.getUserId();*/

        uid = "60b627a3af4b11459077b6df";

        btnPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRecycleViewSize(550, 1100);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRecycleViewSize(1100, 550);
            }
        });

        updateEventsList();

        checkedAdapter = new EventsRecyclerAdapter(getActivity(), eventsCheckedList, 1);
        recyclerViewChecked.setAdapter(checkedAdapter);
        RecyclerView.LayoutManager checkedLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewChecked.setLayoutManager(checkedLayoutManager);

        todoAdapter = new EventsRecyclerAdapter(getActivity(), eventsList, 0);
        recyclerViewToDo.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewToDo.setLayoutManager(layoutManager);

        return todoView;
    }

    public void updateEventsList(){

        Call<ArrayList<Events>> call = ApiClient.getInstance().getApi().getEvents(uid);

        call.enqueue(new Callback<ArrayList<Events>>() {
            @Override
            public void onResponse(Call<ArrayList<Events>> call, Response<ArrayList<Events>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyToast("code: "+ response.code(), getActivity());
                    return;
                }

                eventsList = response.body();

                for (Events event : eventsList){
                    Utils.makeMyLog("event title: ", ""+event.getEventTitle());
                    Utils.makeMyLog("event created at: ", ""+event.getEventCreatedAt());
                    Utils.makeMyLog("event uid: ", ""+event.getUserId());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {
                Utils.makeMyToast("message: "+t.getMessage(), getActivity());
            }
        });
    }

    public void updateCheckedEventsList(){

        Call<ArrayList<Events>> call = ApiClient.getInstance().getApi().getEvents(uid);

        call.enqueue(new Callback<ArrayList<Events>>() {
            @Override
            public void onResponse(Call<ArrayList<Events>> call, Response<ArrayList<Events>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyToast("code: "+ response.code(), getActivity().getBaseContext());
                    return;
                }

                eventsCheckedList = response.body();
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {
                Utils.makeMyToast("message: "+t.getMessage(), getActivity().getBaseContext());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData()
    {
        todoAdapter.setData(eventsList);
        checkedAdapter.setData(eventsCheckedList);
    }

    public void changeRecycleViewSize(int a, int b){
        ConstraintLayout.LayoutParams lpTodo = (ConstraintLayout.LayoutParams) constraintLayoutToDo.getLayoutParams();
        ConstraintLayout.LayoutParams lpChecked = (ConstraintLayout.LayoutParams) constraintLayoutChecked.getLayoutParams();

        lpChecked.height = a;
        lpTodo.height = b;
        constraintLayoutToDo.setLayoutParams(lpTodo);
        constraintLayoutChecked.setLayoutParams(lpChecked);
    }

}