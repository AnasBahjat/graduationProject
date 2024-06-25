package com.example.graduationproject.ui.teacherUi;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.graduationproject.adapters.NotificationsAdapter;
import com.example.graduationproject.backgroundActions.NotificationsService;
import com.example.graduationproject.broadcastReceiver.BroadcastHandler;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.R;
import com.example.graduationproject.databinding.ActivityTeacherBinding;
import com.example.graduationproject.databinding.DialogTeacherMatchingOnCardClickedBinding;
import com.example.graduationproject.databinding.FragmentTeacherBinding;
import com.example.graduationproject.databinding.NotificationsPopupWindowBinding;
import com.example.graduationproject.databinding.PostTeacherRequestPopupWindowBinding;
import com.example.graduationproject.databinding.SideNavigationHeaderBinding;
import com.example.graduationproject.databinding.TeacherInformationPopupWindowBinding;
import com.example.graduationproject.databinding.TeacherLookForJobLayoutBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.AddTeacherMatchingListener;
import com.example.graduationproject.listeners.LastMatchingIdListener;
import com.example.graduationproject.listeners.NotificationsListListener;
import com.example.graduationproject.listeners.ParentInformationListener;
import com.example.graduationproject.listeners.TeacherAccountConfirmationListener;
import com.example.graduationproject.listeners.TeacherAvailabilityListener;
import com.example.graduationproject.listeners.TeacherPostListener;
import com.example.graduationproject.models.Address;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.CustomChildData;
import com.example.graduationproject.models.Notifications;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.models.TeacherPostRequest;
import com.example.graduationproject.ui.commonFragment.ProfileFragment;
import com.example.graduationproject.ui.parentUi.ParentActivity;
import com.example.graduationproject.ui.parentUi.ParentFragment;
import com.example.graduationproject.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class TeacherActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        TeacherAccountConfirmationListener,
        NotificationsListListener,
        AddTeacherMatchingListener,
        ParentInformationListener,TeacherPostListener,
        LastMatchingIdListener, TeacherAvailabilityListener {
    private Database database;
    private String firstName,lastName,email,password,birthDate,phoneNumber,city,country,doneInformation;
    private ActivityTeacherBinding binding ;
    private BroadcastHandler broadcastHandler;

    private PopupWindow notificationPopupWindow ;
    private PopupWindow teacherInformationPopupWindow ;
    List<Notifications> notList ;

    Fragment currentFragment ;
    private NotificationsPopupWindowBinding notificationsPopupWindowBinding;

    private ArrayList<TeacherMatchModel> teacherMatchingDataToPassToTeacherFragment= new ArrayList<>();
    private TeacherMatchModel teacherMatchModelTemp ;
    String tempFirstName = "",tempLastName="";
    List<String> tempPhoneList ;
    TeacherLookForJobLayoutBinding teacherLookForJobLayoutBinding  ;
    private View popupView;

    private List<String> teacherAddedCoursesList = new ArrayList<>();

    private int lastTeacherPostId = 0 ;
    private Dialog teacherLooksForJobDialog;

    private String amPmStart,amPmEnd;
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
    private TeacherPostRequest tpr ;

    private String staticAvailability ;

    private String startTime ="12:00 PM", endTime="12:00 PM";
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("START_MATCHING_FRAGMENT_FOR_TEACHER".equals(intent.getAction())) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    TeacherMatchModel teacherMatchModel =intent.getSerializableExtra("teacherMatchModel",TeacherMatchModel.class);
                    if(teacherMatchModel != null)
                        startCardViewData(teacherMatchModel);
                    else
                        startCardViewData(null);
                }
                else {
                   TeacherMatchModel teacherMatchModel = (TeacherMatchModel) intent.getSerializableExtra("teacherMatchModel");
                   startCardViewData(teacherMatchModel);
                }
            }

            else if("REFRESH_FRAGMENT_TEACHER".equals(intent.getAction())){
                database.getTeacherMatchingData(email,TeacherActivity.this);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTeacherBinding.inflate(getLayoutInflater());
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
        doneInformation=intent.getStringExtra("accountDone");

    }

    private void initialize(){
        database=new Database(this);
        if(Integer.parseInt(doneInformation)==1)
            database.getNotifications(email,this);
        notificationsPopupWindowBinding = NotificationsPopupWindowBinding.inflate(getLayoutInflater());
        notList=new ArrayList<>();
        buildNavigationView();
        initBroadcastReceiver();
        checkAccountDone();
        if(doneInformation.equals("1"))
            database.getTeacherMatchingData(email,this);
      //  checkIfTeacherAccountConfirmed();
        database.getLastTeacherPostId(this);
        database.getTeacherAvailability(email,this);
        checkIfItemSelected();
        binding.overlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    void checkAccountDone(){
        if(Integer.parseInt(doneInformation) == 0){
            binding.accountIsNotConfirmText.setVisibility(View.VISIBLE);
            notList.add(new Notifications(1,"Confirm Account",
                    "Fill in extra information to confirm the account please ...",0));
            binding.numOfNotifications.setText(""+notList.size());
            binding.fragmentsContainer.setVisibility(View.GONE);
            updateNotificationsPopUpWindow();
        }
        else {
            if(!notList.isEmpty()){
                for(int i=0;i<notList.size();i++){
                    if(notList.get(i).getNotificationType() == 1){
                        notList.remove(notList.get(i));
                    }
                }
            }
            Log.d("Not list --->"+notList,"Not list --->"+notList);
            updateNotificationsAdapter();
            binding.accountIsNotConfirmText.setVisibility(View.GONE);
            binding.fragmentsContainer.setVisibility(View.VISIBLE);
            if(notList.isEmpty()){
                binding.numOfNotifications.setText("");
            }
            else {
                binding.numOfNotifications.setText(""+notList.size());
            }
        }
    }



    private void updateNotificationsAdapter(){
        if(notList != null){
            if(!notList.isEmpty()){
                binding.numOfNotifications.setText(""+notList.size());
                notificationsPopupWindowBinding.noNotificationsText.setVisibility(View.GONE);
            }
            else{
                binding.numOfNotifications.setText("");
                notificationsPopupWindowBinding.noNotificationsText.setVisibility(View.GONE);
            }
            notificationsPopupWindowBinding.notificationsRecyclerView.setAdapter(new NotificationsAdapter(notList,this));
        }
        //notificationsPopupWindowBinding.notificationsRecyclerView.notify();
    }






    private void checkIfItemSelected(){
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.homeFragment){
                    teacherMatchingDataToPassToTeacherFragment.clear();
                    database.getTeacherMatchingData(email,TeacherActivity.this);
                    //loadTeacherFragment();
                }
                else if(menuItem.getItemId() == R.id.profileFragment){
                    loadFragment(new ProfileFragment());
                }
                return true;
            }
        });
    }



    private void initBroadcastReceiver(){
        broadcastHandler = new BroadcastHandler();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SHOW_TEACHER_INFORMATION_WINDOW");
        intentFilter.addAction("UPDATE_NOTIFICATIONS_RECYCLER_VIEW");
        intentFilter.addAction("UPDATE_TEACHER_UI");
        int flags = 0 ;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            flags = Context.RECEIVER_NOT_EXPORTED;
        }
        registerReceiver(broadcastHandler,intentFilter,flags);


        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("START_MATCHING_FRAGMENT_FOR_TEACHER");
        localIntentFilter.addAction("REFRESH_FRAGMENT_TEACHER");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,localIntentFilter);
    }


    public void notificationImageClicked(View view) {

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        if(popupView == null){
            popupView = inflater.inflate(R.layout.notifications_popup_window,null);
            notificationsPopupWindowBinding = NotificationsPopupWindowBinding.bind(popupView);
        }
        if(popupView.getParent() != null){
            ((ViewGroup) popupView.getParent()).removeView(popupView);
        }

        notificationPopupWindow = new PopupWindow(popupView,910,1500,true);
        notificationPopupWindow.showAsDropDown(binding.notificationImage,-770,0);

        notificationsPopupWindowBinding.refreshNotificationsRecyclerView.setOnRefreshListener(()->{
            if(doneInformation.equals("1"))
                database.getNotifications(email, TeacherActivity.this);
        });
        viewNotificationsPopUpWindow();
    }



    private void buildNavigationView(){
        binding.navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,binding.drawerLayout,R.string.open_navigation,R.string.close_navigation);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.openSideNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSideNavigationData();
                if(!binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

    }

    private void setSideNavigationData(){

        View headerView = binding.navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.sideNavName);
        TextView emailTextView = headerView.findViewById(R.id.sideNavEmail);

        SideNavigationHeaderBinding sideNavigationHeaderBinding = SideNavigationHeaderBinding.inflate(getLayoutInflater());

        name.setText(firstName+" "+lastName);
        emailTextView.setText(email);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.LookForJob){
            showTeacherLookForJobDialog();
        }
        else if(menuItem.getItemId() == R.id.availablePosts){
            Intent intent = new Intent();
            intent.setAction("SHOW_AVAILABLE_JOBS_FOR_TEACHER");
            sendBroadcast(intent);
        }
        if(menuItem.getItemId() == R.id.logoutId){
            finish();
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void loadTeacherFragment(){
        TeacherFragment teacherFragment = new TeacherFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("teacherMatchingData",teacherMatchingDataToPassToTeacherFragment);
        bundle.putString("email",email);
        bundle.putString("firstName",firstName);
        bundle.putString("lastName",lastName);
        bundle.putString("availability",staticAvailability);

        teacherFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragments_container,teacherFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadFragment(Fragment fragment){
        currentFragment = fragment;
        Bundle bundle=new Bundle();
        bundle.putString("email",email);
        bundle.putString("firstName",firstName);
        bundle.putString("lastName",lastName);
        fragment.setArguments(bundle);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragments_container,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void logoutOnClick(View view) {
        String email=getIntent().getStringExtra("email");
        Intent intent=new Intent(TeacherActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    private void viewNotificationsPopUpWindow(){
        if(Integer.parseInt(doneInformation) == 1)
            database.getNotifications(email,this);
        else if(doneInformation.equals("0"))
            updateNotificationsPopUpWindow();
        else
            notificationsPopupWindowBinding.noNotificationsText.setVisibility(View.VISIBLE);
        /*notificationPopupWindow.showAsDropDown(binding.notificationImage,-620,0);


        if(!notList.isEmpty()){
            notificationsPopupWindowBinding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            notificationsPopupWindowBinding.notificationsRecyclerView.setAdapter(new NotificationsAdapter(notList,this));
            notificationsPopupWindowBinding.noNotificationsText.setVisibility(View.GONE);
            notificationsPopupWindowBinding.notificationsRecyclerView.setVisibility(View.VISIBLE);

        }
        else {
            notificationsPopupWindowBinding.noNotificationsText.setVisibility(View.VISIBLE);
            notificationsPopupWindowBinding.notificationsRecyclerView.setVisibility(View.GONE);
        }*/
    }




    public void updateNotifications(){
        database.getNotifications(email,this);
    }




    public void showTeacherInformationPopupWindow(){
        TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding = TeacherInformationPopupWindowBinding.inflate(getLayoutInflater());
        decrementNotificationsNumber();
        teacherInformationPopupWindow = new PopupWindow(teacherInformationPopupWindowBinding.getRoot(),1380,2000,true);

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

        teacherInformationPopupWindowBinding.availabilityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setAvailabilityForStudent(teacherInformationPopupWindowBinding,checkedId);
            }
        });


        onTextChangedIdText(teacherInformationPopupWindowBinding.idText,teacherInformationPopupWindowBinding.idTextLayout);
        onTextChangedIdText(teacherInformationPopupWindowBinding.cityText,teacherInformationPopupWindowBinding.cityLayout);
        onTextChangedIdText(teacherInformationPopupWindowBinding.countryText,teacherInformationPopupWindowBinding.countryLayout);
        onTextChangedIdText(teacherInformationPopupWindowBinding.edtTextPhoneNumber,teacherInformationPopupWindowBinding.phoneNumber);
        onItemSelectedSpinner(teacherInformationPopupWindowBinding);
    }


    private void setAvailabilityForStudent(TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding, int checkedId){
        if(teacherInformationPopupWindowBinding.availabilityRadioGroup.getCheckedRadioButtonId() == R.id.availSpecificDays){
            teacherInformationPopupWindowBinding.daysLayout.setVisibility(View.VISIBLE);
        }
        else {
            teacherInformationPopupWindowBinding.daysLayout.setVisibility(View.GONE);

        }
    }
    private void checkPhoneNumberField(TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding){
        String phoneNumberStr = teacherInformationPopupWindowBinding.edtTextPhoneNumber.getText().toString().trim();
        if(phoneNumberStr.isEmpty() || phoneNumberStr.length() < 9 || phoneNumberStr.length() > 11){
            teacherInformationPopupWindowBinding.phoneNumber.setError("*Please Enter a valid Phone Number");
        }
        else {
            teacherInformationPopupWindowBinding.phoneNumberPrefix.registerCarrierNumberEditText(teacherInformationPopupWindowBinding.edtTextPhoneNumber);
            PopUpWindows.showConfirmPhoneNumberPopupWindow(this,this,getLayoutInflater(),teacherInformationPopupWindowBinding.phoneNumberPrefix.getFullNumber());
        }
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




    private void updateFieldSpinner(TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding, Spinner passedSpinner){
        String selectedCollege = teacherInformationPopupWindowBinding.collegeSpinner.getSelectedItem().toString();
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
        if(!binding.numOfNotifications.getText().toString().isEmpty()){
            int numOfNotifications = Integer.parseInt(binding.numOfNotifications.getText().toString());
            if(numOfNotifications - 1 > 0){
                binding.numOfNotifications.setText(""+(Integer.parseInt(binding.numOfNotifications.getText().toString()) - 1));
            }
            else {
                binding.numOfNotifications.setText("");
                binding.numOfNotifications.setVisibility(View.GONE);
            }
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
        String cityText = teacherInformationPopupWindowBinding.cityText.getText().toString().trim();
        String countryText = teacherInformationPopupWindowBinding.countryText.getText().toString().trim();
        String phoneNumberStr = teacherInformationPopupWindowBinding.edtTextPhoneNumber.getText().toString();
        int selectedId = teacherInformationPopupWindowBinding.availabilityRadioGroup.getCheckedRadioButtonId() ;
        String availabilityStr = "";
        StringBuilder checkedDays = new StringBuilder();
        String educationalLevel = teacherInformationPopupWindowBinding.educationalLevelSpinner.getSelectedItem().toString();

        if(idStr.isEmpty()){
            teacherInformationPopupWindowBinding.idTextLayout.setError("*Please Fill in this field");
        }

        if(cityText.isEmpty()){
            teacherInformationPopupWindowBinding.cityLayout.setError("*Please Fill in this field");
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


        if(phoneNumberStr.isEmpty() || phoneNumberStr.length() < 9 || phoneNumberStr.length() > 11){
            teacherInformationPopupWindowBinding.phoneNumber.setError("*Please enter a valid Phone Number");
        }
        if(selectedId == R.id.availAnyTime){
            availabilityStr = "Any Time";
            staticAvailability = "Sat , Sun , Mon , Tues , Wed , Thur , Fri";
        }
        else if(selectedId == R.id.availWeekend){
            availabilityStr = "Weekend";
            staticAvailability = "Thurs , Fri";
        }
        else if(selectedId == R.id.availSpecificDays){
            checkedDays = getCheckedDays(teacherInformationPopupWindowBinding);
            availabilityStr = checkedDays.toString();
            staticAvailability = availabilityStr;
        }

        if(!idStr.isEmpty() && !cityText.isEmpty()  && !countryText.isEmpty()
                && !phoneNumberStr.isEmpty() && collegeFlag && fieldFlag ){
                teacherInformationPopupWindowBinding.phoneNumberPrefix.registerCarrierNumberEditText(teacherInformationPopupWindowBinding.edtTextPhoneNumber);
                Teacher teacher=new Teacher(email,idStr,studentOrGraduate+"",
                        graduationYear,collegeStr,fieldStr,staticAvailability,educationalLevel,new Address(cityText,countryText));
                database.updateTeacherInformation(teacher,teacherInformationPopupWindowBinding.phoneNumberPrefix.getFullNumber(),this);
                database.getTeacherMatchingData(email,this);
        }
    }

    private StringBuilder getCheckedDays(TeacherInformationPopupWindowBinding bind){
        StringBuilder choosedDays = new StringBuilder();
        if(bind.saturday.isChecked())
            choosedDays.append("Sat , ");
        if(bind.sunday.isChecked())
            choosedDays.append("Sun , ");
        if(bind.monday.isChecked())
            choosedDays.append("Mon , ");
        if(bind.tuesday.isChecked())
            choosedDays.append("Tues , ");
        if(bind.wednesday.isChecked())
            choosedDays.append("Wed , ");
        if(bind.thursday.isChecked())
            choosedDays.append("Thur , ");
        if(bind.friday.isChecked())
            choosedDays.append("Fri");
        return  choosedDays;
    }



    private void showTeacherLookForJobDialog(){
        teacherLookForJobLayoutBinding = TeacherLookForJobLayoutBinding.inflate(getLayoutInflater());
        teacherLooksForJobDialog = new Dialog(this);
        teacherLooksForJobDialog.setContentView(teacherLookForJobLayoutBinding.getRoot());
        teacherLooksForJobDialog.setCancelable(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(teacherLooksForJobDialog.getWindow()).getAttributes());
        layoutParams.width = 1300;
        layoutParams.height = 2000;
        teacherLooksForJobDialog.getWindow().setAttributes(layoutParams);
        teacherLooksForJobDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(teacherLooksForJobDialog.getWindow() != null)
            teacherLooksForJobDialog.getWindow().setLayout(1300,2000);
        teacherLooksForJobDialog.show();

        teacherLookForJobLayoutBinding.closeTheDialog.setOnClickListener(z->{
            teacherLooksForJobDialog.dismiss();
        });

        if(!teacherAddedCoursesList.isEmpty())
            teacherAddedCoursesList.clear();

        teacherLookForJobLayoutBinding.addCourseTeacherLooksForJob.setOnClickListener(c->{
            String currentCourse = teacherLookForJobLayoutBinding.coursesSpinner.getSelectedItem().toString();
            if(!checkIfCourseAddedToList(currentCourse)) {
                teacherAddedCoursesList.add(currentCourse);
                updateFlexBoxForTeacherAddedCourses();
            }
            else {
                MyAlertDialog.showWarningCourseAdded(this);
            }
        });

        teacherLookForJobLayoutBinding.startTimeEdtText.setOnClickListener(c->{
            setStartTime();
        });

        teacherLookForJobLayoutBinding.endTimeEdtText.setOnClickListener(z->{
            setEndTime();
        });

        teacherLookForJobLayoutBinding.postTeacherRequest.setOnClickListener(a->{
            try {
                postTeacherRequestBtnClicked();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

    }


    private void setStartTime(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
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
                teacherLookForJobLayoutBinding.startTimeEdtText.setText(startTime);
            }
        },hours,mins,false);
        timePickerDialog.show();
    }

    private void setEndTime(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
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
                teacherLookForJobLayoutBinding.endTimeEdtText.setText(endTime);
            }
        },hours,mins,false);
        timePickerDialog.show();
    }

    private void updateFlexBoxForTeacherAddedCourses(){
        if(teacherLookForJobLayoutBinding.coursesFlexBoxLayout.getChildCount() > 0){
            teacherLookForJobLayoutBinding.coursesFlexBoxLayout.removeAllViews();
        }
        teacherLookForJobLayoutBinding.coursesFlexBoxLayout.removeAllViews();
        for(String str : teacherAddedCoursesList){
            LayoutInflater inflater = LayoutInflater.from(this);
            View customView = inflater.inflate(R.layout.course_custom_card,teacherLookForJobLayoutBinding.coursesFlexBoxLayout,false);
            TextView courseName = customView.findViewById(R.id.textViewCourseName);
            ImageView deleteImageView = customView.findViewById(R.id.imageViewDelete);
            courseName.setText(str);
            deleteImageView.setOnClickListener(xs->{
                teacherLookForJobLayoutBinding.coursesFlexBoxLayout.removeView(customView);
              //  if(checkIfCourseAddedToList(str)){
                    teacherAddedCoursesList.remove(str);
               // }
                // ToDo(Delete children from database)
            });
            teacherLookForJobLayoutBinding.coursesFlexBoxLayout.addView(customView);
        }
    }

    private void postTeacherRequestBtnClicked() throws ParseException {
        String educationalLevel = teacherLookForJobLayoutBinding.educationalLevelSpinner.getSelectedItem().toString();
        String duration = teacherLookForJobLayoutBinding.numberOfMonthsEdtText.getText().toString();
        String location = teacherLookForJobLayoutBinding.locationSpinner.getSelectedItem().toString();
        String teachingMethod = teacherLookForJobLayoutBinding.teachingMethodSpinner.getSelectedItem().toString();
        if(teacherLookForJobLayoutBinding.coursesFlexBoxLayout.getChildCount() == 0){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"No Courses","Please add at least one course ..");
        }
        else {
            if(Integer.parseInt(duration) <= 0 || Integer.parseInt(duration) > 12){
                MyAlertDialog.showCustomAlertDialogLoginError(this,"Wrong Duration","Please choose a valid number of months , 0 - 12 months ..");
            }
            else {
                if(!checkStartAndEndTime()){
                    MyAlertDialog.showCustomAlertDialogLoginError(this,"Wrong timing","Please Choose valid start and end time, and make sure there are a least one hour..");
                }
                else {
                    StringBuilder coursesStr = new StringBuilder();
                    for(int i=0;i<teacherAddedCoursesList.size();i++){
                        if(i + 1 != teacherAddedCoursesList.size()){
                            coursesStr.append(teacherAddedCoursesList.get(i)).append(" , ");
                        }
                        else {
                            coursesStr.append(teacherAddedCoursesList.get(i));
                        }
                    }
                    tpr = new TeacherPostRequest(lastTeacherPostId+1,
                            email,coursesStr.toString(),educationalLevel,duration,staticAvailability,location,teachingMethod,startTime,endTime);
                    database.insertTeacherPostRequest(tpr,this);
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


    private boolean checkIfCourseAddedToList(String selectedCourse){
        for (String str : teacherAddedCoursesList){
            if(str.equalsIgnoreCase(selectedCourse)){
                return true ;
            }
        }
        return false ;
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }





    @Override
    public void onResult(int resultFlag) {
        if(resultFlag == 1){
            MyAlertDialog.showCustomDialogForTeacherAccountConfirmed(this);
            doneInformation="1";
            teacherInformationPopupWindow.dismiss();
            checkAccountDone();
        }
        else if(resultFlag == 0){
            // account already confirmed
            MyAlertDialog.teacherAccountAlreadyConfirmed(this);
        }
        else {
            MyAlertDialog.showCustomAlertDialogLoginError(this,this.getString(R.string.errorOccurredTitle),this.getString(R.string.errorOccurredMsg));
        }
    }





    @Override
    public void updateTeacherNotifications(int resultFlag,int notificationId) {
        if(resultFlag == 0){
            notList.add(new Notifications(1,"Confirm Account","Your Account is not Confirmed... Click here to confirm it",0));
            binding.numOfNotifications.setText(""+notList.size());
        }
        else if(resultFlag == 1){
            database.deleteConfirmedAccountNotification(notificationId,this);
        }
        else {
            // ToDo("Handle if there are an error")
        }
    }




    @Override
    public void getNotifications(int result, JSONArray notificationsJsonArray) {
        if(result == 1){
            List<Notifications> notificationsList=new ArrayList<>();
            try {
                for(int i=notificationsJsonArray.length() - 1; i >= 0 ;i--){
                    JSONObject jsonObject = notificationsJsonArray.getJSONObject(i);
                    Notifications notification = new Notifications(Integer.parseInt(jsonObject.getString("notificationType")),jsonObject.getString("notificationTitle"),
                            jsonObject.getString("notificationBody"),
                            Integer.parseInt(jsonObject.getString("isRead")));
                    notificationsList.add(notification);
                }
                if(!notList.isEmpty()){
                    notList.clear();
                }
                notList.addAll(notificationsList);
                if(!notList.isEmpty()){
                    binding.numOfNotifications.setText(""+notList.size());
                }
                else
                    binding.numOfNotifications.setText("");
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
            updateNotificationsPopUpWindow();
            notificationsPopupWindowBinding.refreshNotificationsRecyclerView.setRefreshing(false);
        }
        else {
            notList=null;
        }
    }

    private void updateNotificationsPopUpWindow(){
        if(!notList.isEmpty()){
            notificationsPopupWindowBinding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            notificationsPopupWindowBinding.notificationsRecyclerView.setAdapter(new NotificationsAdapter(notList, this));
            notificationsPopupWindowBinding.noNotificationsText.setVisibility(View.GONE);
            notificationsPopupWindowBinding.notificationsRecyclerView.setVisibility(View.VISIBLE);
        }
        else {
            notificationsPopupWindowBinding.noNotificationsText.setVisibility(View.VISIBLE);
            notificationsPopupWindowBinding.notificationsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void confirmNotificationDeleted(int flag) {
        if(flag==1){
            notList.clear();
            database.getNotifications(email,this);
        }
        else {
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Error","Something went wrong..There are an error occurred");
        }
    }

    public void updateNotificationsList(ArrayList<Notifications> newNotifications){
        Toast.makeText(this,"Notificatons updated ..",Toast.LENGTH_SHORT).show();
        Log.d("----------->service -->"+newNotifications,"----------->service -->"+newNotifications);
    }

    private void startNotificationsService(){
        Intent serviceIntent = new Intent(this, NotificationsService.class);
        serviceIntent.putExtra("email", email);
        startService(serviceIntent);
    }

    @Override
    public void onMatchingAdded(int resultFlag) {

    }

    @Override
    public void getTeacherMatchingData(int resultFlag, JSONArray teacherMatchingData) {
        if(resultFlag == 0){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Error","An Error occurred fetching the data , please try again later");
        }
        else if(resultFlag == -2){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Network Error","A Network Error occurred,Please try again later");
        }
        else if(resultFlag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Request Error","Something went wrong please try again later");
        }
        else if(resultFlag == 1) {
            try {
                if(!teacherMatchingDataToPassToTeacherFragment.isEmpty()){
                    teacherMatchingDataToPassToTeacherFragment.clear();
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
                    teacherMatchingDataToPassToTeacherFragment.add(teacherMatchModel);
                }
                loadTeacherFragment();

            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public void startCardViewData(TeacherMatchModel teacherMatchModel) {
            if(teacherMatchModel != null){
                teacherMatchModelTemp = teacherMatchModel;
                binding.progressBarLayout.setVisibility(View.VISIBLE);
                binding.overlayView.setVisibility(View.VISIBLE);
                database.getParentInformation(teacherMatchModel.getParentEmail(),this);
                ///showTeacherMatchDialog(teacherMatchModel);
            }
    }

    private void showTeacherMatchDialog(TeacherMatchModel teacherMatchModel,String parentName ,List<String> parentPhoneNumbers){
        DialogTeacherMatchingOnCardClickedBinding dialogTeacherMatchingOnCardClickedBinding = DialogTeacherMatchingOnCardClickedBinding.inflate(LayoutInflater.from(this));
        Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogTeacherMatchingOnCardClickedBinding.getRoot());
        //database.getParentInformation(teacherMatchModel.getParentEmail(),this);

        dialog.setCancelable(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = 1250;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        if(dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();

        dialogTeacherMatchingOnCardClickedBinding.sendRequestBtn.setOnClickListener(x->{
            Toast.makeText(this,"123123",Toast.LENGTH_SHORT).show();
        });

        dialogTeacherMatchingOnCardClickedBinding.closeImageView.setOnClickListener(z->{
            dialog.dismiss();
        });

        setDataToMatchDialog(dialogTeacherMatchingOnCardClickedBinding,teacherMatchModel,parentName,parentPhoneNumbers);
    }

    private void setDataToMatchDialog(DialogTeacherMatchingOnCardClickedBinding dialogTeacherMatchingOnCardClickedBinding, TeacherMatchModel teacherMatchModel,String parentName,List<String> parentPhoneNumbers){
        dialogTeacherMatchingOnCardClickedBinding.childNameTextView.setText(teacherMatchModel.getChildren().getChildName());
        dialogTeacherMatchingOnCardClickedBinding.parentNameTextView.setText(parentName);
        dialogTeacherMatchingOnCardClickedBinding.parentEmailTextView.setText(teacherMatchModel.getParentEmail());
        StringBuilder phoneNumbers = new StringBuilder();
        if(parentPhoneNumbers.size() > 1){
            for(int i=0;i<parentPhoneNumbers.size();i++){
                if( i + 1 != parentPhoneNumbers.size())
                    phoneNumbers.append(parentPhoneNumbers.get(i)).append(" , ");
                else
                    phoneNumbers.append(parentPhoneNumbers.get(i));
            }
        }
        else
            phoneNumbers.append(parentPhoneNumbers.get(0));
        dialogTeacherMatchingOnCardClickedBinding.parentPhoneNumberTextView.setText(phoneNumbers.toString());
        dialogTeacherMatchingOnCardClickedBinding.coursesTextView.setText(teacherMatchModel.getCourses());
        dialogTeacherMatchingOnCardClickedBinding.choseDaysTextView.setText(teacherMatchModel.getChoseDays());
        dialogTeacherMatchingOnCardClickedBinding.teachingMethodTextView.setText(teacherMatchModel.getTeachingMethod());
        dialogTeacherMatchingOnCardClickedBinding.timeTextView.setText(teacherMatchModel.getStartTime() + " - "+teacherMatchModel.getEndTime());
        dialogTeacherMatchingOnCardClickedBinding.locationTextView.setText(teacherMatchModel.getLocation());
    }

    private void loadFragmentTeacherMatchFragment(TeacherMatchModel teacherMatchModel){
        TeacherMatchFragment teacherMatchFragment = new TeacherMatchFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("teacherMatchData",teacherMatchModel);
        teacherMatchFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragments_container,teacherMatchFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
                        String parentFirstName = jsonObject.getString("firstname").toLowerCase();
                        String parentLastName = jsonObject.getString("lastname").toLowerCase();
                        parentFirstName = parentFirstName.substring(0,1).toUpperCase()+parentFirstName.substring(1).toLowerCase();
                        parentLastName = parentLastName.substring(0,1).toUpperCase()+parentLastName.substring(1).toLowerCase();

                        String parentBirthDate = jsonObject.getString("birthDate");
                        int genderVal = jsonObject.getInt("gender");
                        String gender = "Male";
                        if(genderVal == 0)
                            gender = "Female";
                        String parentIdNumber = jsonObject.getString("idNumber");
                        String city = jsonObject.getString("city");
                        String country = jsonObject.getString("country");
                        String parentPhoneNumber = jsonObject.getString("phoneNumber");
                        List<String> phoneList = new ArrayList<>();
                        phoneList.add(parentPhoneNumber);
                        tempFirstName = parentFirstName;
                        tempLastName = parentLastName;
                        tempPhoneList = phoneList;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.progressBarLayout.setVisibility(View.GONE);
                            binding.overlayView.setVisibility(View.GONE);
                            showTeacherMatchDialog(teacherMatchModelTemp,tempFirstName+" "+tempLastName,tempPhoneList);
                        }
                    },1500);
                        //showTeacherMatchDialog(teacherMatchModelTemp,parentFirstName+" "+parentLastName,phoneList);
                }
                else {
                    String parentFirstName = "";
                    String parentLastName = "";
                    String parentIdNumber = "";
                    String parentBirthDate ="";
                    int genderVal = 1 ;
                    String gender = "Male";
                    List<Address> addressList = new ArrayList<>();
                    List<String> phoneNumberList = new ArrayList<>();
                    for(int i=0;i<parentInformation.length();i++){
                        JSONObject jsonObject = parentInformation.getJSONObject(i);
                        if(i==0){
                            parentFirstName = jsonObject.getString("firstname").toLowerCase();
                            parentFirstName = parentFirstName.substring(0,1).toUpperCase()+parentFirstName.substring(1).toLowerCase();
                            parentLastName = jsonObject.getString("lastname").toLowerCase();
                            parentLastName = parentLastName.substring(0,1).toUpperCase()+parentLastName.substring(1).toLowerCase();

                            parentBirthDate = jsonObject.getString("birthDate");
                            genderVal = jsonObject.getInt("gender");
                            if(genderVal == 0)
                                gender = "Female";
                            parentIdNumber = jsonObject.getString("idNumber");
                        }
                            String city = jsonObject.getString("city");
                            String country = jsonObject.getString("country");
                            String parentPhoneNumber = jsonObject.getString("phoneNumber");
                            if(!checkAddressExistsInList(addressList,new Address(city,country))){
                                addressList.add(new Address(city,country));
                            }
                            if(!checkPhoneExistsInList(phoneNumberList , parentPhoneNumber)){
                                phoneNumberList.add(parentPhoneNumber);
                            }
                    }
                    tempFirstName = parentFirstName;
                    tempLastName = parentLastName;
                    tempPhoneList = phoneNumberList;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.progressBarLayout.setVisibility(View.GONE);
                            binding.overlayView.setVisibility(View.GONE);
                            showTeacherMatchDialog(teacherMatchModelTemp,tempFirstName+" "+tempLastName,tempPhoneList);
                        }
                    },1500);
                    //showTeacherMatchDialog(teacherMatchModelTemp,parentFirstName+" "+parentLastName,phoneList);
                }
            }
            catch (Exception e){

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
    public void onTeacherPostAdded(int flag) {
        if(flag == 1){
            MyAlertDialog.showDialogForDone(this,"Done","Your post has been added done, you can find it the posted part");
            teacherLooksForJobDialog.dismiss();
           Intent intent = new Intent();
           intent.setAction("UPDATE_TEACHER_POSTED_REQUESTS");
            //intent.putExtra("newAddedJobRequest",tpr);
           sendBroadcast(intent);
        }
        else if(flag == -2){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Connection Error","Something went wrong with your connection please try again later ..");
        }

        else {
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Error","Something went wrong please try again later ..");
        }
    }

    @Override
    public void onLastMatchingIdFetched(int flag, String value) {
        if(flag == 1){
            lastTeacherPostId = Integer.parseInt(value);
        }
        else if(flag == 0){
            lastTeacherPostId = 0 ;
        }
        else {
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Error","An error occurred please try again later ..");
            System.exit(-2);
        }
    }

    @Override
    public void onAvailabilityFetched(int flag, String availability) {
        if(flag == 0){

        }
        else if(flag == -1){

        }
        else if(flag == 1){
            staticAvailability = availability;
        }
        else {

        }
    }
}