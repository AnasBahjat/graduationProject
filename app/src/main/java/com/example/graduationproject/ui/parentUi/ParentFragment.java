package com.example.graduationproject.ui.parentUi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.PostedTeacherRequestsAdapter;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.FragmentParentBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.ParentListenerForParentPostedRequests;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.CustomChildData;
import com.example.graduationproject.models.TeacherMatchModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParentFragment extends Fragment implements ParentListenerForParentPostedRequests {

    private String email,firstName,lastName ;
    private FragmentParentBinding binding;
    private Database database;
    private ArrayList<TeacherMatchModel> parentPostedRequestsList = new ArrayList<>();

    private PostedTeacherRequestsAdapter postedTeacherRequestsAdapter;
    private boolean btn1Clicked = true;
    private boolean btn2Clicked = false;

    private boolean isBroadcastReceiverRegistered = false;

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("NOTIFY_PARENT_FRAGMENT_NEW_TEACHER_MATCH_MODEL_ADDED".equals(intent.getAction())){
                TeacherMatchModel tempTeacherModel ;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                     tempTeacherModel = intent.getParcelableExtra("addedTeacherRequest",TeacherMatchModel.class);
                }
                else {
                    tempTeacherModel = intent.getParcelableExtra("addedTeacherRequest");
                }
                Log.d(tempTeacherModel.getEndTime(),"END time ---->"+tempTeacherModel.getEndTime());
                if(tempTeacherModel != null){
                    Toast.makeText(getContext(),"Broadcast received and model is not null ..",Toast.LENGTH_SHORT).show();
                    List<TeacherMatchModel> tempList = new ArrayList<>();
                    tempList.addAll(parentPostedRequestsList);
                    tempList.add(tempTeacherModel);
                    parentPostedRequestsList.add(tempTeacherModel);
                    postedTeacherRequestsAdapter.filteredList(parentPostedRequestsList);
                }
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentParentBinding.inflate(getLayoutInflater(),container,false);
        getTeacherDataFromActivity();
        initialize();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isBroadcastReceiverRegistered){
            IntentFilter parentFragmentIntentFilter = new IntentFilter();
            parentFragmentIntentFilter.addAction("NOTIFY_PARENT_FRAGMENT_NEW_TEACHER_MATCH_MODEL_ADDED");
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

    /*@Override
    public void onPause() {
        super.onPause();
        if(getActivity() != null)
            getActivity().unregisterReceiver(myBroadcastReceiver);
    }*/

    @Override
    public void onStop() {
        super.onStop();

        if(isBroadcastReceiverRegistered && getActivity() != null){
            getActivity().unregisterReceiver(myBroadcastReceiver);
            isBroadcastReceiverRegistered = false;
        }

        /*if(myBroadcastReceiver != null && getActivity() != null){
            getActivity().unregisterReceiver(myBroadcastReceiver);
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(isBroadcastReceiverRegistered && getActivity() != null){
            getActivity().unregisterReceiver(myBroadcastReceiver);
            isBroadcastReceiverRegistered = false;
        }

      /*  if (myBroadcastReceiver != null && getActivity() != null) {
            getActivity().unregisterReceiver(myBroadcastReceiver);
        }*/
    }

    private void initialize(){
        initDatabase();
        initCoursesRecyclerView();
        setMyPostedRequestsAdapter();
        binding.myPostedRequestsBtn.setOnClickListener(x->{
            binding.parentFragmentSearch.clearFocus();
            binding.parentFragmentSearch.setQuery(null,false);
            setMyPostedRequestsAdapter();
            btn1Clicked = true;
            btn2Clicked = false;
        });

        binding.myReceivedRequestsBtn.setOnClickListener(c->{
            binding.parentFragmentSearch.clearFocus();
            binding.parentFragmentSearch.setQuery(null,false);
            setMyReceivedRequestsAdapter();
            btn1Clicked = false;
            btn2Clicked = true;
        });

        binding.refreshRecyclerView.setOnRefreshListener(()->{
            if(btn1Clicked && !btn2Clicked){
                refreshPostedRequests(1);
            }
            else {
                refreshPostedRequests(2);
            }
        });
    }



    private void getTeacherDataFromActivity(){
        if(getArguments() != null){
            email = getArguments().getString("email");
            firstName = getArguments().getString("firstName");
            lastName = getArguments().getString("lastName");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                parentPostedRequestsList = getArguments().getParcelableArrayList("parentRequestsData",TeacherMatchModel.class);
            }
            else {
                parentPostedRequestsList = getArguments().getParcelableArrayList("parentRequestsData");
            }
        }
        else {
            email="";
            firstName = "";
            lastName="";
            parentPostedRequestsList.clear();
        }
    }

    private void initDatabase(){
        database = new Database(getContext());
    }

    private void initCoursesRecyclerView(){
        binding.postedRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void setMyPostedRequestsAdapter(){
       // List<TeacherMatchModel> parentPostedRequestsList = new ArrayList<>();
        postedTeacherRequestsAdapter = new PostedTeacherRequestsAdapter(parentPostedRequestsList,getContext());
        if(parentPostedRequestsList.isEmpty()){
            binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
            binding.postedRequestsRecyclerView.setVisibility(View.GONE);
        }
        else{
            binding.postedRequestsRecyclerView.setAdapter(postedTeacherRequestsAdapter);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_active);
        binding.myReceivedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);

        binding.parentFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInParentRequestsData(newText);
                return true;
            }
        });
    }

    private void setMyReceivedRequestsAdapter(){



        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.myReceivedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_active);

        binding.parentFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInParentRequestsData(newText);
                return true;
            }
        });

    }



    private void searchInParentRequestsData(String str){
        String text = str.toLowerCase().trim();
        List<TeacherMatchModel> teacherMatchModelList = new ArrayList<>();
        for(TeacherMatchModel model : parentPostedRequestsList){
            if((""+model.getCustomChildData().getChildGrade()).toLowerCase().trim().contains(text)
                    || model.getCustomChildData().getChildName().toLowerCase().trim().contains(text)
                    || model.getChoseDays().toLowerCase().trim().contains(text) || model.getCourses().toLowerCase().trim().contains(text)
                    || model.getLocation().toLowerCase().trim().contains(text) || model.getTeachingMethod().toLowerCase().trim().contains(text)
                    || model.getStartTime().toLowerCase().trim().contains(text) || model.getEndTime().toLowerCase().trim().contains(text)
                    || model.getChildren().getChildAge().toLowerCase().trim().contains(text) || (""+model.getChildren().getChildGender()).toLowerCase().trim().contains(text)){
                teacherMatchModelList.add(model);
            }
            if(teacherMatchModelList.isEmpty()){
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
            }
            else {
                //postedTeacherRequestsAdapter.filteredList(teacherMatchModelList);
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);

            }

        }
    }

    private void refreshPostedRequests(int flag){
        if(flag == 1){
            database.getParentPostedMatchingInformation(email,this);
        }
        else {
            // get received requests ..
        }
    }

    @Override
    public void onPostedParentRequests(int resultFlag, JSONArray parentInformation) {
        if(resultFlag == -3){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","An error occurred please try again later ..");
        }
        else if(resultFlag == -2){
            binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
            binding.postedRequestsRecyclerView.setVisibility(View.GONE);
            binding.refreshRecyclerView.setRefreshing(false);
        }
        else if(resultFlag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Network Error","Please try again later or check your network connection ..");
        }
        else if(resultFlag == 0){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Request Error","Something went wrong ,Please try again later ..");
        }
        else {
            try {
                ArrayList<TeacherMatchModel> tempTeacherMatchModelList = new ArrayList<>();
                for(int i=0;i<parentInformation.length();i++){
                        JSONObject jsonObject = parentInformation.getJSONObject(i);
                        int matchingId = jsonObject.getInt("matchingId");
                        int childId = jsonObject.getInt("childId");
                        String choseDays = jsonObject.getString("choseDays");
                        String courses = jsonObject.getString("courses");
                        String location = jsonObject.getString("location");
                        String teachingMethod = jsonObject.getString("teachingMethod");
                        String startTime = jsonObject.getString("startTime");
                        String endTime = jsonObject.getString("endTime");
                        String childName = jsonObject.getString("childName");
                        String childAge = jsonObject.getString("childAge");
                        String childGender = "Male";
                        if(jsonObject.getString("childGender").equals("0"))
                            childGender = "Female";
                        int childGrade = jsonObject.getInt("childGrade");

                        tempTeacherMatchModelList.add(new TeacherMatchModel(matchingId,email,
                                new CustomChildData(childId,childName,childGrade),choseDays,courses,
                                location,teachingMethod,
                                new Children(childName,childAge,
                                        Integer.parseInt(jsonObject.getString("childGender")),
                                        childGrade),startTime,endTime));
                    }
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                postedTeacherRequestsAdapter.filteredList(tempTeacherMatchModelList);
                binding.refreshRecyclerView.setRefreshing(false);
            }
            catch(Exception ignored){

            }
        }
    }
}