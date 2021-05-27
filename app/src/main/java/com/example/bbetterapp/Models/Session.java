package com.example.bbetterapp.Models;

public class Session {

    String _id, userId;
    int sessionLength, sessionPoints;
    boolean sessionFinished;
    String sessionCreatedAt;

    public Session(String _id, String userId, int sessionLength, int sessionPoints, boolean sessionFinished, String sessionCreatedAt) {
        this._id = _id;
        this.userId = userId;
        this.sessionLength = sessionLength;
        this.sessionPoints = sessionPoints;
        this.sessionFinished = sessionFinished;
        this.sessionCreatedAt = sessionCreatedAt;
    }

    public String getSessionId() {
        return _id;
    }

    public void setSessionId(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(int sessionLength) {
        this.sessionLength = sessionLength;
    }

    public int getSessionPoints() {
        return sessionPoints;
    }

    public void setSessionPoints(int sessionPoints) {
        this.sessionPoints = sessionPoints;
    }

    public boolean isSessionFinished() {
        return sessionFinished;
    }

    public void setSessionFinished(boolean sessionFinished) {
        this.sessionFinished = sessionFinished;
    }

    public String getSessionCreatedAt() {
        return sessionCreatedAt;
    }

    public void setSessionCreatedAt(String sessionCreatedAt) {
        this.sessionCreatedAt = sessionCreatedAt;
    }
}
