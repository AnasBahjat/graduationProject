package com.example.graduationproject.ui.teacherUi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import com.example.graduationproject.R;
import com.example.graduationproject.adapters.MatchingTeacherAdapter;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.FragmentTeacherBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.AddTeacherMatchingListener;
import com.example.graduationproject.listeners.TeacherMatchCardClickListener;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.CustomChildData;
import com.example.graduationproject.models.TeacherMatchModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeacherFragment extends Fragment implements TeacherMatchCardClickListener , AddTeacherMatchingListener {

    private FragmentTeacherBinding binding ;

    private Database database ;
    String email ;
    String teacherName ;
  //  List<Teacher> teachersAllData = new ArrayList<>();

   /* private Map<String,List<Course>> teacherInformationMap=new HashMap<>();
    List<Course> coursesList = new ArrayList<>();
    List<String> availableCoursesScience = Arrays.asList("Math","Physics","Chemistry","Biology","Science for elementary");
    List<String> availableCoursesCommerce = Arrays.asList("Math","English Language","Arabic Language","History","Geography");
    List<String> availableCoursesEngineering = Arrays.asList("Math","Physics","Chemistry","Biology","Science for elementary",
            "English Language","Arabic Language");
    List<String> availableCoursesArts = Arrays.asList("English Language","Arabic Language","History","Geography");
    List<String> availableCourses = Arrays.asList("English Language","Arabic Language","History","Geography");*/
    private ArrayList<TeacherMatchModel> teacherMatchModelData ;
    private MatchingTeacherAdapter matchingTeacherAdapter;
    private boolean btn1Clicked = true ;
    private boolean btn2Clicked = false ;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeacherBinding.inflate(getLayoutInflater(),container,false);
        getTeacherDataFromActivity();
        init();
        return binding.getRoot();
    }

    private void init(){
        initDatabase();
        initCoursesRecyclerView();
        setMyCoursesAdapter();
        binding.suggestedTeacherCourses.setOnClickListener(x->{
            binding.teacherFragmentSearch.clearFocus();
            binding.teacherFragmentSearch.setQuery(null,false);
            setMyCoursesAdapter();
            btn1Clicked = true;
            btn2Clicked = false;
        });
        binding.availableTeacherRequests.setOnClickListener(c->{
            binding.teacherFragmentSearch.clearFocus();
            binding.teacherFragmentSearch.setQuery(null,false);
            setAvailableTeacherMatchingAdapter();
            btn1Clicked = false ;
            btn2Clicked = true ;
        });
        binding.refreshRecyclerView.setOnRefreshListener(()->{
            if(btn1Clicked && !btn2Clicked){
                sendRefreshBroadcast(1);
            }
            else {
                sendRefreshBroadcast(2);
            }
        });
    }

    private void getTeacherDataFromActivity(){
        if(getArguments() != null){
            email = getArguments().getString("email");
            teacherName = getArguments().getString("firstName");
            teacherName = teacherName+" "+getArguments().getString("lastName");
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
        binding.suggestedTeacherCourses.setBackgroundResource(R.drawable.rounded_button_active);
        binding.availableTeacherRequests.setBackgroundResource(R.drawable.rounded_button_inactive);
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
        matchingTeacherAdapter = new MatchingTeacherAdapter(teacherMatchModelData,getContext(),this);
        if(teacherMatchModelData.isEmpty()){
            binding.noDataAddedText.setVisibility(View.VISIBLE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
        }
        else {
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
            binding.addedCoursesRecyclerView.setAdapter(matchingTeacherAdapter);
        }
        binding.suggestedTeacherCourses.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.availableTeacherRequests.setBackgroundResource(R.drawable.rounded_button_active);


        binding.teacherFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInTeacherMatchingData(newText);
                return true;
            }
        });
    }

    private void searchInTeacherMatchingData(String str){
        String text = str.toLowerCase().trim();
        List<TeacherMatchModel> teacherMatchModelList = new ArrayList<>();
        for(TeacherMatchModel model : teacherMatchModelData){
            if((""+model.getCustomChildData().getChildGrade()).toLowerCase().trim().contains(text)
                    || model.getCustomChildData().getChildName().toLowerCase().trim().contains(text)
                    || model.getChoseDays().toLowerCase().trim().contains(text) || model.getCourses().toLowerCase().trim().contains(text)
                    || model.getLocation().toLowerCase().trim().contains(text) || model.getTeachingMethod().toLowerCase().trim().contains(text)
                    || model.getStartTime().toLowerCase().trim().contains(text) || model.getEndTime().toLowerCase().trim().contains(text)
                    || model.getChildren().getChildAge().toLowerCase().trim().contains(text) || (""+model.getChildren().getChildGender()).toLowerCase().trim().contains(text)){
                teacherMatchModelList.add(model);
            }
            if(teacherMatchModelList.isEmpty()){
                binding.noDataAddedText.setVisibility(View.VISIBLE);
                binding.addedCoursesRecyclerView.setVisibility(View.GONE);
            }
            else {
                matchingTeacherAdapter.filteredList(teacherMatchModelList);
                binding.noDataAddedText.setVisibility(View.GONE);
                binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);

            }

        }
    }

    private void initDatabase(){
        database = new Database(getContext());
    }

    private void initCoursesRecyclerView(){
        binding.addedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /*private void setTeacherAvailableCourses(){
        if(teachersAllData.size() == 1){
            Teacher teacher = teachersAllData.get(0);
            if(teacher.getCollege().equalsIgnoreCase("Faculty Of Engineering")){
                String educationalLevel=teacher.getEducationalLevel();
                if(educationalLevel.equalsIgnoreCase("Elementary School")){
                    // any course
                }
                else if(educationalLevel.equalsIgnoreCase("Middle School")){
                    // math , science , english , arabic , IT
                }
                else if(educationalLevel.equalsIgnoreCase("High School")){
                    // math science english ,arabic, physics , chimestry , biology
                }
                else {// any

                }

            }
            else if(teacher.getCollege().equalsIgnoreCase("IT")){
                // available courses - All course
            }

            else if(teacher.getCollege().equalsIgnoreCase("Science And Mathematics")){
                // available course - Math - Physics - Chemistry - Biology
            }

            else if(teacher.getCollege().equalsIgnoreCase("Faculty Of Arts")){
                if(teacher.getField().equalsIgnoreCase("Arabic Literature")){
                    // available course - Arabic - history - geography - any art except languages
                }
                else if(teacher.getField().equalsIgnoreCase("English Literature")){
                    // available course - English - history - geography - any art except other language
                }

                else if(teacher.getField().equalsIgnoreCase("Language Translator")){
                    // available course - Languages - history - geography - any art except other arabic
                }

                else if(teacher.getField().equalsIgnoreCase("Geography , History ,Islamic Religion")){
                    // available course  any art except languages
                }
            }

            else if(teacher.getCollege().equalsIgnoreCase("Faculty Of Commerce")){
                // available course - arts and math and others except
            }
        }
    }*/

    @Override
    public void onCardClicked(TeacherMatchModel teacherMatchModel) {
        Intent intent = new Intent();
        intent.setAction("START_MATCHING_FRAGMENT_FOR_TEACHER");
        intent.putExtra("teacherMatchModel",(Serializable)teacherMatchModel);
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
    }

    private void sendRefreshBroadcast(int flag){
        if(flag == 1){
            database.getTeacherMatchingData(email,this);
        }
        else {
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
                for(int i=0;i<teacherMatchingData.length();i++){
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
                    Log.d("Child name --------> "+childName,"Child name --------> "+childName);
                    TeacherMatchModel teacherMatchModel=new TeacherMatchModel(matchingId,parentEmail,new CustomChildData(childId,childName,childGrade),
                            choseDays,choseCourses,location,teachingMethod,
                            new Children(childName,childAge,childGender,childGrade),startTime,endTime);
                    teacherMatchModelData.add(teacherMatchModel);
                }
                matchingTeacherAdapter.filteredList(teacherMatchModelData);
                binding.refreshRecyclerView.setRefreshing(false);
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}