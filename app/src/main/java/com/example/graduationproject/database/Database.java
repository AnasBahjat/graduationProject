package com.example.graduationproject.database;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.interfaces.RequestResult;
import com.example.graduationproject.listeners.NotificationsListListener;
import com.example.graduationproject.listeners.TeacherAccountConfirmationListener;
import com.example.graduationproject.models.Profile;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.network.ApiService;
import com.example.graduationproject.network.RetrofitInitializer;
import com.example.graduationproject.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Database {
    String updateLoginURL = "http://192.168.1.136/graduationProject/updateLoginState.php/";
    String updateLogoutURL="http://192.168.1.136/graduationProject/updateLogoutState.php/";
    //String updateLogoutURL="http://192.168.1.136/graduationProject/updateLogoutState.php/";naseem

   // private String URL = "http://192.168.1.4/graduationProject/";
    private Context context;
    private RequestQueue requestQueue ;
    private int successFlag;
    public Database(Context context){
        this.context=context;
        requestQueue=Volley.newRequestQueue(context);
    }


    public void checkIfAccountDone(String email,final TeacherAccountConfirmationListener requestResult){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.checkAccountDoneURL,res->{
            if(res.equalsIgnoreCase("Not Done")){
                requestResult.updateTeacherNotifications(0,-5); // show the notification to confirm account ..
            }
            else if(res.equalsIgnoreCase("Error") || res.equalsIgnoreCase("")){
                requestResult.updateTeacherNotifications(-1,-5); // error occurred ..
            }
            else {
                requestResult.updateTeacherNotifications(1,Integer.parseInt(res)); // the account is done don't show notification
            }
        },errRes ->{
            requestResult.updateTeacherNotifications(-2,-5); // error occurred .
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>data=new HashMap<>();
                data.put("email",email.toLowerCase());
                return data;
            }
        };
        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void deleteConfirmedAccountNotification(int notificationId, Context context){
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.deleteConfirmedNotificationURL, res->{
            if(res.equalsIgnoreCase("Error")){
                MyAlertDialog.showCustomAlertDialogLoginError(context,"Error","Something went wrong please try again later");
            }
            else {
                Intent intent = new Intent();
                intent.setAction("UPDATE_NOTIFICATIONS_RECYCLER_VIEW");
                context.sendBroadcast(intent);
            }
        },err->{
            if(err.toString().equalsIgnoreCase("Error")){
                MyAlertDialog.showCustomAlertDialogLoginError(context,"Error","Something went wrong please try again later");
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String,String> data= new HashMap<>();
                data.put("notificationId",notificationId+"");
                return data;
            }
        };
        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void loginCheckRetrofit(String email,String password, final RequestResult requestResult){
        ApiService apiService = RetrofitInitializer.getClient(Constants.URL).create(ApiService.class);
        Call<ResponseBody> call = apiService.loginCheck(email,password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null){
                    try {
                        if(response.body().string().equals("email does not exist")){
                            requestResult.onLoginSuccess("email does not exist",null);
                        }
                        else if(response.body().string().equals("wrong password")){
                            requestResult.onLoginSuccess("wrong password",null);
                        }
                        else if(response.body().string().equals("ERROR")){
                            requestResult.onLoginSuccess("ERROR",null);
                        }
                        else {
                            requestResult.onLoginSuccess("success",new JSONArray(response.body().string()));
                        }
                    }
                    catch (JSONException | IOException e){
                        throw new RuntimeException(e);
                    }
                }
                else {
                    Toast.makeText(context,"--------------------------",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context,t.toString(),Toast.LENGTH_LONG).show();
                successFlag=-1;
            }
        });
    }
    public void loginCheck(String email,String password,final RequestResult requestFlagSetResult){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch (response) {
                    case "email does not exist":
                        requestFlagSetResult.onLoginSuccess("email does not exist", null);
                        break;
                    case "wrong password":
                        requestFlagSetResult.onLoginSuccess("wrong password", null);
                        break;
                    case "ERROR":
                        requestFlagSetResult.onLoginSuccess("ERROR", null);
                        break;
                    default:
                        try {
                            requestFlagSetResult.onLoginSuccess("success", new JSONArray(response));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }
        }, volleyError -> {
            Toast.makeText(context,volleyError.toString()+"1111",Toast.LENGTH_SHORT).show();
            requestFlagSetResult.onLoginSuccess("volleyError",null);
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


    public void registerRetrofitRequest(Profile profile, final RequestResult requestResult){
        ApiService apiService = RetrofitInitializer.getClient(Constants.URL).create(ApiService.class);

        Call<ResponseBody> call = apiService.insertNewProfile(profile.getFirstname(),
                profile.getLastname(),
                profile.getEmail(),
                profile.getBirthDate(),
                profile.getGender(),
                profile.getPassword(),
                profile.getPhoneNumber(),
                profile.getProfileType());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null){
                    try {
                        String message = response.body().string();
                        Log.d("--------> "+message,"--------> "+message);
                        if ("True".equals(message)) {
                            requestResult.onSuccess(1);
                        } else if ("exist".equals(message)) {
                            requestResult.onSuccess(-2);
                        } else {
                            requestResult.onSuccess(0);
                        }
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                        }
                    }
                else {
                    Log.d("Database", "Request failed");
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                successFlag=-1;
                requestResult.onSuccess(-1);
                Log.e("MainActivity", "Request failed", t);
                Toast.makeText(context,t.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }



    public void registerNewProfile(Profile profile, final RequestResult requestFlagSetResult){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.registrationURL, s -> {
            if(s.equals("True")){
                requestFlagSetResult.onSuccess(1);
            }
            else if (s.equals("exist")){
                requestFlagSetResult.onSuccess(-2);
            }
            else {
                Log.d("sssssssssssss----> "+s,"sssssssssssss----> "+s);
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
                data.put("gender",profile.getGender());
                data.put("profileType",profile.getProfileType());
                data.put("birthDate",profile.getBirthDate());
                data.put("idNumber",profile.getFirstname().trim());
                data.put("phoneNumber",profile.getPhoneNumber().trim());
                return data;
            }

        };
        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void updateLoginRetrofit(String email){
        ApiService apiService = RetrofitInitializer.getClient(Constants.URL).create(ApiService.class);

        Call<String> call = apiService.updateLogin(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().equals("done")){
                        Log.d("Data updated ---->","Data updated ---->");
                    }
                    else {
                        Log.d("ERRRRORRRRRRRR !!!!1","ERRRRRRRORRRRRR!!!!!");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

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

    public void updateTeacherInformation(Teacher teacher,final TeacherAccountConfirmationListener requestResult){
        Log.d("Email -----> "+teacher.getEmail(),"Email -----> "+teacher.getEmail());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.updateTeacherInformation, s-> {
            Toast.makeText(context,s,Toast.LENGTH_LONG).show();
                if(s.equalsIgnoreCase("exists")){
                    requestResult.onResult(0);
                }
                else if(s.equalsIgnoreCase("Done insertion")){
                    requestResult.onResult(1);
                }
                else {
                    requestResult.onResult(-1);
                }
        }, err-> {
                requestResult.onResult(-2);
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>data=new HashMap<>();
                data.put("email",teacher.getEmail());
                data.put("idNumber",teacher.getIdNumber());
                data.put("studentOrGraduate",teacher.getStudentOrGraduate());
                data.put("expectedGraduationYear",teacher.getExpectedGraduationYear());
                data.put("college",teacher.getCollege());
                data.put("field",teacher.getField());
                data.put("daysAvailableWeekly",teacher.getDaysAvailableWeekly());
                data.put("hoursAvailableDaily",teacher.getHoursAvailableDaily());
                data.put("city",teacher.getAddress().getCity());
                data.put("country",teacher.getAddress().getCountry());
                return data;
            }
        };
        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getNotifications(String email, final NotificationsListListener notificationsListListener){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.getNotificationsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(s.equalsIgnoreCase("Error")){
                    notificationsListListener.getNotifications(0,null);
                }
                else {
                    try{
                        notificationsListListener.getNotifications(1,new JSONArray(s));
                    }
                    catch (JSONException e){
                        throw new RuntimeException();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                notificationsListListener.getNotifications(-1,null);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data=new HashMap<>();
                data.put("email",email);
                return data;
            }
        };
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
