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
import com.example.bbetterapp.Models.Notes;
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

    private boolean deleted = false;
    private boolean archived = false;

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

                if (direction == ItemTouchHelper.RIGHT) {
                    checkedEvent = eventsList.get(position);
                    checkedEvent.setEventChecked(true);

                    if (utils.isNetworkAvailable()) {

                        checkedEvent.setSynced(1);

                        String eventId = checkedEvent.get_id();

                        Call<Events> call = ApiClient.getInstance().getApi().updateEvent(eventId, checkedEvent);

                        call.enqueue(new Callback<Events>() {
                            @Override
                            public void onResponse(Call<Events> call, Response<Events> response) {
                                if (!response.isSuccessful()) {
                                    Utils.makeMyToast("Something went wrong\n please try again ", getActivity());
                                } else {
                                    Events updatedEvent = response.body();

                                    boolean isUpdated = dbHelper.updateEvent(updatedEvent);

                                    if (isUpdated) {
                                        eventsList.remove(position);
                                        todoAdapter.notifyItemRemoved(position);
                                        eventsCheckedList.add(0, updatedEvent);
                                        checkedAdapter.notifyItemInserted(0);
                                    } else {
                                        Utils.makeMyToast("Something went wrong!", getActivity());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Events> call, Throwable t) {
                                Utils.makeMyToast("Something went wrong\n please try again ", getActivity());
                            }
                        });
                    } else {
                        checkedEvent.setSynced(2);
                        checkedEvent.setEventChecked(true);
                        dbHelper.updateEventCheckedState(checkedEvent.get_id(), 1);
                        dbHelper.updateEventSyncedState(checkedEvent.get_id(), 2);
                        eventsList.remove(position);
                        todoAdapter.notifyItemRemoved(position);
                    }
                }
                if (direction == ItemTouchHelper.LEFT) {
                    deletedEvent = eventsList.get(position);
                    Events undoEvent = deletedEvent;

                    if (deletedEvent.isSynced() != 0) {
                        if (utils.isNetworkAvailable()) {
                            deleted = true;
                            Call<Events> call = ApiClient.getInstance().getApi().deleteEvent(deletedEvent.get_id());

                            call.enqueue(new Callback<Events>() {
                                @Override
                                public void onResponse(Call<Events> call, Response<Events> response) {
                                    if (!response.isSuccessful()) {
                                        Utils.makeMyToast("Something went wrong\nplease try again", getActivity());
                                    } else {
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
                        } else {
                            deleted = false;
                            deletedEvent.setSynced(3);
                            dbHelper.updateEvent(deletedEvent);
                            eventsList.remove(position);
                            todoAdapter.notifyItemRemoved(position);
                        }
                    } else {
                        deleted = true;

                        int isDeleted = dbHelper.deleteEvent(deletedEvent.get_id());

                        if (isDeleted == 1) {
                            eventsList.remove(position);
                            todoAdapter.notifyItemRemoved(position);
                        }
                    }
                    Snackbar.make(recyclerViewToDo, Utils.cutString(deletedEvent.getEventTitle(), 8), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (deleted) {
                                if (utils.isNetworkAvailable()) {
                                    undoEvent.setSynced(1);
                                    Call<Events> undoCall = ApiClient.getInstance().getApi().saveNewEvent(undoEvent);

                                    undoCall.enqueue(new Callback<Events>() {
                                        @Override
                                        public void onResponse(Call<Events> call, Response<Events> response) {
                                            if (!response.isSuccessful()) {
                                                Utils.makeMyToast("Something went wrong", getActivity());
                                            } else {
                                                Events event = response.body();

                                                dbHelper.addNewEvent(event);
                                                eventsList.add(position, event);
                                                todoAdapter.notifyItemInserted(position);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Events> call, Throwable t) {
                                            Utils.makeMyToast("Something went wrong, message: " + t.getMessage(), getActivity());
                                        }
                                    });
                                } else {
                                    dbHelper.addNewEvent(undoEvent);
                                    eventsList.add(position, undoEvent);
                                    todoAdapter.notifyItemInserted(position);
                                }
                            } else {
                                dbHelper.updateEvent(undoEvent);
                                eventsList.add(position, undoEvent);
                                todoAdapter.notifyItemInserted(position);
                            }
                        }
                    }).show();
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

    public void getData() {
        eventsList = eventsUtils.allEventsList(0);
        todoAdapter.setData(eventsList);
        todoAdapter.notifyDataSetChanged();
        eventsCheckedList = eventsUtils.allEventsList(1);
        checkedAdapter.setData(eventsCheckedList);
        checkedAdapter.notifyDataSetChanged();
    }

    public void changeRecycleViewSize(int a, int b) {
        ConstraintLayout.LayoutParams lpTodo = (ConstraintLayout.LayoutParams) constraintLayoutToDo.getLayoutParams();
        ConstraintLayout.LayoutParams lpChecked = (ConstraintLayout.LayoutParams) constraintLayoutChecked.getLayoutParams();

        lpChecked.height = a;
        lpTodo.height = b;
        constraintLayoutToDo.setLayoutParams(lpTodo);
        constraintLayoutChecked.setLayoutParams(lpChecked);
    }
}