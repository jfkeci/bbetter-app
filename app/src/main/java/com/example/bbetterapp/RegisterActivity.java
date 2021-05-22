package com.example.bbetterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bbetterapp.ApiHelper.ApiClient;
import com.example.bbetterapp.Models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText etFirstName, etLastName, etGender, etAge, etUsername, etEmail, etPassword;
    TextView twResult;

    Utils utils;

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

        twResult = findViewById(R.id.twResult);

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
                User user = new User();
                user.setFirstName(etFirstName.getText().toString());
                user.setLastName(etLastName.getText().toString());
                user.setGender(etGender.getText().toString());
                user.setAge(Integer.parseInt(etAge.getText().toString()));
                user.setUserName(etUsername.getText().toString());
                user.setEmail(etEmail.getText().toString());
                user.setPassword(etPassword.getText().toString());

                Call<User> call = ApiClient.getInstance().getApi().createUser(user);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(!response.isSuccessful()){
                            twResult.setText("Code: "+response.code());
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        twResult.setText(t.getMessage());
                    }
                });
            }
        });
    }
}