package com.mahaoyuan.sudoku;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText userName = findViewById(R.id.register_username);
        EditText passWord = findViewById(R.id.register_password);
        String user = userName.getText().toString();
        String psw = passWord.getText().toString();
        Button btn = findViewById(R.id.register_btn);
        btn.setOnClickListener(v->{
            Intent intent = new Intent(RegisterActivity.this, FaceInputActivity.class);
            intent.putExtra("mode","register");
            intent.putExtra("username",user);
            intent.putExtra("password",psw);
            startActivity(intent);
        });

    }
}