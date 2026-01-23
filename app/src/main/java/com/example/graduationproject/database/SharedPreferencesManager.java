package com.example.graduationproject.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.graduationproject.utils.Constants;

public class SharedPreferencesManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferencesManager instance;


    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveLoginDate(String email,String password){
        editor.putString("email",email);
        editor.putString("password",password);
        editor.commit();
    }

    public void removeLoginData(){
        editor.remove("email");
        editor.remove("password");
        editor.commit();
    }

    public String getSavedEmail(){
        return sharedPreferences.getString("email",null);
    }

    public String getSavedPassword(){
        return sharedPreferences.getString("password",null);
    }
}
