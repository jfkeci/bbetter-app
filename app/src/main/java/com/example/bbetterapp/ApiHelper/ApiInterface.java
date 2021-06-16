package com.example.bbetterapp.ApiHelper;

import com.example.bbetterapp.Models.Events;
import com.example.bbetterapp.Models.Notes;
import com.example.bbetterapp.Models.Sessions;
import com.example.bbetterapp.Models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------USERS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    //USERS: GET
    @GET("users/all")
    Call<List<User>> getUsers();
    //USERS: LOGIN
    @GET("users/login/{email}/{password}")
    Call<List<User>> loginUser(@Path("email") String email, @Path("password") String password);
    //USERS: SAVE
    @POST("users/new")
    Call<User> createUser(@Body User user);
    //USERS: PUT
    @PUT("users/{id}")
    Call<User> putUser(@Path("id") String id, @Body User user);
    //USERS: PATCH
    @PATCH("users/{id}")
    Call<User> patchUser(@Path("id") String id, @Body User user);
    //USERS: DELETE
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") String id);




    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------NOTES----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    //NOTES: GET
    @GET("notes/all")
    Call<ArrayList<Notes>> getNotes();
    //NOTES: GET ALL USER NOTES
    @GET("notes/all/{userId}")
    Call<ArrayList<Notes>> getAllUserNotes(@Path("userId") String userId);
    //SAVE NEW NOTE
    @POST("notes/new")
    Call<Notes> saveNewNote(@Body Notes note);
    //UPDATE NOTE
    @PATCH("notes/patch/{noteId}")
    Call<Notes> updateNote(@Path("noteId") String noteId, @Body Notes note);
    //DELETE NOTE
    @DELETE("notes/delete/{noteId}")
    Call<Notes> deleteNote(@Path("noteId") String noteId);
    //GET USER NOTES BY SYNCED
    @GET("notes/all/{userId}/{synced}")
    Call<ArrayList<Notes>> getAllUserNotesBySync(@Path("userId") String userId, @Path("synced") int synced);




    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------SESSIONS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    //SESSIONS: GET
    @GET("sessions/all")
    Call<ArrayList<Sessions>> getSessions();
    //SESSIONS: GET ALL USER SESSIONS
    @GET("sessions/all/{userId}")
    Call<ArrayList<Sessions>> getAllUserSessions(@Path("userId") String userId);
    //SAVE NEW SESSION
    @POST("sessions/new")
    Call<Sessions> saveNewSession(@Body Sessions session);
    //GET USER SESSIONS BY SYNCED
    @GET("notes/all/{userId}/{synced}")
    Call<ArrayList<Sessions>> getAllUserSessionsBySync(@Path("userId") String userId, @Path("synced") int synced);



    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------EVENTS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    //EVENTS: GET CHECKED
    @GET("events/all/{userId}/true")
    Call<ArrayList<Events>> getNonCheckedEvents(@Path("userId") String userId);
    //EVENTS: GET UNCHECKED
    @GET("events/all/{userId}/false")
    Call<ArrayList<Events>> getCheckedEvents(@Path("userId") String userId);
    //GET ALL USER EVENTS
    @GET("events/all/{userId}")
    Call<ArrayList<Events>> getAllUserEvents(@Path("userId") String userId);
    //SAVE EVENT
    @POST("events/new")
    Call<Events> saveNewEvent(@Body Events event);
    //UPDATE EVENT
    @PATCH("events/patch/{eventId}")
    Call<Events> updateEvent(@Path("eventId") String eventId, @Body Events event);
    //DELETE EVENT
    @DELETE("events/delete/{eventId}")
    Call<Events> deleteEvent(@Path("eventId") String eventId);
    //GET USER EVENTS BY SYNCED
    @GET("events/all/{userId}/{synced}")
    Call<ArrayList<Events>> getAllUserEventsBySync(@Path("userId") String userId, @Path("synced") int synced);
}
