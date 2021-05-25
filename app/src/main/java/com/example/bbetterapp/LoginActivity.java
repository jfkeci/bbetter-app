package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Models.User;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView twRegister;
    EditText etEmail, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        twRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void loginUser(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Call<User> call = ApiClient.getInstance().getApi().loginUser(email, password);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyToast("code: " +response.code(), getApplicationContext());
                    return;
                }
                User user = response.body();
                Utils.makeMyLog("username: ", ""+ user.getUserName());
                Utils.makeMyLog("firstname: ", ""+ user.getFirstName());
                Utils.makeMyLog("lastname: ", ""+ user.getLastName());
                if(user.getEmail().equals(email)){
                    startActivity(new Intent(getApplicationContext(), FragmentHolderActivity.class));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Utils.makeMyLog("message: ", ""+t.getMessage());
            }
        });
    }
}