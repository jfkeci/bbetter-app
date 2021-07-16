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

public class Events {
    String _id;
    String userId, eventTitle, eventDetails, eventDate;
    int eventType;
    boolean eventChecked;
    String createdAt;
    int synced;

    Context context;

    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------CONSTRUCTORS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    public Events(String _id, String userId, String eventTitle, String eventDetails, String eventDate, int eventType, boolean eventChecked, String createdAt) {
        this._id = _id;
        this.userId = userId;
        this.eventTitle = eventTitle;
        this.eventDetails = eventDetails;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.eventChecked = eventChecked;
        this.createdAt = createdAt;
    }

    public Events() {
    }

    public Events(Context context) {
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public boolean isEventChecked() {
        return eventChecked;
    }

    public void setEventChecked(boolean eventChecked) {
        this.eventChecked = eventChecked;
    }

    public String getEventCreatedAt() {
        return createdAt;
    }

    public void setEventCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------DB ACTIONS AND UTILS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    public String getEventTypeString(int typeNum) {
        String type = "";

        if (typeNum == 1) {
            type = "Event";
        } else if (typeNum == 2) {
            type = "Reminder";
        } else if (typeNum == 3) {
            type = "ToDo";
        }

        return type;
    }

    public int getEventTypeInt(String type) {
        int typeNum = 0;

        if (type.equals("Event")) {
            typeNum = 1;
        } else if (type.equals("Reminder")) {
            typeNum = 2;
        } else if (type.equals("ToDo")) {
            typeNum = 3;
        }

        return typeNum;
    }

    public ArrayList<Events> allEventsList(int checkedYN) {
        ArrayList<Events> myEvents = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);

        Cursor res = dbHelper.getAllEventsByCheck(checkedYN);

        while (res.moveToNext()) {
            if (res.getInt(8) != 3) {
                boolean finished = false;

                if (checkedYN == 1) {
                    finished = true;
                }
                if (checkedYN == 0) {
                    finished = false;
                }

                Events event = new Events(
                        res.getString(0),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getInt(5),
                        finished,
                        res.getString(7)
                );

                event.setSynced(res.getInt(8));

                myEvents.add(0, event);
            }
        }

        return myEvents;
    }

    public ArrayList<Events> allEventsBySyncedList(int synced) {
        ArrayList<Events> myEvents = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);

        Cursor res = dbHelper.getAllEventsBySynced(synced);

        while (res.moveToNext()) {
            if (res.getInt(8) == synced) {
                boolean finished = false;

                if (res.getInt(6) == 1) {
                    finished = true;
                }

                Events event = new Events(
                        res.getString(0),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getInt(5),
                        finished,
                        res.getString(7)
                );

                event.setSynced(res.getInt(8));

                myEvents.add(0, event);
            }
        }

