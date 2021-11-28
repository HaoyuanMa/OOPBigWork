package com.mahaoyuan.sudoku;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.mahaoyuan.sudoku.CameraHelper.freeCamera;
import static com.mahaoyuan.sudoku.CameraHelper.getCameraInstance;
import static com.mahaoyuan.sudoku.CameraHelper.getOutputMediaFile;
import static com.mahaoyuan.sudoku.CameraHelper.setUpCamera;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FaceInputActivity extends OfflineBaseActivity {

    private Camera mCamera = null;
    private String res = "";
    private boolean lock = false;
    CameraPreview mPreview = null;
    private int id = 0;
    private final int LOGIN = 1;
    private String feature = "";
    private String mode = "";
    private String user = "";
    private String password = "";

    private Handler resultHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == LOGIN) {
                if (mode.equals("login")){
                    Log.i("mhy","before login");
                    if (login()){
                        Intent intent = new Intent(FaceInputActivity.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        ResultDialog resultDialog = new ResultDialog(mPreview.getContext(),"认证失败");
                        resultDialog.show();
                        Button resultBtn = resultDialog.findViewById(R.id.result_btn);
                        resultBtn.setOnClickListener(v->{
                            resultDialog.cancel();
                            finish();
                        });
                    }
                } else {
                    if(!feature.isEmpty() && register()){
                        ResultDialog resultDialog = new ResultDialog(mPreview.getContext(),"注册成功");
                        resultDialog.show();
                        Button resultBtn = resultDialog.findViewById(R.id.result_btn);
                        resultBtn.setOnClickListener(v->{
                            resultDialog.cancel();
                            finish();
                        });
                    } else {
                        ResultDialog resultDialog = new ResultDialog(mPreview.getContext(),"注册失败");
                        resultDialog.show();
                        Button resultBtn = resultDialog.findViewById(R.id.result_btn);
                        resultBtn.setOnClickListener(v->{
                            resultDialog.cancel();
                            finish();
                        });
                    }

                }
            }
        }
    };

    private boolean login(){
        Log.i("mhy","on login");
        class myCallable implements Callable<String>{
            @Override
            public String call() throws Exception {
                Log.i("mhy","login call");
                String url = "http://" + Config.SERVER_HOST + ":" + Config.SERVER_PORT + Config.FACE_VALIDATE_URL;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_name",user);
                    jsonObject.put("feature",feature);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Request request = new Request.Builder()
                        .url(url)
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
                        return "ok";
                    } else {
                        return "failed";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "failed";
                }
            }

        }

        FutureTask<String> futureTask = new FutureTask<String>(new myCallable());

        new Thread(futureTask).start();
        String result = "";
        try {
            result = futureTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result.equals("ok")){
            return true;
        } else {
            return false;
        }
    }

    private boolean register(){
        Log.i("mhy","on register");
        class mCallable implements Callable<String>{
            @Override
            public String call() throws Exception {
                Log.i("mhy","register call");
                String url = "http://" + Config.SERVER_HOST + ":" + Config.SERVER_PORT + Config.REGISTER_URL;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_name",user);
                    jsonObject.put("pass_word",password);
                    jsonObject.put("feature",feature);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Request request = new Request.Builder()
                        .url(url)
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
                        return "ok";
                    } else {
                        return "failed";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "failed";
                }
            }

        }

        FutureTask<String> futureTask = new FutureTask<String>(new mCallable());

        new Thread(futureTask).start();
        String result = "";
        try {
            result = futureTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result.equals("ok")){
            return true;
        } else {
            return false;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_input);

        Intent intent=getIntent();
        mode = intent.getStringExtra("mode");
        user = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        mCamera = getCameraInstance();
        if (mCamera == null){
            Toast.makeText(getApplicationContext(), "请授权应用使用相机！", Toast.LENGTH_SHORT).show();
            finish();
        }
        setUpCamera(mCamera);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Button capture = findViewById(R.id.button_capture);
        capture.setOnClickListener(v->{

            v.setActivated(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!res.equals("11")){
                        lock = true;
                        mCamera.takePicture(null,null,mPicture);
                        while (lock){

                        }
                    }

                    OkHttpClient httpClient = new OkHttpClient();
                    String url = "http://" + Config.HOST + ":" + Config.PORT + Config.FACE_INPUT_RECOGNISE_URL + "?id=" + id;
                    Request request = new Request.Builder()
                            .get()
                            .url(url)
                            .build();
                    Call call = httpClient.newCall(request);

                    try {
                        //同步请求，要放到子线程执行
                        Response response = call.execute();
                        String rs = response.body().string();
                        Log.i("mhy", "okHttpGet run: response:"+ rs);
                        feature = rs;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = LOGIN;
                    resultHandler.sendMessage(message);

                }
            }).start();

        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        freeCamera(mCamera);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        freeCamera(mCamera);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                    if (pictureFile == null){
                        Log.d("mhy", "Error creating media file, check storage permissions");
                        return;
                    }

                    Log.d("mhy","pic taken");

                    OkHttpClient httpClient = new OkHttpClient();
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"),data);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", id + "", fileBody)
                            .build();

                    String url = "http://" + Config.HOST + ":" + Config.PORT + Config.FACE_INPUT_UPLOAD_URL;
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Call call = httpClient.newCall(request);

                    try {
                        //同步请求，要放到子线程执行
                        Response response = call.execute();
                        res = response.body().string();
                        Log.i("mhy", "okHttpGet run: response:"+ res);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lock = false;
                }
            }).start();

            camera.startPreview();
        }
    };
}