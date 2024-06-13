package com.example.graduationproject.ui.teacherUi;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
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
import com.example.graduationproject.backgroundActions.NotificationsService;
import com.example.graduationproject.broadcastReceiver.BroadcastHandler;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.R;
import com.example.graduationproject.databinding.ActivityTeacherBinding;
import com.example.graduationproject.databinding.FragmentTeacherBinding;
import com.example.graduationproject.databinding.NotificationsPopupWindowBinding;
import com.example.graduationproject.databinding.SideNavigationHeaderBinding;
import com.example.graduationproject.databinding.TeacherInformationPopupWindowBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.NotificationsListListener;
import com.example.graduationproject.listeners.TeacherAccountConfirmationListener;
import com.example.graduationproject.models.Address;
import com.example.graduationproject.models.Notifications;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.ui.commonFragment.ProfileFragment;
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

import java.util.ArrayList;
import java.util.List;

public class TeacherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TeacherAccountConfirmationListener, NotificationsListListener {
    private Database database;
    private String firstName,lastName,email,password,birthDate,phoneNumber,city,country,doneInformation;
    private ActivityTeacherBinding binding ;
    private BroadcastHandler broadcastHandler;

    private PopupWindow notificationPopupWindow ;
   // private List<Notifications> notificationsList;
    private PopupWindow teacherInformationPopupWindow ;
    List<Notifications> notList ;
    FragmentTeacherBinding fragmentTeacherBinding ;

    Fragment currentFragment ;
    private NotificationsPopupWindowBinding notificationsPopupWindowBinding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Long timeOutSeconds = 60L;

    PhoneAuthProvider.ForceResendingToken resendingToken;
    String verificationCode;

    private NotificationsAdapter notificationsAdapter;


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
        notificationsPopupWindowBinding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notList=new ArrayList<>();
        buildNavigationView();
        initBroadcastReceiver();
        checkAccountDone();
        loadFragment(new TeacherFragment());
      //  checkIfTeacherAccountConfirmed();
        checkIfItemSelected();
        // startNotificationsService();
    }

    void checkAccountDone(){
        if(Integer.parseInt(doneInformation) == 0){
            binding.accountIsNotConfirmText.setVisibility(View.VISIBLE);
            notList.add(new Notifications(1,"Confirm Account",
                    "Fill in extra information to confirm the account please ...",0));
            binding.numOfNotifications.setText(""+notList.size());
            binding.fragmentsContainer.setVisibility(View.GONE);
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
                    loadFragment(new TeacherFragment());
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
    }


    public void notificationImageClicked(View view) {
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
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = 1500;

        if(!notList.isEmpty()){
            notificationsPopupWindowBinding.notificationsRecyclerView.setAdapter(new NotificationsAdapter(notList,this));
            notificationsPopupWindowBinding.noNotificationsText.setVisibility(View.GONE);
        }
        else {
            notificationsPopupWindowBinding.noNotificationsText.setVisibility(View.VISIBLE);
        }
        notificationPopupWindow = new PopupWindow(notificationsPopupWindowBinding.getRoot(),width,height,true);
        notificationPopupWindow.showAsDropDown(binding.notificationImage,0,0);
    }




    public void updateNotifications(){
        database.getNotifications(email,this);
    }




    public void showTeacherInformationPopupWindow(){
        TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding = TeacherInformationPopupWindowBinding.inflate(getLayoutInflater());
        decrementNotificationsNumber();

        int width = 1290 ;
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
        }
        else if(selectedId == R.id.availWeekend){
            availabilityStr = "Weekend";
        }
        else if(selectedId == R.id.availSpecificDays){
            checkedDays = getCheckedDays(teacherInformationPopupWindowBinding);
            availabilityStr = checkedDays.toString();
        }




        if(!idStr.isEmpty() && !cityText.isEmpty()  && !countryText.isEmpty()
                && !phoneNumberStr.isEmpty() && collegeFlag && fieldFlag ){
                teacherInformationPopupWindowBinding.phoneNumberPrefix.registerCarrierNumberEditText(teacherInformationPopupWindowBinding.edtTextPhoneNumber);
                Teacher teacher=new Teacher(email,idStr,studentOrGraduate+"",
                        graduationYear,collegeStr,fieldStr,availabilityStr,educationalLevel,new Address(cityText,countryText));
                database.updateTeacherInformation(teacher,teacherInformationPopupWindowBinding.phoneNumberPrefix.getFullNumber(),this);
        }
    }

    private StringBuilder getCheckedDays(TeacherInformationPopupWindowBinding bind){
        StringBuilder choosedDays = new StringBuilder();
        if(bind.saturday.isChecked())
            choosedDays.append("Sat,");
        if(bind.sunday.isChecked())
            choosedDays.append("Sun,");
        if(bind.monday.isChecked())
            choosedDays.append("Mon,");
        if(bind.tuesday.isChecked())
            choosedDays.append("Tues,");
        if(bind.wednesday.isChecked())
            choosedDays.append("Wed,");
        if(bind.thursday.isChecked())
            choosedDays.append("Thur,");
        if(bind.friday.isChecked())
            choosedDays.append("Fri");
        return  choosedDays;
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
                for(int i=0;i<notificationsJsonArray.length();i++){
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
            updateNotificationsAdapter();
        }
        else {
            notList=null;
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
}