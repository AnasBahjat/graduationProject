package com.example.graduationproject.messaging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<Integer> unreadMessageCount = new MutableLiveData<>();

    public LiveData<Integer> getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void fetchUnreadMessages(String userId) {
        DatabaseReference chatsReference = FirebaseDatabase.getInstance().getReference().child("chats");
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    DataSnapshot messagesSnapshot = chatSnapshot.child("messages");
                    for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                        msgModelclass message = messageSnapshot.getValue(msgModelclass.class);
                        if (message != null && !message.isRead() && message.getReceiverId().equals(userId)) {
                            count++;
                        }
                    }
                }
                unreadMessageCount.setValue(count); // Update LiveData
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatViewModel", "Database error: " + error.getMessage());
            }
        });
    }
}
