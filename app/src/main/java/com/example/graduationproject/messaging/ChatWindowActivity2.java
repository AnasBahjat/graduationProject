package com.example.graduationproject.messaging;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWindowActivity2 extends AppCompatActivity {



    private String receiverImg, receiverUid, receiverName, senderUID;


    private CircleImageView profile;
    private TextView receiverNameTxt;
    private CardView sendBtn;
    private EditText textMsg;
    private ImageView backImage;


    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;


    private String senderRoom, receiverRoom;


    private RecyclerView messageRecycler;
    private ArrayList<msgModelclass> messagesList;
    private messagesAdpter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatwindo);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        receiverName = getIntent().getStringExtra("nameeee");
        receiverImg = getIntent().getStringExtra("reciverImg");
        receiverUid = getIntent().getStringExtra("uid");

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            return;
        }
        senderUID = firebaseAuth.getCurrentUser().getUid();
        senderRoom = senderUID + receiverUid;
        receiverRoom = receiverUid + senderUID;

        sendBtn = findViewById(R.id.sendbtnn);
        textMsg = findViewById(R.id.textmsg);
        receiverNameTxt = findViewById(R.id.recivername);
        profile = findViewById(R.id.profileimgg);
        messageRecycler = findViewById(R.id.msgadpter);
        backImage = findViewById(R.id.back_arrow);

        receiverNameTxt.setText(receiverName);
        if (receiverImg != null && !receiverImg.isEmpty()) {
            Picasso.get().load(receiverImg).into(profile);
        }
        else{
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/graduationproject-81f3e.appspot.com/o/user.png?alt=media&token=014e8f21-6436-4de5-b52b-e61a685a4dbd").into(profile);
        }

        backImage.setOnClickListener(v -> finish());

        messagesList = new ArrayList<>();
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        messageRecycler.setLayoutManager(lm);
        messagesAdapter = new messagesAdpter(this, messagesList);
        messageRecycler.setAdapter(messagesAdapter);
        listenForMessages();
        updateReadStatus();
        sendBtn.setOnClickListener(v -> sendMessage());
    }

    private void listenForMessages(){
        DatabaseReference chatReference = database.getReference()
                .child("chats")
                .child("senderRoom")
                .child("messages");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    msgModelclass msg = ds.getValue(msgModelclass.class);
                    if(msg != null){
                        messagesList.add(msg);
                    }
                }
                messagesAdapter.notifyDataSetChanged();
                messageRecycler.scrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MyAlertDialog.warningDialog(ChatWindowActivity2.this, "Warning", "Error Fetching Messages ..");
            }
        });
    }

    private void sendMessage(){
        String messageText = textMsg.getText().toString().trim();
        if(messageText.isEmpty()){
            Toast.makeText(ChatWindowActivity2.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
            return;
        }

        textMsg.setText("");
        long time = new Date().getTime();
        msgModelclass senderMsg = new msgModelclass(
                messageText,
                senderUID,
                receiverUid,
                time,
                true
        );

        DatabaseReference senderRef = database.getReference()
                .child("chats")
                .child(senderRoom)
                .child("messages")
                .push();

        senderRef.setValue(senderMsg).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                msgModelclass receiverMsg = new msgModelclass(
                        messageText,
                        senderUID,
                        receiverUid,
                        time,
                        false
                );
                database.getReference()
                        .child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .push()
                        .setValue(receiverMsg)
                        .addOnCompleteListener(task1 ->{
                            if(!task.isSuccessful()){
                                MyAlertDialog.showCustomAlertDialogSpinnerError(
                                        ChatWindowActivity2.this,
                                        "Message Error",
                                        "Failed to send message to receiver"
                                );
                            }
                        });
                 }
            else{
                MyAlertDialog.showCustomAlertDialogSpinnerError(
                        ChatWindowActivity2.this,
                        "Message Error",
                        "Failed to send message"
                );
            }
        });
    }
    @Override
    public void onBackPressed(){
        updateReadStatus();
        super.onBackPressed();
    }

    private void updateReadStatus(){
        DatabaseReference chatRef = database.getReference()
                .child("chats")
                .child(senderRoom)
                .child("messages");

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    msgModelclass msg = ds.getValue(msgModelclass.class);
                    if(msg != null && !msg.isRead && senderUID.equals(msg.getReceiverId())){
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("read", true);
                        ds.getRef().updateChildren(updates)
                                .addOnFailureListener( e -> {
                                    Log.e("Firebase", "Read update failed: " + e.getMessage());});
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MyAlertDialog.warning(ChatWindowActivity2.this,"Warning","Unable to fetch data ..");
            }
        });
    }
}