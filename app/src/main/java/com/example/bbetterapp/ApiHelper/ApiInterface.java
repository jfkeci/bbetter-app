package com.example.bbetterapp.ApiHelper;

import com.example.bbetterapp.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/users/all")
    Call<List<User>> getUsers();

    @GET("/notes/all")
    Call<List<User>> getNotes();

    @GET("/sessions/all")
    Call<List<User>> getSessions();

    @GET("/events/all")
    Call<List<User>> getEvents();

    @GET("/users/login/{email}/{password}")
    Call<String> loginUser(@Path("email") String email, @Path("password") String password);

}
