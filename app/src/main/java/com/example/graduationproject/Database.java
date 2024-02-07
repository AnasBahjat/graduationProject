package com.example.graduationproject;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Database {
    String registrationURL = "http://192.168.1.5/graduationProject/registration.php";
    String loginURL="http://192.168.1.5/graduationProject/data.php";
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context,volleyError.toString()+"",Toast.LENGTH_SHORT).show();
                successFlag=-1;
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>data=new HashMap<>();
                data.put("email",email);
                data.put("password",password);
                return data;
            }
        };

        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }






    public void registerNewProfile(Profile profile,final RequestResult requestFlagSetResult){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, registrationURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(s.equals("True")){
                   // successFlag=1;
                    requestFlagSetResult.onSuccess(1);
                    //Toast.makeText(context,"Registration Done !! ..",Toast.LENGTH_SHORT).show();
                }
                else if (s.equals("exist")){
                    //successFlag=-2;
                    requestFlagSetResult.onSuccess(-2);
                    //Toast.makeText(context,"Email is already exists ..",Toast.LENGTH_SHORT).show();
                }
                else {
                    //successFlag=0;
                    requestFlagSetResult.onSuccess(0);
                    //Toast.makeText(context,"Error registration",Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                successFlag=-1;
                requestFlagSetResult.onSuccess(-1);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>data=new HashMap<>();
                data.put("email",profile.getEmail());
                data.put("firstname",profile.getFirstname());
                data.put("lastname",profile.getLastname());
                data.put("password",profile.getPassword());
                data.put("gender",String.valueOf(profile.getGender()));
                data.put("profileType",String.valueOf(profile.getProfileType()));
                data.put("date",profile.getBirthDate());
                data.put("IDnumber",profile.getFirstname());
                data.put("phonenumber",profile.getPhoneNumber());
                data.put("city",profile.getCity());
                data.put("country",profile.getCountry());
                return data;
            }

        };
        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
