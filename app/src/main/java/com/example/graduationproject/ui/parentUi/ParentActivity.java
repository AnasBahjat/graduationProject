package com.example.graduationproject.ui.parentUi;

import static com.example.graduationproject.R.*;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.CustomSpinnerAdapter;
import com.example.graduationproject.adapters.NotificationsAdapter;
import com.example.graduationproject.broadcastReceiver.BroadcastHandler;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.ActivityParentBinding;
import com.example.graduationproject.databinding.ChildrenInformationPopupWindowBinding;
import com.example.graduationproject.databinding.NotificationsPopupWindowBinding;
import com.example.graduationproject.databinding.ParentInformationPopupWindowBinding;
import com.example.graduationproject.databinding.TeacherInformationPopupWindowBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.AddNewChildListener;
import com.example.graduationproject.listeners.AddTeacherMatchingListener;
import com.example.graduationproject.listeners.GetParentChildren;
import com.example.graduationproject.listeners.NotificationsListListener;
import com.example.graduationproject.listeners.UpdateParentInformation;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.CustomChildData;
import com.example.graduationproject.models.Notifications;
import com.example.graduationproject.models.Parent;
import com.example.graduationproject.models.TeacherMatchModel;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class ParentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,NotificationsListListener, UpdateParentInformation
, GetParentChildren, AddNewChildListener, AddTeacherMatchingListener {

    private Database database;
    private String email,firstName,lastName,password,birthDate,phoneNumber,city,country,doneInformation ;
    private List<Notifications> notificationsList;
    private ActivityParentBinding parentBinding ;
    private PopupWindow notificationPopupWindow;
    private NotificationsPopupWindowBinding notificationPopupWindowBinding ;
    private BroadcastHandler broadcastHandler;
    private ParentInformationPopupWindowBinding parentInformationPopupWindowBinding ;
    private PopupWindow parentInformationPopupWindow;

    private PopupWindow childrenPopupWindow;
    private ChildrenInformationPopupWindowBinding childrenInformationPopupWindowBinding;
    private List<Children> childrenList = new ArrayList<>();
    List<Children> parentChildrenList = new ArrayList<>();
    List<CustomChildData> childrenSpinnerList = new ArrayList<>();
    List<String> coursesListForMatchingTeacher = new ArrayList<>();
    String selectedChildGrade,selectedChildName  ;
    Spinner childrenSpinner,forParentCourses;
    private EditText childNameEditText ;
    private EditText childAgeEditText ;
    private Spinner childGenderSpinner,childGradeSpinner,locationSpinner,teachingMethodSpinner ;
    private Button addNewChildButton,addCourseButton;

    private TextInputLayout childNameText;
    private TextInputLayout ageText;
    AlertDialog newChildDialog;
    private CheckBox sat,sun,mon,tues,wed,thurs,fri;
    private FlexboxLayout flexboxCoursesForMatchingTeacherLayout ;
    private int selectedChildId ;
    private AlertDialog searchingForTeacherDialog;
    private String startTime , endTime ;
    private int startHourSelected,startMinuteSelected ;
    private int endHourSelected,endMinuteSelected ;
    private String amPmStart ;
    private String amPmEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        parentBinding = ActivityParentBinding.inflate(getLayoutInflater());
        setContentView(parentBinding.getRoot());
        getIntentDate();
        init();
    }

    private void getIntentDate(){
        Intent intent = getIntent();
        email=intent.getStringExtra("email");
        firstName=intent.getStringExtra("firstName");
        lastName=intent.getStringExtra("lastName");
        password=intent.getStringExtra("password");
        birthDate=intent.getStringExtra("birthDate");
        phoneNumber=intent.getStringExtra("phoneNumber");
        city=intent.getStringExtra("city");
        country=intent.getStringExtra("country");
        doneInformation=intent.getStringExtra("accountDone");
    }

    private void init(){
        database=new Database(this);
        notificationPopupWindowBinding = NotificationsPopupWindowBinding.inflate(getLayoutInflater());
        notificationPopupWindowBinding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(Integer.parseInt(doneInformation) == 1){
            database.getNotifications(email,this);
        }
        notificationsList = new ArrayList<>();
        buildNavigationView();
        checkAccountDone();
        initBroadcastReceiver();
        loadFragment(new ParentFragment());
        parentBinding.notificationImage.setOnClickListener(asd -> {
            showNotificationPopupWindow();
        });
        checkIfItemSelected();
    }



    private void checkAccountDone(){
        if(Integer.parseInt(doneInformation) == 0){
            parentBinding.accountIsNotConfirmText.setVisibility(View.VISIBLE);
            notificationsList.add(new Notifications(0,"Confirm Account","Fill in extra information to confirm the account please ...",0));
            parentBinding.numOfNotifications.setText(""+notificationsList.size());
            parentBinding.fragmentsContainer.setVisibility(View.GONE);
        }
        else {
            if(!notificationsList.isEmpty()){
                for(int i=0;i<notificationsList.size();i++){
                    if(notificationsList.get(i).getNotificationType() == 0){
                        notificationsList.remove(notificationsList.get(i));
                    }
                }
            }
            updateNotificationsAdapter();
            parentBinding.accountIsNotConfirmText.setVisibility(View.GONE);
            parentBinding.fragmentsContainer.setVisibility(View.VISIBLE);
            if(notificationsList.isEmpty()){
                parentBinding.numOfNotifications.setText("");
            }
            else {
                parentBinding.numOfNotifications.setText(""+notificationsList.size());
            }

        }
    }

    private void updateNotificationsAdapter(){
        if(notificationsList != null){
            if(!notificationsList.isEmpty()){
                parentBinding.numOfNotifications.setText(""+notificationsList.size());
                notificationPopupWindowBinding.noNotificationsText.setVisibility(View.GONE);
            }
            else{
                parentBinding.numOfNotifications.setText("");
                notificationPopupWindowBinding.noNotificationsText.setVisibility(View.GONE);
            }
            notificationPopupWindowBinding.notificationsRecyclerView.setAdapter(new NotificationsAdapter(notificationsList,this));
        }
    }

    private void checkIfItemSelected(){
        parentBinding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.homeFragment){
                    loadFragment(new ParentFragment());
                }
                else if(menuItem.getItemId() == R.id.profileFragment){
                    loadFragment(new ParentProfileFragment());
                }
                return true;
            }
        });
    }


    private void initBroadcastReceiver(){
        broadcastHandler = new BroadcastHandler();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SHOW_PARENT_INFORMATION_WINDOW");
        int flags = 0 ;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            flags = Context.RECEIVER_NOT_EXPORTED;
        }
        registerReceiver(broadcastHandler,intentFilter,flags);
    }

    private void showNotificationPopupWindow(){
        if(Integer.parseInt(doneInformation) == 1)
            database.getNotifications(email,this);
        notificationPopupWindow = new PopupWindow(notificationPopupWindowBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT,1500,true);
        if(notificationsList.isEmpty()){
            notificationPopupWindowBinding.noNotificationsText.setVisibility(View.VISIBLE);
        }
        else {
            notificationPopupWindowBinding.notificationsRecyclerView.setAdapter(new NotificationsAdapter(notificationsList,this));
            notificationPopupWindowBinding.noNotificationsText.setVisibility(View.GONE);
        }
       notificationPopupWindow.showAsDropDown(parentBinding.notificationImage,-550,0);
    }



    public void showParentInformationPopupWindow(){
        parentInformationPopupWindowBinding = ParentInformationPopupWindowBinding.inflate(getLayoutInflater());
        parentInformationPopupWindow = new PopupWindow(parentInformationPopupWindowBinding.getRoot(),1300,2000,true);
        parentInformationPopupWindow.showAtLocation(parentInformationPopupWindowBinding.parentInformationLayout, Gravity.CENTER,0,0);
        notificationPopupWindow.dismiss();

        onTextChangedIdText(parentInformationPopupWindowBinding.idTextParent,parentInformationPopupWindowBinding.idTextParentLayout);
        onTextChangedIdText(parentInformationPopupWindowBinding.edtTextPhoneNumber,parentInformationPopupWindowBinding.phoneNumber);
        onTextChangedIdText(parentInformationPopupWindowBinding.cityText,parentInformationPopupWindowBinding.cityLayout);
        onTextChangedIdText(parentInformationPopupWindowBinding.countryText,parentInformationPopupWindowBinding.countryLayout);

        parentInformationPopupWindowBinding.closeParentInformationPopupWindow.setOnClickListener(a->{
            parentInformationPopupWindow.dismiss();
        });

        parentInformationPopupWindowBinding.confirmInformationBtn.setOnClickListener(ss->{
            confirmParentInformation();
        });

        parentInformationPopupWindowBinding.addChildBtn.setOnClickListener(cl->{
            buildChildPopupWindow();
        });
    }

    private void confirmParentInformation(){
        String id = parentInformationPopupWindowBinding.idTextParent.getText().toString().trim();
        String phone = parentInformationPopupWindowBinding.edtTextPhoneNumber.getText().toString().trim();
        String city = parentInformationPopupWindowBinding.cityText.getText().toString().trim();
        String country = parentInformationPopupWindowBinding.countryText.getText().toString().trim();

        if(id.isEmpty()){
            parentInformationPopupWindowBinding.idTextParentLayout.setError("* Fill in this field ..");
        }
        if(phone.isEmpty()){
            parentInformationPopupWindowBinding.phoneNumber.setError("* Fill in this field ..");
        }

        if(city.isEmpty()){
            parentInformationPopupWindowBinding.cityLayout.setError("* Fill in this field ..");
        }
        if(country.isEmpty()){
            parentInformationPopupWindowBinding.countryLayout.setError("* Fill in this field ..");
        }
        if (!id.isEmpty() && !phone.isEmpty() && !city.isEmpty() && !country.isEmpty() &&
                parentInformationPopupWindowBinding.flexboxLayout.getChildCount() > 0) {
            Parent parent = new Parent(email,id,phone,childrenList,city,country);
            database.confirmParentInformation(parent,this);
        }

    }

    private void buildChildPopupWindow(){
        parentInformationPopupWindowBinding.parentInformationLayout.setVisibility(View.GONE);
        childrenInformationPopupWindowBinding = ChildrenInformationPopupWindowBinding.inflate(getLayoutInflater());
        childrenPopupWindow = new PopupWindow(childrenInformationPopupWindowBinding.getRoot(),1300,1500,true);
        childrenPopupWindow.showAtLocation(childrenInformationPopupWindowBinding.childrenLayout,Gravity.CENTER,0,0);
        childrenPopupWindow.setOutsideTouchable(false);
        childrenInformationPopupWindowBinding.closeImage.setOnClickListener(close->{
            childrenPopupWindow.dismiss();
        });
        childrenPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                parentInformationPopupWindowBinding.parentInformationLayout.setVisibility(View.VISIBLE);
            }
        });

        childrenInformationPopupWindowBinding.addChildBtn.setOnClickListener(cx->{
            addChildBtnClicked();
        });

    }

    private void addChildBtnClicked(){
        String childName = childrenInformationPopupWindowBinding.childNameEditText.getText().toString().trim();
        String childAge = childrenInformationPopupWindowBinding.childAgeEditText.getText().toString().trim();
        String childGender = childrenInformationPopupWindowBinding.childGenderSpinner.getSelectedItem().toString();
        String grade = childrenInformationPopupWindowBinding.childGrade.getSelectedItem().toString();
        int genderValue = 1 ;
        if(childName.isEmpty()){
            childrenInformationPopupWindowBinding.childNameText.setError("* Fill in this field ..");
        }
        if(childAge.isEmpty()){
            childrenInformationPopupWindowBinding.ageText.setError("* Fill in this field ..");
        }

        if(childGender.equals("Female"))
            genderValue = 0 ;

        if(!childName.isEmpty() && !childAge.isEmpty()){
            Children child = new Children(childName,childAge,genderValue,Integer.parseInt(grade));
            childrenList.add(child);
        }
        updateFlexBoxParentChildren();
        childrenPopupWindow.dismiss();
    }

    private void updateFlexBoxParentChildren(){
        parentInformationPopupWindowBinding.flexboxLayout.removeAllViews();
        for(Children child : childrenList){
            LayoutInflater inflater = LayoutInflater.from(this);
            View customView = inflater.inflate(R.layout.custom_child_view,parentInformationPopupWindowBinding.flexboxLayout,false);
            TextView childName = customView.findViewById(R.id.textViewChildName);
            ImageView deleteImageView = customView.findViewById(R.id.imageViewDelete);
            childName.setText(child.getChildName());
            deleteImageView.setOnClickListener(xs->{
                parentInformationPopupWindowBinding.flexboxLayout.removeView(customView);
                // ToDo(Delete children from database)
            });
            parentInformationPopupWindowBinding.flexboxLayout.addView(customView);
        }
    }



    private void loadFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putString("email",email);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragments_container,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void buildNavigationView(){
        parentBinding.navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,parentBinding.drawerLayout,R.string.open_navigation,R.string.close_navigation);
        parentBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        parentBinding.openSideNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!parentBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    parentBinding.drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    public void getNotifications(int result, JSONArray notificationsJsonArray) {
        if(result == 1){
            List<Notifications> notList = new ArrayList<>();
            try {
                for(int i=0;i<notificationsJsonArray.length();i++){
                    JSONObject jsonObject = notificationsJsonArray.getJSONObject(i);
                    Notifications notification = new Notifications(Integer.parseInt(jsonObject.getString("notificationType")),jsonObject.getString("notificationTitle"),
                            jsonObject.getString("notificationBody"),
                            Integer.parseInt(jsonObject.getString("isRead")));
                    notList.add(notification);
                }
                if(!notificationsList.isEmpty()){
                    notificationsList.clear();
                }
                notificationsList.addAll(notList);
                if(!notificationsList.isEmpty()){
                    parentBinding.numOfNotifications.setText(""+notList.size());
                }
                else {
                    parentBinding.numOfNotifications.setText("");
                }
            }
            catch (JSONException e){
                throw new RuntimeException(e);
            }
            updateNotificationsAdapter();
        }
        else {
            notificationsList = null ;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.logoutId){
            finish();
        }
        else if(menuItem.getItemId() == R.id.lookForTeacher){
            if(doneInformation.equalsIgnoreCase("1"))
                database.getParentChildren(email,this);
            else
                MyAlertDialog.showCustomAlertDialogLoginError(this,"Confirm Account","Please Confirm your account first,check notifications");
            //askForSpecificTeacherFragment = new AskForSpecificTeacherFragment();
           // askForSpecificTeacherFragment.setContext(this);
           // loadFragment(askForSpecificTeacherFragment);
        }
        else if(menuItem.getItemId() == R.id.addNewChild){
            if(doneInformation.equalsIgnoreCase("1"))
                showNewChildrenDialog();
            else
                MyAlertDialog.showCustomAlertDialogLoginError(this,"Confirm Account","Please Confirm your account first, check notifications");
        }
        parentBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastHandler);
    }

    @Override
    public void onResult(int flag) {
        if(flag == 1){
            MyAlertDialog.showCustomDialogForParentAccountConfirmed(this);
            doneInformation="1";
            checkAccountDone();
            parentInformationPopupWindow.dismiss();
        }
        else {
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Error","There are an error occurred please try again later ..");
        }
    }

    @Override
    public void getChildrenResult(int flag, JSONArray children) {
        if(flag==1){
            if(!children.toString().isEmpty()){
                for (int i=0;i<children.length();i++){
                    try {
                        JSONObject jsonObject = children.getJSONObject(i);
                        int childId = jsonObject.getInt("childId");
                        String childName = jsonObject.getString("childName");
                        String childAge = jsonObject.getString("childAge");
                        int childGenderVal = jsonObject.getInt("childGender");
                        parentChildrenList.add(new Children(childName,childAge,childGenderVal,jsonObject.getInt("childGrade")));
                        //childrenSpinnerList.add(childId+" , "+childName+" , "+jsonObject.getInt("childGrade"));
                        childrenSpinnerList.add(new CustomChildData(childId,childName,Integer.parseInt(jsonObject.getString("childGrade"))));
                    }
                    catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                showAlertDialogForSearchingForTeacher();

                //askForSpecificTeacherFragment.getChildrenFromParentActivity(parentChildrenList,childrenSpinnerList);
            }
            else {
                MyAlertDialog.errorDialog(this);
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    public void showAlertDialogForSearchingForTeacher(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ParentActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_ask_for_specific_teacher, null);
        builder.setView(dialogView);

        Button confirmTeachersInformationBtn = dialogView.findViewById(R.id.confirmTeachersInformation);
        ImageView closeImageView = dialogView.findViewById(R.id.closeTheDialog);
        childrenSpinner = dialogView.findViewById(R.id.childrenSpinner);
        forParentCourses = dialogView.findViewById(R.id.forParentCourses);
        locationSpinner = dialogView.findViewById(R.id.locationSpinner);
        sat = dialogView.findViewById(R.id.saturday);
        sun = dialogView.findViewById(R.id.sunday);
        mon = dialogView.findViewById(R.id.monday);
        tues = dialogView.findViewById(R.id.tuesday);
        wed = dialogView.findViewById(R.id.wednesday);
        thurs = dialogView.findViewById(R.id.thursday);
        fri = dialogView.findViewById(R.id.friday);
        flexboxCoursesForMatchingTeacherLayout = dialogView.findViewById(R.id.flexboxLayoutMatchTeacher);
        addCourseButton = dialogView.findViewById(R.id.addCourseMatchingTeacherBtn);
        teachingMethodSpinner = dialogView.findViewById(R.id.teachingMethod);

        EditText startTimePickerEditText = dialogView.findViewById(R.id.startTimeEdtText);
        EditText endTimePickerEditText= dialogView.findViewById(R.id.endTimeEdtText);



        searchingForTeacherDialog = builder.create();
        searchingForTeacherDialog.show();
        searchingForTeacherDialog.setCancelable(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(searchingForTeacherDialog.getWindow().getAttributes());
        layoutParams.width = 1300;
        layoutParams.height = 2000;
        searchingForTeacherDialog.getWindow().setAttributes(layoutParams);
        searchingForTeacherDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(searchingForTeacherDialog.getWindow() != null)
            searchingForTeacherDialog.getWindow().setLayout(1300,2000);


        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this,childrenSpinnerList);
        childrenSpinner.setAdapter(adapter);

        closeImageView.setOnClickListener(ad->{
            searchingForTeacherDialog.dismiss();
        });

        childrenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CustomChildData data = (CustomChildData) parent.getItemAtPosition(position);
                String[] splittedString = data.toString().split(",");
                selectedChildId = data.getChildId();
                selectedChildName = splittedString[0].trim();
                selectedChildGrade = splittedString[1].trim();
                updatedCoursesSpinner();
                if(flexboxCoursesForMatchingTeacherLayout.getChildCount() > 0){
                    flexboxCoursesForMatchingTeacherLayout.removeAllViews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addCourseButton.setOnClickListener(Z->{
            String selectedCourse = forParentCourses.getSelectedItem().toString();
            if(!checkIfCourseAddedToList(selectedCourse)){
                coursesListForMatchingTeacher.add(selectedCourse);
                updateFlexBoxMatchingTeacher();
            }
            else {
                MyAlertDialog.showWarningCourseAdded(this);
            }
        });


        setStartAndEndTime();


        startTimePickerEditText.setOnClickListener(c->{
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int mins = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    startHourSelected = hourOfDay ;
                    startMinuteSelected = minute ;
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    c.set(Calendar.MINUTE,minute);
                    c.setTimeZone(TimeZone.getDefault());

                    SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
                    String amPm = amPmFormat.format(c.getTime()); // This will give you AM or PM

                    amPmStart = amPm ;

                    SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                    String time = format.format(c.getTime());
                    startTime = time ;
                    startTimePickerEditText.setText(startTime);

                }
            },hours,mins,false);
            timePickerDialog.show();
        });

        endTimePickerEditText.setOnClickListener(v->{
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int mins = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    endHourSelected = hourOfDay;
                    endMinuteSelected = minute;
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    c.set(Calendar.MINUTE,minute);
                    c.setTimeZone(TimeZone.getDefault());

                    SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
                    String amPm = amPmFormat.format(c.getTime()); // This will give you AM or PM
                    amPmEnd = amPm ;
                    SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                    String time = format.format(c.getTime());

                    endTime = time ;
                    endTimePickerEditText.setText(endTime);
                }
            },hours,mins,false);
            timePickerDialog.show();
        });

        confirmTeachersInformationBtn.setOnClickListener(sd->{
            checkSearchForTeacherDataConfirmBtnClicked();
        });
    }
    private void setStartAndEndTime(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);


        SimpleDateFormat amPmFormatStart = new SimpleDateFormat("a");
        amPmStart = amPmFormatStart.format(calendar.getTime());
        SimpleDateFormat formatStart = new SimpleDateFormat("h:mm a");
        startTime = formatStart.format(calendar.getTime());


        SimpleDateFormat amPmFormatEnd = new SimpleDateFormat("a");
        amPmEnd = amPmFormatEnd.format(calendar.getTime());
        SimpleDateFormat formatEnd = new SimpleDateFormat("h:mm a");
        endTime = formatEnd.format(calendar.getTime());

    }


    private boolean checkIfCourseAddedToList(String selectedCourse){
        for (String str : coursesListForMatchingTeacher){
            if(str.equalsIgnoreCase(selectedCourse)){
                return true ;
            }
        }
        return false ;
    }
    private void updateFlexBoxMatchingTeacher(){
        if(flexboxCoursesForMatchingTeacherLayout.getChildCount() > 0){
            flexboxCoursesForMatchingTeacherLayout.removeAllViews();
        }
        for(String str : coursesListForMatchingTeacher){
            LayoutInflater inflater = LayoutInflater.from(this);
            View customView = inflater.inflate(R.layout.course_custom_card,flexboxCoursesForMatchingTeacherLayout,false);
            TextView courseName = customView.findViewById(R.id.textViewCourseName);
            ImageView deleteImageView = customView.findViewById(R.id.imageViewDelete);
            courseName.setText(str);
            deleteImageView.setOnClickListener(xs->{
                flexboxCoursesForMatchingTeacherLayout.removeView(customView);
                Toast.makeText(this,courseName.getText().toString()+" Removed",Toast.LENGTH_SHORT).show();
                for(int i=0;i<coursesListForMatchingTeacher.size();i++){
                    if(coursesListForMatchingTeacher.get(i).equalsIgnoreCase(courseName.getText().toString()))
                        coursesListForMatchingTeacher.remove(coursesListForMatchingTeacher.get(i));
                }
                // ToDo(Delete children from database)
            });
            flexboxCoursesForMatchingTeacherLayout.addView(customView);
        }
    }
    private void checkSearchForTeacherDataConfirmBtnClicked(){
        StringBuilder selectedDays = checkIfDaysSelected();
        String city = locationSpinner.getSelectedItem().toString();
        String teachingMethodStr = teachingMethodSpinner.getSelectedItem().toString();
        if(selectedDays == null){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Days Error","Please Choose at least one day a week to match a teacher data");
        }
        else {
            if(!checkStartAndEndTime()){
                MyAlertDialog.showCustomAlertDialogLoginError(this,"Wrong timing","Please Choose valid start and end time, and make sure there are a least one hour..");
            }
            else {
                if(flexboxCoursesForMatchingTeacherLayout.getChildCount() == 0){
                    MyAlertDialog.showCustomAlertDialogLoginError(this,"No Courses","Please Choose At least one course ..");
                }
                else {
                    Toast.makeText(this,"Start time : "+startTime,Toast.LENGTH_SHORT).show();
                    if(flexboxCoursesForMatchingTeacherLayout.getChildCount() > 0){
                        StringBuilder courses= new StringBuilder();
                        for(int i=0 ; i <coursesListForMatchingTeacher.size() ; i++){
                            if(i + 1 != coursesListForMatchingTeacher.size()){
                                courses.append(coursesListForMatchingTeacher.get(i)).append(",");
                            }
                            else {
                                courses.append(coursesListForMatchingTeacher.get(i));
                            }
                        }
                        TeacherMatchModel teacherMatchModel=new TeacherMatchModel(new CustomChildData(selectedChildId,selectedChildName,Integer.parseInt(selectedChildGrade))
                                ,selectedDays.toString(),courses.toString(),city,teachingMethodStr,startTime,endTime);
                        database.addNewTeacherMatching(email,teacherMatchModel,this);
                    }
                }
            }
        }
    }
    private boolean checkStartAndEndTime(){
        if(amPmStart.equalsIgnoreCase("AM") && amPmEnd.equalsIgnoreCase("PM"))
            return true;
        if(amPmStart.equalsIgnoreCase("AM") && amPmEnd.equalsIgnoreCase("AM") && startHourSelected + 1 < endHourSelected)
            return true;
        if(amPmStart.equalsIgnoreCase("PM") && amPmEnd.equalsIgnoreCase("PM") && startHourSelected + 1 < endHourSelected)
            return true;
        return false ;
    }

    private StringBuilder checkIfDaysSelected(){
            StringBuilder choosedDays = new StringBuilder();
            if(sat.isChecked() || sun.isChecked() || mon.isChecked() || tues.isChecked() || wed.isChecked() || thurs.isChecked() || fri.isChecked()){
                if(sat.isChecked())
                    choosedDays.append("Sat,");
                if(sun.isChecked())
                    choosedDays.append("Sun,");
                if(mon.isChecked())
                    choosedDays.append("Mon,");
                if(tues.isChecked())
                    choosedDays.append("Tues,");
                if(wed.isChecked())
                    choosedDays.append("Wed,");
                if(thurs.isChecked())
                    choosedDays.append("Thur,");
                if(fri.isChecked())
                    choosedDays.append("Fri");
                return  choosedDays;
            }
            else {
                return null ;
            }
    }

    private void updatedCoursesSpinner(){
        ArrayAdapter <String> adapter ;
        int currentGrade = Integer.parseInt(selectedChildGrade);
        if(currentGrade >= 1 && currentGrade <= 5 ){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.forParentCoursesElementarySchool));
            forParentCourses.setAdapter(adapter);
        }
        else if (currentGrade > 5 && currentGrade <= 9) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.forParentCoursesMiddleSchool));
            forParentCourses.setAdapter(adapter);
        }

        else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.forParentCoursesHighSchool));
            forParentCourses.setAdapter(adapter);
        }
    }

    private void showNewChildrenDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ParentActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_new_child_dialog, null);
        builder.setView(dialogView);

        childNameEditText = dialogView.findViewById(R.id.childNameEditText);
        childAgeEditText = dialogView.findViewById(R.id.childAgeEditText);
        childGenderSpinner = dialogView.findViewById(R.id.childGenderSpinner);
        childGradeSpinner = dialogView.findViewById(R.id.childGrade);
        ImageView closeDialog = dialogView.findViewById(R.id.closeImage);
        addNewChildButton = dialogView.findViewById(R.id.addChildBtn);
        childNameText = dialogView.findViewById(R.id.childNameText);
        ageText=dialogView.findViewById(R.id.ageText);

        newChildDialog = builder.create();
        newChildDialog.show();
        newChildDialog.setCancelable(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(newChildDialog.getWindow().getAttributes());
        layoutParams.width = 1300;
        layoutParams.height = 2000;
        newChildDialog.getWindow().setAttributes(layoutParams);
        newChildDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(newChildDialog.getWindow() != null)
            newChildDialog.getWindow().setLayout(1300,2000);


        onTextChangedIdText(childNameEditText,childNameText);
        onTextChangedIdText(childAgeEditText,ageText);

        closeDialog.setOnClickListener(asd->{
            newChildDialog.dismiss();
        });




        addNewChildButton.setOnClickListener(x->{
            addNewChildButtonClicked();
        });


    }

    private void onTextChangedIdText(EditText passedId, TextInputLayout layoutId){
        passedId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutId.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void addNewChildButtonClicked(){
        String childName = childNameEditText.getText().toString().trim();
        String childAge = childAgeEditText.getText().toString().trim();
        String childGender = childGenderSpinner.getSelectedItem().toString();
        String childGrade = childGradeSpinner.getSelectedItem().toString();
        int newChildGender = 0;

        if(childName.isEmpty()){
            childNameText.setError("* Please Fill In this field");
        }
        if(childAge.isEmpty()){
            ageText.setError("*Please fill in this field");
        }
        if(childName.length() > 15){
            childNameText.setError("*Please Enter a valid name");
        }
        if(!childAge.isEmpty() && Integer.parseInt(childAge) > 18){
            ageText.setError("*Please Enter a valid age value.");
        }

        if(childGender.equalsIgnoreCase("Male")){
            newChildGender=1;
        }

        if(!childAge.isEmpty() && !childName.isEmpty()){
            Children child = new Children(childName,childAge,newChildGender,Integer.parseInt(childGrade));
            database.addNewChild(email,child,this);
        }
    }


    @Override
    public void onChildAdded(int resultFlag) {
        if(resultFlag == 1){
            MyAlertDialog.showDialogForChildAdded(this);
            newChildDialog.dismiss();
        }
        else if(resultFlag == 0){
            MyAlertDialog.showCustomAlertDialogSpinnerError(this,"Error","Error adding new child , try again later..");
        }
        else {
            MyAlertDialog.showCustomAlertDialogSpinnerError(this,"Network Error","Network Error , please try again later ..");
        }
    }

    @Override
    public void onMatchingAdded(int resultFlag) {
        if(resultFlag == 1){
            MyAlertDialog.showDialogForDone(this,"Done","Your requirements is added , wait for requests from teachers ..");
            searchingForTeacherDialog.dismiss();
        }
        else if(resultFlag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Error","Something went wrong , please check your entered data and apply again");
        }
        else if(resultFlag == -2){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Connection Error","Connection error please try again later ..");
        }
        else {
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Something went wrong","There are an error occurred , try again later or restart the application");
        }
    }

    @Override
    public void getTeacherMatchingData(int resultFlag, JSONArray teacherMatchingData) {

    }
}