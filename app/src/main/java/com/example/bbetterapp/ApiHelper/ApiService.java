package com.example.bbetterapp.ApiHelper;

import com.example.bbetterapp.Models.User;
import com.example.bbetterapp.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

//    Utils utils;
//
//    String baseUrl = "https://secret-temple-10001.herokuapp.com/bbetter/";
//
//    Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();
//
//    BBetterApi bBetterApi = retrofit.create(BBetterApi.class);
//
//    public ArrayList<User> getAllUsers(){
//        ArrayList<User> usersList = new ArrayList<User>();
//
//        Call<List<User>> call = bBetterApi.getUsers();
//
//        call.enqueue(new Callback<List<User>>() {
//            @Override
//            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                if(!response.isSuccessful()){
//                    utils.makeMyLog("Response code: ", ""+response.code());
//
//                    return;
//                }
//                List<User> users = response.body();
//
//                for (User user : users){
//                    utils.makeMyLog("name: ", "" + user.getFirstName());
//                    utils.makeMyLog("last name: ", "" + user.getLastName());
//                    utils.makeMyLog("username: ", "" + user.getUsername());
//                    utils.makeMyLog("user notes url : ", "" + user.getUserNotesUrl());
//
//                    usersList.add(user);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<User>> call, Throwable t) {
//                utils.makeMyLog("message: ", "" + t.getMessage());
//            }
//        });
//
//        return usersList;
//    }

}
