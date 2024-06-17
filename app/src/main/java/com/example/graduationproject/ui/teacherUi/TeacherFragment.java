package com.example.graduationproject.ui.teacherUi;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.AvailableCoursesAdapter;
import com.example.graduationproject.adapters.MatchingTeacherAdapter;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.FragmentTeacherBinding;
import com.example.graduationproject.listeners.TellParentDataIsReady;
import com.example.graduationproject.models.Address;
import com.example.graduationproject.models.Course;
import com.example.graduationproject.models.Job;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherFragment extends Fragment {

    private FragmentTeacherBinding binding ;
    private AvailableCoursesAdapter adapter ;
    private RecyclerView recyclerView;
    private Database database ;
    String email ;
    String teacherName ;
    List<Teacher> teachersAllData = new ArrayList<>();

    private Map<String,List<Course>> teacherInformationMap=new HashMap<>();
    List<Course> coursesList = new ArrayList<>();
    List<String> availableCoursesScience = Arrays.asList("Math","Physics","Chemistry","Biology","Science for elementary");
    List<String> availableCoursesCommerce = Arrays.asList("Math","English Language","Arabic Language","History","Geography");
    List<String> availableCoursesEngineering = Arrays.asList("Math","Physics","Chemistry","Biology","Science for elementary",
            "English Language","Arabic Language");
    List<String> availableCoursesArts = Arrays.asList("English Language","Arabic Language","History","Geography");
    List<String> availableCourses = Arrays.asList("English Language","Arabic Language","History","Geography");
    private ArrayList<TeacherMatchModel> teacherMatchModelData ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeacherBinding.inflate(getLayoutInflater(),container,false);
        getTeacherDataFromActivity(savedInstanceState);
        init();
        binding.suggestedTeacherCourses.setOnClickListener(x->{
            setMyCoursesAdapter();
        });
        binding.availableTeacherRequests.setOnClickListener(c->{
            setAvailableTeacherMatchingAdapter();
        });
        return binding.getRoot();
    }

    private void init(){
        initDatabase();
        initCoursesRecyclerView();
        setMyCoursesAdapter();
    }

    private void getTeacherDataFromActivity(Bundle bundle){
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
            Log.d("The size is ---->"+teacherMatchModelData.size(),"The size is ---->"+teacherMatchModelData.size());
        }
        else {
            email="";
            teacherName="";
        }
    }

    public void setMyCoursesAdapter(){
        List<TeacherMatchModel> teacherMatchModelList = new ArrayList<>();
        MatchingTeacherAdapter matchingTeacherAdapter = new MatchingTeacherAdapter(teacherMatchModelList,getContext());
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
        binding.teacherFragmentSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                matchingTeacherAdapter.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setAvailableTeacherMatchingAdapter(){
        MatchingTeacherAdapter matchingTeacherAdapter = new MatchingTeacherAdapter(teacherMatchModelData,getContext());
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
        binding.teacherFragmentSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                matchingTeacherAdapter.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initDatabase(){
        database = new Database(getContext());
    }

    private void initCoursesRecyclerView(){
        binding.addedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setTeacherAvailableCourses(){
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
    }
}