package com.example.bbetterapp.Models;

public class Events {
    int _id;
    String userId, eventTitle, eventDetails, eventDate;
    int eventType;
    boolean eventChecked;
    String eventCreatedAt;

    public Events(int _id, String userId, String eventTitle, String eventDetails, String eventDate, int eventType, boolean eventChecked, String eventCreatedAt) {
        this._id = _id;
        this.userId = userId;
        this.eventTitle = eventTitle;
        this.eventDetails = eventDetails;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.eventChecked = eventChecked;
        this.eventCreatedAt = eventCreatedAt;
    }

    public Events() { }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
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
        return eventCreatedAt;
    }

    public void setEventCreatedAt(String eventCreatedAt) {
        this.eventCreatedAt = eventCreatedAt;
    }
}
