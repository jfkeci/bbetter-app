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
    @POST("notes/new")
    Call<Notes> createNote(@Body Notes note);




    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------SESSIONS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    //SESSIONS: GET
    @GET("sessions/all")
    Call<ArrayList<Sessions>> getSessions();

    //SESSIONS: GET ALL USER SESSIONS
    @GET("sessions/all/{userId}")
    Call<ArrayList<Sessions>> getAllUserSessions(@Path("userId") String userId);




    /* --------------------------------------------------------------------------------*/
    /* ----------------------------------------EVENTS----------------------------------------*/
    /* --------------------------------------------------------------------------------*/
    //EVENTS: GET CHECKED
    @GET("events/all/{userId}/true")
    Call<ArrayList<Events>> getNonCheckedEvents(@Path("userId") String userId);
    //EVENTS: GET UNCHECKED
    @GET("events/all/{userId}/false")
    Call<ArrayList<Events>> getCheckedEvents(@Path("userId") String userId);

}
