package com.example.bbetterapp.Models;

import android.content.Context;

public class Affirmations {

    String _id, userId, promptId, content, createdAt;
    int synced;

    Context context;

    public Affirmations(String _id, String userId, String promptId, String content, String createdAt, int synced) {
        this._id = _id;
        this.userId = userId;
        this.promptId = promptId;
        this.content = content;
        this.createdAt = createdAt;
        this.synced = synced;
    }

    public Affirmations(Context context){
        this.context = context;
    }

    public Affirmations(){}

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

    public String getPromptId() {
        return promptId;
    }

    public void setPromptId(String promptId) {
        this.promptId = promptId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
