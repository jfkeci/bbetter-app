package com.example.bbetterapp.Models;

public class Session {

    String sessionId, userId;
    int sessionLength, sessionPoints;
    boolean sessionFinished;
    String sessionCreatedAt;

    public Session(String sessionId, String userId, int sessionLength, int sessionPoints, boolean sessionFinished, String sessionCreatedAt) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.sessionLength = sessionLength;
        this.sessionPoints = sessionPoints;
        this.sessionFinished = sessionFinished;
        this.sessionCreatedAt = sessionCreatedAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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
