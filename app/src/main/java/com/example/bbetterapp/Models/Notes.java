package com.example.bbetterapp.Models;

public class Notes {
    String _id, userId, noteTitle, noteContent, createdAt, noteUpdatedAt;
    Boolean noteArchived;

    public Notes(String _id, String userId, String noteTitle, String noteContent, String createdAt, String noteUpdatedAt, Boolean noteArchived) {
        this._id = _id;
        this.userId = userId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.createdAt = createdAt;
        this.noteUpdatedAt = noteUpdatedAt;
        this.noteArchived = noteArchived;
    }

    public Notes() { }

    public String getNoteId() {
        return _id;
    }

    public void setNoteId(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteCreatedAt() {
        return createdAt;
    }

    public void setNoteCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getNoteUpdatedAt() {
        return noteUpdatedAt;
    }

    public void setNoteUpdatedAt(String noteUpdatedAt) {
        this.noteUpdatedAt = noteUpdatedAt;
    }

    public Boolean getNoteArchived() {
        return noteArchived;
    }

    public void setNoteArchived(Boolean noteArchived) {
        this.noteArchived = noteArchived;
    }
}
