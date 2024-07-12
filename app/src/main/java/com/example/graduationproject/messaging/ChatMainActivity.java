package com.example.graduationproject.messaging;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.ui.parentUi.ParentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatMainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdpter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView imglogout;
    ImageView cumbut, setbut;
    TextView title;
    String profileType;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        cumbut = findViewById(R.id.camBut);
        setbut = findViewById(R.id.settingBut);
        title = findViewById(R.id.title1);

        usersArrayList = new ArrayList<>();

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdpter(ChatMainActivity.this, usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);
       // FirebaseUser currentUser1 = auth.getCurrentUser();



        DatabaseReference reference = database.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            profileType = dataSnapshot.child("profileType").getValue(String.class);
                            if (profileType != null) {
                                fetchOppositeUsers(profileType);
                                Log.d("ProfileType", "Profile Type: " + profileType);
                                // Use profileType as needed
                            } else {
                                Log.d("ProfileType", "Profile Type not found for user: " + currentUserId);
                            }
                        } else {
                            Log.d("ProfileType", "User not found in database: " + currentUserId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("ProfileType", "Error retrieving profile type: " + databaseError.getMessage());
                    }
                });
                /*usersArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if(!currentUserId.equals(users.userId) && !users.profileType.equalsIgnoreCase(profileType)){
                        usersArrayList.add(users);
                    }
                }
                adapter.notifyDataSetChanged();*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });



        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userReference = database.getReference().child("user").child(currentUser.getUid());
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users user = snapshot.getValue(Users.class);
                    if (user != null) {
                        String userName = user.getUserName();
                        title.setText(userName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        } else {
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Error","Unable to fetch messages , please try again later ...");
        }

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                // This method is called when a new child is added to the reference
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                // This method is called when an existing child is updated
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // This method is called when a child is removed from the reference
                Users removedUser = snapshot.getValue(Users.class);
                for (int i = 0; i < usersArrayList.size(); i++) {
                    if (usersArrayList.get(i).getUserId().equals(removedUser.getUserId())) {
                        usersArrayList.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // This method is called when a child is moved within the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // This method is called when the listener is cancelled
            }
        });

        imglogout = findViewById(R.id.logoutimg);

        imglogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //super.getClass();
                finish(); // This closes the current activity and returns to the previous one
            }
        });

        setbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatMainActivity.this, SettingMessagingActivity.class);
                startActivity(intent);
            }
        });

        cumbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
            }
        });
        DatabaseReference userReference = database.getReference().child("user").child(currentUser.getUid()).child("profileType");
        if (auth.getCurrentUser() == null) {
            super.getClass();
        }
    }
    private void fetchOppositeUsers(String currentUserProfileType) {
        DatabaseReference reference = database.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                usersArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users != null && !currentUserId.equals(users.getUserId()) && !users.getProfileType().equals(currentUserProfileType)) {
                        usersArrayList.add(users);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
