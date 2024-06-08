package com.example.graduationproject.ui.parentUi;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.NotificationsAdapter;
import com.example.graduationproject.broadcastReceiver.BroadcastHandler;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.ActivityParentBinding;
import com.example.graduationproject.databinding.ChildrenInformationPopupWindowBinding;
import com.example.graduationproject.databinding.NotificationsPopupWindowBinding;
import com.example.graduationproject.databinding.ParentInformationPopupWindowBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.NotificationsListListener;
import com.example.graduationproject.listeners.UpdateParentInformation;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.Notifications;
import com.example.graduationproject.models.Parent;
import com.example.graduationproject.ui.commonFragment.ProfileFragment;
import com.example.graduationproject.ui.teacherUi.TeacherFragment;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class ParentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,NotificationsListListener, UpdateParentInformation {

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
        //database.getNotifications(email,this);
        notificationsList = new ArrayList<>();
        buildNavigationView();
        checkAccountDone();
        initBroadcastReceiver();
        loadFragment(new ParentFragment());
        parentBinding.notificationImage.setOnClickListener(asd -> {
            showNotificationPopupWindow();
        });
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
            parentBinding.accountIsNotConfirmText.setVisibility(View.GONE);
            parentBinding.fragmentsContainer.setVisibility(View.VISIBLE);
            if(notificationsList.isEmpty()){
                parentBinding.numOfNotifications.setText(null);
            }
            else {
                parentBinding.numOfNotifications.setText(""+notificationsList.size());
            }

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
        notificationPopupWindowBinding = NotificationsPopupWindowBinding.inflate(getLayoutInflater());
        notificationPopupWindow = new PopupWindow(notificationPopupWindowBinding.getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT,1500,true);
        notificationPopupWindowBinding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(notificationsList.isEmpty()){
            notificationPopupWindowBinding.noNotificationsText.setVisibility(View.VISIBLE);
        }
        else {
            notificationPopupWindowBinding.notificationsRecyclerView.setAdapter(new NotificationsAdapter(notificationsList,this));
            notificationPopupWindowBinding.noNotificationsText.setVisibility(View.GONE);
        }
        notificationPopupWindow.showAsDropDown(parentBinding.notificationImage,0,0);
    }

    public void showParentInformationPopupWindow(){
        parentInformationPopupWindowBinding = ParentInformationPopupWindowBinding.inflate(getLayoutInflater());
        parentInformationPopupWindow = new PopupWindow(parentInformationPopupWindowBinding.getRoot(),1300,2000,true);
        parentInformationPopupWindow.showAtLocation(parentInformationPopupWindowBinding.parentInformationLayout, Gravity.CENTER,0,0);
        notificationPopupWindow.dismiss();

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
                if(!(notificationsList.size()==1 && notificationsList.get(0).getNotificationType() == 0)){
                    notificationsList.clear();
                }
                notificationsList.addAll(notList);
                if(!notList.isEmpty()){
                    parentBinding.numOfNotifications.setText(""+notList.size());
                }
                else {
                    parentBinding.numOfNotifications.setText(null);
                }
            }
            catch (JSONException e){
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.logoutId){
            finish();
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
}