package com.example.bbetterapp.Models;

import android.content.Context;
import android.database.Cursor;

import com.example.bbetterapp.Db.MyDbHelper;

import java.util.ArrayList;

public class Events {
    String _id;
    String userId, eventTitle, eventDetails, eventDate;
    int eventType;
    boolean eventChecked;
    String createdAt;
    int synced;

    Context context;

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

    public Events(Context context) {
        this.context = context;
    }

    public int isSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public Events() { }

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



    public String getEventTypeString(int typeNum){
        String type = "";

        if(typeNum == 1){
            type = "Event";
        }else if(typeNum == 2){
            type = "Reminder";
        }else if(typeNum == 3){
            type = "ToDo";
        }

        return type;
    }

    public int getEventTypeInt(String type){
        int typeNum = 0;

        if(type.equals("Event")){
            typeNum = 1;
        }else if(type.equals("Reminder")){
            typeNum = 2;
        }else if(type.equals("Todo")){
            typeNum = 3;
        }

        return typeNum;
    }

    public ArrayList<Events> allEventsList(int checkedYN){
        ArrayList<Events> myEvents = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);

        Cursor res = dbHelper.getAllEventsByCheck(checkedYN);

        while(res.moveToNext()){
            boolean finished = false;

            if(checkedYN == 1){
                finished = true;
            }if(checkedYN == 0){
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

            myEvents.add(0, event);
        }

        return myEvents;
    }

    public ArrayList<Events> allEventsByDateList(String dateSelected, int checkedYN){
        ArrayList<Events> myEvents = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);

        myEvents.clear();

        Cursor res = dbHelper.getAllEventsByCheck(checkedYN);

        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){

            if(res.getString(4).contains(dateSelected)){
                boolean finished = false;

                if(Integer.parseInt(res.getString(6)) == 0){
                    finished = false;
                }if(Integer.parseInt(res.getString(6)) == 1){
                    finished = true;
                }

                if(checkedYN == 0){
                    finished = false;
                }if(checkedYN == 1){
                    finished = true;
                }

                if(!finished){
                    if(res.getString(4).contains(dateSelected)){
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

                        myEvents.add(event);
                    }
                }
            }
        }

        return myEvents;
    }
}
