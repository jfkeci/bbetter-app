package com.example.bbetterapp.ApiHelper;

import com.example.bbetterapp.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    /* ----------------------------------------USERS----------------------------------------*/
    //USERS: GET
    @GET("/users/all")
    Call<List<User>> getUsers();

    //USERS: LOGIN
    @GET("/users/login/{email}/{password}")
    Call<String> loginUser(@Path("email") String email, @Path("password") String password);

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

    /* ----------------------------------------NOTES----------------------------------------*/
    //NOTES: GET
    @GET("/notes/all")
    Call<List<User>> getNotes();

    /* ----------------------------------------SESSIONS----------------------------------------*/
    //SESSIONS: GET
    @GET("/sessions/all")
    Call<List<User>> getSessions();

    /* ----------------------------------------EVENTS----------------------------------------*/
    //EVENTS: GET
    @GET("/events/all")
    Call<List<User>> getEvents();
}
