package com.example.graduationproject.ui.afterLogin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.adapters.NotificationsAdapter;
import com.example.graduationproject.broadcastReceiver.BroadcastHandler;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.R;
import com.example.graduationproject.databinding.ActivityAfterLoginBinding;
import com.example.graduationproject.databinding.NotificationsPopupWindowBinding;
import com.example.graduationproject.databinding.TeacherInformationPopupWindowBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.interfaces.RequestResult;
import com.example.graduationproject.listeners.TeacherAccountConfirmationListener;
import com.example.graduationproject.models.Notifications;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.ui.teacherFragment.TeacherFragment;
import com.example.graduationproject.ui.login.LoginActivity;
import com.example.graduationproject.broadcastReceiver.BroadcastHandler;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AfterLoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TeacherAccountConfirmationListener {
    private Database database;
    private String firstName,lastName,email,password,birthDate,phoneNumber,city,country,profileType="1",doneInformation="0";
    private ActivityAfterLoginBinding binding ;
    private BroadcastHandler broadcastHandler;

    private PopupWindow notificationPopupWindow ;
    private List<Notifications> notificationsList;
    private PopupWindow teacherInformationPopupWindow ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAfterLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentDate();
        initialize();
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
      //  profileType = intent.getStringExtra("profileType");
       // doneInformation=intent.getStringExtra("accountDone");
    }

    private void initialize(){
        database=new Database(this);
        if(profileType.equals("0")) {
            // ToDo (load parent fragment)
        }
        else{
            loadFragment(new TeacherFragment());
        }
        notificationsList = new ArrayList<>();
        notificationsList.add(new Notifications(0,"Confirm Your Account","Add the necessary information to complete your account",0));
        binding.numOfNotifications.setText(""+notificationsList.size());

        checkIfAccountConfirmed();
        buildNavigationView();
        initBroadcastReceiver();
    }

    private void initBroadcastReceiver(){
        broadcastHandler = new BroadcastHandler();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("showTeacherInformationWindow");
        int flags = 0 ;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            flags = Context.RECEIVER_NOT_EXPORTED;
        }
        registerReceiver(broadcastHandler,intentFilter,flags);
    }

    private void checkIfAccountConfirmed(){
        if(doneInformation.equals("0")){
            binding.notificationImage.setOnClickListener(t->{
                viewNotificationsPopUpWindow();
            });
        }
    }



    private void buildNavigationView(){
        binding.navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open_navigation,R.string.close_navigation);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.openSideNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }



    private void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragments_container,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void logoutOnClick(View view) {
        String email=getIntent().getStringExtra("email");
        Intent intent=new Intent(AfterLoginActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }


    private void viewNotificationsPopUpWindow(){
        com.example.graduationproject.databinding.NotificationsPopupWindowBinding notificationsPopupWindowBinding = NotificationsPopupWindowBinding.inflate(getLayoutInflater());
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = 1500;
        notificationsPopupWindowBinding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationsPopupWindowBinding.notificationsRecyclerView.setAdapter(new NotificationsAdapter(notificationsList,this));
        notificationPopupWindow = new PopupWindow(notificationsPopupWindowBinding.getRoot(),width,height,true);
        notificationPopupWindow.showAsDropDown(binding.notificationImage,0,0);
    }

    public void showTeacherInformationPopupWindow(){

        TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding = TeacherInformationPopupWindowBinding.inflate(getLayoutInflater());
        decrementNotificationsNumber();

        int width = 1150 ;
        int height = 2000;
        teacherInformationPopupWindow = new PopupWindow(teacherInformationPopupWindowBinding.getRoot(),width,height,true);

        teacherInformationPopupWindow.showAtLocation(teacherInformationPopupWindowBinding.teacherInformationLayout, Gravity.CENTER,0,0);
        notificationPopupWindow.dismiss();

        teacherInformationPopupWindowBinding.closeTeacherInformationPopupWindow.setOnClickListener(v ->{
            teacherInformationPopupWindow.dismiss();
        });


        teacherInformationPopupWindowBinding.collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFieldSpinner(teacherInformationPopupWindowBinding,teacherInformationPopupWindowBinding.collegeSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        teacherInformationPopupWindowBinding.confirmInformationBtn.setOnClickListener(ss->{
            checkConfirmationButtonClicked(teacherInformationPopupWindowBinding);
        });

        onTextChangedIdText(teacherInformationPopupWindowBinding.idText,teacherInformationPopupWindowBinding.idTextLayout);
        onTextChangedIdText(teacherInformationPopupWindowBinding.cityText,teacherInformationPopupWindowBinding.cityLayout);
        onTextChangedIdText(teacherInformationPopupWindowBinding.countryText,teacherInformationPopupWindowBinding.countryLayout);
        onTextChangedIdText(teacherInformationPopupWindowBinding.numOfHoursADayEditText,teacherInformationPopupWindowBinding.hoursAvailableLayout);
        onTextChangedIdText(teacherInformationPopupWindowBinding.numOfDaysAWeekEditText,teacherInformationPopupWindowBinding.daysAvailableLayout);
        onItemSelectedSpinner(teacherInformationPopupWindowBinding);
    }


    private void onItemSelectedSpinner(TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding){
        teacherInformationPopupWindowBinding.fieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teacherInformationPopupWindowBinding.fieldErrorText.setVisibility(View.GONE);
                teacherInformationPopupWindowBinding.fieldInformationLayout.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateFieldSpinner(TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding,Spinner passedSpinner){
        String selectedCollege = teacherInformationPopupWindowBinding.collegeSpinner.getSelectedItem().toString();
        Log.d("123123123 "+selectedCollege,"123123123 "+selectedCollege);
        ArrayAdapter <String> adapter ;
        teacherInformationPopupWindowBinding.collegeLayout.setError(null);
        teacherInformationPopupWindowBinding.collegeErrorText.setVisibility(View.GONE);

        if(selectedCollege.equalsIgnoreCase("Faculty Of Engineering")){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.engineeringCollegeFieldsSpinner));
            teacherInformationPopupWindowBinding.fieldSpinner.setAdapter(adapter);
        }
        else if(selectedCollege.equalsIgnoreCase("IT")){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.IT));
            teacherInformationPopupWindowBinding.fieldSpinner.setAdapter(adapter);
        }

        else if(selectedCollege.equalsIgnoreCase("Science And Mathematics")){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.scienceAndMathematicsSpinner));
            teacherInformationPopupWindowBinding.fieldSpinner.setAdapter(adapter);
        }


        else if(selectedCollege.equalsIgnoreCase("Faculty Of Arts")){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.facultyOfArtsSpinner));
            teacherInformationPopupWindowBinding.fieldSpinner.setAdapter(adapter);
        }

        else if(selectedCollege.equalsIgnoreCase("Faculty Of Commerce")){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.getResources().getStringArray(R.array.facultyOfCommerceSpinner));
            teacherInformationPopupWindowBinding.fieldSpinner.setAdapter(adapter);
        }

    }
    private void decrementNotificationsNumber(){
        int numOfNotifications = Integer.parseInt(binding.numOfNotifications.getText().toString());
        if(numOfNotifications - 1 > 0){
            binding.numOfNotifications.setText(""+(Integer.parseInt(binding.numOfNotifications.getText().toString()) - 1));
        }
        else {
            binding.numOfNotifications.setText("0");
            binding.numOfNotifications.setVisibility(View.GONE);
        }
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

    private void checkConfirmationButtonClicked(TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding){
        boolean collegeFlag = true , fieldFlag = true;
        String idStr = teacherInformationPopupWindowBinding.idText.getText().toString().trim();
        int studentOrGraduate = 1;
        if(teacherInformationPopupWindowBinding.studentOrGraduateRadioGroup.getCheckedRadioButtonId() == teacherInformationPopupWindowBinding.studentRadioButton.getId())
            studentOrGraduate=0;

        String collegeStr = teacherInformationPopupWindowBinding.collegeSpinner.getSelectedItem().toString().trim();
        String fieldStr = teacherInformationPopupWindowBinding.fieldSpinner.getSelectedItem().toString().trim();
        String graduationYear = teacherInformationPopupWindowBinding.graduationYearSpinner.getSelectedItem().toString().trim();
        String daysAvailableWeekly = teacherInformationPopupWindowBinding.numOfDaysAWeekEditText.getText().toString().trim();
        String hoursAvailableDaily = teacherInformationPopupWindowBinding.numOfHoursADayEditText.getText().toString().trim();
        String cityText = teacherInformationPopupWindowBinding.cityText.getText().toString().trim();
        String countryText = teacherInformationPopupWindowBinding.countryText.getText().toString().trim();

        if(idStr.isEmpty()){
            teacherInformationPopupWindowBinding.idTextLayout.setError("*Please Fill in this field");
        }

        if(cityText.isEmpty()){
            teacherInformationPopupWindowBinding.cityLayout.setError("*Please Fill in this field");
        }

        if(daysAvailableWeekly.isEmpty()){
            teacherInformationPopupWindowBinding.daysAvailableLayout.setError("*Please Fill in this field");
        }

        if(hoursAvailableDaily.isEmpty()){
            teacherInformationPopupWindowBinding.hoursAvailableLayout.setError("*Please Fill in this field");
        }

        if(countryText.isEmpty()){
            teacherInformationPopupWindowBinding.countryLayout.setError("*Please Fill in this field");
        }

        if(collegeStr.equalsIgnoreCase("Choose")){
            collegeFlag=false;
            teacherInformationPopupWindowBinding.collegeErrorText.setText(this.getString(R.string.collegeErrorMsg));
            teacherInformationPopupWindowBinding.collegeErrorText.setVisibility(View.VISIBLE);
            teacherInformationPopupWindowBinding.collegeLayout.setError("");
            //MyAlertDialog.showCustomAlertDialogSpinnerError(this,"Invalid College","Choose Valid College Value ..");
        }
        if(fieldStr.equalsIgnoreCase("Choose")){
            fieldFlag=false;
            teacherInformationPopupWindowBinding.fieldErrorText.setText(this.getString(R.string.fieldErrorMsg));
            teacherInformationPopupWindowBinding.fieldErrorText.setVisibility(View.VISIBLE);
            teacherInformationPopupWindowBinding.fieldInformationLayout.setError("");
            //MyAlertDialog.showCustomAlertDialogSpinnerError(this,"Invalid Field","Choose Valid Field Value ..");
        }

        if(!collegeStr.equalsIgnoreCase("Choose")){
            collegeFlag=true;
        }
        if(!fieldStr.equalsIgnoreCase("Choose")){
            fieldFlag=true;
        }

        if(!daysAvailableWeekly.isEmpty() && Integer.parseInt(daysAvailableWeekly) > 7){
            teacherInformationPopupWindowBinding.daysAvailableLayout.setError("*Please enter a valid number of days ..");
        }

        if(!hoursAvailableDaily.isEmpty() && Integer.parseInt(hoursAvailableDaily) > 24){
            teacherInformationPopupWindowBinding.hoursAvailableLayout.setError("*Please enter a valid number of Hours a day ..");
        }


        if(!idStr.isEmpty() && !cityText.isEmpty()  && !countryText.isEmpty() && !daysAvailableWeekly.isEmpty()
                && !hoursAvailableDaily.isEmpty() && collegeFlag && fieldFlag){
                Teacher teacher=new Teacher("11111111111111111",idStr,studentOrGraduate+"",
                        graduationYear,collegeStr,fieldStr,
                        daysAvailableWeekly,hoursAvailableDaily);
                database.updateTeacherInformation(teacher,this);
        }
    }

    public void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.teacher_information_popup_window);
        dialog.getWindow().setLayout(1300, 2000);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_corners);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastHandler);
    }


    @Override
    public void onResult(int resultFlag) {
        if(resultFlag == 1){
            MyAlertDialog.showCustomDialogForTeacherAccountConfirmed(this);
        }
        else if(resultFlag == 0){
            // account already confirmed
            MyAlertDialog.teacherAccountAlreadyConfirmed(this);
        }
        else if(resultFlag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(this,this.getString(R.string.errorOccurredTitle),this.getString(R.string.errorOccurredMsg));
        }
        else {
            // volley error ...
        }
    }
}