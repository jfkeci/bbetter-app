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
        etPassword.setText("test1234");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser(){
        User user = new User();

        boolean fieldsFilled = true;

        if(Utils.isEmpty(etFirstName)){
            fieldsFilled = etValidation(etFirstName, "First name field required");;
        }else{
            user.setFirstName(etFirstName.getText().toString());
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etLastName)){
            fieldsFilled = etValidation(etLastName, "Last name field required");
        }else{
            user.setLastName(etLastName.getText().toString());
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etGender)){
            fieldsFilled = etValidation(etGender, "Gender field required");
        }else{
            user.setGender(etGender.getText().toString());
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etAge)){
            fieldsFilled = etValidation(etAge, "Age field required");
        }else{
            user.setAge(Integer.parseInt(etAge.getText().toString()));
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etUsername)){
            fieldsFilled = etValidation(etUsername, "Username field required");
        }else{
            user.setUserName(etUsername.getText().toString());
            fieldsFilled = true;
        }

        if(Utils.isEmpty(etEmail)){
            fieldsFilled = etValidation(etEmail, "Email field required");
        }else{
            user.setEmail(etEmail.getText().toString());
            fieldsFilled = true;
        }

        if((etPassword.getText().toString().length()) < 8){
            if(Utils.isEmpty(etPassword)){
                fieldsFilled = etValidation(etPassword, "Password field required");
            }else{
                fieldsFilled = etValidation(etPassword, "Password should have at least 8 characters");
            }
        }else if((etPassword.getText().toString().length()) >= 8){
            user.setPassword(etPassword.getText().toString());
            fieldsFilled = true;
        }

        if(fieldsFilled){
            int validate = validateNewUser(user);
            if(validate == 1){
                etValidation(etEmail, "User with this email already exists");
            }
            else if(validate == 2){
                etValidation(etUsername, "Username taken, try another one");
            }
            else if(validate == 3){
                Call<User> saveUserCall = ApiClient.getInstance().getApi().createUser(user);

                saveUserCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(!response.isSuccessful()){
                            Utils.makeMyLog("Save user call was not successful: ", ""+response.code());
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Utils.makeMyLog("Save user onFailure message: ", t.getMessage());
                    }
                });
            }
        }
    }

    private int validateNewUser(User newUser){
        final int[] state = new int[1];

        boolean usernameExists = false;
        boolean emailExists = false;

        state [0] = 3;

        Call<List<User>> call = ApiClient.getInstance().getApi().getUsers();

        

        try {


        }
        catch (Exception ex){

        }

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(!response.isSuccessful()){
                    Utils.makeMyLog("List of user response was not successful: ", ""+response.code());
                    return;
                }

                List<User> users = response.body();

                for(User user : users){
                    Utils.makeMyLog("username: ", ""+user.getUserName());
                    Utils.makeMyLog("user email: ", ""+user.getEmail());
                    if(user.getEmail().equals(newUser.getEmail())){
                        state[0] = 2;
                        Utils.makeMyLog("state of email in tehe request: ", ""+state[0]);

                    }else if(user.getUserName().equals(newUser.getUserName())){
                        state[0] = 1;
                        Utils.makeMyLog("state of username in the request: ", ""+state[0]);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Utils.makeMyLog("List users, onFailure message: ", ""+ t.getMessage());
            }
        });
        Utils.makeMyLog("state before return: ", ""+state[0]);
        return state[0];
    }

    private boolean etValidation(EditText editText, String message){
        editText.requestFocus();
        Utils.makeMyToast(""+message, getApplicationContext());
        return false;
    }

}