        return myEvents;
    }

    public ArrayList<Events> allEventsByDateList(String dateSelected, int checkedYN) {
        ArrayList<Events> myEvents = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);

        Cursor res = dbHelper.getAllEventsByCheck(checkedYN);

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            if (res.getInt(8) != 3) {
                if (res.getString(4).contains(dateSelected)) {
                    boolean finished = false;

                    if (Integer.parseInt(res.getString(6)) == 1) {
                        finished = true;
                    }

                    if (checkedYN == 1) {
                        finished = true;
                    }

                    if (!finished) {
                        if (res.getString(4).contains(dateSelected)) {
                            Events event = new Events(
                                    res.getString(0),
                                    res.getString(1),
                                    res.getString(2),
                                    res.getString(3),
                                    res.getString(4),
                                    res.getInt(5),
                                    finished,
                                    res.getString(7)
                            );

                            event.setSynced(res.getInt(8));

                            myEvents.add(event);
                        }
                    }
                }
            }
        }

        return myEvents;
    }

    public void syncEventsApiDb() {

        ArrayList<Events> dbEvents0;
        ArrayList<Events> dbEvents2;
        ArrayList<Events> dbEvents3;

        MyDbHelper dbHelper = new MyDbHelper(context);
        Utils utils = new Utils(context);

        //SAVING OFFLINE DATA WHERE SYNCED = 0
        dbEvents0 = allEventsBySyncedList(0);
        for (Events event : dbEvents0) {
            event.setSynced(1);
            saveSyncEvent(event);
        }
        //UPDATING OFFLINE DATA WHERE SYNCED = 2
        dbEvents2 = allEventsBySyncedList(2);
        for (Events event : dbEvents2) {
            event.setSynced(1);
            updateSyncEvent(event);
        }
        //DELETING OFFLINE DATA WHERE SYNCED = 0
        dbEvents3 = allEventsBySyncedList(3);
        for (Events event : dbEvents3) {
            event.setSynced(1);
            deleteSyncEvent(event);
        }

        Call<ArrayList<Events>> call0 = ApiClient.getInstance().getApi().getAllUserEventsBySync(utils.getMyUserId(), 0);
        call0.enqueue(new Callback<ArrayList<Events>>() {
            @Override
            public void onResponse(Call<ArrayList<Events>> call, Response<ArrayList<Events>> response) {
                if (!response.isSuccessful()) {
                    Utils.makeMyLog("Sync failed", "");
                } else {
                    ArrayList<Events> newEvents = response.body();

                    for (Events event : newEvents) {
                        dbHelper.addNewEvent(event);
                        updateEventSyncedStateApiAndDb(event, 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {

            }
        });
        Call<ArrayList<Events>> call2 = ApiClient.getInstance().getApi().getAllUserEventsBySync(utils.getMyUserId(), 2);
        call2.enqueue(new Callback<ArrayList<Events>>() {
            @Override
            public void onResponse(Call<ArrayList<Events>> call, Response<ArrayList<Events>> response) {
                if (!response.isSuccessful()) {
                    Utils.makeMyLog("Sync failed", "");
                } else {
                    ArrayList<Events> updatedEvents = response.body();
                    for (Events event : updatedEvents) {
                        dbHelper.updateEvent(event);
                        updateEventSyncedStateApiAndDb(event, 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {

            }
        });
        Call<ArrayList<Events>> call3 = ApiClient.getInstance().getApi().getAllUserEventsBySync(utils.getMyUserId(), 3);
        call3.enqueue(new Callback<ArrayList<Events>>() {
            @Override
            public void onResponse(Call<ArrayList<Events>> call, Response<ArrayList<Events>> response) {
                if (!response.isSuccessful()) {
                    Utils.makeMyLog("Sync failed", "");
                } else {
                    ArrayList<Events> deletedEvents = response.body();
                    for (Events event : deletedEvents) {
                        deleteSyncEvent(event);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Events>> call, Throwable t) {

            }
        });
    }

    private void saveSyncEvent(Events event) { //IF ON DB BUT NOT ON API
        MyDbHelper dbHelper = new MyDbHelper(context);

        event.setSynced(1);
        Call<Events> call = ApiClient.getInstance().getApi().saveNewEvent(event);

        call.enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
                if (!response.isSuccessful()) {
                    Utils.makeMyLog("Sync failed", "");
                } else {
                    Events savedEvent = response.body();

                    dbHelper.updateEventSyncedState(savedEvent.get_id(), 1);
                }
            }

            @Override
            public void onFailure(Call<Events> call, Throwable t) {
                Utils.makeMyLog("Sync failed", t.getMessage());
            }
        });
    }

    private void updateSyncEvent(Events event) { //IF ON DB BUT NOT ON API
        MyDbHelper dbHelper = new MyDbHelper(context);

        event.setSynced(1);
        event.setEventCreatedAt(null);

        Call<Events> call = ApiClient.getInstance().getApi().updateEvent(event.get_id(), event);

        call.enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
                if (!response.isSuccessful()) {
                    Utils.makeMyLog("Failed to sync", "");
                } else {
                    Events updatedEvent = response.body();

                    dbHelper.updateEvent(updatedEvent);
                }
            }

            @Override
            public void onFailure(Call<Events> call, Throwable t) {
                Utils.makeMyLog("Failed to sync", t.getMessage());
            }
        });
    }

    private void deleteSyncEvent(Events event) { //IF ON DB BUT NOT ON API
        MyDbHelper dbHelper = new MyDbHelper(context);

        Call<Events> call = ApiClient.getInstance().getApi().deleteEvent(event.get_id());

        call.enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
                if (!response.isSuccessful()) {
                    Utils.makeMyLog("Failed to sync", "");
                } else {
                    Events deletedEvent = response.body();

                    dbHelper.deleteEvent(deletedEvent.get_id());
                }
            }

            @Override
            public void onFailure(Call<Events> call, Throwable t) {
                Utils.makeMyLog("Failed to sync", t.getMessage());
            }
        });
    }

    public void updateEventSyncedStateApiAndDb(Events event, int state) {
        event.setSynced(state);
        MyDbHelper dbHelper = new MyDbHelper(context);
        Call<Events> call = ApiClient.getInstance().getApi().updateEvent(event.get_id(), event);
        call.enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
                if (!response.isSuccessful()) {
                    Utils.makeMyLog("Failed to sync", "");
                } else {
                    Events updatedEvent = response.body();
                    dbHelper.updateEventSyncedState(event.get_id(), state);
                }
            }

            @Override
            public void onFailure(Call<Events> call, Throwable t) {
                Utils.makeMyLog("Failed to sync", t.getMessage());
            }
        });
    }
}
