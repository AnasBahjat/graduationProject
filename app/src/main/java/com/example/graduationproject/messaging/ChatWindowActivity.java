//package com.example.graduationproject.messeging;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.graduationproject.R;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class chatwindo extends AppCompatActivity {
//    String reciverimg, reciverUid, reciverName, SenderUID;
//    CircleImageView profile;
//    TextView reciverNName;
//    FirebaseDatabase database;
//    FirebaseAuth firebaseAuth;
//    public static String senderImg;
//    public static String reciverIImg;
//    CardView sendbtn;
//    EditText textmsg;
//
//    String senderRoom, reciverRoom;
//    RecyclerView messageAdpter;
//    ArrayList<msgModelclass> messagesArrayList;
//    messagesAdpter mmessagesAdpter;
//    ChatViewModel chatViewModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chatwindo);
//
//        database = FirebaseDatabase.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        reciverName = getIntent().getStringExtra("nameeee");
//        reciverimg = getIntent().getStringExtra("reciverImg");
//        reciverUid = getIntent().getStringExtra("uid");
//
//        messagesArrayList = new ArrayList<>();
//
//        sendbtn = findViewById(R.id.sendbtnn);
//        textmsg = findViewById(R.id.textmsg);
//        reciverNName = findViewById(R.id.recivername);
//        profile = findViewById(R.id.profileimgg);
//        messageAdpter = findViewById(R.id.msgadpter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setStackFromEnd(true);
//        messageAdpter.setLayoutManager(linearLayoutManager);
//        mmessagesAdpter = new messagesAdpter(chatwindo.this, messagesArrayList);
//        messageAdpter.setAdapter(mmessagesAdpter);
//
//        Picasso.get().load(reciverimg).into(profile);
//        reciverNName.setText(reciverName);
//
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            SenderUID = auth.getCurrentUser().getUid();
//        } else {
//            return;
//        }
//
//        senderRoom = SenderUID + reciverUid;
//        reciverRoom = reciverUid + SenderUID;
//       // updateReadStatus();
//
//
//        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
//
//        //DatabaseReference chatreference1 = database.getReference().child("chats").child(reciverRoom).child("messages");
//
//        // Initialize the ViewModel and observe the unread message count
//       // chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
//       // chatViewModel.fetchUnreadMessages(SenderUID);
//       // chatViewModel.getUnreadMessageCount().observe(this, new Observer<Integer>() {
//        //    @Override
//        //    public void onChanged(Integer unreadCount) {
//                // Update UI with unreadCount if needed
//         //       Log.d("UnreadCount", "Unread messages: " + unreadCount);
//       //     }
//      //  });
//
//
//        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages");
//
//        chatReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                messagesArrayList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
//                    if (messages != null) {
//                        ////////////////////
//                        messagesArrayList.add(messages);
//                        Log.d("msgad", "msg adapter run: " + "++++++++++++++++++++++++++++++++++++++++++++++++");
//                        ///////////
//                        String messageId = dataSnapshot.getKey();
//                        DatabaseReference messageReference = dataSnapshot.getRef();
//
//                        Map<String, Object> updates = new HashMap<>();
//                        updates.put("read", true);  // Set the new value for the 'read' attribute
//
//                        messageReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d("Firebase", "Message updated successfully.");
//                                } else {
//                                    Log.e("Firebase", "Failed to update message: " + task.getException().getMessage());
//                                }
//                            }
//                        });
//                        ////////////
//                    }
//                }
//                mmessagesAdpter.notifyDataSetChanged();// Notify adapter after updating data
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle database error if necessary
//            }
//        });
//
////        chatreference1.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
////                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
////                    if (messages!= null) {
////                        if (!messages.isRead() && messages.getReceiverId().equals(SenderUID)) {
////                            messages.setRead(true);
////                          //  dataSnapshot.getRef().setValue(messages);
////                        }
////                    //    messagesArrayList.add(messages);
////                    }
////                }
////               // mmessagesAdpter.notifyDataSetChanged();
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////            }
////        });
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                senderImg = snapshot.child("profilepic").getValue().toString();
//                reciverIImg = reciverimg;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//
//        sendbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String message = textmsg.getText().toString();
//                if (message.isEmpty()) {
//                    Toast.makeText(chatwindo.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                textmsg.setText("");
//                Date date = new Date();
//                msgModelclass messagess = new msgModelclass(message, SenderUID, reciverUid, date.getTime(),false);
//
//                database.getReference().child("chats")
//                        .child(senderRoom)
//                        .child("messages")
//                        .push()
//                        .setValue(messagess)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    // Create a new message instance for the receiver with isRead set to false
//                                    msgModelclass messagess1 = new msgModelclass(message, SenderUID, reciverUid, date.getTime(),false);
//                                    database.getReference().child("chats")
//                                            .child(reciverRoom)
//                                            .child("messages")
//                                            .push()
//                                            .setValue(messagess1)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (!task.isSuccessful()) {
//                                                        Toast.makeText(chatwindo.this, "Failed to send message to receiver", Toast.LENGTH_SHORT).show();
//                                                        Log.e("Firebase", "Failed to send message to receiver", task.getException());
//                                                    }
//                                                }
//                                            });
//                                } else {
//                                    Toast.makeText(chatwindo.this, "Failed to send message", Toast.LENGTH_SHORT).show();
//                                    Log.e("Firebase", "Failed to send message", task.getException());
//                                }
//                            }
//                        });
//
//            }
//
//        });
//
//    }
//    private void updateReadStatus() {
//        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child("messages");
//
//        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
//                    if (messages != null && !messages.isRead() && messages.getReceiverId().equals(SenderUID)) {
//                        String messageId = dataSnapshot.getKey();
//                        DatabaseReference messageReference = dataSnapshot.getRef();
//
//                        Map<String, Object> updates = new HashMap<>();
//                        updates.put("read", true);  // Set the new value for the 'read' attribute
//
//                        messageReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d("Firebase", "Message updated successfully.");
//                                } else {
//                                    Log.e("Firebase", "Failed to update message: " + task.getException().getMessage());
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle database error if necessary
//            }
//        });
//    }
//
//}
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
import com.example.graduationproject.errorHandling.MyAlertDialog;
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
    String reciverimg, reciverUid, reciverName,receiverEmail, senderUID;
    CircleImageView profile;
    TextView reciverNName;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    public static String senderImg;
    public static String reciverIImg;
    CardView sendbtn;
    EditText textmsg;

    String senderRoom, reciverRoom;
    RecyclerView messagesRecyclerView;
    ArrayList<msgModelclass> messagesArrayList;
    messagesAdpter messagesAdapter;
    ImageView backImage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatwindo);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        reciverName = getIntent().getStringExtra("nameeee");
        receiverEmail = getIntent().getStringExtra("email");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        reciverNName = findViewById(R.id.recivername);
        profile = findViewById(R.id.profileimgg);
        messagesRecyclerView = findViewById(R.id.msgadpter);
        backImage = findViewById(R.id.back_arrow);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter = new messagesAdpter(ChatWindowActivity.this, messagesArrayList);
        messagesRecyclerView.setAdapter(messagesAdapter);

        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText(receiverEmail);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            senderUID = auth.getCurrentUser().getUid();
        } else {
            return;
        }

        backImage.setOnClickListener(v->{
            finish();
        });
        Log.d("Sender UID --------> "+senderUID,"Sender UID --------> "+senderUID);
        Log.d("Reciever UID --------> "+reciverUid,"Reciver Uid --------> "+reciverUid);

        senderRoom = senderUID + reciverUid;
        reciverRoom = reciverUid + senderUID;

        DatabaseReference reference = database.getReference().child("users").child(firebaseAuth.getUid());

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
                messagesAdapter.notifyDataSetChanged();
                messagesRecyclerView.scrollToPosition(messagesArrayList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if necessary
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.child("profilePic").exists()) {
                    senderImg = snapshot.child("profilePic").getValue(String.class);
                } else {
                    senderImg = "https://firebasestorage.googleapis.com/v0/b/graduationproject-81f3e.appspot.com/o/user.png?alt=media&token=014e8f21-6436-4de5-b52b-e61a685a4dbd"; // or default image URL
                }

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
                msgModelclass messages = new msgModelclass(message, senderUID, reciverUid, date.getTime(), true);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Create a new message instance for the receiver with isRead set to false
                                    msgModelclass message1 = new msgModelclass(message, senderUID, reciverUid, date.getTime(), false);
                                    database.getReference().child("chats")
                                            .child(reciverRoom)
                                            .child("messages")
                                            .push()
                                            .setValue(message1)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        MyAlertDialog.showCustomAlertDialogSpinnerError(ChatWindowActivity.this, "Message Error", "Failed to send message to receiver ...");
                                                        Toast.makeText(ChatWindowActivity.this, "Failed to send message to receiver", Toast.LENGTH_SHORT).show();
                                                        Log.e("Firebase", "Failed to send message to receiver", task.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    MyAlertDialog.showCustomAlertDialogSpinnerError(ChatWindowActivity.this, "Message Error", "Failed to send message to receiver ...");
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
