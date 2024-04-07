package com.example.graduationproject.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.graduationproject.interfaces.RequestResult;
import com.example.graduationproject.models.Profile;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Database {
    String registrationURL = "http://192.168.1.5/graduationProject/registration.php";
    String loginURL="http://192.168.1.5/graduationProject/login.php";
    String updateLoginURL="http://192.168.1.5/graduationProject/updateLoginState.php";
    String updateLogoutURL="http://192.168.1.5/graduationProject/updateLogoutState.php";
    private Context context;
    private RequestQueue requestQueue ;
    private int successFlag;
    public Database(Context context){
        this.context=context;
        requestQueue=Volley.newRequestQueue(context);
    }
    public void loginCheck(String email,String password,final RequestResult requestFlagSetResult){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("email does not exist")){
                    requestFlagSetResult.onLoginSuccess("email does not exist",null);
                }
                else if(response.equals("wrong password")){
                    requestFlagSetResult.onLoginSuccess("wrong password",null);
                }
                else if(response.equals("ERROR")){
                    requestFlagSetResult.onLoginSuccess("ERROR",null);
                }
                else {
                    try {
                        requestFlagSetResult.onLoginSuccess("success",new JSONArray(response));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, volleyError -> {
            Toast.makeText(context,volleyError.toString()+"",Toast.LENGTH_SHORT).show();
            successFlag=-1;
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>data=new HashMap<>();
                data.put("email",email.toLowerCase());
                data.put("password",password);
                return data;
            }
        };

        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }



    public void registerNewProfile(Profile profile, final RequestResult requestFlagSetResult){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, registrationURL, s -> {
            if(s.equals("True")){
                requestFlagSetResult.onSuccess(1);
            }
            else if (s.equals("exist")){
                requestFlagSetResult.onSuccess(-2);
            }
            else {
                requestFlagSetResult.onSuccess(0);
            }
        }, volleyError -> {
            successFlag=-1;
            requestFlagSetResult.onSuccess(-1);
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>data=new HashMap<>();
                data.put("email",profile.getEmail().toLowerCase());
                data.put("firstname",profile.getFirstname().trim());
                data.put("lastname",profile.getLastname().trim());
                data.put("password",profile.getPassword());
                data.put("gender",String.valueOf(profile.getGender()));
                data.put("profileType",String.valueOf(profile.getProfileType()));
                data.put("date",profile.getBirthDate());
                data.put("IDnumber",profile.getFirstname().trim());
                data.put("phonenumber",profile.getPhoneNumber().trim());
                data.put("city",profile.getCity().trim());
                data.put("country",profile.getCountry().trim());
                return data;
            }

        };
        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void updateLogin(String email) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, updateLoginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(s.equals("done")){
                    Log.d("Data updated ---->","Data updated ---->");
                }
                else {
                    Log.d("ERRRRORRRRRRRR !!!!1","ERRRRRRRORRRRRR!!!!!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>data=new HashMap<>();
                data.put("email",email);
                return data;
            }
        };
        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void updateLogout(String email){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, updateLogoutURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(s.equals("done")){
                    Log.d("Data updated ---->","Data updated ---->");
                }
                else {
                    Log.d("ERRRRORRRRRRRR !!!!1","ERRRRRRRORRRRRR!!!!!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>data=new HashMap<>();
                data.put("email",email);
                return data;
            }
        };
        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
