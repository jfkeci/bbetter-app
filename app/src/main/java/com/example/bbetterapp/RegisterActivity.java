package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Models.User;

import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity{

    EditText etFirstName, etLastName, etGender, etAge, etUsername, etEmail, etPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);

        etFirstName = findViewById(R.id.etFirstNameRegister);
        etLastName = findViewById(R.id.etLastNameRegister);
        etGender = findViewById(R.id.etGenderRegister);
        etAge = findViewById(R.id.etAgeRegister);
        etUsername = findViewById(R.id.etUsernameRegister);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);

        etFirstName.setText("Jakov");
        etLastName.setText("Sabolicek");
        etGender.setText("m");
        etAge.setText("22");
        etUsername.setText("jfkeci");
        etEmail.setText("jfkeci111@gmail.com");
        etPassword.setText("test123");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void registerUser(){
        User user = new User();

        boolean fieldsFilled = true;

        if(Utils.isEmpty(etFirstName)){
            etFirstName.requestFocus();
            Utils.makeMyToast("First name field required", getApplicationContext());
            fieldsFilled = false;
        }else{
            user.setFirstName(etFirstName.getText().toString());
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etLastName)){
            etLastName.requestFocus();
            Utils.makeMyToast("Last name field required", getApplicationContext());
            fieldsFilled = false;
        }else{
            user.setLastName(etLastName.getText().toString());
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etGender)){
            etGender.requestFocus();
            Utils.makeMyToast("Gender field required", getApplicationContext());
            fieldsFilled = false;
        }else{
            user.setGender(etGender.getText().toString());
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etAge)){
            etAge.requestFocus();
            Utils.makeMyToast("Age field required", getApplicationContext());
            fieldsFilled = false;
        }else{
            user.setAge(Integer.parseInt(etAge.getText().toString()));
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etUsername)){
            etUsername.requestFocus();
            Utils.makeMyToast("Username field required", getApplicationContext());
            fieldsFilled = false;
        }else{
            user.setUserName(etUsername.getText().toString());
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etEmail)){
            etEmail.requestFocus();
            Utils.makeMyToast("Email field required", getApplicationContext());
            fieldsFilled = false;
        }else{
            user.setEmail(etEmail.getText().toString());
            fieldsFilled = true;
        }

        if((etPassword.getText().toString().length()) < 8){
            if(Utils.isEmpty(etPassword)){
                etPassword.requestFocus();
                Utils.makeMyToast("Password field required", getApplicationContext());
                fieldsFilled = false;
            }else{
                etPassword.requestFocus();
                Utils.makeMyToast("Password should have at least 8 characters", getApplicationContext());
                fieldsFilled = false;
            }

        }else{
            user.setPassword(etPassword.getText().toString());
            fieldsFilled = true;
        }


        if(fieldsFilled){
            if(validateNewUser(user) == 1){
                Utils.makeMyToast("User with this email already exists", getApplicationContext());
            }
            else if(validateNewUser(user) == 2){
                Utils.makeMyToast("Username taken, try another one", getApplicationContext());
            }
            else if(validateNewUser(user) == 3){
                Call<User> saveUserCall = ApiClient.getInstance().getApi().createUser(user);

                saveUserCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(!response.isSuccessful()){
                            Utils.makeMyToast("code: " + response.code(), getApplicationContext());
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Utils.makeMyToast("" + t.getMessage(), getApplicationContext());
                    }
                });
            }
        }
    }

    public int validateNewUser(User newUser){
        final int[] state = new int[1];

        Call<List<User>> call = ApiClient.getInstance().getApi().getUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyToast("code: " + response.code(), getApplicationContext());
                    return;
                }

                List<User> users = response.body();

                for(User user : users){
                    if(user.getEmail().equals(newUser.getEmail())){
                        state[0] = 1;
                    }else if(user.getUserName().equals(newUser.getUserName())){
                        state[0] = 2;
                    }else{
                        state[0] = 3;
                    }
                    Utils.makeMyLog("myuser: username: ", ""+user.getUserName());
                    Utils.makeMyLog("myuser: first name: ", ""+user.getFirstName());
                    Utils.makeMyLog("myuser: last name: ", ""+user.getLastName());
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Utils.makeMyToast(""+ t.getMessage(), getApplicationContext());
            }
        });
        int option = state[0];

        return option;
    }

}