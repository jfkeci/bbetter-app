package com.example.bbetterapp.Models;

import android.graphics.drawable.Drawable;

public class Apps {

    String appname;
    Drawable appicon;
    int status; //if status is 0 app is unlocked, otherwise its locked
    String packagename;
    int synced;

    public Apps(String appname, Drawable appicon, int status, String packagename) {
        this.appname = appname;
        this.appicon = appicon;
        this.status = status;
        this.packagename = packagename;
    }

    public Apps() {

    }

    public int isSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }
}

