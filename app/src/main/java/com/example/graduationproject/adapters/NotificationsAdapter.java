package com.example.graduationproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.databinding.NotificationsCardLayoutBinding;
import com.example.graduationproject.listeners.NotificationClickListener;
import com.example.graduationproject.models.Notifications;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{
    private List<Notifications> notificationsList;
    private Context context;
    private NotificationClickListener clickListener;
    public NotificationsAdapter(List<Notifications> notificationsList, Context context, NotificationClickListener clickListener) {
        this.notificationsList = notificationsList;
        this.context=context;
        this.clickListener=clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        NotificationsCardLayoutBinding binding = NotificationsCardLayoutBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding,context,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notifications notification = notificationsList.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        NotificationsCardLayoutBinding binding;
        Context context;
        NotificationClickListener clickListener;
        public ViewHolder(NotificationsCardLayoutBinding binding,Context context,NotificationClickListener listener) {
            super(binding.getRoot());
            this.binding=binding;
            this.context=context;
            this.clickListener=listener;
        }
        public void bind(Notifications notification){
            binding.notificationTitle.setText(notification.getNotificationTitle());
            binding.notificationBody.setText(notification.getNotificationBody());


            binding.notificationLayout.setOnClickListener(v ->{
                binding.notificationLayout.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                clickListener.onNotificationClicked(notification);
            });

            if(notification.getIsNotificationRead()==0){
                binding.notificationLayout.setCardBackgroundColor(Color.parseColor("#E1E3E4"));
            }

            else if(notification.getIsNotificationRead()==1){
                binding.notificationLayout.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }


          /*  if(notification.getNotificationType()==2){ // parent sent request to teacher (this will be shown in the teacher notifications)
                binding.notificationLayout.setBackgroundColor(context.getColor(R.color.white));
                binding.notificationLayout.setBackgroundDrawable(AppCompatResources.getDrawable(context,R.drawable.rounded_corner_read_notification));
            }
            else if(notification.getNotificationType()==3){ // teacher sent request to parent (this will be shown in the parent notifications)
                binding.notificationLayout.setBackgroundColor(context.getColor(R.color.white));
                binding.notificationLayout.setBackgroundDrawable(AppCompatResources.getDrawable(context,R.drawable.rounded_corner_read_notification));
            }
            else if(notification.getNotificationType()==7){ // teacher sent request to parent (this will be shown in the parent notifications)
                binding.notificationLayout.setBackgroundColor(context.getColor(R.color.white));
                binding.notificationLayout.setBackgroundDrawable(AppCompatResources.getDrawable(context,R.drawable.rounded_corner_read_notification));
            }
            else if(notification.getNotificationType()==8){ // teacher sent request to parent (this will be shown in the parent notifications)
                binding.notificationLayout.setBackgroundColor(context.getColor(R.color.white));
                binding.notificationLayout.setBackgroundDrawable(AppCompatResources.getDrawable(context,R.drawable.rounded_corner_read_notification));
            }*/
        }
    }
}
