package com.example.graduationproject.database;

import android.content.Context;
import android.content.Intent;
import android.telephony.CellSignalStrengthGsm;
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
import com.example.graduationproject.listeners.GetParentChildren;
import com.example.graduationproject.listeners.LastMatchingIdListener;
import com.example.graduationproject.listeners.NotificationsListListener;
import com.example.graduationproject.listeners.ParentListenerForParentPostedRequests;
import com.example.graduationproject.listeners.ParentInformationListener;
import com.example.graduationproject.listeners.TeacherAccountConfirmationListener;
import com.example.graduationproject.listeners.TellParentDataIsReady;
import com.example.graduationproject.listeners.UpdateParentInformation;
import com.example.graduationproject.listeners.UpdateTeacherPostedRequestListener;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.Parent;
import com.example.graduationproject.models.Profile;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.network.ApiService;
import com.example.graduationproject.network.RetrofitInitializer;
import com.example.graduationproject.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

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
                return  data;
            }
        };
        queue.add(stringRequest);
    }

    public void getTeacherMatchingData(String email,final AddTeacherMatchingListener addTeacherMatchingListener){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.getTeacherMatchingData,res->{
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

    public void updatePostedTeacherRequest(String email,TeacherMatchModel teacherMatchModel,final UpdateTeacherPostedRequestListener updateTeacherPostedRequest){
        requestQueue = Volley.newRequestQueue(context);
        Log.d("matching id --->"+teacherMatchModel.getMatchingId(),"matching id --->"+teacherMatchModel.getMatchingId());
        Log.d("Child ID --> "+teacherMatchModel.getCustomChildData().getChildId(),"Child ID --> "+teacherMatchModel.getCustomChildData().getChildId());
        Log.d("Child NAME --> "+teacherMatchModel.getCustomChildData().getChildName(),"Child NAME --> "+teacherMatchModel.getCustomChildData().getChildName());
        Log.d("Child GRADE --> "+teacherMatchModel.getCustomChildData().getChildGrade(),"Child ID --> "+teacherMatchModel.getCustomChildData().getChildGrade());
        Log.d("Child selected days --> "+teacherMatchModel.getChoseDays(),"Child days --> "+teacherMatchModel.getChoseDays());
        Log.d("Child selected city --> "+teacherMatchModel.getLocation(),"Child city --> "+teacherMatchModel.getLocation());
        Log.d("Child selected method --> "+teacherMatchModel.getTeachingMethod(),"Child method --> "+teacherMatchModel.getTeachingMethod());
        Log.d("Child selected method --> "+teacherMatchModel.getStartTime(),"Child method --> "+teacherMatchModel.getStartTime());
        Log.d("Child selected method --> "+teacherMatchModel.getEndTime(),"Child method --> "+teacherMatchModel.getEndTime());

        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.updatePostedTeacherRequest,res->{
            Toast.makeText(context,"The res is -->"+res,Toast.LENGTH_LONG).show();
            if(res.equalsIgnoreCase("no data"))
                updateTeacherPostedRequest.onDataUpdate(-2);
            else if(res.equalsIgnoreCase("Error"))
                updateTeacherPostedRequest.onDataUpdate(-1);
            else if(res.equalsIgnoreCase("Connection Error"))
                updateTeacherPostedRequest.onDataUpdate(-3);
            else if(res.equalsIgnoreCase("Done")){
                updateTeacherPostedRequest.onDataUpdate(1);
                Log.d("123123123123","123123123123Anas");
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
}