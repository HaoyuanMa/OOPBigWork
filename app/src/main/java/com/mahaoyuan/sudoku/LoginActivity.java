package com.mahaoyuan.sudoku;

import okhttp3.*;
import okio.BufferedSink;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class LoginActivity extends AppCompatActivity {

    private String user = "";
    private static final int LOGIN_SUCCESS = 1;
    private Handler resultHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == LOGIN_SUCCESS){
                Intent intent = new Intent(LoginActivity.this, FaceInputActivity.class);
                intent.putExtra("mode","login");
                intent.putExtra("username",user);
                startActivity(intent);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(v -> login());
    }

    private void login(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                EditText userName = findViewById(R.id.username);
                EditText passWord = findViewById(R.id.password);
                user = userName.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_name",userName.getText().toString());
                    jsonObject.put("pass_word",passWord.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Request request = new Request.Builder()
                        .url("http://"+Config.SERVER_HOST+":"+Config.SERVER_PORT+Config.LOGIN_URL)
                        .addHeader("Content-Type","application/json")
                        .addHeader("Data-Type","text")
                        .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonObject.toString()))
                        .build();
                OkHttpClient httpClient = new OkHttpClient();
                Call call = httpClient.newCall(request);
                try {
                    //同步请求，要放到子线程执行
                    Response response = call.execute();
                    Log.i("mhy", "okHttpGet run: response:"+ response.body().string());
                    if (response.code()==200){
                        Message message = new Message();
                        message.what = LOGIN_SUCCESS;
                        resultHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}