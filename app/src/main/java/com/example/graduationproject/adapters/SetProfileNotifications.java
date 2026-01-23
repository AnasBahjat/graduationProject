package com.example.graduationproject.adapters;

import android.content.Context;

import com.example.graduationproject.database.Database;
import com.example.graduationproject.listeners.NotificationsListListener;
import com.example.graduationproject.models.Notifications;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetProfileNotifications  {
    private String email ;
    private Context context ;
    private Database database ;
    public SetProfileNotifications(String email,Context context){
        this.email=email;
        this.context=context;
        database=new Database(context);
    }

    public void getDatabaseNotifications(){
        database.getNotifications(email,(NotificationsListListener)context);
    }

}
