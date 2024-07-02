package com.example.graduationproject.ui.parentUi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.CustomSpinnerAdapter;
import com.example.graduationproject.adapters.ParentPostedRequestsAdapter;
import com.example.graduationproject.adapters.TeacherPostedRequestsAdapter;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.ConfirmDeleteDialogLayoutBinding;
import com.example.graduationproject.databinding.DialogParentPostedRequestCardBinding;
import com.example.graduationproject.databinding.DialogSendRequestToTeacherLayoutBinding;
import com.example.graduationproject.databinding.FragmentParentBinding;
import com.example.graduationproject.databinding.ParentFilterLayoutBinding;
import com.example.graduationproject.databinding.TeacherPostedRequestsCardToShowToParentBinding;
import com.example.graduationproject.databinding.UpdateParentPostedRequestBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.GetParentChildren;
import com.example.graduationproject.listeners.GetParentChildrenForRequest;
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
import com.example.graduationproject.models.ParentRequestToSend;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.models.TeacherPostRequest;
import com.example.graduationproject.utils.FilterData;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

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
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParentFragment extends Fragment implements ParentListenerForParentPostedRequests,
        ParentPostRequestClickListener,
        GetParentChildren,
        ParentInformationListener,
        UpdateTeacherPostedRequestListener,
        ParentPostRequestDeleteListener,
        OnAllTeacherPostedRequestsForParentListener,
        TeacherPostRequestClickListener, GetParentChildrenForRequest {

    private String email;
    private String firstName;
    private String lastName;
    private String idNumber ;
    List<Address> parentAddress = new ArrayList<>() ;
    List<String> phoneNumbersList = new ArrayList<>();
    private FragmentParentBinding binding;
    private Database database;
    private ArrayList<TeacherMatchModel> parentPostedRequestsList = new ArrayList<>();

    private TeacherPostedRequestsAdapter teacherPostedRequestsAdapter;

    private boolean myCoursesBtnForParent = false;
    private boolean myPostedRequestsBtnForParent = true;
    private int currentClickedPostedCardId = 0 ;

    private boolean isBroadcastReceiverRegistered = false;
    private Dialog updateParentPostedRequestDialog ;

    private DialogParentPostedRequestCardBinding dialogParentPostedRequestCardBinding;
    private UpdateParentPostedRequestBinding postTeacherRequestPopupWindowBinding;
    private final List<CustomChildData> childrenSpinnerList = new ArrayList<>();
    private final List<Children> parentChildrenList = new ArrayList<>();
    TeacherMatchModel requestModelTemp ;
    private int selectedChildId;
    private String selectedChildName,selectedChildGrade ;

    private  TeacherMatchModel requestModel;

    List<String> coursesList = new ArrayList<>();
    TeacherMatchModel tmm ;

    private String startTime ="12:00 PM", endTime="12:00 PM";
    private Dialog clickedCardDialog;
    private Dialog deleteParentPostedTeacherMatchingDialog;
    private boolean browseTeacherPostedRequestsForParent = false;
    private boolean myReceivedRequestsForParent = false;

    private final List<TeacherPostRequest> teacherPostedRequestsForParentList = new ArrayList<>();

    private TeacherPostRequest tempTeacherPostRequestForSendingRequest ;

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
    private Dialog filterDialog ;
    private ParentFilterLayoutBinding parentFilterLayoutBinding ;
    private final List<String> filterSelectedLocationList = new ArrayList<>();
    private final List<String> filterSelectedCoursesList = new ArrayList<>();
    private final List<String> filterSelectedGenderList = new ArrayList<>();
    private final List<String> filterSelectedGradeList = new ArrayList<>();
    private final FilterData filterDataObject = new FilterData() ;
    private final List<String> filterTeachingMethodList = new ArrayList<>();
    ParentPostedRequestsAdapter parentPostedRequests ;
    Dialog teacherPostedCardDialog;
    TeacherPostedRequestsCardToShowToParentBinding teacherPostedRequestsCardToShowToParentBinding;
    Dialog sendRequestToTeacherDialog ;
    DialogSendRequestToTeacherLayoutBinding dialogSendRequestToTeacherLayoutBinding;
    int selectedChildGender ;


    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if("NOTIFY_PARENT_FRAGMENT_NEW_TEACHER_MATCH_MODEL_ADDED".equals(intent.getAction())){
                database.getParentPostedMatchingInformation(email,ParentFragment.this);
            }
            else if("SHOW_RECEIVED_REQUESTS_FOR_PARENT".equals(intent.getAction())){
                myCoursesBtnForParent = false;
                myPostedRequestsBtnForParent = false;
                browseTeacherPostedRequestsForParent=false;
                myReceivedRequestsForParent =true;
                //ToDo(Show parent received requests ..);
            }

            else if("UPDATE_POSTED_DATA_FOR_PARENT".equals(intent.getAction())){
                myCoursesBtnClicked();
            }
            else if("SHOW_TEACHER_POSTED_REQUESTS_FOR_PARENT".equals(intent.getAction())){
                myCoursesBtnForParent = false;
                myPostedRequestsBtnForParent = false;
                browseTeacherPostedRequestsForParent=true;
                myReceivedRequestsForParent =false;
                binding.filterLayout.setVisibility(View.VISIBLE);
                setPostedTeacherRequestsForParent();
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
        binding.filterLayout.setVisibility(View.GONE);
        setMyPostedRequestsAdapter();
        binding.myPostedRequestsBtn.setOnClickListener(x->{
            myCoursesBtnClicked();
        });

        binding.myReceivedRequestsBtn.setOnClickListener(c->{
            myPostedRequestsBtnClicked();
        });

        binding.refreshRecyclerView.setOnRefreshListener(()->{
            if(myCoursesBtnForParent && !myPostedRequestsBtnForParent && !browseTeacherPostedRequestsForParent && !myReceivedRequestsForParent){
                refreshAction(1);
            }
            else if(!myCoursesBtnForParent && myPostedRequestsBtnForParent && !browseTeacherPostedRequestsForParent && !myReceivedRequestsForParent){
                refreshAction(2);
            }
            else if(!myCoursesBtnForParent && !myPostedRequestsBtnForParent && browseTeacherPostedRequestsForParent && !myReceivedRequestsForParent){
                refreshAction(3); // update browse teacher posted list for parent ..
            }
            else if(!myCoursesBtnForParent && !myPostedRequestsBtnForParent && !browseTeacherPostedRequestsForParent && myReceivedRequestsForParent){
                refreshAction(4); // update posted parent requests for know list for parent ..
                // it will be received requests and btn2 will be posted requests
            }
        });
        binding.filterLayout.setOnClickListener(z->{
            showFilterDialogForParent();
        });
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

    private void myCoursesBtnClicked(){
        binding.parentFragmentSearch.clearFocus();
        binding.parentFragmentSearch.setQuery(null,false);
        binding.filterLayout.setVisibility(View.GONE);
        myCoursesBtnForParent = true;
        myPostedRequestsBtnForParent = false;
        browseTeacherPostedRequestsForParent = false ;
        myReceivedRequestsForParent = false;
        setMyCoursesRequestsAdapter();
    }

    private void myPostedRequestsBtnClicked(){
        binding.parentFragmentSearch.clearFocus();
        binding.parentFragmentSearch.setQuery(null,false);
        binding.filterLayout.setVisibility(View.GONE);
        myCoursesBtnForParent = false;
        myPostedRequestsBtnForParent = true;
        browseTeacherPostedRequestsForParent = false ;
        myReceivedRequestsForParent = false ;
        setMyPostedRequestsAdapter();
    }

    private void setMyCoursesRequestsAdapter(){
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_active);
        binding.myReceivedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);

        binding.parentFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInParentPostedRequestsForParent(newText);
                return true;
            }
        });
        //ToDo(Here to get the parent courses for children ..)
        // data.getMyCourses(email,this)
    }

    private void setMyPostedRequestsAdapter(){

        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.myReceivedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_active);

        binding.parentFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInParentPostedRequestsForParent(newText);
                return true;
            }
        });
        database.getParentPostedMatchingInformation(email,this);
    }



    private void searchInParentPostedRequestsForParent(String str){
        String text = str.toLowerCase().trim();
        List<TeacherMatchModel> teacherMatchModelList = new ArrayList<>();
        for(TeacherMatchModel model : parentPostedRequestsList){
            if((""+model.getCustomChildData().getChildGrade()).toLowerCase().trim().contains(text)
                    || model.getCustomChildData().getChildName().toLowerCase().trim().contains(text)
                    || model.getChoseDays().toLowerCase().trim().contains(text) || model.getCourses().toLowerCase().trim().contains(text)
                    || model.getLocation().toLowerCase().trim().contains(text) || model.getTeachingMethod().toLowerCase().trim().contains(text)
                    || model.getStartTime().toLowerCase().trim().contains(text) || model.getEndTime().toLowerCase().trim().contains(text)
                    || (model.getCustomChildData().getChildAge()+"").toLowerCase().trim().contains(text) || (""+model.getCustomChildData().getGender()).toLowerCase().trim().contains(text)){
                teacherMatchModelList.add(model);
            }
        }
        if(teacherMatchModelList.isEmpty()){
            binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
            binding.noChildrenCourses.setVisibility(View.GONE);
            binding.noTeacherPostedRequests.setVisibility(View.GONE);
            binding.postedRequestsRecyclerView.setVisibility(View.GONE);
        }
        else {
            //postedTeacherRequestsAdapter.filteredList(teacherMatchModelList);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noChildrenCourses.setVisibility(View.GONE);
            binding.noTeacherPostedRequests.setVisibility(View.GONE);
            binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
            parentPostedRequests.filteredList(teacherMatchModelList);
        }
    }

    private void refreshAction(int flag){
        if(flag == 1){
            // get my posted requests
        }
        else if(flag == 2) {
            database.getParentPostedMatchingInformation(email,this);
        }
        else if(flag == 3){
            database.getAllTeacherPostedRequestsForParent(this);
        }
        else if(flag == 4){
            //
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
                        double priceMin = jsonObject.getDouble("priceMin");
                        double priceMax = jsonObject.getDouble("priceMax");
                        String startDate = jsonObject.getString("startDate");
                        String endDate = jsonObject.getString("endDate");
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

                        tempTeacherMatchModelList.add(new TeacherMatchModel(matchingId,new CustomChildData(childId,childName,childGrade,childGender,childAge),
                                matchingChoseDays,courses,location,
                                teachingMethod,startTime,endTime,priceMin,priceMax,startDate,endDate,new Parent(email,idNumber,firstName,lastName,parentBirthDate,parentId,parentAddress,phoneNumbersList)));
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
            assert getView() != null;
            parentPostedRequests = new ParentPostedRequestsAdapter(parentPostedRequestsList, getContext(), this);
            /*binding.postedRequestsRecyclerView.setItemAnimator(new DefaultItemAnimator(){
                @Override
                public void onAddFinished(RecyclerView.ViewHolder item) {
                    super.onAddFinished(item);
                    item.itemView.setAlpha(0);
                    item.itemView.animate().alpha(1).setDuration(2000).start();
                }
            });*/
            binding.postedRequestsRecyclerView.setAdapter(parentPostedRequests);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
            Snackbar.make(getView(), "Your Posted Data Updated", Snackbar.LENGTH_SHORT).setDuration(500).show();
            // parentPostedRequests.filteredList(tempTeacherMatchModelList);
        }
        binding.refreshRecyclerView.setRefreshing(false);
    }

    @Override
    public void onClick(TeacherMatchModel requestModel) {
        currentClickedPostedCardId = requestModel.getMatchingId();
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        binding.overlayView.setVisibility(View.VISIBLE);
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
            dialogParentPostedRequestCardBinding.priceTextView.setText(requestModel.getPriceMinimum()+"$"+" - "+requestModel.getPriceMaximum()+"$");
            dialogParentPostedRequestCardBinding.dateTextView.setText(requestModel.getStartDate()+"  -  "+requestModel.getEndDate());

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
            postTeacherRequestPopupWindowBinding.priceFromEditText.setText(requestModel.getPriceMinimum()+"");
            postTeacherRequestPopupWindowBinding.priceToEditText.setText(requestModel.getPriceMaximum()+"");
            postTeacherRequestPopupWindowBinding.startDateEdtText.setText(requestModel.getStartDate());
            postTeacherRequestPopupWindowBinding.endDateEdtText.setText(requestModel.getEndDate());
            //postTeacherRequestPopupWindowBinding.startDateEdtText.setText(requestModel.getStartDate());
           // postTeacherRequestPopupWindowBinding.startDateEdtText.setText(requestModel.getEndDate());



            postTeacherRequestPopupWindowBinding.startTimeEdtText.setOnClickListener(c->{
                setStartTime();
            });

            postTeacherRequestPopupWindowBinding.endTimeEdtText.setOnClickListener(j->{
                setEndTime();
            });
            postTeacherRequestPopupWindowBinding.startDateEdtText.setOnClickListener(z->{
                setStartDate();
            });
            postTeacherRequestPopupWindowBinding.endDateEdtText.setOnClickListener(c->{
                setEndDate();
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
            if(!checkStartAndEndDate()){
                MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Wrong Date","Please choose a valid start and end dates in future .. ");
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
                        double priceMin = Double.parseDouble(postTeacherRequestPopupWindowBinding.priceFromEditText.getText().toString());
                        double priceMax = Double.parseDouble(postTeacherRequestPopupWindowBinding.priceToEditText.getText().toString());
                        String startDate = postTeacherRequestPopupWindowBinding.startDateEdtText.getText().toString();
                        String endDate = postTeacherRequestPopupWindowBinding.endDateEdtText.getText().toString();
                        if(postTeacherRequestPopupWindowBinding.priceFromEditText.getText().toString().isEmpty() || postTeacherRequestPopupWindowBinding.priceToEditText.getText().toString().isEmpty()||priceMin < 1.0 ||priceMax > 100.0 || priceMin >= priceMax){
                            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Invalid Price","Please Choose A Valid Price Value ..\n0.0 - 100 $");
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
                                    teachingMethodStr,startTime,endTime,priceMin,priceMax,startDate,endDate);
                            database.updateParentPostedRequest(email,tmm,this);

                        }
                    }
                }
            }
        }
    }

    private boolean checkStartAndEndDate(){
        try {
            return areDatesValid(postTeacherRequestPopupWindowBinding.startDateEdtText.getText().toString(), postTeacherRequestPopupWindowBinding.endDateEdtText.getText().toString());
        } catch (ParseException e) {
            return false ;
        }
    }



    public  boolean areDatesValid(String startDateStr, String endDateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        Date startDate = sdf.parse(startDateStr);
        Date endDate = sdf.parse(endDateStr);

        Date currentDate = new Date();

        if (startDate.after(currentDate) && endDate.after(currentDate)) {
            return startDate.before(endDate);
        }

        return false;
    }

    private void setEndDate(){
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select End Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
            postTeacherRequestPopupWindowBinding.endDateEdtText.setText(date);
        });
        materialDatePicker.show(requireActivity().getSupportFragmentManager(),"");

    }

    private void setStartDate(){
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Start Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
            postTeacherRequestPopupWindowBinding.startDateEdtText.setText(date);
        });
        materialDatePicker.show(requireActivity().getSupportFragmentManager(),"");
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
        String[] splittedCourses = requestModel.getCourses().trim().split(",");
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
                String gender;
                String birthDate;
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
            postTeacherRequestPopupWindowBinding.progressBarLayout.setVisibility(View.VISIBLE);
            this.requestModel = tmm ;
            clickedCardDialog.dismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    postTeacherRequestPopupWindowBinding.progressBarLayout.setVisibility(View.GONE);
                    updateParentPostedRequestDialog.dismiss();
                    database.getParentPostedMatchingInformation(email,ParentFragment.this);
                }
            },1000);
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

    private void setPostedTeacherRequestsForParent(){
        myCoursesBtnForParent=false;
        myPostedRequestsBtnForParent=false;
        browseTeacherPostedRequestsForParent = true;
        myReceivedRequestsForParent = false;
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
                double price = jsonObject.getDouble("price");
                String startDate = jsonObject.getString("startDate");
                String endDate = jsonObject.getString("endDate");
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
                                ),teacherPostStartTime,teacherPostEndTime,price,startDate,endDate));
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
            assert getView() != null;
            teacherPostedRequestsAdapter =
                    new TeacherPostedRequestsAdapter(teacherPostedRequestsForParentList,getContext(),this);
            binding.postedRequestsRecyclerView.setAdapter(teacherPostedRequestsAdapter);
            binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
            binding.noTeacherPostedRequests.setVisibility(View.GONE);
            binding.noChildrenCourses.setVisibility(View.GONE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.refreshRecyclerView.setRefreshing(false);

            binding.parentFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchInTeacherPostedDataForParent(newText);
                    return true;
                }
            });

            Snackbar.make(getView(), "Teacher Posted Requests Updated", Snackbar.LENGTH_SHORT).setDuration(500).show();
        }
    }

    private void searchInTeacherPostedDataForParent(String textToSearch){
        textToSearch = textToSearch.toLowerCase();
        List<TeacherPostRequest> teacherPostRequestSearchList = new ArrayList<>();
        for(TeacherPostRequest model : teacherPostedRequestsForParentList){
            int flag = 0 ;
            if(model.getLocation().toLowerCase().contains(textToSearch) || model.getCourses().toLowerCase().contains(textToSearch) ||
            model.getEducationLevel().toLowerCase().contains(textToSearch) || model.getEndTime().toLowerCase().equalsIgnoreCase(textToSearch)||
            model.getAvailability().toLowerCase().contains(textToSearch) || model.getDuration().toLowerCase().contains(textToSearch) ||
            model.getStartTime().toLowerCase().contains(textToSearch) || model.getTeacherEmail().toLowerCase().contains(textToSearch) ||
            model.getTeachingMethod().toLowerCase().contains(textToSearch) || model.getTeacherData().getEducationalLevel().toLowerCase().contains(textToSearch) ||
            model.getTeacherData().getTeacherName().toLowerCase().contains(textToSearch) || model.getTeacherData().getAvailability().toLowerCase().contains(textToSearch) || model.getTeacherData().getField().toLowerCase().contains(textToSearch)){
                Log.d("Model.Location ---> "+model.getLocation(),"Model.Location ---> "+model.getLocation());
                flag = 1 ;
                teacherPostRequestSearchList.add(model);
            }

            else if(model.getTeacherData().getGender() == 1 && textToSearch.equalsIgnoreCase("Male"))
                teacherPostRequestSearchList.add(model);
            else if(model.getTeacherData().getGender() == 0 && textToSearch.equalsIgnoreCase("Female"))
                teacherPostRequestSearchList.add(model);

            if(flag == 0){
                for(Address address : model.getTeacherData().getAddressesList()){
                    if(address.getCity().toLowerCase().contains(textToSearch) || address.getCountry().toLowerCase().contains(textToSearch)){
                        flag = 1 ;
                        teacherPostRequestSearchList.add(model);
                        break;
                    }
                }
            }

            if(flag == 0){
                for(String phone : model.getTeacherData().getPhoneNumbersList()){
                    if (phone.equalsIgnoreCase(textToSearch)){
                        teacherPostRequestSearchList.add(model);
                        break;
                    }
                }
            }
        }
        if(teacherPostRequestSearchList.isEmpty()){
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noTeacherPostedRequests.setVisibility(View.VISIBLE);
            binding.noChildrenCourses.setVisibility(View.GONE);
            binding.postedRequestsRecyclerView.setVisibility(View.GONE);
        }
        else {
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noTeacherPostedRequests.setVisibility(View.GONE);
            binding.noChildrenCourses.setVisibility(View.GONE);
            teacherPostedRequestsAdapter.filteredList(teacherPostRequestSearchList);
            binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTeacherPostClicked(TeacherPostRequest teacherPostRequest) {
        if(getContext() != null){
            teacherPostedCardDialog = new Dialog(getContext());
            teacherPostedRequestsCardToShowToParentBinding = TeacherPostedRequestsCardToShowToParentBinding.inflate(LayoutInflater.from(getContext()));
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
            teacherPostedRequestsCardToShowToParentBinding.choseDaysTextView.setText(teacherPostRequest.getTeacherData().getAvailability());
            teacherPostedRequestsCardToShowToParentBinding.dateTextView.setText(teacherPostRequest.getStartDate()+"  -  "+teacherPostRequest.getEndDate());
            teacherPostedRequestsCardToShowToParentBinding.timeTextView.setText(String.format("%s - %s", teacherPostRequest.getStartTime(), teacherPostRequest.getEndTime()));
            teacherPostedRequestsCardToShowToParentBinding.locationTextView.setText(teacherPostRequest.getLocation());
            teacherPostedRequestsCardToShowToParentBinding.collegeTextView.setText(teacherPostRequest.getTeacherData().getField());
            teacherPostedRequestsCardToShowToParentBinding.priceTextView.setText(teacherPostRequest.getPrice()+"$");
            if(teacherPostRequest.getDuration().equalsIgnoreCase("1")){
                teacherPostedRequestsCardToShowToParentBinding.durationTextView.setText(teacherPostRequest.getDuration()+" Month");
            }
            else {
                teacherPostedRequestsCardToShowToParentBinding.durationTextView.setText(teacherPostRequest.getDuration()+" Months");
            }
            String teacherGender = "Male";
            if(teacherPostRequest.getTeacherData().getGender() == 0){
                teacherGender = "Female";
            }
            teacherPostedRequestsCardToShowToParentBinding.teacherGenderTextView.setText(teacherGender);

            String teacherBirthDate = teacherPostRequest.getTeacherData().getBirthDate();
            String age = calculateAge(teacherBirthDate);
            teacherPostedRequestsCardToShowToParentBinding.teacherAgeTextView.setText(String.format("%s Years", age));
            teacherPostedRequestsCardToShowToParentBinding.educationLevelTextView.setText(teacherPostRequest.getEducationLevel());
            if(teacherPostRequest.getEducationLevel().equalsIgnoreCase("Elementary School"))
                teacherPostedRequestsCardToShowToParentBinding.gradesTextView.setText("1st - 5th Grades");
            else if(teacherPostRequest.getEducationLevel().equalsIgnoreCase("Middle School"))
                teacherPostedRequestsCardToShowToParentBinding.gradesTextView.setText("6th - 10th Grades");
            else if(teacherPostRequest.getEducationLevel().equalsIgnoreCase("High School"))
                teacherPostedRequestsCardToShowToParentBinding.gradesTextView.setText("11th and 12 Grades");
            else
                teacherPostedRequestsCardToShowToParentBinding.gradesTextView.setText("1st  -  12th Grades");

            teacherPostedRequestsCardToShowToParentBinding.sendRequestToTeacherBtn.setOnClickListener(m->{
                sendRequestToTeacherBtnClicked(teacherPostRequest);
            });
        }
    }

    private void sendRequestToTeacherBtnClicked(TeacherPostRequest teacherPostRequest){
        if(getContext() != null){
            tempTeacherPostRequestForSendingRequest = teacherPostRequest ;
            sendRequestToTeacherDialog = new Dialog(getContext());
            dialogSendRequestToTeacherLayoutBinding = DialogSendRequestToTeacherLayoutBinding.inflate(LayoutInflater.from(getContext()));
            sendRequestToTeacherDialog.setContentView(dialogSendRequestToTeacherLayoutBinding.getRoot());
            sendRequestToTeacherDialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(sendRequestToTeacherDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            sendRequestToTeacherDialog.getWindow().setAttributes(layoutParams);
            sendRequestToTeacherDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            if(sendRequestToTeacherDialog.getWindow() != null)
                sendRequestToTeacherDialog.getWindow().setLayout(1300,ViewGroup.LayoutParams.WRAP_CONTENT);
            sendRequestToTeacherDialog.show();

            dialogSendRequestToTeacherLayoutBinding.closeImage.setOnClickListener(c->{
                sendRequestToTeacherDialog.dismiss();
            });

            dialogSendRequestToTeacherLayoutBinding.teacherNameTextView.setText(teacherPostRequest.getTeacherData().getTeacherName());
            dialogSendRequestToTeacherLayoutBinding.coursesTextView.setText(teacherPostRequest.getCourses());
            dialogSendRequestToTeacherLayoutBinding.locationTextView.setText(teacherPostRequest.getLocation());
            dialogSendRequestToTeacherLayoutBinding.priceTextView.setText(teacherPostRequest.getPrice()+"");

            database.getParentChildrenForRequest(email,this);
        }
    }

    private void setChildrenSpinnerData(){
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(),childrenSpinnerList);
        dialogSendRequestToTeacherLayoutBinding.childrenSpinner.setAdapter(adapter);
        dialogSendRequestToTeacherLayoutBinding.childrenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CustomChildData data = (CustomChildData) parent.getItemAtPosition(position);
                String[] splittedString = data.toString().split(",");
                selectedChildId = data.getChildId();
                selectedChildName = splittedString[0].trim();
                selectedChildGrade = splittedString[1].trim();
                selectedChildGender = data.getGender();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<CustomChildData> listOfChildrenForRequest = new ArrayList<>();
        dialogSendRequestToTeacherLayoutBinding.addChildBtn.setOnClickListener(z->{
            if((tempTeacherPostRequestForSendingRequest.getTeacherData().getEducationalLevel().equalsIgnoreCase("Elementary School") && Integer.parseInt(selectedChildGrade) > 0 && Integer.parseInt(selectedChildGrade) <= 5) ||
                    (tempTeacherPostRequestForSendingRequest.getTeacherData().getEducationalLevel().equalsIgnoreCase("Middle School") && Integer.parseInt(selectedChildGrade) > 5 && Integer.parseInt(selectedChildGrade) <= 10)||
                    (tempTeacherPostRequestForSendingRequest.getTeacherData().getEducationalLevel().equalsIgnoreCase("High School") && Integer.parseInt(selectedChildGrade) > 10 && Integer.parseInt(selectedChildGrade) <= 12)||
                    (tempTeacherPostRequestForSendingRequest.getTeacherData().getEducationalLevel().equalsIgnoreCase("Any"))){
                CustomChildData childData = new CustomChildData(selectedChildId,selectedChildName,Integer.parseInt(selectedChildGrade),selectedChildGender);
                if(!checkIfChildAddedToFlexBox(listOfChildrenForRequest,childData)){
                    listOfChildrenForRequest.add(childData);
                    updateRequestFlexBoxForParent(listOfChildrenForRequest);
                }
                else {
                    MyAlertDialog.childWarningAlreadyExists(getContext());
                }
            }
            else {
                if(tempTeacherPostRequestForSendingRequest.getEducationLevel().equalsIgnoreCase("Elementary School"))
                    MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Teacher unavailable","Teacher Can Teach Only 1st - 5th Grades");
                else if(tempTeacherPostRequestForSendingRequest.getEducationLevel().equalsIgnoreCase("Middle School"))
                    MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Teacher unavailable","Teacher Can Teach Only 6th - 10th Grades");
                else if(tempTeacherPostRequestForSendingRequest.getEducationLevel().equalsIgnoreCase("High School"))
                     MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Teacher unavailable","Teacher Can Teach Only 11th - 12th Grades");
            }
        });

        dialogSendRequestToTeacherLayoutBinding.confirmRequestToSendToTeacherBtn.setOnClickListener(p->{
            if(dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout.getChildCount() == 0){
                MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"No child ","Please add at least one child to send the request to teacher ..");
            }
            else {
                if(dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout.getChildCount() == 1){
                    View customView = dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout.getChildAt(0);
                    TextView textView = customView.findViewById(R.id.textViewChildName);
                    String[] split = textView.getText().toString().split(",");
                    int id = Integer.parseInt(split[1].trim());
                    ParentRequestToSend parentRequestToSend = new ParentRequestToSend(tempTeacherPostRequestForSendingRequest.getTeacherPostRequestId(),email,id);
                    // store in database and send notification to teacher ..
                }
                else {
                    String childIds = "";
                    List<Integer> childrenIds = new ArrayList<>();
                    for(int i = 0 ;i < dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout.getChildCount();i++){
                        View customView = dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout.getChildAt(i);
                        TextView textView = customView.findViewById(R.id.textViewChildName);
                        String[] split = textView.getText().toString().split(",");
                        childrenIds.add(Integer.parseInt(split[1].trim()));
                        if(i == 0 ){
                            childIds = split[1].trim();
                        }
                        else {
                            childIds += " , "+Integer.parseInt(split[1].trim());
                        }
                    }
                    ParentRequestToSend parentRequestToSend = new ParentRequestToSend(tempTeacherPostRequestForSendingRequest.getTeacherPostRequestId(),email,childrenIds);

                }
            }
        });
    }

    private void updateRequestFlexBoxForParent(List<CustomChildData> listOfChildren){
        if(dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout.getChildCount() > 0)
            dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout.removeAllViews();
        for(CustomChildData child : listOfChildren){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.custom_child_view,dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout,false);
            TextView childName = customView.findViewById(R.id.textViewChildName);
            ImageView deleteImageView = customView.findViewById(R.id.imageViewDelete);
            childName.setText(child.getChildName()+" , "+child.getChildGrade());
            deleteImageView.setOnClickListener(xs->{
                dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout.removeView(customView);
            });
            Toast.makeText(getContext(), childName.getText().toString(), Toast.LENGTH_SHORT).show();
            dialogSendRequestToTeacherLayoutBinding.childrenFlexBoxLayout.addView(customView);
        }
    }

    private boolean checkIfChildAddedToFlexBox(List<CustomChildData> listOfChildren , CustomChildData customChildData){
        for(CustomChildData child : listOfChildren){
            if(child.getChildId() == customChildData.getChildId())
                return true;
        }
        return false;
    }


    private String calculateAge(String teacherBirthDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(teacherBirthDate,formatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears()+"";
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
            layoutParams.height = 2500;
            filterDialog.getWindow().setAttributes(layoutParams);
            if(filterDialog.getWindow() != null)
                filterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            filterDialog.show();

            parentFilterLayoutBinding.closeImage.setOnClickListener(z->{
                filterDialog.dismiss();
            });

            parentFilterLayoutBinding.filterCancelBtn.setOnClickListener(c->{
                filterDialog.dismiss();
            });

            filterSelectedLocationList.clear();
            filterSelectedGradeList.clear();
            filterSelectedGenderList.clear();
            filterSelectedCoursesList.clear();
            filterTeachingMethodList.clear();


            setLocationFlexBox();
            setCoursesFlexBox();
            setGenderFlexBox();
            setChildGradeFlexBox();
            setTeachingMethodFlexBox();

            parentFilterLayoutBinding.locationEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setFlexBoxEnabled(parentFilterLayoutBinding.locationFlexBox1, s.toString().isEmpty());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            parentFilterLayoutBinding.coursesEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setFlexBoxEnabled(parentFilterLayoutBinding.coursesFlexBox, s.toString().isEmpty());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });



            parentFilterLayoutBinding.filterConfirmBtn.setOnClickListener(Z->{
                updateFilteredRecyclerView();
                filterDialog.dismiss();
            });
        }
    }



    private void updateFilteredRecyclerView(){
        String locationEditTextStr = parentFilterLayoutBinding.locationEditText.getText().toString();
        String coursesEditTextStr = parentFilterLayoutBinding.coursesEditText.getText().toString();

        if(getView() != null){
            if(!filterSelectedLocationList.isEmpty() &&
                    filterSelectedCoursesList.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationList(teacherPostedRequestsForParentList,filterSelectedLocationList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }

            else if(filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() && filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() && filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnCoursesList(teacherPostedRequestsForParentList,filterSelectedCoursesList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }

            else if(!filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationListAndCoursesList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterSelectedCoursesList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }
            else if(!filterSelectedLocationList.isEmpty() &&
                    filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationListAndGenderList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterSelectedGenderList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }

            else if(!filterSelectedLocationList.isEmpty() &&
                    filterSelectedCoursesList.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationListAndGradeList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterSelectedGradeList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }

            else if(!filterSelectedLocationList.isEmpty() &&
                    filterSelectedCoursesList.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationListAndTeachingMethodList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }
            else if(!filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationListCoursesListAndGenderList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterSelectedCoursesList,filterSelectedGenderList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }

            else if(!filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationListCoursesListAndGradeList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterSelectedCoursesList,filterSelectedGradeList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }

            else if(!filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){
                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationListCoursesListAndTeachingMethodList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterSelectedCoursesList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }


            else if(!filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){
                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationListCoursesListGenderListAndGradeList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterSelectedCoursesList,filterSelectedGenderList,filterSelectedGradeList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }

            else if(!filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnLocationListCoursesListGenderListAndTeachingMethodList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterSelectedCoursesList,filterSelectedGenderList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }
            else if(!filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnAllList(teacherPostedRequestsForParentList,filterSelectedLocationList,filterSelectedCoursesList,filterSelectedGenderList,filterSelectedGradeList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }

            else if(filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedCoursesAndGenderList(teacherPostedRequestsForParentList,filterSelectedCoursesList,filterSelectedGenderList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }

            else if(filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedCoursesAndGradeList(teacherPostedRequestsForParentList,filterSelectedCoursesList,filterSelectedGradeList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }

            else if(filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedCoursesAndTeachingMethodList(teacherPostedRequestsForParentList,filterSelectedCoursesList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }

            else if(filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedCoursesAndGenderListAndGradeList(teacherPostedRequestsForParentList,filterSelectedCoursesList,filterSelectedGenderList,filterSelectedGradeList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }

            else if(filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedCoursesAndGenderListAndTeachingMethodList(teacherPostedRequestsForParentList,filterSelectedCoursesList,filterSelectedGenderList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }

            else if(filterSelectedLocationList.isEmpty() &&
                    !filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedCoursesAndGenderListGradeAndTeachingMethodList(teacherPostedRequestsForParentList,filterSelectedCoursesList,filterSelectedGenderList,filterSelectedGradeList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }

            else if(filterSelectedLocationList.isEmpty() &&
                    filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnGenderListGradeList(teacherPostedRequestsForParentList,filterSelectedGenderList,filterSelectedGradeList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }

            else if(filterSelectedLocationList.isEmpty() &&
                    filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnGenderListTeachingMethodList(teacherPostedRequestsForParentList,filterSelectedGenderList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }

            else if(filterSelectedLocationList.isEmpty() &&
                    filterSelectedCoursesList.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnGenderListGradeTeachingMethodList(teacherPostedRequestsForParentList,filterSelectedGenderList,filterSelectedGradeList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }

            }
            else if(filterSelectedLocationList.isEmpty() &&
                    filterSelectedCoursesList.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                if(browseTeacherPostedRequestsForParent){
                    List<TeacherPostRequest> filteredList =
                            filterDataObject.filterBasedOnGradeTeachingMethodList(teacherPostedRequestsForParentList,filterSelectedGradeList,filterTeachingMethodList);
                    if(filteredList.isEmpty()){
                        binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                        binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                        binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                        Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                    else{
                        binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                        binding.noPostedRequestTextView.setVisibility(View.GONE);
                        binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        teacherPostedRequestsAdapter.filteredList(filteredList);
                        Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                    }
                }
            }


            if(!locationEditTextStr.isEmpty() && coursesEditTextStr.isEmpty() && filterSelectedGenderList.isEmpty() && filterSelectedGradeList.isEmpty() && filterTeachingMethodList.isEmpty()){
                filterBasedOnLocationNoList(locationEditTextStr);
            }


            else if(locationEditTextStr.isEmpty() && !coursesEditTextStr.isEmpty() && filterSelectedGenderList.isEmpty() && filterSelectedGradeList.isEmpty() && filterTeachingMethodList.isEmpty()){

                filterBasedOnCoursesNoList(coursesEditTextStr);
            }



            else if(!locationEditTextStr.isEmpty() && !coursesEditTextStr.isEmpty() && filterSelectedGenderList.isEmpty() && filterSelectedGradeList.isEmpty() && filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationAndCoursesNoList(locationEditTextStr,coursesEditTextStr);
            }


            else if(locationEditTextStr.isEmpty() && coursesEditTextStr.isEmpty() && !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() && filterTeachingMethodList.isEmpty()){

                filterBasedOnSelectedGenderList();
            }


            else if(locationEditTextStr.isEmpty() && coursesEditTextStr.isEmpty() && filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() && filterTeachingMethodList.isEmpty()){

                filterBasedOnSelectedGradeList();
            }

            else if(locationEditTextStr.isEmpty() && coursesEditTextStr.isEmpty() && filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() && !filterTeachingMethodList.isEmpty()){

                filterBasedOnSelectedTeachingMethodList();
            }
            else if(!locationEditTextStr.isEmpty() &&
                    coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationNoListAndGenderList(locationEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    coursesEditTextStr.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationNoListAndGradeList(locationEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    coursesEditTextStr.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationNoListAndTeachingMethodList(locationEditTextStr);
            }
            else if(!locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationNoListCoursesNoListAndGenderList(locationEditTextStr,coursesEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationNoListCoursesNoListAndGradeList(locationEditTextStr,coursesEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationNoListCoursesNoListAndTeachingMethodList(locationEditTextStr,coursesEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationNoListCoursesNoListGenderListAndGradeList(locationEditTextStr,coursesEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationNoListCoursesNoListGenderListAndTeachingMethodList(locationEditTextStr,coursesEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){

                filterBasedOnLocationNoListCoursesNoListGradeListAndTeachingMethodList(locationEditTextStr,coursesEditTextStr);
            }

            else if(locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()) {

                filterBasedOnCourseNoListAndGenderList(coursesEditTextStr);
            }

            else if(locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()) {

                filterBasedOnCourseNoListAndGradeList(coursesEditTextStr);
            }

            else if(locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()) {

                filterBasedOnCourseNoListAndTeachingMethodList(coursesEditTextStr);
            }

            else if(locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()) {

                filterBasedOnCourseNoListAndGenderAndGrade(coursesEditTextStr);
            }

            else if(locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()) {

                filterBasedOnCourseNoListAndGenderGradeAndTeachingMethod(coursesEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    filterTeachingMethodList.isEmpty()){
                filterBasedOnLocationNoListGenderAndGradeList(locationEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){
                filterBasedOnLocationNoListGenderAndTeachingMethodList(locationEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    coursesEditTextStr.isEmpty() &&
                    filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){
                filterBasedOnLocationNoListGradeAndTeachingMethodList(locationEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    !coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){
                filterBasedOnLocationCoursesNoListGenderGradeTeachingMethodList(locationEditTextStr,coursesEditTextStr);
            }

            else if(!locationEditTextStr.isEmpty() &&
                    coursesEditTextStr.isEmpty() &&
                    !filterSelectedGenderList.isEmpty() &&
                    !filterSelectedGradeList.isEmpty() &&
                    !filterTeachingMethodList.isEmpty()){
                filterBasedOnLocationNoListGenderGradeTeachingMethodList(locationEditTextStr);
            }

            // ToDo ... more ...
        }
    }

    private void filterBasedOnLocationNoListGenderGradeTeachingMethodList(String location){
        assert getView() != null ;
        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterTeacherBasedOnLocationNoListGenderGradeTeachingMethodList(teacherPostedRequestsForParentList,location,filterSelectedGenderList,filterSelectedGradeList,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnLocationCoursesNoListGenderGradeTeachingMethodList(String location,String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterTeacherPostRequestsBasedOnLocationCoursesNoListGenderGradeTeachingMethodList(teacherPostedRequestsForParentList,location,courses,filterSelectedGenderList,filterSelectedGradeList,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnLocationNoListGradeAndTeachingMethodList(String location){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListGradeAndTeachingMethodList(teacherPostedRequestsForParentList,location,filterSelectedGradeList,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnLocationNoListGenderAndTeachingMethodList(String location){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListGenderAndTeachingMethodList(teacherPostedRequestsForParentList,location,filterSelectedGenderList,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnLocationNoListGenderAndGradeList(String location){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListGenderAndGradeList(teacherPostedRequestsForParentList,location,filterSelectedGenderList,filterSelectedGradeList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnCourseNoListAndGenderGradeAndTeachingMethod(String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnCourseNoListAndGenderGradeAndTeachingMethod(teacherPostedRequestsForParentList,courses,filterSelectedGenderList,filterSelectedGradeList,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnCourseNoListAndGenderAndGrade(String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnCourseNoListAndGenderAndGrade(teacherPostedRequestsForParentList,courses,filterSelectedGenderList,filterSelectedGradeList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnCourseNoListAndTeachingMethodList(String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnCoursesNoListAndTeachingMethod(teacherPostedRequestsForParentList,courses,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnCourseNoListAndGradeList(String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnCoursesNoListAndGrade(teacherPostedRequestsForParentList,courses,filterSelectedGradeList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnCourseNoListAndGenderList(String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnCoursesNoListAndGenderList(teacherPostedRequestsForParentList,courses,filterSelectedGenderList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }


    private void filterBasedOnLocationNoListCoursesNoListGradeListAndTeachingMethodList(String location,String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListCoursesNoListGradeListAndTeachingMethodList(teacherPostedRequestsForParentList,location,courses,filterSelectedGradeList,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnLocationNoListCoursesNoListGenderListAndTeachingMethodList(String location,String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListCoursesNoListGenderListAndTeachingMethodList(teacherPostedRequestsForParentList,location,courses,filterSelectedGenderList,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }


    private void filterBasedOnLocationNoListCoursesNoListGenderListAndGradeList(String location,String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListCoursesNoListGenderListAndGradeList(teacherPostedRequestsForParentList,location,courses,filterSelectedGenderList,filterSelectedGradeList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnLocationNoListCoursesNoListAndTeachingMethodList(String location,String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListCoursesNoListAndTeachingMethodList(teacherPostedRequestsForParentList,location,courses,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }


    private void filterBasedOnLocationNoListCoursesNoListAndGradeList(String location,String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListCoursesNoListAndGradeList(teacherPostedRequestsForParentList,location,courses,filterSelectedGradeList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }


    private void filterBasedOnLocationNoListCoursesNoListAndGenderList(String location,String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListCoursesNoListAndGenderList(teacherPostedRequestsForParentList,location,courses,filterSelectedGenderList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }


    private void filterBasedOnLocationNoListAndTeachingMethodList(String location){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListAndTeachingMethodList(teacherPostedRequestsForParentList,location,filterSelectedGradeList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }

    }

    private void filterBasedOnLocationNoListAndGradeList(String location){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListAndGradeList(teacherPostedRequestsForParentList,location,filterSelectedGradeList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }


    private void filterBasedOnLocationNoListAndGenderList(String location){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnLocationNoListAndGenderList(teacherPostedRequestsForParentList,location,filterSelectedGenderList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }



    private void filterBasedOnSelectedTeachingMethodList(){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList = filterDataObject.filterBasedOnSelectedTeachingMethodList(teacherPostedRequestsForParentList,filterTeachingMethodList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }


    private void filterBasedOnSelectedGradeList(){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterTeacherPostRequestsBasedOnSelectedGradeList(teacherPostedRequestsForParentList, filterSelectedGradeList);
            if (filteredList.isEmpty()) {
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
            } else {
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnSelectedGenderList(){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterBasedOnSelectedGenderList(teacherPostedRequestsForParentList,filterSelectedGenderList);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnLocationAndCoursesNoList(String location, String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterTeacherPostRequestBasedOnLocationAndCourse(teacherPostedRequestsForParentList,location,courses);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnLocationNoList(String location){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterTeacherPostedBasedOnLocation(teacherPostedRequestsForParentList,location);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }

    private void filterBasedOnCoursesNoList(String courses){
        assert getView() != null ;

        if(browseTeacherPostedRequestsForParent){
            List<TeacherPostRequest> filteredList =
                    filterDataObject.filterTeacherPostRequestBasedOnCourse(teacherPostedRequestsForParentList,courses);
            if(filteredList.isEmpty()){
                binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.postedRequestsRecyclerView.setVisibility(View.GONE);
                Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
            else{
                binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.postedRequestsRecyclerView.setVisibility(View.VISIBLE);
                teacherPostedRequestsAdapter.filteredList(filteredList);
                Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
            }
        }
    }


    private void setFlexBoxEnabled(FlexboxLayout flexboxLayout,Boolean status){
        for(int i=0;i<flexboxLayout.getChildCount() ; i++){
            flexboxLayout.getChildAt(i).setEnabled(status);
        }
    }


    private void setTeachingMethodFlexBox(){
        String[] teachingMethodArrayForFilter = getResources().getStringArray(R.array.teachingMethodsFilter);
        if(getContext() != null){
            if(parentFilterLayoutBinding.teachingMethodFlexBox.getChildCount() > 0)
                parentFilterLayoutBinding.teachingMethodFlexBox.removeAllViews();

            if(!filterTeachingMethodList.isEmpty())
                filterTeachingMethodList.clear();


            for(String str : teachingMethodArrayForFilter){
                AppCompatButton appCompatButton = new AppCompatButton(getContext());
                appCompatButton.setAllCaps(false);
                appCompatButton.setText(str.trim());
                appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                                getResources().getDisplayMetrics()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                appCompatButton.setGravity(Gravity.CENTER);
                appCompatButton.setLayoutParams(layoutParams);
                int paddingDp = 8;
                int paddingPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
                appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


                FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int marginDp = 8;
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
                flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                appCompatButton.setLayoutParams(flexboxLayoutParams);
                appCompatButton.setOnClickListener(C->{
                    if(checkIfButtonInList(filterTeachingMethodList,appCompatButton.getText().toString())){
                        appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                        filterTeachingMethodList.remove(appCompatButton.getText().toString());
                    }
                    else{
                        appCompatButton.setBackgroundResource(R.drawable.selected_view);
                        filterTeachingMethodList.add(appCompatButton.getText().toString());
                    }
                });
                parentFilterLayoutBinding.teachingMethodFlexBox.addView(appCompatButton);
            }
        }
    }

    private void setChildGradeFlexBox(){
        if(getContext() != null){
            String[] childGradeArrayForFilter = getResources().getStringArray(R.array.childGradeFilter);
            if(parentFilterLayoutBinding.childrenGradeFlexBox.getChildCount() > 0){
                parentFilterLayoutBinding.childrenGradeFlexBox.removeAllViews();
            }
            if(!filterSelectedGradeList.isEmpty())
                filterSelectedGradeList.clear();

            for(String str : childGradeArrayForFilter){
                AppCompatButton appCompatButton = new AppCompatButton(getContext());
                appCompatButton.setAllCaps(false);
                appCompatButton.setText(str.trim());
                appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                                getResources().getDisplayMetrics()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                appCompatButton.setGravity(Gravity.CENTER);
                appCompatButton.setLayoutParams(layoutParams);

                int paddingDp = 8;
                int paddingPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
                appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


                FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int marginDp = 8;
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
                flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                appCompatButton.setLayoutParams(flexboxLayoutParams);

                appCompatButton.setOnClickListener(C->{
                    if(str.equalsIgnoreCase("Any")){
                        if(checkIfButtonInList(filterSelectedGradeList,"Any")){

                            appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                            filterSelectedGradeList.remove("Any");
                        }
                        else{
                            appCompatButton.setBackgroundResource(R.drawable.selected_view);
                            filterSelectedGradeList.add("Any");
                        }
                    }
                    else {
                        int number = 0 ;
                        Pattern pattern = Pattern.compile("\\d+");
                        Matcher matcher = pattern.matcher(appCompatButton.getText().toString());
                        if (matcher.find())
                            number = Integer.parseInt(matcher.group());

                        if(checkIfButtonInList(filterSelectedGradeList,number+"")){

                            appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                            filterSelectedGradeList.remove(number+"");
                        }
                        else{
                            appCompatButton.setBackgroundResource(R.drawable.selected_view);
                            filterSelectedGradeList.add(number+"");
                        }
                    }
                });
                parentFilterLayoutBinding.childrenGradeFlexBox.addView(appCompatButton);
            }
        }
    }

    private void setGenderFlexBox(){
        if(getContext() != null){
            String[] childGenderArrayForFilter = getResources().getStringArray(R.array.childGenderFilter);
            if(parentFilterLayoutBinding.childrenGenderFlexBox.getChildCount() > 0){
                parentFilterLayoutBinding.childrenGenderFlexBox.removeAllViews();
            }

            if(!filterSelectedGenderList.isEmpty())
                filterSelectedGenderList.clear();

            for(String str : childGenderArrayForFilter){
                AppCompatButton appCompatButton = new AppCompatButton(getContext());
                appCompatButton.setAllCaps(false);
                appCompatButton.setText(str.trim());
                appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                                getResources().getDisplayMetrics()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                appCompatButton.setGravity(Gravity.CENTER);
                appCompatButton.setLayoutParams(layoutParams);

                int paddingDp = 8;
                int paddingPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
                appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


                FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int marginDp = 8;
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
                flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                appCompatButton.setLayoutParams(flexboxLayoutParams);

                appCompatButton.setOnClickListener(C->{
                    if(checkIfButtonInList(filterSelectedGenderList,appCompatButton.getText().toString())){
                        appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                        filterSelectedGenderList.remove(appCompatButton.getText().toString());
                    }
                    else{
                        appCompatButton.setBackgroundResource(R.drawable.selected_view);
                        filterSelectedGenderList.add(appCompatButton.getText().toString());
                    }
                });
                parentFilterLayoutBinding.childrenGenderFlexBox.addView(appCompatButton);
            }
        }
    }




    private void setCoursesFlexBox(){
        String[] coursesArrayForFilter = getResources().getStringArray(R.array.allCourses);
        if(parentFilterLayoutBinding.coursesFlexBox.getChildCount() > 0)
            parentFilterLayoutBinding.coursesFlexBox.removeAllViews();

        if(!filterSelectedCoursesList.isEmpty())
            filterSelectedCoursesList.clear();

        if(getContext() != null){
            for(String str : coursesArrayForFilter){
                AppCompatButton appCompatButton = new AppCompatButton(getContext());
                appCompatButton.setAllCaps(false);
                appCompatButton.setText(str);
                appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                                getResources().getDisplayMetrics()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                appCompatButton.setGravity(Gravity.CENTER);
                appCompatButton.setLayoutParams(layoutParams);

                int paddingDp = 8;
                int paddingPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
                appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


                FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int marginDp = 8;
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
                flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                appCompatButton.setLayoutParams(flexboxLayoutParams);

                appCompatButton.setOnClickListener(s->{
                    if(checkIfButtonInList(filterSelectedCoursesList,appCompatButton.getText().toString())){
                        appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                        filterSelectedCoursesList.remove(appCompatButton.getText().toString());
                    }
                    else {
                        appCompatButton.setBackgroundResource(R.drawable.selected_view);
                        filterSelectedCoursesList.add(appCompatButton.getText().toString());
                    }
                });
                parentFilterLayoutBinding.coursesFlexBox.addView(appCompatButton);
            }
        }
    }
    private boolean checkIfButtonInList(List<String> list, String selectedCourse){
        for (String str : list){
            if(str.equalsIgnoreCase(selectedCourse)){
                return true ;
            }
        }
        return false ;
    }


    private void setLocationFlexBox(){
        assert getContext() != null ;
        String[] locationArrayForFilter = getResources().getStringArray(R.array.locationArray);
        if(parentFilterLayoutBinding.locationFlexBox1.getChildCount() > 0)
            parentFilterLayoutBinding.locationFlexBox1.removeAllViews();

        if(!filterSelectedLocationList.isEmpty())
            filterSelectedLocationList.clear();


        for(String str : locationArrayForFilter){
            AppCompatButton appCompatButton = new AppCompatButton(getContext());
            appCompatButton.setAllCaps(false);
            appCompatButton.setText(str);
            appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                            getResources().getDisplayMetrics()),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            appCompatButton.setGravity(Gravity.CENTER);
            appCompatButton.setLayoutParams(layoutParams);

            int paddingDp = 8; // Example padding in dp
            int paddingPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
            appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


            FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int marginDp = 8;
            int marginPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
            flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
            appCompatButton.setLayoutParams(flexboxLayoutParams);

            appCompatButton.setOnClickListener(s->{
                if(checkIfButtonInList(filterSelectedLocationList,appCompatButton.getText().toString())){
                    appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                    filterSelectedLocationList.remove(appCompatButton.getText().toString());
                }
                else {
                    appCompatButton.setBackgroundResource(R.drawable.selected_view);
                    filterSelectedLocationList.add(appCompatButton.getText().toString());
                }
            });
            parentFilterLayoutBinding.locationFlexBox1.addView(appCompatButton);
        }
    }


    @Override
    public void getChildrenForRequestResult(int flag, JSONArray childrenData) {
        if(flag == 1){
            if(!childrenSpinnerList.isEmpty()){
                childrenSpinnerList.clear();
            }
            if(!parentChildrenList.isEmpty()){
                parentChildrenList.clear();
            }

            for(int i=0;i<childrenData.length();i++){
                try {
                    JSONObject jsonObject = childrenData.getJSONObject(i);
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
            setChildrenSpinnerData();
        }
        else {
            MyAlertDialog.errorDialog(getContext());
        }
    }
}