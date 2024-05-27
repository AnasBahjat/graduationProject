package com.example.graduationproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.databinding.NotificationsCardLayoutBinding;
import com.example.graduationproject.models.Notifications;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{
    private List<Notifications> notificationsList;
    private Context context;
    public NotificationsAdapter(List<Notifications> notificationsList,Context context) {
        this.notificationsList = notificationsList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        NotificationsCardLayoutBinding binding = NotificationsCardLayoutBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding,context);
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
        public ViewHolder(NotificationsCardLayoutBinding binding,Context context) {
            super(binding.getRoot());
            this.binding=binding;
            this.context=context;
        }
        public void bind(Notifications notification){
            binding.notificationTitle.setText(notification.getNotificationTitle());
            binding.notificationBody.setText(notification.getNotificationBody());
            binding.notificationLayout.setOnClickListener(v ->{
                notification.setIsNotificationRead(1);
                notificationClicked(notification.getNotificationType());
            });

            if(notification.getIsNotificationRead()==0){
                binding.notificationLayout.setBackgroundColor(context.getColor(R.color.unreadColor));
               // binding.notificationLayout.setBackground(AppCompatResources.getDrawable(context,R.drawable.rounded_corner_unread_notification));
                binding.notificationLayout.setBackgroundDrawable(AppCompatResources.getDrawable(context,R.drawable.rounded_corner_unread_notification));
            }

            if(notification.getIsNotificationRead()==1){
                binding.notificationLayout.setBackgroundColor(context.getColor(R.color.white));
                binding.notificationLayout.setBackgroundDrawable(AppCompatResources.getDrawable(context,R.drawable.rounded_corner_read_notification));

            }
        }

        private void notificationClicked(int notificationType){
            if(notificationType == 1){
                Intent intent = new Intent();
                intent.setAction("showTeacherInformationWindow");
                context.sendBroadcast(intent);
            }
        }
    }
}
