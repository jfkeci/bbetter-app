package com.example.bbetterapp.Fragments;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
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

import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

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
    private Events eventsUtils;
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
        eventsUtils = new Events(getActivity());
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

        eventsList = eventsUtils.allEventsList(0);
        eventsCheckedList = eventsUtils.allEventsList(1);

        checkedAdapter = new EventsRecyclerAdapter(getActivity(), eventsCheckedList, 1);
        recyclerViewChecked.setAdapter(checkedAdapter);
        RecyclerView.LayoutManager checkedLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewChecked.setLayoutManager(checkedLayoutManager);

        todoAdapter = new EventsRecyclerAdapter(getActivity(), eventsList, 0);
        recyclerViewToDo.setAdapter(todoAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewToDo.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();

                if(direction == ItemTouchHelper.RIGHT){
                    checkedEvent = eventsList.get(position);

                    if(utils.isNetworkAvailable()){
                        if(checkedEvent.isSynced() != 2){
                            checkedEvent.setSynced(1);
                        }

                        checkedEvent.setEventChecked(true);

                        Call<Events> call = ApiClient.getInstance().getApi().updateEvent(checkedEvent.get_id(), checkedEvent);

                        call.enqueue(new Callback<Events>() {
                            @Override
                            public void onResponse(Call<Events> call, Response<Events> response) {
                                if(!response.isSuccessful()){
                                    Utils.makeMyToast("Something went wrong\n please try again ", getActivity());
                                }else{
                                    Events updatedEvent = response.body();

                                    boolean isUpdated = dbHelper.updateEvent(updatedEvent);

                                    if(isUpdated){
                                        eventsList.remove(position);
                                        todoAdapter.notifyItemRemoved(position);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Events> call, Throwable t) {
                                Utils.makeMyToast("Something went wrong\n please try again ", getActivity());
                            }
                        });
                    }else{
                        checkedEvent.setSynced(2);
                        checkedEvent.setEventChecked(true);
                        dbHelper.updateEventCheckedState(checkedEvent.get_id(), 1);
                        dbHelper.updateEventSyncedState(checkedEvent.get_id(), 2);
                        eventsList.remove(position);
                        todoAdapter.notifyItemRemoved(position);
                    }
                }
                if(direction == ItemTouchHelper.LEFT){
                    deletedEvent = eventsList.get(position);

                    if(utils.isNetworkAvailable()){
                        Call<Events> call = ApiClient.getInstance().getApi().deleteEvent(deletedEvent.get_id());

                        call.enqueue(new Callback<Events>() {
                            @Override
                            public void onResponse(Call<Events> call, Response<Events> response) {
                                if(!response.isSuccessful()){
                                    Utils.makeMyToast("Something went wrong\nplease try again", getActivity());
                                }else{
                                    dbHelper.deleteEvent(deletedEvent.get_id());
                                    eventsList.remove(position);
                                    todoAdapter.notifyItemRemoved(position);
                                }
                            }

                            @Override
                            public void onFailure(Call<Events> call, Throwable t) {
                                Utils.makeMyToast("Something went wrong\nplease try again", getActivity());
                            }
                        });
                    }else{
                        deletedEvent.setSynced(3);
                        dbHelper.updateEvent(deletedEvent);
                        eventsList.remove(position);
                        todoAdapter.notifyItemRemoved(position);
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.myPink))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep_white)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.myGoodGreen))
                        .addSwipeRightActionIcon(R.drawable.ic_check_white)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewToDo);

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

/*
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull @org.jetbrains.annotations.NotNull RecyclerView recyclerView, @NonNull @org.jetbrains.annotations.NotNull RecyclerView.ViewHolder viewHolder, @NonNull @org.jetbrains.annotations.NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @org.jetbrains.annotations.NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.LEFT) {
                deletedEvent = eventsList.get(position);

                String event_id = String.valueOf(deletedEvent.get_id());

                int deleted = dbHelper.deleteEvent(event_id);

                if (deleted == 1) {
                    eventsList.remove(deletedEvent);
                    todoAdapter.notifyItemRemoved(position);
                }

                Snackbar.make(recyclerViewToDo, deletedEvent.getEventTitle(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean undone = dbHelper.addNewEventWithId(deletedEvent);

                        if (undone) {
                            eventsList.add(position, deletedEvent);
                            todoAdapter.notifyItemInserted(position);
                        } else {
                            Utils.makeMyToast("Something went wrong!", getActivity());
                        }
                    }
                }).show();

            }
            if (direction == ItemTouchHelper.RIGHT) {
                checkedEvent = eventsList.get(position);

                eventsList.remove(checkedEvent);
                todoAdapter.notifyItemRemoved(position);

                boolean checked = dbHelper.eventSetChecked(checkedEvent.get_id(),1);

                if (checked) {
                    Utils.makeMyToast("Awesome!", getActivity());
                    eventsCheckedList.add(checkedEvent);
                    checkedAdapter.notifyDataSetChanged();
                } else {
                    Utils.makeMyToast("Something went wrong!", getActivity());
                }
                Snackbar.make(recyclerViewToDo, checkedEvent.getEventTitle(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = dbHelper.eventSetChecked(checkedEvent.get_id(), 0);
                        if (checked) {
                            eventsList.add(position, checkedEvent);
                            todoAdapter.notifyItemInserted(position);
                            eventsCheckedList.remove(checkedEvent);
                            checkedAdapter.notifyDataSetChanged();
                        } else {
                            Utils.makeMyToast("Something went wrong!", getActivity());
                        }
                    }
                }).show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.myPink))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep_white)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.myGoodGreen))
                    .addSwipeRightActionIcon(R.drawable.ic_check_white)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewToDo);
*/
