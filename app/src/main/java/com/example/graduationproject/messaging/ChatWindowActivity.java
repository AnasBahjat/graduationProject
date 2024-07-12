package com.example.graduationproject.messaging;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class ChatWindowActivity extends AppCompatActivity {
    String reciverimg, reciverUid, reciverName, senderUID;
    CircleImageView profile;
    TextView reciverNName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String senderImg;
    public static String reciverIImg;
    CardView sendbtn;
    EditText textmsg;







    String senderRoom, reciverRoom;
    RecyclerView messageAdpter;
    ArrayList<msgModelclass> messagesArrayList;
    messagesAdpter mmessagesAdpter;
    ImageView backImage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatwindo);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        reciverNName = findViewById(R.id.recivername);
        profile = findViewById(R.id.profileimgg);
        messageAdpter = findViewById(R.id.msgadpter);
        backImage = findViewById(R.id.back_arrow);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdpter.setLayoutManager(linearLayoutManager);
        mmessagesAdpter = new messagesAdpter(ChatWindowActivity.this, messagesArrayList);
        messageAdpter.setAdapter(mmessagesAdpter);

        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText(reciverName);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            senderUID = auth.getCurrentUser().getUid();
            Log.d("current user ---> "+auth.getCurrentUser().getUid(),"current user ---> "+auth.getCurrentUser().getUid());
        } else {
            return;
        }

        backImage.setOnClickListener(v->{
            updateReadStatus();
            finish();
        });

        senderRoom = senderUID + reciverUid;
        reciverRoom = reciverUid + senderUID;

        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());

        // Call the method to update the read status
        updateReadStatus();

        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    if (messages != null) {
                        messagesArrayList.add(messages);
                        Log.d("msgad", "msg adapter run: " + "++++++++++++++++++++++++++++++++++++++++++++++++");
                    }
                }
                mmessagesAdpter.notifyDataSetChanged(); // Notify adapter after updating data
                messageAdpter.scrollToPosition(messagesArrayList.size() - 1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if necessary
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue().toString();
                reciverIImg = reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatWindowActivity.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmsg.setText("");
                Date date = new Date();
                msgModelclass messagess = new msgModelclass(message, senderUID, reciverUid, date.getTime(), true);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messagess)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Create a new message instance for the receiver with isRead set to false
                                    msgModelclass messagess1 = new msgModelclass(message, senderUID, reciverUid, date.getTime(), false);
                                    database.getReference().child("chats")
                                            .child(reciverRoom)
                                            .child("messages")
                                            .push()
                                            .setValue(messagess1)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Toast.makeText(ChatWindowActivity.this, "Failed to send message to receiver", Toast.LENGTH_SHORT).show();
                                                        Log.e("Firebase", "Failed to send message to receiver", task.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(ChatWindowActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                                    Log.e("Firebase", "Failed to send message", task.getException());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        updateReadStatus();
        super.onBackPressed();
    }


    private void updateReadStatus() {
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    if (messages != null && !messages.isRead() && messages.getReceiverId().equals(senderUID)) {
                        String messageId = dataSnapshot.getKey();
                        DatabaseReference messageReference = dataSnapshot.getRef();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("read", true);  // Set the new value for the 'read' attribute

                        messageReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Firebase", "Message updated successfully.");
                                } else {
                                    Log.e("Firebase", "Failed to update message: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if necessary
            }
        });
    }
}
