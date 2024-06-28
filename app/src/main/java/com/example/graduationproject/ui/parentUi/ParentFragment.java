package com.example.graduationproject.ui.parentUi;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.CustomSpinnerAdapter;
import com.example.graduationproject.adapters.ParentPostedRequestsAdapter;
import com.example.graduationproject.adapters.TeacherPostedRequestsAdapter;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.ConfirmDeleteDialogLayoutBinding;
import com.example.graduationproject.databinding.DialogParentPostedRequestCardBinding;
import com.example.graduationproject.databinding.FragmentParentBinding;
import com.example.graduationproject.databinding.ParentFilterLayoutBinding;
import com.example.graduationproject.databinding.TeacherPostedRequestsCardToShowToParentBinding;
import com.example.graduationproject.databinding.UpdateParentPostedRequestBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.GetParentChildren;
import com.example.graduationproject.listeners.OnAllTeacherPostedRequestsForParentListener;
import com.example.graduationproject.listeners.ParentInformationListener;
import com.example.graduationproject.listeners.ParentListenerForParentPostedRequests;
import com.example.graduationproject.listeners.ParentPostRequestClickListener;
import com.example.graduationproject.listeners.ParentPostRequestDeleteListener;
import com.example.graduationproject.listeners.TeacherPostRequestClickListener;
import com.example.graduationproject.listeners.UpdateTeacherPostedRequestListener;
import com.example.graduationproject.models.Address;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.CustomChildData;
import com.example.graduationproject.models.Parent;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.models.TeacherPostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class ParentFragment extends Fragment implements ParentListenerForParentPostedRequests,
        ParentPostRequestClickListener,
        GetParentChildren,
        ParentInformationListener,
        UpdateTeacherPostedRequestListener,
        ParentPostRequestDeleteListener,
        OnAllTeacherPostedRequestsForParentListener,
        TeacherPostRequestClickListener {

    private String email,firstName,lastName,birthDate,gender,idNumber ;
    List<Address> parentAddress = new ArrayList<>() ;
    List<String> phoneNumbersList = new ArrayList<>();
    private FragmentParentBinding binding;
    private Database database;
    private ArrayList<TeacherMatchModel> parentPostedRequestsList = new ArrayList<>();

    private ParentPostedRequestsAdapter parentPostedRequests;

    private boolean btn1Clicked = true;
    private boolean btn2Clicked = false;
    private int currentClickedPostedCardId = 0 ;

    private boolean isBroadcastReceiverRegistered = false;
    private Dialog updateParentPostedRequestDialog ;

    private DialogParentPostedRequestCardBinding dialogParentPostedRequestCardBinding;
    private UpdateParentPostedRequestBinding postTeacherRequestPopupWindowBinding;
    private final List<CustomChildData> childrenSpinnerList = new ArrayList<>();
    private final List<Children> parentChildrenList = new ArrayList<>();
    TeacherMatchModel requestModelTemp ;
    private int selectedChildId,selectedChildGender ;
    private String selectedChildName,selectedChildGrade ;

    private  TeacherMatchModel requestModel;
    private String [] splittedCourses ;

    List<String> coursesList = new ArrayList<>();
    TeacherMatchModel tmm ;

    private String amPmStart,amPmEnd;
    private String startTime ="12:00 PM", endTime="12:00 PM";
    private Dialog clickedCardDialog;
    private Dialog deleteParentPostedTeacherMatchingDialog;
    private boolean browseTeacherPostedRequestsForParent = false;

    private List<TeacherPostRequest> teacherPostedRequestsForParentList = new ArrayList<>();


    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
    private Dialog filterDialog ;
    private ParentFilterLayoutBinding parentFilterLayoutBinding ;
    private String[] locationArrayForFilter;
    private List<String> filterSelectedLocation = new ArrayList<>();

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("NOTIFY_PARENT_FRAGMENT_NEW_TEACHER_MATCH_MODEL_ADDED".equals(intent.getAction())){
                database.getParentPostedMatchingInformation(email,ParentFragment.this);
            }
            else if("PARENT_POSTED_REQUESTS_ITEM_CLICKED".equals(intent.getAction())){
                btn1Clicked = true;
                btn2Clicked = false;
                browseTeacherPostedRequestsForParent=false;
                binding.refreshRecyclerView.setRefreshing(true);
                setMyPostedRequestsAdapter();
                database.getParentPostedMatchingInformation(email,ParentFragment.this);
            }

            else if("UPDATE_POSTED_DATA_FOR_PARENT".equals(intent.getAction())){
                myPostedBtnClicked();
            }
            else if("SHOW_TEACHER_POSTED_REQUESTS_FOR_PARENT".equals(intent.getAction())){
                setParentRequestsForTeacher();
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
            parentFragmentIntentFilter.addAction("PARENT_POSTED_REQUESTS_ITEM_CLICKED");
            parentFragmentIntentFilter.addAction("UPDATE_POSTED_DATA_FOR_PARENT");
            parentFragmentIntentFilter.addAction("SHOW_TEACHER_POSTED_REQUESTS_FOR_PARENT");
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

    @Override
    public void onStop() {
        super.onStop();

        if(isBroadcastReceiverRegistered && getActivity() != null){
            getActivity().unregisterReceiver(myBroadcastReceiver);
            isBroadcastReceiverRegistered = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isBroadcastReceiverRegistered && getActivity() != null){
            getActivity().unregisterReceiver(myBroadcastReceiver);
            isBroadcastReceiverRegistered = false;
        }
    }

    private void initialize(){
        initDatabase();
        initCoursesRecyclerView();
        setMyPostedRequestsAdapter();
        binding.myPostedRequestsBtn.setOnClickListener(x->{
            myPostedBtnClicked();
        });

        binding.myReceivedRequestsBtn.setOnClickListener(c->{
            myReceivedRequestsBtnClicked();
        });

        binding.refreshRecyclerView.setOnRefreshListener(()->{
            if(btn1Clicked && !btn2Clicked && !browseTeacherPostedRequestsForParent){
                refreshAction(1);
            }
            else if(!btn1Clicked && btn2Clicked && !browseTeacherPostedRequestsForParent){
                refreshAction(2);
            }
            else if(!btn1Clicked && !btn2Clicked && browseTeacherPostedRequestsForParent){
                refreshAction(3); // update browse teacher posted list for parent ..
            }
        });
        binding.filterLayout.setOnClickListener(z->{
            showFilterDialogForParent();
        });
    }

    private void showFilterDialogForParent(){
        if(getContext() != null){
            parentFilterLayoutBinding = ParentFilterLayoutBinding.inflate(LayoutInflater.from(getContext()));
            filterDialog = new Dialog(getContext());
            filterDialog.setContentView(parentFilterLayoutBinding.getRoot());
            filterDialog.setCancelable(false);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(filterDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 1700;
            filterDialog.getWindow().setAttributes(layoutParams);
            if(filterDialog.getWindow() != null)
                filterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            filterDialog.show();

            parentFilterLayoutBinding.closeImage.setOnClickListener(z->{
                filterDialog.dismiss();
            });

            setLocationFlexBox();
        }
    }

    private void setLocationFlexBox(){
         locationArrayForFilter = getResources().getStringArray(R.array.locationArray);
         if(parentFilterLayoutBinding.locationFlexBox1.getChildCount() > 0)
             parentFilterLayoutBinding.locationFlexBox1.removeAllViews();

        for(String str : locationArrayForFilter){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.custom_layout_for_filter_view,parentFilterLayoutBinding.locationFlexBox1,false);
            TextView courseName = customView.findViewById(R.id.textViewTemp);
            courseName.setText(str.trim());
            LinearLayout parentLayout = customView.findViewById(R.id.parentLayout);
            parentLayout.setOnClickListener(new View.OnClickListener() {
                boolean isSelected = false ;
                @Override
                public void onClick(View v) {
                    isSelected = !isSelected;
                    if(isSelected){
                        filterSelectedLocation.add(courseName.getText().toString());
                        customView.setBackgroundResource(R.drawable.selected_view);
                    }
                    else {
                        customView.setBackgroundResource(R.drawable.rounded_corners);
                    }
                }
            });
        }

    }

    private void myPostedBtnClicked(){
        binding.parentFragmentSearch.clearFocus();
        binding.parentFragmentSearch.setQuery(null,false);
        setMyPostedRequestsAdapter();
        btn1Clicked = true;
        btn2Clicked = false;
        browseTeacherPostedRequestsForParent = false ;
    }

    private void myReceivedRequestsBtnClicked(){
        binding.parentFragmentSearch.clearFocus();
        binding.parentFragmentSearch.setQuery(null,false);
        setMyReceivedRequestsAdapter();
        btn1Clicked = false;
        btn2Clicked = true;
        browseTeacherPostedRequestsForParent = false ;
    }



    private void getTeacherDataFromActivity(){
        if(getArguments() != null){
            email = getArguments().getString("email");
            firstName = getArguments().getString("firstName");
            lastName = getArguments().getString("lastName");
           /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                parentPostedRequestsList = getArguments().getParcelableArrayList("parentRequestsData",TeacherMatchModel.class);
            }
            else {
                parentPostedRequestsList = getArguments().getParcelableArrayList("parentRequestsData");
            }*/
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
        database.getParentPostedMatchingInformation(email,this);
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

    private void refreshAction(int flag){
        if(flag == 1){
            database.getParentPostedMatchingInformation(email,this);
        }
        else if(flag == 2) {
            // get received requests ..
        }
        else if(flag == 3){
            database.getAllTeacherPostedRequestsForParent(this);
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
                if(!phoneNumbersList.isEmpty())
                    phoneNumbersList.clear();
                if(!parentAddress.isEmpty())
                    parentAddress.clear();
                for(int i=parentInformation.length() - 1 ;i>=0;i--){
                        JSONObject jsonObject = parentInformation.getJSONObject(i);
                        firstName = jsonObject.getString("firstname");
                        firstName = firstName.substring(0,1).toUpperCase()+firstName.substring(1).toLowerCase();
                        lastName = jsonObject.getString("lastname");
                        lastName = lastName.substring(0,1).toUpperCase()+lastName.substring(1).toLowerCase();
                        int parentGender = jsonObject.getInt("parentGender");
                        int parentId = jsonObject.getInt("parentId");
                        String parentBirthDate = jsonObject.getString("birthDate");
                        idNumber = jsonObject.getString("idNumber");
                        int childId = jsonObject.getInt("childId");
                        String childName = jsonObject.getString("childName");
                        int childAge = jsonObject.getInt("childAge");
                        int childGender = jsonObject.getInt("childGender");
                        int childGrade = jsonObject.getInt("childGrade");
                        int matchingId = jsonObject.getInt("matchingId");
                        String matchingChoseDays = jsonObject.getString("choseDays");
                        Log.e("Post "+i+" courses -----> "+matchingChoseDays,"Post "+i+" courses -----> "+matchingChoseDays);
                        String courses = jsonObject.getString("courses");
                        Log.e("Post "+i+" courses -----> "+courses,"Post "+i+" courses -----> "+courses);
                        String location = jsonObject.getString("location");
                        String teachingMethod=jsonObject.getString("teachingMethod");
                        String startTime = jsonObject.getString("startTime");
                        String endTime = jsonObject.getString("endTime");
                        if(i==parentInformation.length() - 1){
                            String phoneNumbers = jsonObject.getString("phoneNumbers");
                            if(phoneNumbers.contains(",")){
                                String [] splitPhoneNumbers = phoneNumbers.split(",");
                                for(String str : splitPhoneNumbers){
                                    phoneNumbersList.add(str.trim());
                                }
                            }
                            else
                                phoneNumbersList.add(phoneNumbers.trim());
                            String addresses = jsonObject.getString("addresses");
                            if(addresses.contains("|")){
                                String[] splitAddresses = addresses.split("\\|");
                                for (String splitAddress : splitAddresses) {
                                    String[] currentAddress = splitAddress.split(",");
                                    parentAddress.add(new Address(currentAddress[0], currentAddress[1]));
                                }
                            }
                            else{
                                String [] splitAddress = addresses.split(",");
                                parentAddress.add(new Address(splitAddress[0],splitAddress[1]));
                            }
                    }

                        tempTeacherMatchModelList.add(new TeacherMatchModel(matchingId,new CustomChildData(childId,childName,childGrade,childGender),
                                matchingChoseDays,courses,location,
                                teachingMethod,startTime,endTime,new Parent(email,idNumber,firstName,lastName,parentBirthDate,parentId,parentAddress,phoneNumbersList)));

                }
                parentPostedRequestsList = tempTeacherMatchModelList;
                updateParentPostedRequests();
            }
            catch(Exception ignored){

            }
        }
    }

    private void updateParentPostedRequests(){
        if(parentPostedRequestsList.isEmpty()){
            binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
            binding.postedRequestsRecyclerView.setVisibility(View.GONE);
        }
        else{
            parentPostedRequests = new ParentPostedRequestsAdapter(parentPostedRequestsList,getContext(),this);
            binding.postedRequestsRecyclerView.setAdapter(parentPostedRequests);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);

            // parentPostedRequests.filteredList(tempTeacherMatchModelList);
        }
        binding.refreshRecyclerView.setRefreshing(false);
    }

    @Override
    public void onClick(TeacherMatchModel requestModel) {
        currentClickedPostedCardId = requestModel.getMatchingId();
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        binding.overlayView.setVisibility(View.VISIBLE);
       // database.getParentInformation(email,this);
        this.requestModel = requestModel;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.progressBarLayout.setVisibility(View.GONE);
                binding.overlayView.setVisibility(View.GONE);
                showClickedCardDialog();
            }
        },1500);
    }

    private void showClickedCardDialog(){
        if(requestModel != null && getContext() != null){
             dialogParentPostedRequestCardBinding = DialogParentPostedRequestCardBinding.inflate(LayoutInflater.from(getContext()));
             clickedCardDialog = new Dialog(getContext());
            clickedCardDialog.setContentView(dialogParentPostedRequestCardBinding.getRoot());
            clickedCardDialog.setCancelable(false);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(clickedCardDialog.getWindow()).getAttributes());
            layoutParams.width = 1250;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            clickedCardDialog.getWindow().setAttributes(layoutParams);
            if(clickedCardDialog.getWindow() != null)
                clickedCardDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            clickedCardDialog.show();

            dialogParentPostedRequestCardBinding.childNameTextView.setText(requestModel.getCustomChildData().getChildName());

            String parentFirstName = firstName.substring(0,1).toUpperCase()+firstName.substring(1).toLowerCase();
            String parentLastName = lastName.substring(0,1).toUpperCase()+lastName.substring(1).toLowerCase();
            dialogParentPostedRequestCardBinding.parentNameTextView.setText(parentFirstName+" "+parentLastName);
            StringBuilder phoneNumbersStr = new StringBuilder();
            Log.d("isPhoneNumersList empty ? --> "+phoneNumbersList.isEmpty(),"isPhoneNumersList empty ? --> "+phoneNumbersList.isEmpty());
            if(phoneNumbersList.size() > 1){
                for(int i=0;i<phoneNumbersList.size();i++){
                    if( i + 1 != phoneNumbersList.size())
                        phoneNumbersStr.append(phoneNumbersList.get(i)).append(" , ");
                    else
                        phoneNumbersStr.append(phoneNumbersList.get(i));
                }
            }
            else
                phoneNumbersStr.append(phoneNumbersList.get(0));

            dialogParentPostedRequestCardBinding.parentPhoneNumberTextView.setText(phoneNumbersStr.toString());
            dialogParentPostedRequestCardBinding.parentEmailTextView.setText(email);
            dialogParentPostedRequestCardBinding.coursesTextView.setText(requestModel.getCourses());
            dialogParentPostedRequestCardBinding.choseDaysTextView.setText(requestModel.getChoseDays());
            dialogParentPostedRequestCardBinding.teachingMethodTextView.setText(requestModel.getTeachingMethod());
            dialogParentPostedRequestCardBinding.timeTextView.setText(requestModel.getStartTime() + " - "+requestModel.getEndTime());
            dialogParentPostedRequestCardBinding.locationTextView.setText(requestModel.getLocation());


            dialogParentPostedRequestCardBinding.closeImageView.setOnClickListener(a->{
                clickedCardDialog.dismiss();
            });
            dialogParentPostedRequestCardBinding.cardSettings.setOnClickListener(b->{
                requestModelTemp = requestModel;
                showPopupMenuForCard();
            });
        }
    }

    private void showPopupMenuForCard(){
        PopupMenu popupMenu = new PopupMenu(getContext(),dialogParentPostedRequestCardBinding.cardSettings);
        popupMenu.getMenuInflater().inflate(R.menu.menu_parent_posted_card_view,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.editCard){
                postTeacherRequestPopupWindowBinding = UpdateParentPostedRequestBinding.inflate(LayoutInflater.from(getContext()));
                database.getParentChildren(email,this);
            }
            else if(item.getItemId() == R.id.deleteCard){
                showDeleteDialog();
            }
            return true;
        });
    }

    private void showDeleteDialog(){
        if(getContext() != null){
            ConfirmDeleteDialogLayoutBinding confirmDeleteDialogLayoutBinding = ConfirmDeleteDialogLayoutBinding.inflate(LayoutInflater.from(getContext()));
            deleteParentPostedTeacherMatchingDialog = new Dialog(getContext());
            deleteParentPostedTeacherMatchingDialog.setContentView(confirmDeleteDialogLayoutBinding.getRoot());
            deleteParentPostedTeacherMatchingDialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(deleteParentPostedTeacherMatchingDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 600;
            deleteParentPostedTeacherMatchingDialog.getWindow().setAttributes(layoutParams);
            if(deleteParentPostedTeacherMatchingDialog.getWindow() != null)
                deleteParentPostedTeacherMatchingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            deleteParentPostedTeacherMatchingDialog.show();

            confirmDeleteDialogLayoutBinding.deleteBtn.setOnClickListener(x->{
               // deleteParentPostedTeacherMatchingDialog(teacherPostRequest);
                database.deleteParentPostedRequest(currentClickedPostedCardId,this);
            });

            confirmDeleteDialogLayoutBinding.cancelBtn.setOnClickListener(c->{
                deleteParentPostedTeacherMatchingDialog.dismiss();
            });
        }
    }

    private void showEditCardItemDialog(){
        postTeacherRequestPopupWindowBinding = UpdateParentPostedRequestBinding.inflate(LayoutInflater.from(getContext()));
        if(getContext() != null){
            updateParentPostedRequestDialog = new Dialog(getContext());
            updateParentPostedRequestDialog.setContentView(postTeacherRequestPopupWindowBinding.getRoot());
            updateParentPostedRequestDialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(updateParentPostedRequestDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 2000;
            updateParentPostedRequestDialog.getWindow().setAttributes(layoutParams);
            updateParentPostedRequestDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            if(updateParentPostedRequestDialog.getWindow() != null)
                updateParentPostedRequestDialog.getWindow().setLayout(1300,2000);
            updateParentPostedRequestDialog.show();


            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(),childrenSpinnerList);
            postTeacherRequestPopupWindowBinding.childrenSpinner.setAdapter(adapter);
            int currentChildPosition = adapter.getPosition(requestModelTemp.getCustomChildData());


            if(currentChildPosition >= 0){
                postTeacherRequestPopupWindowBinding.childrenSpinner.setSelection(currentChildPosition);
            }

            postTeacherRequestPopupWindowBinding.childrenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CustomChildData data = (CustomChildData) parent.getItemAtPosition(position);
                    String[] splittedString = data.toString().split(",");
                    selectedChildId = data.getChildId();
                    selectedChildName = splittedString[0].trim();
                    selectedChildGrade = splittedString[1].trim();
                    selectedChildGender = data.getGender();
                    updatedCoursesSpinner();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            postTeacherRequestPopupWindowBinding.childrenSpinner.setEnabled(false);
            setSelectedDays();
            startTime = requestModelTemp.getStartTime();
            endTime=requestModel.getEndTime();
            postTeacherRequestPopupWindowBinding.startTimeEdtText.setText(requestModel.getStartTime());
            postTeacherRequestPopupWindowBinding.endTimeEdtText.setText(requestModel.getEndTime());



            postTeacherRequestPopupWindowBinding.startTimeEdtText.setOnClickListener(c->{
                setStartTime();
            });

            postTeacherRequestPopupWindowBinding.endTimeEdtText.setOnClickListener(j->{
                setEndTime();
            });



            if(postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher.getChildCount() > 0){
                postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher.removeAllViews();
            }
            setFlexBoxAddedCoursesForPostedRequest();
            postTeacherRequestPopupWindowBinding.confirmTeachersInformation.setOnClickListener(z->{
                try {
                    updateParentPostedRequest();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });

            postTeacherRequestPopupWindowBinding.cancelButton.setOnClickListener(z->{
                updateParentPostedRequestDialog.dismiss();
            });




            postTeacherRequestPopupWindowBinding.addCourseMatchingTeacherBtn.setOnClickListener(f->{
                String selectedCourse = postTeacherRequestPopupWindowBinding.forParentCourses.getSelectedItem().toString();
                if(!checkIfCourseAddedToList(selectedCourse)){
                    coursesList.add(selectedCourse);
                    updateFlexBoxMatchingTeacher();
                }
                else {
                    MyAlertDialog.showWarningCourseAdded(getContext());
                }
            });
            postTeacherRequestPopupWindowBinding.closeTheDialog.setOnClickListener(d->{
                updateParentPostedRequestDialog.dismiss();
            });
        }
    }

    private void updateParentPostedRequest() throws ParseException {
        StringBuilder selectedDays = new StringBuilder();
        selectedDays = getSelectedDays();
        if(selectedDays == null){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Days Error","Please Choose at least one day a week to match a teacher data");
        }
        else {
            if(!checkStartAndEndTime()){
                MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Wrong timing","Please Choose valid start and end time, and make sure there are at least one hour..");
            }
            else {
                if(postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher.getChildCount() == 0){
                    MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"No Courses","Please Choose At least one course ..");
                }
                else {
                    StringBuilder courses= new StringBuilder();
                    for(int i=0 ; i < coursesList.size() ; i++){
                        if(i + 1 != coursesList.size()){
                            courses.append(coursesList.get(i)).append(" , ");
                        }
                        else {
                            courses.append(coursesList.get(i));
                        }
                    }
                    String city = postTeacherRequestPopupWindowBinding.locationSpinner.getSelectedItem().toString();
                    String teachingMethodStr = postTeacherRequestPopupWindowBinding.teachingMethod.getSelectedItem().toString();
                    tmm = new TeacherMatchModel(requestModel.getMatchingId(),new CustomChildData(selectedChildId,selectedChildName,Integer.parseInt(selectedChildGrade))
                            ,selectedDays.toString(),courses.toString(),
                            city,
                            teachingMethodStr,startTime,endTime);
                    database.updateParentPostedRequest(email,tmm,this);
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

    private StringBuilder getSelectedDays(){
        StringBuilder choosedDays = new StringBuilder();
        if(postTeacherRequestPopupWindowBinding.saturday.isChecked() ||
                postTeacherRequestPopupWindowBinding.sunday.isChecked() ||
                postTeacherRequestPopupWindowBinding.monday.isChecked() ||
                postTeacherRequestPopupWindowBinding.tuesday.isChecked() ||
                postTeacherRequestPopupWindowBinding.wednesday.isChecked() ||
                postTeacherRequestPopupWindowBinding.thursday.isChecked() ||
                postTeacherRequestPopupWindowBinding.friday.isChecked()){

            if(postTeacherRequestPopupWindowBinding.saturday.isChecked())
                choosedDays.append("Sat , ");
            if(postTeacherRequestPopupWindowBinding.sunday.isChecked())
                choosedDays.append("Sun , ");
            if(postTeacherRequestPopupWindowBinding.monday.isChecked())
                choosedDays.append("Mon , ");
            if(postTeacherRequestPopupWindowBinding.tuesday.isChecked())
                choosedDays.append("Tues , ");
            if(postTeacherRequestPopupWindowBinding.wednesday.isChecked())
                choosedDays.append("Wed , ");
            if(postTeacherRequestPopupWindowBinding.thursday.isChecked())
                choosedDays.append("Thur , ");
            if(postTeacherRequestPopupWindowBinding.friday.isChecked())
                choosedDays.append("Fri");
            return  choosedDays;
        }
        else {
            return null ;
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
                postTeacherRequestPopupWindowBinding.startTimeEdtText.setText(startTime);
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
                postTeacherRequestPopupWindowBinding.endTimeEdtText.setText(endTime);
            }
        },hours,mins,false);
        timePickerDialog.show();
    }

    private void updateFlexBoxMatchingTeacher(){
        if(postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher.getChildCount() > 0){
            postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher.removeAllViews();
        }

        for(String str : coursesList){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customeView = inflater.inflate(R.layout.course_custom_card,postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher,false);
            TextView courseName = customeView.findViewById(R.id.textViewCourseName);
            ImageView deleteImageView = customeView.findViewById(R.id.imageViewDelete);
            courseName.setText(str);
            deleteImageView.setOnClickListener(g->{
                postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher.removeView(customeView);
                for(int i=0; i < coursesList.size() ; i++){
                    if(coursesList.get(i).equalsIgnoreCase(courseName.getText().toString())){
                        coursesList.remove(coursesList.get(i));
                    }
                }
            });
            postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher.addView(customeView);
        }
    }
    private void setFlexBoxAddedCoursesForPostedRequest(){
        splittedCourses  = requestModel.getCourses().trim().split(",");
        try{
            for(String str : splittedCourses){
                if(!checkIfCourseAddedToList(str))
                    coursesList.add(str.trim());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View customView = inflater.inflate(R.layout.course_custom_card,postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher,false);
                TextView courseName = customView.findViewById(R.id.textViewCourseName);
                ImageView deleteImageView = customView.findViewById(R.id.imageViewDelete);
                courseName.setText(str.trim());
                deleteImageView.setOnClickListener(e->{
                    postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher.removeView(customView);
                    for(int i=0; i < coursesList.size() ; i++){
                        if(coursesList.get(i).equalsIgnoreCase(courseName.getText().toString())){
                            coursesList.remove(coursesList.get(i));
                        }
                    }
                });
                postTeacherRequestPopupWindowBinding.flexboxLayoutMatchTeacher.addView(customView);
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



    private void setSelectedDays(){
       // StringBuilder selectedDays = new StringBuilder();
        if(requestModel.getChoseDays().contains("Sat")){
            postTeacherRequestPopupWindowBinding.saturday.setChecked(true);
        }

        if(requestModel.getChoseDays().contains("Sun")){
            postTeacherRequestPopupWindowBinding.sunday.setChecked(true);
        }

        if(requestModel.getChoseDays().contains("Mon")){
            postTeacherRequestPopupWindowBinding.monday.setChecked(true);
        }

        if(requestModel.getChoseDays().contains("Tues")){
            postTeacherRequestPopupWindowBinding.tuesday.setChecked(true);
        }

        if(requestModel.getChoseDays().contains("Wed")){
            postTeacherRequestPopupWindowBinding.wednesday.setChecked(true);
        }

        if(requestModel.getChoseDays().contains("Thur")){
            postTeacherRequestPopupWindowBinding.thursday.setChecked(true);
        }

        if(requestModel.getChoseDays().contains("Fri")){
            postTeacherRequestPopupWindowBinding.friday.setChecked(true);
        }

    }

    private void updatedCoursesSpinner(){
        ArrayAdapter<String> adapter ;
        int currentGrade = Integer.parseInt(selectedChildGrade);
        if(getContext() != null){
            if(currentGrade >= 1 && currentGrade <= 5){
                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getContext().getResources().getStringArray(R.array.forParentCoursesElementarySchool));
                postTeacherRequestPopupWindowBinding.forParentCourses.setAdapter(adapter);
            }
            else if (currentGrade > 5 && currentGrade <= 9) {
                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getContext().getResources().getStringArray(R.array.forParentCoursesMiddleSchool));
                postTeacherRequestPopupWindowBinding.forParentCourses.setAdapter(adapter);
            }

            else {
                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getContext().getResources().getStringArray(R.array.forParentCoursesHighSchool));
                postTeacherRequestPopupWindowBinding.forParentCourses.setAdapter(adapter);
            }
        }
    }

    @Override
    public void getChildrenResult(int flag, JSONArray children) {
        if(flag == 1){
            if(!childrenSpinnerList.isEmpty()){
                childrenSpinnerList.clear();
            }
            if(!parentChildrenList.isEmpty()){
                parentChildrenList.clear();
            }

            for(int i=0;i<children.length();i++){
                try {
                    JSONObject jsonObject = children.getJSONObject(i);
                    int childId = jsonObject.getInt("childId");
                    String childName = jsonObject.getString("childName");
                    String childAge = jsonObject.getString("childAge");
                    int childGenderVal = jsonObject.getInt("childGender");
                    parentChildrenList.add(new Children(childName,childAge,childGenderVal,jsonObject.getInt("childGrade")));
                    childrenSpinnerList.add(new CustomChildData(childId,childName,jsonObject.getInt("childGrade"),childGenderVal));
                }
                catch(JSONException e){
                    throw new RuntimeException(e);
                }
            }
            showEditCardItemDialog();
        }
        else {
            MyAlertDialog.errorDialog(getContext());
        }
    }

    @Override
    public void onResultParentInformation(int resultFlag, JSONArray parentInformation) {
        if(resultFlag == -1){
            // error
        }
        else if(resultFlag == -2){
            // No data
        }
        else if(resultFlag == -3){
            // connection error
        }
        else if(resultFlag == -4){
            // volley error
        }
        else {
            try {
                if(parentInformation.length() == 1){
                    JSONObject jsonObject = parentInformation.getJSONObject(0);
                    firstName = jsonObject.getString("firstname").toLowerCase();
                    lastName = jsonObject.getString("lastname").toLowerCase();
                    firstName = firstName.substring(0,1).toUpperCase()+firstName.substring(1).toLowerCase();
                    lastName = lastName.substring(0,1).toUpperCase()+lastName.substring(1).toLowerCase();
                    birthDate = jsonObject.getString("birthDate");
                    int genderVal = jsonObject.getInt("gender");
                    gender = "Male";
                    if(genderVal == 0)
                        gender = "Female";
                    idNumber = jsonObject.getString("idNumber");
                    String city = jsonObject.getString("city");
                    String country = jsonObject.getString("country");
                    if(!parentAddress.isEmpty())
                        parentAddress.clear();
                    parentAddress.add(new Address(city,country));
                    String parentPhoneNumber = jsonObject.getString("phoneNumber");
                    phoneNumbersList.add(parentPhoneNumber);
                    List<String> phoneList = new ArrayList<>();
                    phoneList.add(parentPhoneNumber);
                    phoneNumbersList = phoneList;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           binding.progressBarLayout.setVisibility(View.GONE);
                           binding.overlayView.setVisibility(View.GONE);
                            showClickedCardDialog();
                        }
                    },1500);
                    //showTeacherMatchDialog(teacherMatchModelTemp,parentFirstName+" "+parentLastName,phoneList);
                }
                else {
                    int genderVal = 1 ;
                    gender = "Male";
                    List<Address> addressList = new ArrayList<>();
                    List<String> phoneNumberList = new ArrayList<>();
                    for(int i=0;i<parentInformation.length();i++){
                        JSONObject jsonObject = parentInformation.getJSONObject(i);
                        if(i==0){
                            firstName = jsonObject.getString("firstname").toLowerCase();
                            firstName = firstName.substring(0,1).toUpperCase()+firstName.substring(1).toLowerCase();
                            lastName = jsonObject.getString("lastname").toLowerCase();
                            lastName = lastName.substring(0,1).toUpperCase()+lastName.substring(1).toLowerCase();

                            birthDate = jsonObject.getString("birthDate");
                            genderVal = jsonObject.getInt("gender");
                            if(genderVal == 0)
                                gender = "Female";
                            idNumber = jsonObject.getString("idNumber");
                        }
                        String city = jsonObject.getString("city");
                        String country = jsonObject.getString("country");
                        addressList.add(new Address(city,country));
                        String parentPhoneNumber = jsonObject.getString("phoneNumber");
                        if(!checkAddressExistsInList(addressList,new Address(city,country))){
                            addressList.add(new Address(city,country));
                        }
                        if(!checkPhoneExistsInList(phoneNumberList , parentPhoneNumber)){
                            phoneNumberList.add(parentPhoneNumber);
                        }
                    }
                    phoneNumbersList = phoneNumberList;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.progressBarLayout.setVisibility(View.GONE);
                            binding.overlayView.setVisibility(View.GONE);
                            showClickedCardDialog();
                           // showTeacherMatchDialog(teacherMatchModelTemp,tempFirstName+" "+tempLastName,tempPhoneList);
                        }
                    },1500);
                }
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }

    }

    private boolean checkAddressExistsInList(List<Address> addressList,Address address){
        return addressList.contains(address);
    }
    private boolean checkPhoneExistsInList(List<String> phoneList,String phoneNumber){
        return phoneList.contains(phoneNumber);
    }

    @Override
    public void onDataUpdate(int flag) {
        if(flag == 1){
           // MyAlertDialog.showDialogForDone(getContext(),"Done","Data Updated ..");
            // ToDo (ERROR THE DATA IS NOT UPDATED ...)
            //binding.tempProgressBar.setVisibility(View.VISIBLE);
            postTeacherRequestPopupWindowBinding.progressBarLayout.setVisibility(View.VISIBLE);
            this.requestModel = tmm ;
            clickedCardDialog.dismiss();
          //  database.getParentInformation(email,this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //binding.tempProgressBar.setVisibility(View.GONE);
                    postTeacherRequestPopupWindowBinding.progressBarLayout.setVisibility(View.GONE);
                    updateParentPostedRequestDialog.dismiss();
                    database.getParentPostedMatchingInformation(email,ParentFragment.this);
                }
            },3000);
        }
        else {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"ERROR","An Error occurred please try again later ..");
        }
    }

    @Override
    public void onParentPostDeleted(int flag) {
        if(flag == 3){
            deleteParentPostedTeacherMatchingDialog.dismiss();
            clickedCardDialog.dismiss();
            database.getParentPostedMatchingInformation(email,this);
        }
        else if(flag == -2){

        }
        else if(flag == 0){

        }
        else {

        }
    }

    private void setParentRequestsForTeacher(){
        btn1Clicked=false;
        btn2Clicked=false;
        browseTeacherPostedRequestsForParent = true;
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.myReceivedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        database.getAllTeacherPostedRequestsForParent(this);
    }

    @Override
    public void onAllTeacherPostedRequestsForParentFetched(int flag, JSONArray teacherPostsData) throws JSONException {
        if(flag == -1){

        }
        else if(flag == -2){

        }
        else if(flag == 0){

        }
        else if(flag == 1){
            if(!teacherPostedRequestsForParentList.isEmpty()){
                teacherPostedRequestsForParentList.clear();
            }
            List<String> teacherPhoneNumbersList = new ArrayList<>();
            List<Address> teacherAddressesList = new ArrayList<>();
            for(int i = teacherPostsData.length() - 1 ; i >= 0 ; i--){
                JSONObject jsonObject = teacherPostsData.getJSONObject(i);
                String teacherEmail = jsonObject.getString("teacherEmail");
                String teacherFirstName = jsonObject.getString("firstname");
                teacherFirstName = teacherFirstName.substring(0,1).toUpperCase()+teacherFirstName.substring(1).toLowerCase();
                String teacherLastName = jsonObject.getString("lastname");
                teacherLastName = teacherLastName.substring(0,1).toUpperCase()+teacherLastName.substring(1).toLowerCase();
                int teacherGender = jsonObject.getInt("gender");
                String teacherBirthDate = jsonObject.getString("birthDate");
                String idNumber = jsonObject.getString("idNumber");
                int studentOrGraduate = jsonObject.getInt("studentOrGraduate");
                String expectedGraduationYear = jsonObject.getString("expectedGraduationYear");
                String teacherCollege = jsonObject.getString("college");
                String teacherField = jsonObject.getString("field");
                String teacherStaticAvailability = jsonObject.getString("availability");
                String educationLevel = jsonObject.getString("educationLevel");
                String teacherPostStartTime = jsonObject.getString("startTime");
                String teacherPostEndTime = jsonObject.getString("endTime");
                int teacherPostId = jsonObject.getInt("postId");
                String selectedCourses = jsonObject.getString("courses");
                int duration = jsonObject.getInt("duration");
                String teacherAvailabilityForJob = jsonObject.getString("availabilityForJob");
                String location = jsonObject.getString("location");
                String teachingMethod = jsonObject.getString("teachingMethod");
                if(i==teacherPostsData.length() - 1){
                    String teacherPhoneNumbers = jsonObject.getString("phoneNumbers");
                    if(teacherPhoneNumbers.contains(",")){
                        String[] splitTeacherPhoneNumbers = teacherPhoneNumbers.split(",");
                        teacherPhoneNumbersList.addAll(Arrays.asList(splitTeacherPhoneNumbers));
                    }
                    else
                        teacherPhoneNumbersList.add(teacherPhoneNumbers);

                    String teacherAddresses = jsonObject.getString("addresses");
                    if(teacherAddresses.contains("|")){
                        String[] teacherAddressesSplit = teacherAddresses.split("\\|");
                        for(int j = 0;j<teacherAddressesSplit.length;j++){
                            String[] split = teacherAddressesSplit[j].split(",");
                            teacherAddressesList.add(new Address(split[0].trim(),split[1].trim()));
                        }
                    }
                    else{
                        String[] split = teacherAddresses.split(",");
                        teacherAddressesList.add(new Address(split[0].trim(),split[1].trim()));
                    }
                }
                teacherPostedRequestsForParentList.add(new TeacherPostRequest(teacherPostId,teacherEmail,selectedCourses,educationLevel,
                        duration+"",teacherAvailabilityForJob,location,teachingMethod,
                        new Teacher(teacherEmail,idNumber,studentOrGraduate+"",expectedGraduationYear,teacherCollege,
                                teacherField,teacherGender,teacherBirthDate,teacherStaticAvailability,educationLevel,teacherAddressesList,
                                teacherPhoneNumbersList,teacherFirstName+" "+teacherLastName
                                ),teacherPostStartTime,teacherPostEndTime));
            }
            setTeacherPostsForParent();
        }
    }

    private void setTeacherPostsForParent(){
        if(teacherPostedRequestsForParentList.isEmpty()){
            binding.postedRequestsRecyclerView.setVisibility(View.GONE);
            binding.noTeacherPostedRequests.setVisibility(View.VISIBLE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noChildrenCourses.setVisibility(View.GONE);
        }
        else {
            TeacherPostedRequestsAdapter teacherPostedRequestsAdapter =
                    new TeacherPostedRequestsAdapter(teacherPostedRequestsForParentList,getContext(),this);
            binding.postedRequestsRecyclerView.setAdapter(teacherPostedRequestsAdapter);
            binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
            binding.noTeacherPostedRequests.setVisibility(View.GONE);
            binding.noChildrenCourses.setVisibility(View.GONE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.refreshRecyclerView.setRefreshing(false);
        }
    }

    @Override
    public void onTeacherPostClicked(TeacherPostRequest teacherPostRequest) {
        if(getContext() != null){
            Dialog teacherPostedCardDialog = new Dialog(getContext());
            TeacherPostedRequestsCardToShowToParentBinding teacherPostedRequestsCardToShowToParentBinding = TeacherPostedRequestsCardToShowToParentBinding.inflate(LayoutInflater.from(getContext()));
            teacherPostedCardDialog.setContentView(teacherPostedRequestsCardToShowToParentBinding.getRoot());
            teacherPostedCardDialog.setCancelable(false);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(teacherPostedCardDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            teacherPostedCardDialog.getWindow().setAttributes(layoutParams);
            teacherPostedCardDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            if(teacherPostedCardDialog.getWindow() != null)
                teacherPostedCardDialog.getWindow().setLayout(1300,ViewGroup.LayoutParams.WRAP_CONTENT);
            teacherPostedCardDialog.show();

            teacherPostedRequestsCardToShowToParentBinding.closeImageView.setOnClickListener(c->{
                teacherPostedCardDialog.dismiss();
            });

            teacherPostedRequestsCardToShowToParentBinding.teacherNameTextView.setText(teacherPostRequest.getTeacherData().getTeacherName());
            teacherPostedRequestsCardToShowToParentBinding.teacherEmailTextView.setText(teacherPostRequest.getTeacherEmail());

            StringBuilder teacherPhones = new StringBuilder();
            List<String> tempList = teacherPostRequest.getTeacherData().getPhoneNumbersList();
            for(int i=0;i < tempList.size();i++){
                if(i + 1 != tempList.size()){
                    teacherPhones.append(tempList.get(i)).append("\n");
                }
                else {
                    teacherPhones.append(tempList.get(i));
                }
            }
            teacherPostedRequestsCardToShowToParentBinding.teacherPhoneNumberTextView.setText(teacherPhones);
            teacherPostedRequestsCardToShowToParentBinding.coursesTextView.setText(teacherPostRequest.getCourses());
            teacherPostedRequestsCardToShowToParentBinding.teachingMethodTextView.setText(teacherPostRequest.getTeachingMethod());
            teacherPostedRequestsCardToShowToParentBinding.durationTextView.setText(teacherPostRequest.getDuration());
            teacherPostedRequestsCardToShowToParentBinding.timeTextView.setText(String.format("%s - %s", teacherPostRequest.getStartTime(), teacherPostRequest.getEndTime()));
            teacherPostedRequestsCardToShowToParentBinding.locationTextView.setText(teacherPostRequest.getLocation());
            teacherPostedRequestsCardToShowToParentBinding.collegeTextView.setText(teacherPostRequest.getTeacherData().getField());
            String teacherGender = "Male";
            if(teacherPostRequest.getTeacherData().getGender() == 0){
                teacherGender = "Female";
            }
            teacherPostedRequestsCardToShowToParentBinding.teacherGenderTextView.setText(teacherGender);

            String teacherBirthDate = teacherPostRequest.getTeacherData().getBirthDate();
            String age = calculateAge(teacherBirthDate);
            teacherPostedRequestsCardToShowToParentBinding.teacherAgeTextView.setText(String.format("%s Years", age));
        }
    }
    private String calculateAge(String teacherBirthDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(teacherBirthDate,formatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears()+"";
    }
}