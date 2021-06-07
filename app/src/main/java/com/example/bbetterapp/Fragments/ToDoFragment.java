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
    private Utils utils;

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
        utils = new Utils(getActivity());

        eventsList = new ArrayList<>();
        eventsCheckedList = new ArrayList<>();

        recyclerViewToDo = todoView.findViewById(R.id.recyclerViewToDo);
        recyclerViewChecked = todoView.findViewById(R.id.recyclerViewToDoDone);
        constraintLayoutToDo = todoView.findViewById(R.id.constraintLayoutToDo);
        constraintLayoutChecked = todoView.findViewById(R.id.constraintLayoutChecked);
        btnPending = todoView.findViewById(R.id.btnPending);
        btnDone = todoView.findViewById(R.id.btnDone);

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

        eventsList = utils.allEventsList(0);
        eventsCheckedList = utils.allEventsList(1);


        for(int i=0; i<5; i++){
            Events event = new Events(
                    "res.getString("+i+")",
                    utils.getMyUserId(),
                    "res.getString(2)",
                    "res.getString(3)",
                    "res.getString(4)",
                    5,
                    false,
                    utils.getDateNow(1)
            );
            eventsList.add(event);
        }
        for(int i=5; i<10; i++){
            Events event = new Events(
                    "res.getString("+i+")",
                    utils.getMyUserId(),
                    "res.getString(2)",
                    "res.getString(3)",
                    "res.getString(4)",
                    5,
                    true,
                    utils.getDateNow(1)
            );
            eventsCheckedList.add(event);
        }



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

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData()
    {
        todoAdapter.setData(eventsList);
        todoAdapter.notifyDataSetChanged();
        checkedAdapter.setData(eventsCheckedList);
        checkedAdapter.notifyDataSetChanged();
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