package com.example.graduationproject.ui.teacherUi;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.MatchingTeacherAdapter;
import com.example.graduationproject.databinding.ConfirmDeleteDialogLayoutBinding;
import com.example.graduationproject.databinding.TeacherPostedRequestCardLayoutBinding;
import com.example.graduationproject.databinding.UpdateParentPostedRequestBinding;
import com.example.graduationproject.databinding.UpdatePostedTeacherLookForAJobLayoutBinding;
import com.example.graduationproject.listeners.DeletePostedRequestListener;
import com.example.graduationproject.listeners.OnTeacherPostRequestUpdateListener;
import com.example.graduationproject.listeners.TeacherPostListener;
import com.example.graduationproject.listeners.TeacherPostRequestClickListener;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.FragmentTeacherBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.AddTeacherMatchingListener;
import com.example.graduationproject.listeners.PostedTeacherRequestsListener;
import com.example.graduationproject.listeners.TeacherMatchCardClickListener;
import com.example.graduationproject.adapters.TeacherPostedRequestsAdapter;
import com.example.graduationproject.models.Address;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.CustomChildData;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.models.TeacherPostRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.integrity.internal.r;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class TeacherFragment extends Fragment implements TeacherMatchCardClickListener,
        AddTeacherMatchingListener,
        PostedTeacherRequestsListener,
        TeacherPostRequestClickListener, OnTeacherPostRequestUpdateListener,
        DeletePostedRequestListener {

    private FragmentTeacherBinding binding ;

    private Database database ;
    String email ;
    String teacherName ;

    private ArrayList<TeacherMatchModel> teacherMatchModelData = new ArrayList<>() ;
    private MatchingTeacherAdapter matchingTeacherAdapter;
    private boolean myCoursesBtnForParent = true ;
    private boolean myPostedRequestsBtnForTeacher = false ;
    private boolean isBroadcastReceiverRegistered = false;
    boolean browseParentPostedRequestsBtnForTeacher = false;

    List<TeacherPostRequest> teacherPostedRequestsList = new ArrayList<>();

    private TeacherPostedRequestsAdapter teacherPostedRequestsAdapter ;

    List<String> teacherPhoneNumbersList = new ArrayList<>();
    List<Address> teacherAddressesList = new ArrayList<>();
    TeacherPostRequest newTeacherRequest ;
    Dialog teacherPostedRequestCardDialog;
    private TeacherPostedRequestCardLayoutBinding teacherPostedRequestCardLayoutBinding ;

    private String amPmStart,amPmEnd;
    private String startTime ="12:00 PM", endTime="12:00 PM";
    UpdatePostedTeacherLookForAJobLayoutBinding updatePostedTeacherLookForAJobLayoutBinding;
    private Dialog updatePostedRequestDialog ;
    private List<String> coursesList =new ArrayList<>();
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
    private  String teacherAvailability ;

    private Dialog deleteRequestConfirmationDialog ;


    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("UPDATE_TEACHER_POSTED_REQUESTS".equals(intent.getAction())){
                btnMyPostedRequestsClicked();
            }
            else if("SHOW_AVAILABLE_JOBS_FOR_TEACHER".equals(intent.getAction())){
                browseParentPostedRequestsBtnForTeacher = true;
                setAvailableTeacherMatchingAdapter();
            }
            else if("SHOW_PARENT_POSTED_REQUESTS_FOR_TEACHER".equals(intent.getAction())){
                browseParentPostedRequestsBtnForTeacher = true;
                database.getTeacherMatchingData(email,TeacherFragment.this);
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeacherBinding.inflate(getLayoutInflater(),container,false);
        getTeacherDataFromActivity();
        init();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isBroadcastReceiverRegistered){
            IntentFilter parentFragmentIntentFilter = new IntentFilter();
            parentFragmentIntentFilter.addAction("UPDATE_TEACHER_POSTED_REQUESTS");
            parentFragmentIntentFilter.addAction("SHOW_AVAILABLE_JOBS_FOR_TEACHER");
            parentFragmentIntentFilter.addAction("SHOW_PARENT_POSTED_REQUESTS_FOR_TEACHER");
            int flags = 0 ;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                flags = Context.RECEIVER_NOT_EXPORTED;
            }
            if(getActivity() != null){
                getActivity().registerReceiver(myBroadcastReceiver,parentFragmentIntentFilter, flags);
                isBroadcastReceiverRegistered = true ;
            }
        }
    }

    private void updatePostRequestsAdapter(){
        teacherPostedRequestsList.add(newTeacherRequest);
        teacherPostedRequestsAdapter.filteredList(teacherPostedRequestsList);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isBroadcastReceiverRegistered && getActivity() != null){
            getActivity().unregisterReceiver(myBroadcastReceiver);
            isBroadcastReceiverRegistered = false;
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(isBroadcastReceiverRegistered && getActivity() != null){
            getActivity().unregisterReceiver(myBroadcastReceiver);
            isBroadcastReceiverRegistered = false;
        }
    }

    private void init(){
        initDatabase();
        initCoursesRecyclerView();
        btnMyPostedRequestsClicked();
        binding.myPostedRequestsBtn.setOnClickListener(x->{
            btnMyCoursesClicked();

        });
        binding.availableTeacherRequests.setOnClickListener(c->{
            btnMyPostedRequestsClicked();
        });
        binding.refreshRecyclerView.setOnRefreshListener(()->{
            if(browseParentPostedRequestsBtnForTeacher && !myCoursesBtnForParent && !myPostedRequestsBtnForTeacher){
                sendRefreshBroadcast(3); // update available job for teacher ..
            }
            else if(myCoursesBtnForParent && !myPostedRequestsBtnForTeacher){
                sendRefreshBroadcast(1);
            }
            else if(!myCoursesBtnForParent && myPostedRequestsBtnForTeacher){
                sendRefreshBroadcast(2);
            }
        });
        database.getTeacherPostedRequests(email,this);
    }

    private void btnMyCoursesClicked(){
        binding.teacherFragmentSearch.clearFocus();
        binding.teacherFragmentSearch.setQuery(null,false);
        myCoursesBtnForParent = true;
        myPostedRequestsBtnForTeacher = false;
        browseParentPostedRequestsBtnForTeacher = false;
        binding.filterLayout.setVisibility(View.GONE);
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_active);
        binding.availableTeacherRequests.setBackgroundResource(R.drawable.rounded_button_inactive);
        setMyCoursesAdapter();

    }

    private void btnMyPostedRequestsClicked(){
        binding.teacherFragmentSearch.clearFocus();
        binding.teacherFragmentSearch.setQuery(null,false);
        myCoursesBtnForParent = false ;
        myPostedRequestsBtnForTeacher = true ;
        browseParentPostedRequestsBtnForTeacher = false;
        binding.filterLayout.setVisibility(View.GONE);
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.availableTeacherRequests.setBackgroundResource(R.drawable.rounded_button_active);

        binding.teacherFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInTeacherPostedRequestsForTeacher(newText);
                return true;
            }
        });

        database.getTeacherPostedRequests(email,this);
    }

    private void searchInTeacherPostedRequestsForTeacher(String textToSearch){
        textToSearch = textToSearch.toLowerCase();
        List<TeacherPostRequest> tempTeacherPostedRequestsList = new ArrayList<>();
        for(TeacherPostRequest model : teacherPostedRequestsList){
            if(model.getTeacherEmail().toLowerCase().trim().contains(textToSearch) || model.getTeacherData().getTeacherName().toLowerCase().trim().contains(textToSearch)||
            model.getCourses().toLowerCase().trim().contains(textToSearch) || model.getTeachingMethod().toLowerCase().trim().contains(textToSearch) ||
            model.getDuration().toLowerCase().trim().contains(textToSearch) || model.getAvailability().toLowerCase().contains(textToSearch) ||
            model.getLocation().toLowerCase().contains(textToSearch) || model.getEducationLevel().toLowerCase().contains(textToSearch) ||
            model.getTeacherData().getEducationalLevel().toLowerCase().equalsIgnoreCase(textToSearch) || model.getTeacherData().getCollege().toLowerCase().contains(textToSearch) ||
            model.getTeacherData().getField().toLowerCase().contains(textToSearch)){
                tempTeacherPostedRequestsList.add(model);
                continue;
            }
            if(model.getTeacherData().getGender() == 1 && textToSearch.equalsIgnoreCase("Male")){
                tempTeacherPostedRequestsList.add(model);
                continue;
            }
            if(model.getTeacherData().getGender() == 0 && textToSearch.equalsIgnoreCase("Female")){
                tempTeacherPostedRequestsList.add(model);
                continue;
            }
            int flag = 0 ;
            for(Address address : model.getTeacherData().getAddressesList()){
                if(address.getCity().toLowerCase().contains(textToSearch) || address.getCountry().toLowerCase().contains(textToSearch)){
                    tempTeacherPostedRequestsList.add(model);
                    flag = 1;
                    break;
                }
            }

            if(flag == 0){
                for(String phone : model.getTeacherData().getPhoneNumbersList()){
                    if(phone.contains(textToSearch)){
                        tempTeacherPostedRequestsList.add(model);
                        break;
                    }
                }
            }
        }
        if(tempTeacherPostedRequestsList.isEmpty()){
            binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
            binding.noMatchedData.setVisibility(View.GONE);
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
        }
        else {
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noMatchedData.setVisibility(View.GONE);
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
            teacherPostedRequestsAdapter.filteredList(tempTeacherPostedRequestsList);
        }
    }

    private void setTeacherPostedData(){
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.availableTeacherRequests.setBackgroundResource(R.drawable.rounded_button_active);
        binding.teacherFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInParentPostedRequestsForTeacher(newText);
                return true;
            }
        });
        database.getTeacherPostedRequests(email,this);
    }


    private void updatePostedAdapterData(){
        if(teacherPostedRequestsList.isEmpty()){
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
            binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.refreshRecyclerView.setEnabled(false);
        }
        else {
           // teacherPostedRequestsAdapter = new TeacherPostedRequestsAdapter(teacherPostedRequestsList,getContext(),this);
            teacherPostedRequestsAdapter = new TeacherPostedRequestsAdapter(teacherPostedRequestsList,getContext(),this);
            binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.refreshRecyclerView.setEnabled(true);
            binding.refreshRecyclerView.setRefreshing(false);
            binding.addedCoursesRecyclerView.setAdapter(teacherPostedRequestsAdapter);
        }
    }

    private void getTeacherDataFromActivity(){
        if(getArguments() != null){
            email = getArguments().getString("email");
            teacherName = getArguments().getString("firstName");
            teacherName = teacherName+" "+getArguments().getString("lastName");
            teacherAvailability = getArguments().getString("availability");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                teacherMatchModelData = getArguments().getParcelableArrayList("teacherMatchingData",TeacherMatchModel.class);
            }
            else {
                teacherMatchModelData = getArguments().getParcelableArrayList("teacherMatchingData");
            }
        }
        else {
            email="";
            teacherName="";
            teacherAvailability = "";
        }
    }

    public void setMyCoursesAdapter(){
        List<TeacherMatchModel> teacherMatchModelList = new ArrayList<>();
        matchingTeacherAdapter = new MatchingTeacherAdapter(teacherMatchModelList,getContext(),this);
        if(teacherMatchModelList.isEmpty()){
            binding.noDataAddedText.setVisibility(View.VISIBLE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
        }
        else{
            binding.addedCoursesRecyclerView.setAdapter(matchingTeacherAdapter);
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
        }

        binding.teacherFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               // matchingTeacherAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void setAvailableTeacherMatchingAdapter(){
        if(teacherMatchModelData.isEmpty()){
            binding.noDataAddedText.setVisibility(View.VISIBLE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noMatchedData.setVisibility(View.GONE);

        }
        else {
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noMatchedData.setVisibility(View.GONE);
            matchingTeacherAdapter = new MatchingTeacherAdapter(teacherMatchModelData,getContext(),this);
            binding.addedCoursesRecyclerView.setAdapter(matchingTeacherAdapter);
        }
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.availableTeacherRequests.setBackgroundResource(R.drawable.rounded_button_active);


        binding.teacherFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInParentPostedRequestsForTeacher(newText);
                return true;
            }
        });
        myCoursesBtnForParent = false ;
        myPostedRequestsBtnForTeacher = false;
        updateBtnStatus();
    }

    private  void  updateBtnStatus(){
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.availableTeacherRequests.setBackgroundResource(R.drawable.rounded_button_inactive);
    }

    private void searchInParentPostedRequestsForTeacher(String str){
        String text = str.toLowerCase();
        List<TeacherPostRequest> teacherMatchModelList = new ArrayList<>();
        Log.d(text , text);
        for(TeacherPostRequest model : teacherPostedRequestsList){
            if(model.getTeacherData().getTeacherName().toLowerCase().contains(text.trim()) || model.getTeacherEmail().toLowerCase().contains(text.trim())||
            model.getCourses().toLowerCase().contains(text) || model.getTeachingMethod().toLowerCase().contains(text.trim()) ||
            model.getDuration().equalsIgnoreCase(text) || model.getLocation().toLowerCase().contains(text.trim()) ||
            model.getTeacherData().getField().toLowerCase().contains(text) || model.getTeacherData().getCollege().toLowerCase().contains(text.trim())||
            model.getTeacherData().getAvailability().toLowerCase().contains(text) || model.getTeacherData().getEducationalLevel().toLowerCase().contains(text)){
                teacherMatchModelList.add(model);
                continue;
            }
            if(model.getTeacherData().getGender() == 1 && text.equalsIgnoreCase("Male")){
                    teacherMatchModelList.add(model);
                    continue;
                }
            else if(model.getTeacherData().getGender() == 0 && text.equalsIgnoreCase("Female")){
                    teacherMatchModelList.add(model);
                    continue;
                }
            int flag = 0;
            for(Address address : model.getTeacherData().getAddressesList()){
                    if(address.getCity().toLowerCase().contains(text) || address.getCountry().toLowerCase().contains(text)){
                        flag = 1;
                        teacherMatchModelList.add(model);
                        break ;
                    }
            }

            if(flag == 0){
                for(String phone : model.getTeacherData().getPhoneNumbersList()){
                    if(phone.equals(text)){
                        teacherMatchModelList.add(model);
                        break;
                    }
                }
            }
        }
        if(teacherMatchModelList.isEmpty() && getView() != null){
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
            binding.noMatchedData.setVisibility(View.VISIBLE);
        }
        else {
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noMatchedData.setVisibility(View.GONE);
            teacherPostedRequestsAdapter.filteredList(teacherMatchModelList);
        }
    }

    private void initDatabase(){
        database = new Database(getContext());
    }

    private void initCoursesRecyclerView(){
        binding.addedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onCardClicked(TeacherMatchModel teacherMatchModel) {
        Intent intent = new Intent();
        intent.setAction("START_MATCHING_FRAGMENT_FOR_TEACHER");
        intent.putExtra("teacherMatchModel",(Serializable)teacherMatchModel);
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
    }

    private void sendRefreshBroadcast(int flag){
        if(flag == 1){
           // database.getTeacherMatchingData(email,this);
        }
        else if(flag == 2){
            database.getTeacherPostedRequests(email,this);
        }
        else if (flag == 3){
            database.getTeacherMatchingData(email,this);
        }
        //Intent intent=new Intent();
        //intent.setAction("REFRESH_FRAGMENT_TEACHER");
        //LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
    }

    @Override
    public void onMatchingAdded(int resultFlag) {

    }

    @Override
    public void getTeacherMatchingData(int resultFlag, JSONArray teacherMatchingData) {
        if(resultFlag == 0){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","An Error occurred fetching the data , please try again later");
        }
        else if(resultFlag == -2){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Network Error","A Network Error occurred,Please try again later");
        }
        else if(resultFlag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Request Error","Something went wrong please try again later");
        }
        else if(resultFlag == 1) {
            try {
                if(!teacherMatchModelData.isEmpty()){
                    teacherMatchModelData.clear();
                }
                for(int i=teacherMatchingData.length() - 1 ;i >= 0 ;i--){
                    JSONObject jsonObject = teacherMatchingData.getJSONObject(i);
                    int matchingId = jsonObject.getInt("matchingId");
                    String parentEmail = jsonObject.getString("parentEmail");
                    int childId = jsonObject.getInt("childId");
                    String choseDays = jsonObject.getString("choseDays");
                    String choseCourses = jsonObject.getString("courses");
                    String location = jsonObject.getString("location");
                    String teachingMethod = jsonObject.getString("teachingMethod");
                    String childName = jsonObject.getString("childName");
                    String childAge = jsonObject.getString("childAge");
                    int childGender =jsonObject.getInt("childGender");
                    int childGrade = jsonObject.getInt("childGrade");
                    String startTime = jsonObject.getString("startTime");
                    String endTime = jsonObject.getString("endTime");
                    double priceMin = jsonObject.getDouble("priceMin");
                    double priceMax = jsonObject.getDouble("priceMax");
                    TeacherMatchModel teacherMatchModel=new TeacherMatchModel(matchingId,parentEmail,new CustomChildData(childId,childName,childGrade),
                            choseDays,choseCourses,location,teachingMethod,
                            new Children(childName,childAge,childGender,childGrade),startTime,endTime,priceMin,priceMax);
                    teacherMatchModelData.add(teacherMatchModel);
                }
               // matchingTeacherAdapter.filteredList(teacherMatchModelData);
                setAvailableTeacherMatchingAdapter();
                binding.refreshRecyclerView.setRefreshing(false);
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateParentPostedRequestsForTeacher(){

    }


    @Override
    public void onDataFetched(int flag, JSONArray data) {
        if(flag == 1){
            if(data.length() == 0){
                binding.addedCoursesRecyclerView.setVisibility(View.GONE);
                binding.noDataAddedText.setVisibility(View.VISIBLE);
            }
            else {
                if(!teacherPostedRequestsList.isEmpty())
                    teacherPostedRequestsList.clear();
                try{
                    if(!teacherPhoneNumbersList.isEmpty()){
                        teacherPhoneNumbersList.clear();
                    }
                    if(!teacherAddressesList.isEmpty())
                        teacherAddressesList.clear();

                    for(int i= (data.length() - 1) ; i >= 0 ; i--){
                        JSONObject jsonObject=data.getJSONObject(i);
                        String firstName = jsonObject.getString("firstname");
                        String lastname = jsonObject.getString("lastname");
                        firstName = firstName.substring(0,1).toUpperCase()+firstName.substring(1).toLowerCase();
                        lastname = lastname.substring(0,1).toUpperCase()+lastname.substring(1).toLowerCase();

                        int gender = jsonObject.getInt("gender");
                        int profileType = jsonObject.getInt("profileType");
                        int studentOrGraduate = jsonObject.getInt("studentOrGraduate");
                        String idNumber = jsonObject.getString("idNumber");
                        String expectedGraduationYear = jsonObject.getString("expectedGraduationYear");
                        String college = jsonObject.getString("college");
                        String field = jsonObject.getString("field");
                        String availability = jsonObject.getString("availabilityForJob");
                        int postId = jsonObject.getInt("postId");
                        String teacherEmail  = jsonObject.getString("teacherEmail");
                        String courses = jsonObject.getString("courses");
                        String educationLevel = jsonObject.getString("educationLevel");
                        String duration = jsonObject.getString("duration");
                        String location = jsonObject.getString("location");
                        String teachingMethod = jsonObject.getString("teachingMethod");
                        String startTime = jsonObject.getString("startTime");
                        String endTime = jsonObject.getString("endTime");
                        double price =jsonObject.getDouble("price");
                        if(i==data.length() - 1){
                            String phoneNumbers = jsonObject.getString("phoneNumbers");
                            if(phoneNumbers.contains(",")){
                                String[] splitPhoneNumbers = phoneNumbers.split(",");
                                teacherPhoneNumbersList.addAll(Arrays.asList(splitPhoneNumbers));
                            }
                            else
                                teacherPhoneNumbersList.add(phoneNumbers.trim());

                            String address = jsonObject.getString("addresses");
                            if(address.contains("|")){
                                String [] splitAddress = address.split("\\|");
                                for(String str : splitAddress){
                                    String[] splitCurrentAddress = str.split(",");
                                    teacherAddressesList.add(new Address(splitCurrentAddress[0].trim(),splitCurrentAddress[1].trim()));
                                }
                            }
                            else{
                                String[] splitCurrentAddress = address.split(",");
                                teacherAddressesList.add(new Address(splitCurrentAddress[0].trim(),splitCurrentAddress[1].trim()));
                            }
                        }
                        //teacherPostedRequestsList.add(new TeacherPostRequest(postId,teacherEmail,courses,educationLevel,duration,location,teachingMethod));
                        teacherPostedRequestsList.add(new TeacherPostRequest(postId,email,courses,educationLevel,duration,availability,location,
                                teachingMethod,new Teacher(email,idNumber,studentOrGraduate+"",
                                expectedGraduationYear,college,field,gender,availability,educationLevel,
                                teacherAddressesList,teacherPhoneNumbersList,firstName+" "+lastname),startTime,endTime,price));
                    }
                    updatePostedAdapterData();
                    binding.refreshRecyclerView.setRefreshing(false);
                }
                catch (JSONException e){
                    throw new RuntimeException(e);
                }
            }
        }
        else {

        }
    }

    @Override
    public void onTeacherPostClicked(TeacherPostRequest teacherPostRequest) {
        if(getContext() != null){
            teacherPostedRequestCardDialog = new Dialog(getContext());
            teacherPostedRequestCardLayoutBinding = TeacherPostedRequestCardLayoutBinding.inflate(LayoutInflater.from(getContext()));
            teacherPostedRequestCardDialog.setContentView(teacherPostedRequestCardLayoutBinding.getRoot());
            teacherPostedRequestCardDialog.setCancelable(false);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(teacherPostedRequestCardDialog.getWindow()).getAttributes());
            layoutParams.width = 1250;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            teacherPostedRequestCardDialog.getWindow().setAttributes(layoutParams);
            if(teacherPostedRequestCardDialog.getWindow() != null)
                teacherPostedRequestCardDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            teacherPostedRequestCardDialog.show();

            teacherPostedRequestCardLayoutBinding.closeImageView.setOnClickListener(x->{
                teacherPostedRequestCardDialog.dismiss();
            });
            
            teacherPostedRequestCardLayoutBinding.teacherNameTextView.setText(teacherPostRequest.getTeacherData().getTeacherName());
            teacherPostedRequestCardLayoutBinding.teacherEmailTextView.setText(teacherPostRequest.getTeacherEmail());
            List<String> phones = teacherPostRequest.getTeacherData().getPhoneNumbersList();
            StringBuilder phonesStringBuilder = new StringBuilder();
            for(int i=0;i<phones.size();i++){
                if(i + 1 != phones.size())
                    phonesStringBuilder.append(phones.get(i)).append("\n");
                else
                    phonesStringBuilder.append(phones.get(i));
            }
            teacherPostedRequestCardLayoutBinding.teacherPhoneNumberTextView.setText(phonesStringBuilder.toString());
            teacherPostedRequestCardLayoutBinding.coursesTextView.setText(teacherPostRequest.getCourses());
            teacherPostedRequestCardLayoutBinding.teachingMethodTextView.setText(teacherPostRequest.getTeachingMethod());
            teacherPostedRequestCardLayoutBinding.durationTextView.setText(teacherPostRequest.getDuration());
            teacherPostedRequestCardLayoutBinding.timeTextView.setText(teacherPostRequest.getTeacherData().getAvailability());
            teacherPostedRequestCardLayoutBinding.locationTextView.setText(teacherPostRequest.getLocation());
            teacherPostedRequestCardLayoutBinding.priceTextView.setText(teacherPostRequest.getPrice()+"$");
            teacherPostedRequestCardLayoutBinding.cardSettings.setOnClickListener(z->{
                showSettingsPopupMenu(teacherPostRequest);
            });
        }
    }

    private void showSettingsPopupMenu(TeacherPostRequest teacherPostRequest){
        PopupMenu popupMenu = new PopupMenu(getContext(),teacherPostedRequestCardLayoutBinding.cardSettings);
        popupMenu.getMenuInflater().inflate(R.menu.menu_parent_posted_card_view,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.editCard){
                updatePostedTeacherLookForAJobLayoutBinding = UpdatePostedTeacherLookForAJobLayoutBinding.inflate(LayoutInflater.from(getContext()));
                setTeacherEditPostedRequest(teacherPostRequest);
            }
            else if(item.getItemId() == R.id.deleteCard){
                deleteTeacherPostedRequest(teacherPostRequest);
            }
            return true;
        });
    }

    private void deleteTeacherPostedRequest(TeacherPostRequest teacherPostRequest){
        if(getContext() != null){
            ConfirmDeleteDialogLayoutBinding confirmDeleteDialogLayoutBinding = ConfirmDeleteDialogLayoutBinding.inflate(LayoutInflater.from(getContext()));
            deleteRequestConfirmationDialog = new Dialog(getContext());
            deleteRequestConfirmationDialog.setContentView(confirmDeleteDialogLayoutBinding.getRoot());
            deleteRequestConfirmationDialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(deleteRequestConfirmationDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 600;
            deleteRequestConfirmationDialog.getWindow().setAttributes(layoutParams);
            if(deleteRequestConfirmationDialog.getWindow() != null)
                deleteRequestConfirmationDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            deleteRequestConfirmationDialog.show();

            confirmDeleteDialogLayoutBinding.deleteBtn.setOnClickListener(x->{
                deletePostedRequest(teacherPostRequest);
            });

            confirmDeleteDialogLayoutBinding.cancelBtn.setOnClickListener(c->{
                deleteRequestConfirmationDialog.dismiss();
            });
        }
    }

    private void deletePostedRequest(TeacherPostRequest teacherPostRequest){
        database.deleteTeacherPostedRequest(teacherPostRequest,this);
    }


    private void setTeacherEditPostedRequest(TeacherPostRequest teacherPostRequest){
        if(getContext() != null){
            updatePostedRequestDialog = new Dialog(getContext());
            updatePostedRequestDialog.setContentView(updatePostedTeacherLookForAJobLayoutBinding.getRoot());
            updatePostedRequestDialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(updatePostedRequestDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 2300;
            updatePostedRequestDialog.getWindow().setAttributes(layoutParams);
            if(updatePostedRequestDialog.getWindow() != null)
                updatePostedRequestDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            updatePostedRequestDialog.show();

            updatePostedTeacherLookForAJobLayoutBinding.closeTheDialog.setOnClickListener(z->{
                updatePostedRequestDialog.dismiss();
            });
            setFlexBoxSelectedCourses(teacherPostRequest.getCourses());
            updatePostedTeacherLookForAJobLayoutBinding.addCourseTeacherLooksForJob.setOnClickListener(z->{
                String selectedCourse = updatePostedTeacherLookForAJobLayoutBinding.coursesSpinner.getSelectedItem().toString();
                if(!checkIfCourseAddedToList(selectedCourse)){
                    coursesList.add(selectedCourse);
                    updateFlexBox();
                }
                else
                    MyAlertDialog.showWarningCourseAdded(getContext());
            });

            setEducationLevelSpinner(teacherPostRequest);
            setSelectedDays(teacherPostRequest);
            updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.setText(teacherPostRequest.getDuration());

            updatePostedTeacherLookForAJobLayoutBinding.startTimeEdtText.setText(teacherPostRequest.getStartTime());
            updatePostedTeacherLookForAJobLayoutBinding.endTimeEdtText.setText(teacherPostRequest.getEndTime());
            updatePostedTeacherLookForAJobLayoutBinding.priceEditText.setText(teacherPostRequest.getPrice()+"");
            startTime = teacherPostRequest.getStartTime();
            endTime = teacherPostRequest.getEndTime();

            updatePostedTeacherLookForAJobLayoutBinding.startTimeEdtText.setOnClickListener(x->{
                setStartTime();
            });
            updatePostedTeacherLookForAJobLayoutBinding.endTimeEdtText.setOnClickListener(v->{
                setEndTime();
            });

            String[] locationEntries = getResources().getStringArray(R.array.locationArray);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, locationEntries);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            updatePostedTeacherLookForAJobLayoutBinding.locationSpinner.setAdapter(adapter);
            setLocationSpinnerSelectedItem(locationEntries,teacherPostRequest);
            setTeachingMethodSpinner(teacherPostRequest);

            updatePostedTeacherLookForAJobLayoutBinding.updateTeacherRequestBtn.setOnClickListener(z->{
                try {
                    updateBtnClicked(teacherPostRequest);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void updateBtnClicked(TeacherPostRequest teacherPostRequest) throws ParseException {
        if(updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.getChildCount() == 0){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Courses Error","Please choose at least one course and add it ,,");
        }
        else {
            if(!updatePostedTeacherLookForAJobLayoutBinding.sunday.isChecked() && !updatePostedTeacherLookForAJobLayoutBinding.saturday.isChecked() &&
                    !updatePostedTeacherLookForAJobLayoutBinding.monday.isChecked() && !updatePostedTeacherLookForAJobLayoutBinding.tuesday.isChecked() &&
                    !updatePostedTeacherLookForAJobLayoutBinding.wednesday.isChecked() && !updatePostedTeacherLookForAJobLayoutBinding.thursday.isChecked() &&
                    !updatePostedTeacherLookForAJobLayoutBinding.friday.isChecked()){
                MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"No days","No days selected , please choose at least one day ..");
            }
            else {
                int numOfMonths = Integer.parseInt(updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.getText().toString());
                if(updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.getText().toString().isEmpty() || numOfMonths <= 0 || numOfMonths > 12){
                    MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Wrong duration","The duration must be at least one month and at most 12 months");
                }
                else {
                    if(!checkStartAndEndTime()){
                        MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Wrong timing","Please Choose valid start and end time, and make sure there are at least one hour..");
                    }
                    else {
                        double price = Double.parseDouble(updatePostedTeacherLookForAJobLayoutBinding.priceEditText.getText().toString());
                        if(updatePostedTeacherLookForAJobLayoutBinding.priceEditText.getText().toString().isEmpty() || price < 1.0 || price > 100.0)
                            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Invalid Price","Please Choose A Valid Price Value ..\n0.0 - 100 $");
                        else {
                            StringBuilder updatedCourses = new StringBuilder();
                            String updatedEducationLevel = updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.getSelectedItem().toString();
                            StringBuilder updatedDays = new StringBuilder();
                            String updatedNumOfMonths = updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.getText().toString();
                            String updatedLocation = updatePostedTeacherLookForAJobLayoutBinding.locationSpinner.getSelectedItem().toString();
                            String updatedTeachingMethod = updatePostedTeacherLookForAJobLayoutBinding.teachingMethodSpinner.getSelectedItem().toString();
                            for(int i = 0 ;i < coursesList.size();i++){
                                if(i + 1 != coursesList.size())
                                    updatedCourses.append(coursesList.get(i)).append(" , ");
                                else
                                    updatedCourses.append(coursesList.get(i));
                            }
                            if(updatePostedTeacherLookForAJobLayoutBinding.saturday.isChecked())
                                updatedDays.append("Sat , ");
                            if(updatePostedTeacherLookForAJobLayoutBinding.sunday.isChecked())
                                updatedDays.append("Sun , ");
                            if(updatePostedTeacherLookForAJobLayoutBinding.monday.isChecked())
                                updatedDays.append("Mon , ");
                            if(updatePostedTeacherLookForAJobLayoutBinding.tuesday.isChecked())
                                updatedDays.append("Tues , ");
                            if(updatePostedTeacherLookForAJobLayoutBinding.wednesday.isChecked())
                                updatedDays.append("Wed , ");
                            if(updatePostedTeacherLookForAJobLayoutBinding.thursday.isChecked())
                                updatedDays.append("Thur , ");
                            if(updatePostedTeacherLookForAJobLayoutBinding.friday.isChecked())
                                updatedDays.append("Fri");

                            TeacherPostRequest tpr = new TeacherPostRequest(teacherPostRequest.getTeacherPostRequestId(),
                                    teacherPostRequest.getTeacherEmail(),updatedCourses.toString(),updatedEducationLevel,
                                    updatedNumOfMonths,updatedDays.toString(),updatedLocation,updatedTeachingMethod,startTime,endTime,price);
                            binding.progressBarLayout.setVisibility(View.VISIBLE);
                            database.updateTeacherPostedRequest(tpr,this);
                        }
                    }
                }
            }
        }
    }

    private boolean checkStartAndEndTime() throws ParseException {
        Date startDate = timeFormat.parse(startTime);
        Date endDate = timeFormat.parse(endTime);

        if(startDate != null && endDate != null && startDate.before(endDate) && isEndTimeAtLeastOneHourLater(startDate,endDate)){
            return true;
        }
        return false;
    }

    private boolean isEndTimeAtLeastOneHourLater(Date startTime,Date endTime){
        final long oneHourInMillis = 3600000 ;
        Date oneHourLater = new Date(startTime.getTime()+oneHourInMillis);
        return endTime.after(oneHourLater);
    }

    private void setTeachingMethodSpinner(TeacherPostRequest teacherPostRequest){
        if(teacherPostRequest.getTeachingMethod().equalsIgnoreCase("Face To Face"))
            updatePostedTeacherLookForAJobLayoutBinding.teachingMethodSpinner.setSelection(1);
        else
            updatePostedTeacherLookForAJobLayoutBinding.teachingMethodSpinner.setSelection(0);
    }

    private void setLocationSpinnerSelectedItem(String [] locationEntries, TeacherPostRequest teacherPostRequest){
        for (int i = 0; i < locationEntries.length; i++) {
            if (locationEntries[i].equals(teacherPostRequest.getLocation())) {
                updatePostedTeacherLookForAJobLayoutBinding.locationSpinner.setSelection(i);
                break;
            }
        }
    }


    private void setStartTime(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                c.set(Calendar.MINUTE,minute);
                c.setTimeZone(TimeZone.getDefault());


                SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
                String amPm = amPmFormat.format(c.getTime());
                amPmStart = amPm;


                SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                String time = format.format(c.getTime());
                startTime = time ;
                updatePostedTeacherLookForAJobLayoutBinding.startTimeEdtText.setText(startTime);
            }
        },hours,mins,false);
        timePickerDialog.show();
    }

    private void setEndTime(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                c.set(Calendar.MINUTE,minute);
                c.setTimeZone(TimeZone.getDefault());

                SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
                String amPm = amPmFormat.format(c.getTime());
                amPmEnd = amPm;

                SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                String time = format.format(c.getTime());
                endTime = time ;
                updatePostedTeacherLookForAJobLayoutBinding.endTimeEdtText.setText(endTime);
            }
        },hours,mins,false);
        timePickerDialog.show();
    }


    private void setSelectedDays(TeacherPostRequest teacherPostRequest){
        if(teacherPostRequest.getTeacherData().getAvailability().contains("Sat")){
            updatePostedTeacherLookForAJobLayoutBinding.saturday.setChecked(true);
        }

        if(teacherPostRequest.getTeacherData().getAvailability().contains("Sun")){
            updatePostedTeacherLookForAJobLayoutBinding.sunday.setChecked(true);
        }

        if(teacherPostRequest.getTeacherData().getAvailability().contains("Mon")){
            updatePostedTeacherLookForAJobLayoutBinding.monday.setChecked(true);
        }

        if(teacherPostRequest.getTeacherData().getAvailability().contains("Tues")){
            updatePostedTeacherLookForAJobLayoutBinding.tuesday.setChecked(true);
        }

        if(teacherPostRequest.getTeacherData().getAvailability().contains("Wed")){
            updatePostedTeacherLookForAJobLayoutBinding.wednesday.setChecked(true);
        }

        if(teacherPostRequest.getTeacherData().getAvailability().contains("Thur")){
            updatePostedTeacherLookForAJobLayoutBinding.thursday.setChecked(true);
        }

        if(teacherPostRequest.getTeacherData().getAvailability().contains("Fri")){
            updatePostedTeacherLookForAJobLayoutBinding.friday.setChecked(true);
        }
    }

    private void setEducationLevelSpinner(TeacherPostRequest teacherPostRequest){
        if(teacherPostRequest.getEducationLevel().equals("Elementary School")){
            updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.setSelection(0);
        }
        else if(teacherPostRequest.getEducationLevel().equals("Middle School")){
            updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.setSelection(1);
        }

        else if(teacherPostRequest.getEducationLevel().equals("High School")){
            updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.setSelection(3);
        }
        else {
            updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.setSelection(4);
        }
    }

    private void updateFlexBox(){
        if(updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.getChildCount() > 0) {
            updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.removeAllViews();
        }
            for(String str : coursesList){
                if(!checkIfCourseAddedToList(str))
                    coursesList.add(str.trim());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View customView = inflater.inflate(R.layout.course_custom_card,updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout,false);
                TextView courseName = customView.findViewById(R.id.textViewCourseName);
                ImageView deleteImageView = customView.findViewById(R.id.imageViewDelete);
                courseName.setText(str.trim());
                deleteImageView.setOnClickListener(e->{
                    updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.removeView(customView);
                    for(int i=0; i < coursesList.size() ; i++){
                        if(coursesList.get(i).equalsIgnoreCase(courseName.getText().toString())){
                            coursesList.remove(coursesList.get(i));
                        }
                    }
                });
                updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.addView(customView);
            }
    }

    private void setFlexBoxSelectedCourses(String courses){
        String[] splittedCourses = courses.trim().split(",");
        try{
            for(String str : splittedCourses){
                if(!checkIfCourseAddedToList(str))
                    coursesList.add(str.trim());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View customView = inflater.inflate(R.layout.course_custom_card,updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout,false);
                TextView courseName = customView.findViewById(R.id.textViewCourseName);
                ImageView deleteImageView = customView.findViewById(R.id.imageViewDelete);
                courseName.setText(str.trim());
                deleteImageView.setOnClickListener(e->{
                    updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.removeView(customView);
                    for(int i=0; i < coursesList.size() ; i++){
                        if(coursesList.get(i).equalsIgnoreCase(courseName.getText().toString())){
                            coursesList.remove(coursesList.get(i));
                        }
                    }
                });
                updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.addView(customView);
            }
        }
        catch (Exception e){
            Log.e("The exception is -------> "+e.getMessage(),"The exception is -------> "+e.getMessage());
        }
    }

    private boolean checkIfCourseAddedToList(String selectedCourse){
        for (String str : coursesList){
            if(str.equalsIgnoreCase(selectedCourse)){
                return true ;
            }
        }
        return false ;
    }

    @Override
    public void onTeacherPostRequestUpdate(int flag) {
        if(flag == -2){

        }
        else if(flag == -1){

        }
        else if(flag == 0){

        }
        else if(flag == 1){
            database.getTeacherPostedRequests(email,this);
            Log.d("Showing the progress bar ..","Showing the progress bar ..");
            updatePostedRequestDialog.dismiss();
            teacherPostedRequestCardDialog.dismiss();
            new Handler().postDelayed(() -> {
                binding.progressBarLayout.setVisibility(View.GONE);
                Log.d("hiding the progress bar ..","hiding the progress bar ..");
            },1500);
        }
        else {

        }
    }

    @Override
    public void onPostedRequestDeleted(int flag) {
        if(flag == 1){
            database.getTeacherPostedRequests(email,this);
            deleteRequestConfirmationDialog.dismiss();
            teacherPostedRequestCardDialog.dismiss();
        }
        else if(flag == -1){

        }
        else if(flag == -2){

        }
        else if(flag == -0){

        }
        else if(flag == -3){

        }
    }
}