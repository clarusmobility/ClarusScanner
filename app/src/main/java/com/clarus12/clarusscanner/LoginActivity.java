package com.clarus12.clarusscanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.clarus12.clarusscanner.dto.LoginResponseDto;
import com.clarus12.clarusscanner.retrofit.Methods;
import com.clarus12.clarusscanner.retrofit.RetrofitClient;
import com.clarus12.clarusscanner.volley.LoginRequest;

import org.json.JSONObject;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private String TAG  = "LoginActivity";

    private RequestQueue queue;

    private Button btn_login;
    private EditText edit_email, edit_password;


    private String url = "https://clarus12.com/api/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        String pref_token = PreferenceManager.getString(LoginActivity.this,PreferenceManager.ACCESS_TOKEN);
        if (pref_token != null && pref_token.length() > 0) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {

        edit_email = (EditText)findViewById(R.id.edit_email);
        edit_password = (EditText)findViewById(R.id.edit_password);

        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
        String userEmail = edit_email.getText().toString();
        String userPassword = edit_password.getText().toString();

        if (userEmail == null || userEmail.length() == 0) {
            Toast.makeText(getApplicationContext(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else if (pattern.matcher(userEmail).matches() == false ) {
            Toast.makeText(getApplicationContext(), "이메일 형식이 맞지 않습니다", Toast.LENGTH_SHORT).show();
        }
        else if (userPassword == null || userPassword.length() == 0) {
            Toast.makeText(getApplicationContext(), "패스워드를 입력해주세요", Toast.LENGTH_SHORT).show();
        }
        else {
            // user email 저장
            PreferenceManager.setString(LoginActivity.this, PreferenceManager.USER_EMAIL,  userEmail);
            // request
            requestLogin(userEmail, userPassword);
        }


    }

    private void login2() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

        edit_email = (EditText)findViewById(R.id.edit_email);
        edit_password = (EditText)findViewById(R.id.edit_password);

        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
        String userID = edit_email.getText().toString();
        String userPassword = edit_password.getText().toString();

    }

    private void requestLogin(String userEmail, String userPassword) {

        Methods methods = RetrofitClient.getRetrofitInstance(LoginActivity.this).create(Methods.class);
        Call<LoginResponseDto> call = methods.loginRequest(userEmail, userPassword);

        call.enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                Log.e(TAG, "onResponse:" + response);

                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse:" + response.headers().get("access_token"));
                    Log.e(TAG, "onResponse:" + response.headers().get("refresh_token"));

                    if (response.headers().get("roleId") != null &&
                            (Long.parseLong(response.headers().get("roleId")) == 1
                                    || Long.parseLong(response.headers().get("roleId")) == 2)) {

                        PreferenceManager.setString(LoginActivity.this, PreferenceManager.ACCESS_TOKEN,  response.headers().get("access_token").substring(7));
                        PreferenceManager.setString(LoginActivity.this, PreferenceManager.REFRESH_TOKEN,  response.headers().get("refresh_token").substring(7));

                        // Log.e(TAG, "onResponse:" + PreferenceManager.getString(LoginActivity.this, PreferenceManager.ACCESS_TOKEN));
                        // Log.e(TAG, "onResponse:" + PreferenceManager.getString(LoginActivity.this, PreferenceManager.REFRESH_TOKEN));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                        Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Toast.makeText(getApplicationContext(), "해당 계정은 권한이 없습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                Log.e(TAG, "onResponse:" + t.getMessage());
            }
        });
    }
}