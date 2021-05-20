package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.bbetterapp.ApiHelper.BBetterApi;
import com.example.bbetterapp.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textViewResult);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://secret-temple-10001.herokuapp.com/bbetter/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BBetterApi bBetterApi = retrofit.create(BBetterApi.class);

        Call<List<User>> call = bBetterApi.getUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: " + response.code());
                    return;
                }
                List<User> users = response.body();

                for (User user : users){
                    String content = "\n\n-----------------------\n ";
                    content += "name: " + user.getFirstName() + "\n ";
                    content += "last name: " + user.getLastName() + "\n ";
                    content += "username: " + user.getUsername();
                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

    }
}