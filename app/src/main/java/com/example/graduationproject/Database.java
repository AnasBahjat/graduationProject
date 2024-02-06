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

import java.util.HashMap;
import java.util.Map;

public class Database {
    String registrationURL = "http://192.168.1.5/graduationProject/registration.php";
    String loginURL="http://192.168.1.5/graduationProject/selectingData.php";
    private Context context;
    private RequestQueue requestQueue ;
    private int successFlag;
    public Database(Context context){
        this.context=context;
        requestQueue=Volley.newRequestQueue(context);
    }

    public void loginCheck(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET, loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("True")){
                    Toast.makeText(context,"Login successfully ",Toast.LENGTH_SHORT).show();
                    successFlag=1;
                }
                else {
                    Toast.makeText(context,"Wrong information",Toast.LENGTH_SHORT).show();
                    successFlag=0;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context,volleyError.toString()+"",Toast.LENGTH_SHORT).show();
                successFlag=-1;
            }
        });

        requestQueue.add(stringRequest);
    }

    public int getSuccessFlag() {
        return successFlag;
    }

    public int registerNewProfile(Profile profile){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, registrationURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(s.equals("True")){
                    successFlag=1;
                    Toast.makeText(context,"Registration Done !! ..",Toast.LENGTH_SHORT).show();
                    Log.d("-------iii----> "+getSuccessFlag(),"-----iii-------->"+getSuccessFlag());
                }
                else if (s.equals("exist")){
                    successFlag=-2;
                    Toast.makeText(context,"Email is already exists ..",Toast.LENGTH_SHORT).show();
                }
                else {
                    successFlag=0;
                    Toast.makeText(context,"Error registration",Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                successFlag=-1;
                Toast.makeText(context,volleyError.toString()+"--->",Toast.LENGTH_SHORT).show();

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
        Log.d("Here in database","here in database");
        return successFlag;
    }
}
