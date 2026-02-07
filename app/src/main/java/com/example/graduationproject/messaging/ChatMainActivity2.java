package com.example.graduationproject.messaging;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.databinding.ActivityRegisterBinding;
import com.example.graduationproject.databinding.ChatsActivityBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatMainActivity2 extends AppCompatActivity {

    private RecyclerView recyclerView;
    ChatsAdapter chatsAdapter;
    List<ChatUser> usersList = new ArrayList<>();
    Map<String, MessageModel> lastMessagesMap = new HashMap<>();
    String currentUserId;
    DatabaseReference usersRef, chatsRef;
    Set<String> loadedUsers;
    ImageView backImage;
    String currentUserName ;
    TextView userNameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_main2);
        init();
    }

    private void init(){
        backImage = findViewById(R.id.logoutimg);
        recyclerView = findViewById(R.id.chatsRecycler);
        currentUserName = getIntent().getStringExtra("userName");
        userNameTextView = findViewById(R.id.userName);
        userNameTextView.setText(currentUserName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadedUsers = new HashSet<>();
        currentUserId = FirebaseAuth.getInstance().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
        chatsAdapter = new ChatsAdapter(this, usersList, lastMessagesMap);
        recyclerView.setAdapter(chatsAdapter);
        loadUserChats();
    }

    /*private void loadUsers(){
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ChatUser user = ds.getValue(ChatUser.class);
                    if(!user.getUserId().equals(currentUserId)){
                        usersList.add(user);
                        loadLastMessage(user.getUserId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void loadUserChats(){
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                lastMessagesMap.clear();
                loadedUsers.clear();
                for(DataSnapshot chatSnap : snapshot.getChildren()){
                    String chatId = chatSnap.getKey();
                    if(chatId.contains(currentUserId)){
                        String otherUserId = chatId.replace(currentUserId, "");
                        if(!loadedUsers.contains(otherUserId)){
                            loadedUsers.add(otherUserId);
                            fetchUser(otherUserId, chatId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchUser(String otherUserId, String chatId){
        usersRef.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ChatUser user = snapshot.getValue(ChatUser.class);
                    usersList.add(user);
                    loadLastMessage(chatId, otherUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadLastMessage(String chatId, String otherUserId){
        chatsRef.child(chatId).child("messages")
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            MessageModel msg = ds.getValue(MessageModel.class);
                            lastMessagesMap.put(otherUserId, msg);
                            chatsAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    public void finishChatActivity(View view) {
        finish();
    }

    /*private void loadLastMessage(String otherUserId){
        String chatId1 = currentUserId + otherUserId;
        String chatId2 = otherUserId + currentUserId;


        chatsRef.child(chatId1).child("messages")
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(lastMessageListener(otherUserId, chatId2));

        chatsRef.child(chatId2).child("messages")
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(lastMessageListener(otherUserId, chatId1));
    }

    private ValueEventListener lastMessageListener(String userId, String fallbackChatId){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        MessageModel msg = ds.getValue(MessageModel.class);
                        lastMessagesMap.put(userId, msg);
                        chatsAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
    }*/
}