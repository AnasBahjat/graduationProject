package com.example.graduationproject.messaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduationproject.R;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>{

    Context context;
    List<ChatUser> usersList;
    Map<String, MessageModel> lastMessagesMap;


    public ChatsAdapter(Context context, List<ChatUser> usersList, Map<String, MessageModel> lastMessagesMap) {
        this.context = context;
        this.usersList = usersList;
        this.lastMessagesMap = lastMessagesMap;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_card, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatUser user = usersList.get(position);
        holder.userName.setText(user.getUserName());
        MessageModel lastMsg = lastMessagesMap.get(user.getUserId());
        if(lastMsg != null){
            holder.lastMessage.setText(lastMsg.getMessage());
        }
        else{
            holder.lastMessage.setText("");
        }
        // Glide/Picasso can be used for image loading
         Glide.with(context).load(user.getProfilePic()).into(holder.profileImage);


        // TODO: Open chat screen when chat clicked, there are userID, user email within the User object in the adapter.

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatWindowActivity2.class);
            intent.putExtra("nameeee",user.getUserName());
            intent.putExtra("email", user.getMail());
            intent.putExtra("reciverImg",user.getProfilePic());
            intent.putExtra("uid",user.getUserId());
            context.startActivity(intent);
           // Toast.makeText(context, "Chat "+user.getUserName(), Toast.LENGTH_LONG).show();

        });

        /*holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatMainActivity2.class);
            intent.putExtra("userId", user.getUserId());
            context.startActivity(intent);
        });*/
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImage;
        TextView userName, lastMessage;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
