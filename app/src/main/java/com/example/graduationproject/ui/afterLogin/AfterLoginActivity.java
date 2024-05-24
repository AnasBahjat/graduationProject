package com.example.graduationproject.ui.afterLogin;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

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
import com.example.graduationproject.models.Notifications;
import com.example.graduationproject.ui.teacherFragment.TeacherFragment;
import com.example.graduationproject.ui.login.LoginActivity;
import com.example.graduationproject.broadcastReceiver.BroadcastHandler;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class AfterLoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Database database;
    private String firstName,lastName,email,password,birthDate,phoneNumber,city,country,profileType="1",doneInformation="0";
    private ActivityAfterLoginBinding binding ;
    private BroadcastHandler broadcastHandler;

    private PopupWindow notificationPopupWindow ;


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
        if(profileType.equals("0")) {
            // ToDo (load parent fragment)
        }
        else{
            loadFragment(new TeacherFragment());
        }
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
            binding.numOfNotifications.setText("1");
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
        int width = 1000;
        int height = 1500;
        notificationsPopupWindowBinding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Notifications> notificationsList = new ArrayList<>();
        notificationsList.add(new Notifications(0,"Confirm Your Account","Add the necessary information to complete your account"));
        notificationsList.add(new Notifications(0,"Confirm Your Account","Add the necessary information to complete your account"));
        notificationsList.add(new Notifications(0,"Confirm Your Account","Add the necessary information to complete your account"));
        notificationsList.add(new Notifications(0,"Confirm Your Account","Add the necessary information to complete your account"));
        notificationsPopupWindowBinding.notificationsRecyclerView.setAdapter(new NotificationsAdapter(notificationsList,this));
        notificationPopupWindow = new PopupWindow(notificationsPopupWindowBinding.getRoot(),width,height,true);
        notificationPopupWindow.showAsDropDown(binding.notificationImage,0,0);
    }

    public void showTeacherInformationPopupWindow(){
        TeacherInformationPopupWindowBinding teacherInformationPopupWindowBinding = TeacherInformationPopupWindowBinding.inflate(getLayoutInflater());
        int width = 1300 ;
        int height = 1800;
        PopupWindow teacherInformationPopupWindow = new PopupWindow(teacherInformationPopupWindowBinding.getRoot(),width,height,true);

        teacherInformationPopupWindow.showAtLocation(teacherInformationPopupWindowBinding.teacherInformationLayout, Gravity.CENTER,0,0);
        notificationPopupWindow.dismiss();

        teacherInformationPopupWindowBinding.closeTeacherInformationPopupWindow.setOnClickListener(v ->{
            teacherInformationPopupWindow.dismiss();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastHandler);
    }
}