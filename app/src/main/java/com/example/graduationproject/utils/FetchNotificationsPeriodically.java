package com.example.graduationproject.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.graduationproject.listeners.NotificationsListListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class FetchNotificationsPeriodically extends Worker {

    private static WeakReference<NotificationsListListener> callback ;
    private final Context context;
    private final String email ;

    public static void setCallback(NotificationsListListener notificationsListListener){
        callback = new WeakReference<>(notificationsListListener);
    }
    public FetchNotificationsPeriodically(Context context, @NonNull WorkerParameters params){
        super(context,params);
        this.context=context;
        this.email = params.getInputData().getString("email");
    }

    @NonNull
    @Override
    public Result doWork() {
        fetchNotifications();
        return Result.success();
    }

    private void fetchNotifications(){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getNotificationsURL, this::handleResponse, this::handleError){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("email",email);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void handleResponse(String resp){
        if(resp.equalsIgnoreCase("Error")){
            if(callback != null && callback.get() != null)
                callback.get().getNotifications(0,null);
        }
        else{
            try {
                if(callback != null && callback.get() != null)
                    callback.get().getNotifications(1,new JSONArray(resp));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleError(VolleyError error){
        if(callback != null && callback.get() != null)
            callback.get().getNotifications(-1,null);
    }
}
