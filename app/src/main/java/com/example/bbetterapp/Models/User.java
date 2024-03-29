package com.example.bbetterapp.Models;

public class User {
    private String _id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private String gender;
    private int age;
    private String userNotesUrl;
    private String userEventsUrl;
    private String userSessionsUrl;

    public User(String userId, String firstName, String lastName, String userName, String email, String password, String gender, int age, String userNotesUrl, String userEventsUrl, String userSessionsUrl) {
        this._id = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.userNotesUrl = userNotesUrl;
        this.userEventsUrl = userEventsUrl;
        this.userSessionsUrl = userSessionsUrl;
    }

    public User() {
    }

    public String getUserId() {
        return _id;
    }

    public void setUserId(String userId) {
        this._id = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUserNotesUrl() {
        return userNotesUrl;
    }

    public void setUserNotesUrl(String userNotesUrl) {
        this.userNotesUrl = userNotesUrl;
    }

    public String getUserEventsUrl() {
        return userEventsUrl;
    }

    public void setUserEventsUrl(String userEventsUrl) {
        this.userEventsUrl = userEventsUrl;
    }

    public String getUserSessionsUrl() {
        return userSessionsUrl;
    }

    public void setUserSessionsUrl(String userSessionsUrl) {
        this.userSessionsUrl = userSessionsUrl;
    }
}
