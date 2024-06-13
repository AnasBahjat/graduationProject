package com.example.graduationproject.backgroundActions;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.graduationproject.models.Notifications;
import com.example.graduationproject.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsService extends Service {
    private Handler handler;
    private Runnable runnable;
    private final int INTERVAL = 2500;
    private String email ;


    @Override
    public void onCreate() {
        super.onCreate();
        handler=new Handler();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        email = intent.getStringExtra("email");
        runnable=new Runnable() {
            @Override
            public void run() {
                checkForChanges();
                handler.postDelayed(this,INTERVAL);
            }
        };
        handler.post(runnable);
        return START_STICKY;
    }

    private void checkForChanges(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.checkNotificationsChange,res->{
            try{
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Notifications>>(){}.getType();
                List<Notifications> notificationsList = gson.fromJson(res,listType);
                Intent intent = new Intent("UPDATE_TEACHER_UI");
                intent.putParcelableArrayListExtra("notificationsData",new ArrayList<>(notificationsList));
                sendBroadcast(intent);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        },err->{

        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data=new HashMap<>();
                data.put("email",email);
                return data;
            }
        };
        queue.add(stringRequest);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
