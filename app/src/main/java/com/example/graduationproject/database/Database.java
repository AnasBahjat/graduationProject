package com.example.graduationproject.database;

import android.content.Context;
import android.content.Intent;
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
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.interfaces.RequestResult;
import com.example.graduationproject.listeners.AddNewChildListener;
import com.example.graduationproject.listeners.AddTeacherMatchingListener;
import com.example.graduationproject.listeners.DeletePostedRequestListener;
import com.example.graduationproject.listeners.FetchCoursesListener;
import com.example.graduationproject.listeners.GetParentChildren;
import com.example.graduationproject.listeners.GetParentChildrenForRequest;
import com.example.graduationproject.listeners.LastMatchingIdListener;
import com.example.graduationproject.listeners.NotificationsListListener;
import com.example.graduationproject.listeners.OnAllTeacherPostedRequestsForParentListener;
import com.example.graduationproject.listeners.OnCheckIfRequestSentBeforeListener;
import com.example.graduationproject.listeners.OnParentCoursesFetchedForConflictListener;
import com.example.graduationproject.listeners.OnParentCoursesReceivedListener;
import com.example.graduationproject.listeners.OnParentSentRequestFetchListener;
import com.example.graduationproject.listeners.OnParentToTeacherRequestSentListener;
import com.example.graduationproject.listeners.OnSentRequestDeletedListener;
import com.example.graduationproject.listeners.OnTeacherCoursesFetchedForConflictListener;
import com.example.graduationproject.listeners.OnTeacherCoursesFetchedListener;
import com.example.graduationproject.listeners.OnTeacherToParentRequestSentListener;
import com.example.graduationproject.listeners.OnProfileDataFetchListener;
import com.example.graduationproject.listeners.OnCourseAddedListener;
import com.example.graduationproject.listeners.OnTeacherCoursesReceivedListener;
import com.example.graduationproject.listeners.OnTeacherPostRequestUpdateListener;
import com.example.graduationproject.listeners.OnReceivedRequestsListener;
import com.example.graduationproject.listeners.ParentListenerForParentPostedRequests;
import com.example.graduationproject.listeners.ParentInformationListener;
import com.example.graduationproject.listeners.ParentPostRequestDeleteListener;
import com.example.graduationproject.listeners.ParentRequestToSendListener;
import com.example.graduationproject.listeners.PostedTeacherRequestsListener;
import com.example.graduationproject.listeners.TeacherAccountConfirmationListener;
import com.example.graduationproject.listeners.TeacherAvailabilityListener;
import com.example.graduationproject.listeners.TeacherPostListener;
import com.example.graduationproject.listeners.TellParentDataIsReady;
import com.example.graduationproject.listeners.UpdateParentInformation;
import com.example.graduationproject.listeners.UpdateTeacherPostedRequestListener;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.Parent;
import com.example.graduationproject.models.ParentReceivedRequest;
import com.example.graduationproject.models.ParentRequestToSend;
import com.example.graduationproject.models.Profile;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.models.TeacherPostRequest;
import com.example.graduationproject.network.ApiService;
import com.example.graduationproject.network.RetrofitInitializer;
import com.example.graduationproject.utils.Constants;
import com.example.graduationproject.utils.DateUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Database {
    String updateLoginURL = "http://192.168.1.4/graduationProject/updateLoginState.php/";
    String updateLogoutURL="http://192.168.1.4/graduationProject/updateLogoutState.php/";

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
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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
                profile.getProfileType());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null){
                    try {
                        String message = response.body().string();
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

    public void updateTeacherInformation(Teacher teacher,String fullPhoneNumber,final TeacherAccountConfirmationListener requestResult){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Constants.updateTeacherInformation, s-> {
                if(s.equalsIgnoreCase("exists")){
                    requestResult.onResult(0);
                }
                else if(s.equalsIgnoreCase("Done insertion")){
                    requestResult.onResult(1);
                }
                else {
                    Log.d(s,s);
                    requestResult.onResult(-1);
                }
        }, err-> {
                Log.d(err.toString(),err.toString());
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
                data.put("availability",teacher.getAvailability());
                data.put("city",teacher.getAddress().getCity());
                data.put("country",teacher.getAddress().getCountry());
                data.put("educationLevel",teacher.getEducationalLevel());
                data.put("phoneNumber",fullPhoneNumber);
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
    public void confirmParentInformation(Parent parent, final UpdateParentInformation updateParentInformation){
        Gson gson = new Gson();
        String jsonChildren = gson.toJson(parent.getChildrenList());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.updateParentInformation,resp->{
            if(resp.equalsIgnoreCase("Done Insertion")){
                updateParentInformation.onResult(1);
            }
            else {
                Log.d("--------> parent "+resp,"------> parent"+resp);
                updateParentInformation.onResult(0);
            }
        },err->{
            Log.d("--------> parent "+err.toString(),"------> parent"+err.toString());

            updateParentInformation.onResult(-1);
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("email",parent.getEmail());
                data.put("idNumber",parent.getIdNumber());
                data.put("phoneNumber",parent.getPhoneNumber());
                data.put("city",parent.getCity());
                data.put("country",parent.getCountry());
                data.put("children",jsonChildren);
                return data ;
            }
        };
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void getTeacherAllData(String email, final TellParentDataIsReady tellParentDataIsReady){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getAllTeacherData,resp->{
            try {
                Log.d("Reqesttt---> "+resp,"Reqesttt---> "+resp);
                if(resp.equalsIgnoreCase("Connection Error")){
                    tellParentDataIsReady.onDataReady(-3,null);
                }
                else if(resp.equalsIgnoreCase("ERROR")){
                    tellParentDataIsReady.onDataReady(-2,null);
                }
                else if(resp.equalsIgnoreCase("No data")){
                    tellParentDataIsReady.onDataReady(-1,null);
                }
                else{
                    tellParentDataIsReady.onDataReady(1,new JSONArray(resp));
                }
            }
            catch (JSONException e){
                tellParentDataIsReady.onDataReady(-5,null);
            }
        },error->{
            tellParentDataIsReady.onDataReady(-4,null);
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap();
                data.put("email",email);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getParentChildren(String parentEmail, final GetParentChildren getParentChildrenResult){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getParentChildren,resp->{
            try {
                if(resp.equalsIgnoreCase("No Data")){
                    getParentChildrenResult.getChildrenResult(-1,null);
                }
                else if(resp.equalsIgnoreCase("ERROR")){
                    getParentChildrenResult.getChildrenResult(-2,null);
                }
                else if(resp.equalsIgnoreCase("Connection Error")){
                    getParentChildrenResult.getChildrenResult(0,null);
                }
                else {
                    getParentChildrenResult.getChildrenResult(1,new JSONArray(resp));
                }
            }
            catch (JSONException e){

            }
        },err ->{
            getParentChildrenResult.getChildrenResult(-5,null);
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("email",parentEmail);
                return data ;
            }
        };
        queue.add(stringRequest);
    }

    public void getParentChildrenForRequest(String parentEmail, final GetParentChildrenForRequest getParentChildrenForRequestResult){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getParentChildren,resp->{
            try {
                if(resp.equalsIgnoreCase("No Data")){
                    getParentChildrenForRequestResult.getChildrenForRequestResult(-1,null);
                }
                else if(resp.equalsIgnoreCase("ERROR")){
                    getParentChildrenForRequestResult.getChildrenForRequestResult(-2,null);
                }
                else if(resp.equalsIgnoreCase("Connection Error")){
                    getParentChildrenForRequestResult.getChildrenForRequestResult(0,null);
                }
                else {
                    getParentChildrenForRequestResult.getChildrenForRequestResult(1,new JSONArray(resp));
                }
            }
            catch (JSONException e){

            }
        },err ->{
            getParentChildrenForRequestResult.getChildrenForRequestResult(-5,null);
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("email",parentEmail);
                return data ;
            }
        };
        queue.add(stringRequest);
    }


    public void addNewChild(String parentEmail, Children child, final AddNewChildListener addNewChildListener){

        RequestQueue queue=Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.addNewChild,resp->{
            if(resp.equalsIgnoreCase("Done")){
                addNewChildListener.onChildAdded(1);
            }
            else {
                addNewChildListener.onChildAdded(0);
            }
        },err->{
            addNewChildListener.onChildAdded(-1);
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("parentEmail",parentEmail);
                data.put("childName",child.getChildName());
                data.put("childAge",child.getChildAge());
                data.put("childGender",child.getChildGender()+"");
                data.put("childGrade",child.getGrade()+"");
                return data ;
            }
        };
        queue.add(stringRequest);
    }

    public void addNewTeacherMatching(String parentEmail , TeacherMatchModel teacherMatchModel,final AddTeacherMatchingListener addTeacherMatchingListener){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.addNewTeacherMatching,response ->{
            if(response.equalsIgnoreCase("Done"))
                addTeacherMatchingListener.onMatchingAdded(1);
            else if(response.equalsIgnoreCase("Error"))
                addTeacherMatchingListener.onMatchingAdded(-1);
            else
                addTeacherMatchingListener.onMatchingAdded(-2);
        },error ->{
            addTeacherMatchingListener.onMatchingAdded(0);
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> data = new HashMap();
                data.put("parentEmail",parentEmail);
                data.put("childId",teacherMatchModel.getCustomChildData().getChildId()+"");
                data.put("choseDays",teacherMatchModel.getChoseDays());
                data.put("courses",teacherMatchModel.getCourses());
                data.put("location",teacherMatchModel.getLocation());
                data.put("teachingMethod",teacherMatchModel.getTeachingMethod());
                data.put("startTime",teacherMatchModel.getStartTime());
                data.put("endTime",teacherMatchModel.getEndTime());
                data.put("priceMin",teacherMatchModel.getPriceMinimum()+"");
                data.put("priceMax",teacherMatchModel.getPriceMaximum()+"");
                data.put("startDate",teacherMatchModel.getStartDate());
                data.put("endDate",teacherMatchModel.getEndDate());
                return  data;
            }
        };
        queue.add(stringRequest);
    }

    public void getTeacherMatchingData(String email,final AddTeacherMatchingListener addTeacherMatchingListener){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getParentPostedTeacherMatchingData, res->{
            if(res.equalsIgnoreCase("Error"))
                addTeacherMatchingListener.getTeacherMatchingData(0,null);
            else if(res.equalsIgnoreCase("Connection Error"))
                addTeacherMatchingListener.getTeacherMatchingData(-2,null);
            else {
                try {
                    addTeacherMatchingListener.getTeacherMatchingData(1,new JSONArray(res));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },err->{
            addTeacherMatchingListener.getTeacherMatchingData(-1,null);
        });
        queue.add(stringRequest);
    }

    public void getParentInformation(String parentEmail , final ParentInformationListener parentInformationListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getParentInformation,resp->{
            if(resp.equalsIgnoreCase("ERROR"))
                parentInformationListener.onResultParentInformation(-1,null);
            else if(resp.equalsIgnoreCase("No Data"))
                parentInformationListener.onResultParentInformation(-2,null);
            else if(resp.equalsIgnoreCase("Connection Error"))
                parentInformationListener.onResultParentInformation(-3,null);
            else {
                try {
                    parentInformationListener.onResultParentInformation(1,new JSONArray(resp));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },error->{
            parentInformationListener.onResultParentInformation(-4,null);
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("parentEmail",parentEmail);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getParentPostedMatchingInformation(String parentEmail , final ParentListenerForParentPostedRequests parentInformationListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getParentPostedMatchingInformation,resp->{
            if (resp.equalsIgnoreCase("ERROR")){
                parentInformationListener.onPostedParentRequests(-3,null);
            }

            else if(resp.equalsIgnoreCase("No Requests"))
                parentInformationListener.onPostedParentRequests(-2,null);

            else if(resp.equalsIgnoreCase("Connection Error")){
                parentInformationListener.onPostedParentRequests(-1,null);
            }
            else {
                try {
                    parentInformationListener.onPostedParentRequests(1,new JSONArray(resp));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },err->{
            parentInformationListener.onPostedParentRequests(0,null);
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("email",parentEmail);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void updateParentPostedRequest(String email, TeacherMatchModel teacherMatchModel, final UpdateTeacherPostedRequestListener updateTeacherPostedRequest){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.updateParentPostedRequest, res->{
            if(res.equalsIgnoreCase("no data"))
                updateTeacherPostedRequest.onDataUpdate(-2);
            else if(res.equalsIgnoreCase("Error"))
                updateTeacherPostedRequest.onDataUpdate(-1);
            else if(res.equalsIgnoreCase("Connection Error"))
                updateTeacherPostedRequest.onDataUpdate(-3);
            else if(res.equalsIgnoreCase("Done")){
                updateTeacherPostedRequest.onDataUpdate(1);
            }
            else
                updateTeacherPostedRequest.onDataUpdate(-4);
        },err->{
            updateTeacherPostedRequest.onDataUpdate(0);
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("childId", teacherMatchModel.getCustomChildData().getChildId() + "");
                data.put("matchingId", teacherMatchModel.getMatchingId() + "");
                data.put("email", email);
                data.put("selectedDays", teacherMatchModel.getChoseDays());
                data.put("startTime", teacherMatchModel.getStartTime());
                data.put("endTime", teacherMatchModel.getEndTime());
                data.put("courses", teacherMatchModel.getCourses());
                data.put("location", teacherMatchModel.getLocation());
                data.put("teachingMethod", teacherMatchModel.getTeachingMethod());
                data.put("priceMin", teacherMatchModel.getPriceMinimum()+"");
                data.put("priceMax", teacherMatchModel.getPriceMaximum()+"");
                data.put("startDate", teacherMatchModel.getStartDate());
                data.put("endDate", teacherMatchModel.getEndDate());
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getLastMatchingId(final LastMatchingIdListener lastMatchingIdListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constants.getLastMatchingIdValue,res->{
            if(res.equalsIgnoreCase("Empty Table")){
                lastMatchingIdListener.onLastMatchingIdFetched(0,null);
            }
            else if(res.equalsIgnoreCase("Error")){
                lastMatchingIdListener.onLastMatchingIdFetched(-1,null);
            }
            else if(res.equalsIgnoreCase("Connection Error")){
                lastMatchingIdListener.onLastMatchingIdFetched(-2,null);
            }
            else {
                lastMatchingIdListener.onLastMatchingIdFetched(1,res);
            }
        },err->{
            lastMatchingIdListener.onLastMatchingIdFetched(-3,null);
        });

        requestQueue.add(stringRequest);
    }

    public void insertTeacherPostRequest(TeacherPostRequest teacherPostRequest,final TeacherPostListener teacherPostListener){
        requestQueue = Volley.newRequestQueue(context);


        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.insertTeacherPostRequest,resp->{
            if(resp.equalsIgnoreCase("Connection Error"))
                teacherPostListener.onTeacherPostAdded(-2);
            else if(resp.equalsIgnoreCase("Error")){
                teacherPostListener.onTeacherPostAdded(-1);
            }
            else if(resp.equalsIgnoreCase("Done"))
                teacherPostListener.onTeacherPostAdded(1);
        },err->{
            Log.d("the error is ++++ "+err,"the error is ++++ "+err);
            teacherPostListener.onTeacherPostAdded(0);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> data = new HashMap<>();
                data.put("teacherEmail",teacherPostRequest.getTeacherEmail());
                data.put("courses",teacherPostRequest.getCourses());
                data.put("educationLevel",teacherPostRequest.getEducationLevel());
                data.put("availabilityForJob",teacherPostRequest.getAvailability());
                data.put("duration",teacherPostRequest.getDuration());
                data.put("location",teacherPostRequest.getLocation());
                data.put("teachingMethod",teacherPostRequest.getTeachingMethod());
                data.put("startTime",teacherPostRequest.getStartTime());
                data.put("endTime",teacherPostRequest.getEndTime());
                data.put("price",teacherPostRequest.getPrice()+"");
                data.put("startDate",teacherPostRequest.getStartDate());
                data.put("endDate",teacherPostRequest.getEndDate());
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void getLastTeacherPostId(final LastMatchingIdListener lastMatchingIdListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constants.getLastTeacherPostId,res->{
            if(res.equalsIgnoreCase("Empty Table")){
                lastMatchingIdListener.onLastMatchingIdFetched(0,null);
            }
            else if(res.equalsIgnoreCase("Error")){
                lastMatchingIdListener.onLastMatchingIdFetched(-1,null);
            }
            else if(res.equalsIgnoreCase("Connection Error")){
                lastMatchingIdListener.onLastMatchingIdFetched(-2,null);
            }
            else {
                lastMatchingIdListener.onLastMatchingIdFetched(1,res);
            }
        },err->{
            lastMatchingIdListener.onLastMatchingIdFetched(-3,null);
        });
        requestQueue.add(stringRequest);
    }

    public void getTeacherPostedRequests(String email,final PostedTeacherRequestsListener postedTeacherRequestsListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getTeacherPostedRequests,resp->{
            if(resp.equalsIgnoreCase("Connection Error"))
                postedTeacherRequestsListener.onDataFetched(-1,null);
            else if(resp.equalsIgnoreCase("Error"))
                postedTeacherRequestsListener.onDataFetched(0,null);
            else {
                try {
                    postedTeacherRequestsListener.onDataFetched(1,new JSONArray(resp));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },err->{
            postedTeacherRequestsListener.onDataFetched(-1,null);
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("email",email);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void getTeacherAvailability(String email, final TeacherAvailabilityListener teacherAvailabilityListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getTeacherAvailability,resp->{
            if(resp.equalsIgnoreCase("Error"))
                teacherAvailabilityListener.onAvailabilityFetched(0,null);
            else if(resp.equalsIgnoreCase("Connection Error"))
                teacherAvailabilityListener.onAvailabilityFetched(-1,null);
            else
                teacherAvailabilityListener.onAvailabilityFetched(1,resp);
        },err->{
            teacherAvailabilityListener.onAvailabilityFetched(-2,null);
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("email",email);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void updateTeacherPostedRequest(TeacherPostRequest teacherPostRequest,final OnTeacherPostRequestUpdateListener onTeacherPostRequestUpdateListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.updateTeacherPostedRequest,resp->{
            if(resp.equalsIgnoreCase("Connection Error"))
                onTeacherPostRequestUpdateListener.onTeacherPostRequestUpdate(-2);
            else if(resp.equalsIgnoreCase("Error Updating"))
                onTeacherPostRequestUpdateListener.onTeacherPostRequestUpdate(-1);
            else if(resp.equalsIgnoreCase("No Request"))
                onTeacherPostRequestUpdateListener.onTeacherPostRequestUpdate(0);
            else if(resp.equalsIgnoreCase("updated"))
                onTeacherPostRequestUpdateListener.onTeacherPostRequestUpdate(1);
        },err->{
            onTeacherPostRequestUpdateListener.onTeacherPostRequestUpdate(2);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> data = new HashMap<>();
                data.put("postId",teacherPostRequest.getTeacherPostRequestId()+"");
                data.put("teacherEmail",teacherPostRequest.getTeacherEmail());
                data.put("courses",teacherPostRequest.getCourses());
                data.put("educationLevel",teacherPostRequest.getEducationLevel());
                data.put("availabilityForJob",teacherPostRequest.getAvailability());
                data.put("duration",teacherPostRequest.getDuration());
                data.put("location",teacherPostRequest.getLocation());
                data.put("teachingMethod",teacherPostRequest.getTeachingMethod());
                data.put("startTime",teacherPostRequest.getStartTime());
                data.put("endTime",teacherPostRequest.getEndTime());
                data.put("price",teacherPostRequest.getPrice()+"");
                data.put("startDate",teacherPostRequest.getStartDate());
                data.put("endDate",teacherPostRequest.getEndDate());
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void deleteTeacherPostedRequest(TeacherPostRequest teacherPostRequest,final DeletePostedRequestListener deletePostedRequestListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.deleteTeacherPostedRequest, resp->{
            if(resp.equalsIgnoreCase("Deleted")){
                deletePostedRequestListener.onPostedRequestDeleted(1);
            }
            else if(resp.equalsIgnoreCase("Error")){
                deletePostedRequestListener.onPostedRequestDeleted(-1);
            }
            else if(resp.equalsIgnoreCase("No Request")){
                deletePostedRequestListener.onPostedRequestDeleted(-3);
            }
            else {
                deletePostedRequestListener.onPostedRequestDeleted(0);
            }
        },err->{
            deletePostedRequestListener.onPostedRequestDeleted(-2);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("postId",teacherPostRequest.getTeacherPostRequestId()+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void deleteParentPostedRequest(int parentPostedId,final ParentPostRequestDeleteListener parentPostRequestDeleteListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.deleteParentPostedRequest,resp->{
            if(resp.trim().equalsIgnoreCase("Done"))
                parentPostRequestDeleteListener.onParentPostDeleted(3);
            else if(resp.trim().equalsIgnoreCase("No Request"))
                parentPostRequestDeleteListener.onParentPostDeleted(-2);
            else if(resp.trim().equalsIgnoreCase("Error"))
                parentPostRequestDeleteListener.onParentPostDeleted(0);
        },err->{
            parentPostRequestDeleteListener.onParentPostDeleted(-1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> data = new HashMap<>();
                data.put("postId",parentPostedId+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getAllTeacherPostedRequestsForParent(final OnAllTeacherPostedRequestsForParentListener onAllTeacherPostedRequestsForParentListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,Constants.getAllTeacherPostedRequests,resp->{
            try {
                if (resp.trim().equalsIgnoreCase("No Requests")) {
                    onAllTeacherPostedRequestsForParentListener.onAllTeacherPostedRequestsForParentFetched(0, null);

                } else if (resp.trim().equalsIgnoreCase("Error")) {
                    onAllTeacherPostedRequestsForParentListener.onAllTeacherPostedRequestsForParentFetched(-1, null);

                } else if (resp.trim().equalsIgnoreCase("Connection Error")) {
                    onAllTeacherPostedRequestsForParentListener.onAllTeacherPostedRequestsForParentFetched(-2, null);
                } else {
                    onAllTeacherPostedRequestsForParentListener.onAllTeacherPostedRequestsForParentFetched(1, new JSONArray(resp));
                }
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        },err->{
            try{
                onAllTeacherPostedRequestsForParentListener.onAllTeacherPostedRequestsForParentFetched(-2,null);
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getCurrentProfileData(String email,final OnProfileDataFetchListener onProfileDataFetchListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getCurrentProfileData,resp->{

        },err->{

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("email",email);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void addParentSentRequestToTeacher(ParentRequestToSend parentRequestToSend,final OnParentToTeacherRequestSentListener onParentToTeacherRequestSent){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.addParentSentRequestToTeacher,resp->{
            if(resp.equalsIgnoreCase("Connection Error"))
                onParentToTeacherRequestSent.onParentToTeacherRequestSent(-2);
            else if(resp.equalsIgnoreCase("Error"))
                onParentToTeacherRequestSent.onParentToTeacherRequestSent(0);
            else if(resp.equalsIgnoreCase("Done"))
                onParentToTeacherRequestSent.onParentToTeacherRequestSent(1);
            else
                onParentToTeacherRequestSent.onParentToTeacherRequestSent(-3);
        },err->{
            onParentToTeacherRequestSent.onParentToTeacherRequestSent(-1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("teacherPostRequestId", String.valueOf(parentRequestToSend.getTeacherPostRequestId()));
                data.put("parentEmail", parentRequestToSend.getParentEmail());
                data.put("teacherEmail", parentRequestToSend.getTeacherEmail());
                JSONArray jsonArray = new JSONArray();
                for (Integer childId : parentRequestToSend.getChildrenIds()) {
                    jsonArray.put(childId);
                }
                data.put("childrenIds", jsonArray.toString());
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getTeacherReceivedRequests(String email , final OnReceivedRequestsListener onTeacherReceivedRequests ){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getTeacherReceivedRequests,res->{
            if(res.equalsIgnoreCase("Connection Error"))
                onTeacherReceivedRequests.onRequestsReceived(-2,null);
            else if(res.equalsIgnoreCase("Error"))
                onTeacherReceivedRequests.onRequestsReceived(-1,null);
            else if(res.equalsIgnoreCase("No Requests"))
                onTeacherReceivedRequests.onRequestsReceived(0,null);
            else {
                try {
                    onTeacherReceivedRequests.onRequestsReceived(1,new JSONArray(res));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },err->{
            onTeacherReceivedRequests.onRequestsReceived(-2,null);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("email",email);
                return data ;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void setNotificationIsRead(int notificationId){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.setNotificationIsRead,res->{

        },err->{

        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> data = new HashMap<>();
                data.put("id",notificationId+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getAllTeacherCoursesDates(String teacherEmail,final OnTeacherCoursesReceivedListener onTeacherCoursesReceivedListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getTeacherCoursesDates,resp->{
            if(resp.equalsIgnoreCase("No Courses")){
                onTeacherCoursesReceivedListener.onCoursesReceived(0,null);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onTeacherCoursesReceivedListener.onCoursesReceived(-1,null);
            }
            else if (resp.equalsIgnoreCase("Connection Error")){
                onTeacherCoursesReceivedListener.onCoursesReceived(-2,null);
            }
            else {
                try {
                    onTeacherCoursesReceivedListener.onCoursesReceived(1,new JSONArray(resp));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },err->{
            onTeacherCoursesReceivedListener.onCoursesReceived(-1,null);
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> data = new HashMap<>();
                data.put("teacherEmail",teacherEmail);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void insertTeacherCourse(TeacherPostRequest tpr,String parentEmail,int childId, final OnCourseAddedListener onTeacherCourseAddedListener) {
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.insertTeacherCourse,resp->{
           // Toast.makeText(context, resp, Toast.LENGTH_SHORT).show();
            Log.d("ERROR adding course --------> "+resp,"Errrr adding course ----> "+resp);
            if(resp.equalsIgnoreCase("Done")){
                onTeacherCourseAddedListener.onCourseAdded(1);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onTeacherCourseAddedListener.onCourseAdded(-1);
            }
            else {
                onTeacherCourseAddedListener.onCourseAdded(-2);
            }
        },error ->{
            onTeacherCourseAddedListener.onCourseAdded(-1);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("teacherEmail",tpr.getTeacherEmail());
                data.put("parentEmail",parentEmail);
                data.put("childId",childId+"");
                data.put("requestId",tpr.getTeacherPostRequestId()+"");
                data.put("courses",tpr.getCourses());
                data.put("educationLevel",tpr.getEducationLevel());
                data.put("duration", tpr.getDuration());
                data.put("availabilityForJob",tpr.getAvailability());
                data.put("location",tpr.getLocation());
                data.put("teachingMethod",tpr.getTeachingMethod());
                data.put("startTime",tpr.getStartTime());
                data.put("endTime",tpr.getEndTime());
                data.put("startDate",tpr.getStartDate());
                data.put("endDate",tpr.getEndDate());
                data.put("price",tpr.getPrice()+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void setTeacherReceivedRequestToDecline(int requestId){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.deleteRequestFromSent, response ->{

        },error -> {

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> data = new HashMap<>();
                data.put("requestId",requestId+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void addTeacherSentRequestToParent(String teacherEmail,TeacherMatchModel teacherMatchModel,final OnTeacherToParentRequestSentListener onTeacherToParentRequestSentListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.addTeacherSentRequestToParent,resp->{
            if(resp.equalsIgnoreCase("Request Sent Before")){
                onTeacherToParentRequestSentListener.onTeacherToParentRequestSent(0);
            }
            else if(resp.equalsIgnoreCase("Done")){
                onTeacherToParentRequestSentListener.onTeacherToParentRequestSent(1);
            }

            else if(resp.equalsIgnoreCase("Error")){
                onTeacherToParentRequestSentListener.onTeacherToParentRequestSent(-1);
            }
            else {
                onTeacherToParentRequestSentListener.onTeacherToParentRequestSent(-2);

            }
        },err->{
            onTeacherToParentRequestSentListener.onTeacherToParentRequestSent(-1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> data = new HashMap<>();
                data.put("matchingId",teacherMatchModel.getMatchingId()+"");
                data.put("parentEmail",teacherMatchModel.getParentEmail());
                data.put("teacherEmail",teacherEmail);
                data.put("childId",teacherMatchModel.getCustomChildData().getChildId()+"");
                return data ;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void checkIfTeacherRequestSentBefore(String teacherEmail,TeacherMatchModel teacherMatchModel,final OnCheckIfRequestSentBeforeListener onCheckIfRequestSentBefore){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.checkIfTeacherRequestSentBefore,resp->{
            if (resp.equalsIgnoreCase("Exists")){
                onCheckIfRequestSentBefore.onRequestSent(0);
            }
            else if(resp.equalsIgnoreCase("Not Exist")){
                onCheckIfRequestSentBefore.onRequestSent(1);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onCheckIfRequestSentBefore.onRequestSent(-1);
            }
            else {
                onCheckIfRequestSentBefore.onRequestSent(-2);
            }
        },err->{
            onCheckIfRequestSentBefore.onRequestSent(-2);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("teacherEmail",teacherEmail);
                data.put("parentEmail",teacherMatchModel.getParentEmail());
                data.put("matchingId",teacherMatchModel.getMatchingId()+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    /*public void checkIfParentRequestSentBefore(String parentEmail,TeacherPostRequest teacherPostRequest,final OnCheckIfRequestSentBeforeListener onCheckIfRequestSentBefore){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.checkIfParentRequestSentBefore,resp->{
            if (resp.equalsIgnoreCase("Exists")){
                onCheckIfRequestSentBefore.onRequestSent(0);
            }
            else if(resp.equalsIgnoreCase("Not Exist")){
                onCheckIfRequestSentBefore.onRequestSent(1);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onCheckIfRequestSentBefore.onRequestSent(-1);
            }
            else {
                onCheckIfRequestSentBefore.onRequestSent(-2);
            }
        },err->{
            onCheckIfRequestSentBefore.onRequestSent(-2);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("parentEmail",parentEmail);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }*/

    public void getParentReceivedRequest(String parentEmail, final OnReceivedRequestsListener onReceivedRequestsListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getParentReceivedRequest,resp->{
            Log.d("-----> "+resp,"-----> "+resp);
            if(resp.equalsIgnoreCase("No Requests")){
                onReceivedRequestsListener.onRequestsReceived(0,null);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onReceivedRequestsListener.onRequestsReceived(-1,null);

            }
            else if(resp.equalsIgnoreCase("Connection Error")){
                onReceivedRequestsListener.onRequestsReceived(-2,null);

            }
            else {
                try {
                    onReceivedRequestsListener.onRequestsReceived(1,new JSONArray(resp));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },err->{
            onReceivedRequestsListener.onRequestsReceived(-2,null);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("parentEmail",parentEmail);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getAllParentChildrenCoursesDates(String email,final OnParentCoursesReceivedListener onParentCoursesReceivedListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getAllParentChildrenCoursesDates,resp->{
            if(resp.equalsIgnoreCase("No Courses")){
                onParentCoursesReceivedListener.onParentCoursesReceived(0,null);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onParentCoursesReceivedListener.onParentCoursesReceived(-1,null);
            }
            else if(resp.equalsIgnoreCase("Connection Error")){
                onParentCoursesReceivedListener.onParentCoursesReceived(-2,null);
            }
            else {
                try {
                    onParentCoursesReceivedListener.onParentCoursesReceived(1,new JSONArray(resp));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        },err->{
            onParentCoursesReceivedListener.onParentCoursesReceived(-2,null);

        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> data = new HashMap<>();
                data.put("email",email);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    /*public void insertParentCourse(int requestId,double price,TeacherMatchModel tmr,String teacherEmail, final OnCourseAddedListener onCourseAddedListener) {
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.insertParentCourse,resp->{
            if(resp.equalsIgnoreCase("Done")){
                onCourseAddedListener.onCourseAdded(1);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onCourseAddedListener.onCourseAdded(-1);
            }
            else {
                onCourseAddedListener.onCourseAdded(-2);
            }
        },error ->{
            onCourseAddedListener.onCourseAdded(-1);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("parentEmail",tmr.getParentEmail());
                data.put("teacherEmail",teacherEmail);
                data.put("requestId",requestId+"");
                data.put("childId",tmr.getCustomChildData().getChildId()+"");
                data.put("choseDays",tmr.getChoseDays());
                data.put("courses", tmr.getCourses());
                data.put("location",tmr.getLocation());
                data.put("teachingMethod",tmr.getTeachingMethod());
                data.put("startTime",tmr.getStartTime());
                data.put("endTime",tmr.getEndTime());
                data.put("startDate",tmr.getStartDate());
                data.put("endDate",tmr.getEndDate());
                data.put("price",price+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }*/

    public void insertParentCourse(double price, ParentReceivedRequest prr, final OnCourseAddedListener onCourseAddedListener) {
        Log.d("Child Id is ---------> "+prr.getTeacherMatchModel().getChildId(),"Child Id is ---------> "+prr.getTeacherMatchModel().getChildId());
        Log.d("Child Id is ---------> "+prr.getTeacherMatchModel().getCustomChildData().getChildId(),"Child Id is ---------> "+prr.getTeacherMatchModel().getCustomChildData().getChildId());
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.insertParentCourse,resp->{
            Log.d("cccccccccc------> "+resp,"cccccccccc------> "+resp);
            if(resp.equalsIgnoreCase("Done")){
                onCourseAddedListener.onCourseAdded(1);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onCourseAddedListener.onCourseAdded(-1);
            }
            else {
                onCourseAddedListener.onCourseAdded(-2);
            }
        },error ->{
            onCourseAddedListener.onCourseAdded(-1);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("parentEmail",prr.getTeacherMatchModel().getParentEmail());
                data.put("teacherEmail",prr.getTeacher().getEmail());
                data.put("requestId",prr.getRequestId()+"");
                data.put("childId",prr.getTeacherMatchModel().getCustomChildData().getChildId()+"");
                data.put("choseDays",prr.getTeacherMatchModel().getChoseDays());
                data.put("courses", prr.getTeacherMatchModel().getCourses());
                data.put("location",prr.getTeacherMatchModel().getLocation());
                data.put("teachingMethod",prr.getTeacherMatchModel().getTeachingMethod());
                data.put("startTime",prr.getTeacherMatchModel().getStartTime());
                data.put("endTime",prr.getTeacherMatchModel().getEndTime());
                data.put("startDate",prr.getTeacherMatchModel().getStartDate());
                data.put("endDate",prr.getTeacherMatchModel().getEndDate());
                data.put("duration", DateUtils.calculateDurationInDays(prr.getTeacherMatchModel().getStartDate(),prr.getTeacherMatchModel().getEndDate())+"");
                data.put("educationLevel", getEducationLevel(prr.getTeacherMatchModel().getCustomChildData().getChildGrade()));
                data.put("price",price+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    private String getEducationLevel(int grade){
        if(grade > 0 && grade <= 5){
            return  "Elementary School";
        }
        else if(grade > 5 && grade <= 10){
            return  "Middle School";
        }
        else if(grade > 10 && grade <= 12){
            return  "High School";
        }
        else {
            return "Any";
        }
    }

    public void setParentReceivedRequestToDecline(int requestId){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.deleteParentRequestFromSent, response ->{

        },error -> {

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> data = new HashMap<>();
                data.put("requestId",requestId+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void checkIfParentRequestSentBefore(String parentEmail,TeacherPostRequest teacherPostRequest , final OnCheckIfRequestSentBeforeListener onCheckIfRequestSentBefore){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.checkIfParentRequestSentBefore,resp->{
            if (resp.equalsIgnoreCase("Exists")){
                onCheckIfRequestSentBefore.onRequestSent(0);
            }
            else if(resp.equalsIgnoreCase("Not Exist")){
                onCheckIfRequestSentBefore.onRequestSent(1);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onCheckIfRequestSentBefore.onRequestSent(-1);
            }
            else {
                onCheckIfRequestSentBefore.onRequestSent(-2);
            }
        },err->{
            onCheckIfRequestSentBefore.onRequestSent(-2);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("teacherEmail",teacherPostRequest.getTeacherEmail());
                data.put("parentEmail",parentEmail);
                data.put("postId",teacherPostRequest.getTeacherPostRequestId()+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void deleteTeacherSentRequestToParent(String teacherEmail,TeacherMatchModel teacherMatchModel,final OnSentRequestDeletedListener onSentRequestDeleted){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.deleteTeacherSentRequest,resp->{
            if (resp.equalsIgnoreCase("No Request")){
                onSentRequestDeleted.onSentRequestDeleted(0);
            }
            else if(resp.equalsIgnoreCase("Done")){
                onSentRequestDeleted.onSentRequestDeleted(1);
            }
            else if(resp.equalsIgnoreCase("Request Accepted Before")){
                onSentRequestDeleted.onSentRequestDeleted(2);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onSentRequestDeleted.onSentRequestDeleted(-1);
            }
            else {
                onSentRequestDeleted.onSentRequestDeleted(-2);
            }
        },err->{
            onSentRequestDeleted.onSentRequestDeleted(-2);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("teacherEmail",teacherEmail);
                data.put("matchingId",teacherMatchModel.getMatchingId()+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void deleteParentSentRequestToTeacher(String parentEmail,TeacherPostRequest teacherPostRequest,final OnSentRequestDeletedListener onSentRequestDeleted){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.deleteParentSentRequest,resp->{
            if (resp.equalsIgnoreCase("No Request")){
                onSentRequestDeleted.onSentRequestDeleted(0);
            }
            else if(resp.equalsIgnoreCase("Done")){
                onSentRequestDeleted.onSentRequestDeleted(1);
            }
            else if(resp.equalsIgnoreCase("Request Accepted Before")){
                onSentRequestDeleted.onSentRequestDeleted(2);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onSentRequestDeleted.onSentRequestDeleted(-1);
            }
            else {
                onSentRequestDeleted.onSentRequestDeleted(-2);
            }
        },err->{
            onSentRequestDeleted.onSentRequestDeleted(-2);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("parentEmail",parentEmail);
                data.put("postId",teacherPostRequest.getTeacherPostRequestId()+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getTeacherSpecificReceivedRequest(int parentSentRequestId, final OnParentSentRequestFetchListener onParentSentRequestFetched){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getTeacherSpecificReceivedRequest,resp->{
            if (resp.equalsIgnoreCase("Error")){
                onParentSentRequestFetched.onParentSentRequestFetched(0,null);
            }
            else if (resp.equalsIgnoreCase("Connection Error")){
                onParentSentRequestFetched.onParentSentRequestFetched(-1,null);
            }
            else {
                try {
                    onParentSentRequestFetched.onParentSentRequestFetched(1,new JSONArray(resp));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },err->{
            onParentSentRequestFetched.onParentSentRequestFetched(-2,null);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("requestId",parentSentRequestId+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getAllTeacherCourses(String teacherEmail,final FetchCoursesListener onCoursesFetched){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getAllTeacherCourses,resp->{
            Log.d("Fetching teacher courses ----> "+resp,"Fetching teacher courses ----> "+resp);
            if(resp.equalsIgnoreCase("Connection Error")){
                onCoursesFetched.onCoursesFetched(-2,null);
            }
            else if(resp.equalsIgnoreCase("Error")){
                onCoursesFetched.onCoursesFetched(-1,null);
            }
            else if(resp.equalsIgnoreCase("No Courses")){
                onCoursesFetched.onCoursesFetched(0,null);
            }
            else {
                try {
                    onCoursesFetched.onCoursesFetched(1,new JSONArray(resp));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },err->{
            onCoursesFetched.onCoursesFetched(-1,null);
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("teacherEmail",teacherEmail);
                return data ;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getAllTeacherCoursesDatesBeforeSendRequest(String teacherEmail,final OnTeacherCoursesFetchedForConflictListener onTeacherCoursesFetched){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getAllTeacherCoursesDatesAndTime,resp->{
            try {
                if(resp.equalsIgnoreCase("Error")){
                    onTeacherCoursesFetched.onTeacherCoursesFetched(-1,null);
                }
                else if(resp.equalsIgnoreCase("Connection Error")){
                    onTeacherCoursesFetched.onTeacherCoursesFetched(-2,null);
                }
                else {
                    JSONObject jsonResponse = new JSONObject(resp);
                    JSONArray teacherTableCourses = jsonResponse.getJSONArray("teacherCourses");
                   // JSONArray parentTableCourses = jsonResponse.getJSONArray("parentChildrenCourses");
                    onTeacherCoursesFetched.onTeacherCoursesFetched(1,teacherTableCourses);
                }
            }
            catch(Exception e){
              //  onTeacherCoursesFetched.onTeacherCoursesFetched(-2,null,null);
                throw new RuntimeException(e);
            }
        },err->{
            onTeacherCoursesFetched.onTeacherCoursesFetched(-1,null);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> data = new HashMap<>();
                data.put("teacherEmail",teacherEmail);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getAllParentCoursesDatesBeforeSendRequest(String parentEmail,final OnParentCoursesFetchedForConflictListener onParentCOursesFetchedForConflictListener){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getAllParentCoursesDatesAndTime,resp->{
            Log.d("RRRESSSPPP ----> "+resp,"RRRESSSPPP ----> "+resp);
            try {
                if(resp.equalsIgnoreCase("Error")){
                    onParentCOursesFetchedForConflictListener.onParentCoursesFetched(-1,null);
                }
                else if(resp.equalsIgnoreCase("Connection Error")){
                    onParentCOursesFetchedForConflictListener.onParentCoursesFetched(-2,null);
                }
                else {
                    JSONObject jsonResponse = new JSONObject(resp);
                    JSONArray parentTableCourses = jsonResponse.getJSONArray("parentCourses");
                    onParentCOursesFetchedForConflictListener.onParentCoursesFetched(1,parentTableCourses);
                }
            }
            catch(Exception e){
               // onParentCOursesFetchedForConflictListener.onParentCoursesFetched(-2,null,null);
                throw new RuntimeException(e);
            }
        },err->{
            onParentCOursesFetchedForConflictListener.onParentCoursesFetched(-1,null);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> data = new HashMap<>();
                data.put("parentEmail",parentEmail);
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void removeNotification(int notificationId){
        requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,Constants.removeNotification,resp->{},err->{}){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> data = new HashMap<>();
                data.put("notId",notificationId+"");
                return data;
            }
        };
        requestQueue.add(stringRequest);
    }
}
