package com.example.bbetterapp.Fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bbetterapp.Adapters.EventsRecyclerAdapter;
import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Db.MyDbHelper;
import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.R;
import com.example.bbetterapp.Utils;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private CompactCalendarView calendarView;
    private EditText etEvent;
    private ImageButton btnNext, btnPrev, btnSaveEvent, btnSetTime;
    private TextView tvDate, tvTime;
    private RecyclerView recyclerViewCalendar;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm");
    private SimpleDateFormat currentMonthFormat = new SimpleDateFormat("MMMM yyyy");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private MyDbHelper dbHelper;
    private Utils utils;
    private Events eventUtils;
    private EventsRecyclerAdapter calendarAdapter;

    private ArrayList<Events> eventsList;

    private String eventTypeSelected;
    private String dateSelected="";
    private int nHour, nMinute;

    private long atTime;

    private int countFirst = 0;

    private Events checkedEvent = new Events();
    private Events deletedEvent = new Events();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        InitLayout(v);

        eventsList = eventUtils.allEventsList(0);

        InitRecycleView();
        InitEventTypeSpinner(v);
        InitCalendar();
        InitButtonSetTime();

        return v;
    }

    private void InitLayout(View v){
        utils = new Utils(getActivity());
        eventUtils = new Events(getActivity());
        dbHelper = new MyDbHelper(getActivity());

        calendarView = v.findViewById(R.id.calendarView);
        btnNext = v.findViewById(R.id.btnNext);
        btnPrev = v.findViewById(R.id.btnPrev);
        btnSaveEvent = v.findViewById(R.id.btnSaveEvent);
        btnSetTime = v.findViewById(R.id.btnSetTime);
        tvDate = v.findViewById(R.id.tvDate);
        tvTime = v.findViewById(R.id.tvTime);
        etEvent = v.findViewById(R.id.etEvent);
        recyclerViewCalendar = v.findViewById(R.id.recyclerViewCalendar);

        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        String currentDateandTime = currentMonthFormat.format(new Date());
        tvDate.setText(currentDateandTime);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollLeft();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollRight();
            }
        });

        btnSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewEvent();
            }
        });

        etEvent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    recyclerViewCalendar.setVisibility(View.INVISIBLE);
                }else{
                    recyclerViewCalendar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void saveNewEvent(){
        if(etEvent.getText().toString().length()<1){
            Utils.makeMyToast("Please write a description for your "+eventTypeSelected, getActivity());
        }else{

            Events newEvent = new Events();

            newEvent.setUserId(utils.getMyUserId());
            newEvent.setEventTitle(etEvent.getText().toString());
            newEvent.setEventDetails("type: "+eventTypeSelected);
            newEvent.setEventDate(dateSelected + " at " + tvTime.getText().toString());
            newEvent.setEventType(eventUtils.getEventTypeInt(eventTypeSelected));
            newEvent.setEventChecked(false);
            newEvent.setSynced(0);

            if(utils.isNetworkAvailable()){

                Call<Events> call = ApiClient.getInstance().getApi().saveNewEvent(newEvent);

                newEvent.setSynced(1);

                call.enqueue(new Callback<Events>() {
                    @Override
                    public void onResponse(Call<Events> call, Response<Events> response) {
                        if(!response.isSuccessful()){
                            Utils.makeMyToast("Something went wrong\ntry again...", getActivity());
                        }else{

                            Events savedEvent = response.body();

                            boolean isInserted = dbHelper.addNewEvent(savedEvent);

                            if(isInserted){

                                dbHelper.setEventSynced(savedEvent.get_id(), 1);

                                getData();

                                etEvent.setText("");

                                closeKeyboard();
                            }else{
                                Utils.makeMyToast("Failed to add new " + eventTypeSelected, getActivity());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Events> call, Throwable t) {
                        Utils.makeMyToast("message: "+t.getMessage(), getActivity());
                    }
                });

            }else{
                newEvent.set_id(utils.getDateNow(4)+eventTypeSelected);
                newEvent.setEventCreatedAt(utils.getDateNow(1));

                boolean isInserted = dbHelper.addNewEvent(newEvent);

                if(isInserted){

                    getData();

                    etEvent.setText("");

                    closeKeyboard();

                }else{
                    Utils.makeMyToast("Failed to add new " + eventTypeSelected, getActivity());
                }
            }
        }
    }

    private void InitCalendar() {
        calendarView.removeAllEvents();

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                dateSelected = epochToDate(dateToEpoch(dateClicked));
                getData();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                String currentMonth = currentMonthFormat.format(firstDayOfNewMonth);
                tvDate.setText(currentMonth);
            }
        });

    }

    private void InitRecycleView(){
        eventsList.clear();

        eventsList = eventUtils.allEventsList(0);

        calendarAdapter = new EventsRecyclerAdapter(getActivity(), eventsList, 0);
        recyclerViewCalendar.setAdapter(calendarAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewCalendar.setLayoutManager(layoutManager);

        calendarAdapter.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    deletedEvent = eventsList.get(position);

                    if(utils.isNetworkAvailable()){

                    }else{
                        String eventId = deletedEvent.get_id();

                        int deleted = dbHelper.deleteEvent(eventId);

                        if (deleted == 1) {
                            eventsList.remove(deletedEvent);
                            calendarAdapter.notifyItemRemoved(position);
                        }
                    }

                    Snackbar.make(recyclerViewCalendar, eventUtils.getEventTypeString(deletedEvent.getEventType()), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(utils.isNetworkAvailable()){

                            }else{
                                boolean undone = dbHelper.addNewEvent(deletedEvent);

                                if(undone) {
                                    eventsList.add(position, deletedEvent);
                                    calendarAdapter.notifyItemInserted(position);
                                }else{
                                    Utils.makeMyToast("Something went wrong!", getActivity());
                                }
                            }


                        }
                    }).show();

                }
                if (direction == ItemTouchHelper.RIGHT) {
                    checkedEvent = eventsList.get(position);

                    if(utils.isNetworkAvailable()){

                    }else{
                        boolean checked = dbHelper.eventSetChecked(checkedEvent.get_id(),1);

                        if (checked) {
                            dbHelper.setEventSynced(checkedEvent.get_id(), checkedEvent.isSynced());
                            Utils.makeMyToast("Awesome!", getActivity());
                            eventsList.remove(checkedEvent);
                            calendarAdapter.notifyDataSetChanged();
                        }else {
                            Utils.makeMyToast("Something went wrong!", getActivity());
                        }
                    }

                    Snackbar.make(recyclerViewCalendar, eventUtils.getEventTypeString(checkedEvent.getEventType()), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean checked = dbHelper.eventSetChecked(checkedEvent.get_id(), 0);
                            if (checked) {
                                eventsList.add(position, checkedEvent);
                                calendarAdapter.notifyItemInserted(position);
                            } else {
                                Utils.makeMyToast("Something went wrong!", getActivity());
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
        itemTouchHelper.attachToRecyclerView(recyclerViewCalendar);
    }

    private void InitButtonSetTime() {
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                nHour = hourOfDay;
                                nMinute = minute;
                                String time = nHour+":"+nMinute;
                                SimpleDateFormat f24hours = new SimpleDateFormat(
                                        "HH:mm"
                                );
                                try {
                                    Date date = f24hours.parse(time);
                                    tvTime.setText(f24hours.format(date));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        },24,0,true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(nHour,nMinute);
                timePickerDialog.show();
            }
        });

    }

    private void InitEventTypeSpinner(View v) {
        Spinner eventTypeSpinner = v.findViewById(R.id.spinnerEventType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.eventTypes, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(adapter);
        eventTypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        eventTypeSelected = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
    public void getData()
    {
        eventsList = eventUtils.allEventsByDateList(dateSelected, 0);
        calendarAdapter.setData(eventsList);
    }
    private void closeKeyboard(){
        recyclerViewCalendar.setVisibility(View.VISIBLE);
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String makeDateAndTime(String date, String time){
        String dateAndTime = date + " at " + time;
        return dateAndTime;
    }

    //Filters and converters
    public String epochToDate(Long epoch){
        String sDate;

        sDate = dateFormat.format(new Date(epoch*1000));

        return sDate;
    }
    public long dateToEpoch(Date date){
        long epoch;

        epoch = date.getTime() / 1000;

        return epoch;
    }
}