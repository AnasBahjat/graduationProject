package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.NotificationsCardLayoutBinding;
import com.example.graduationproject.models.Notifications;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{
    private List<Notifications> notificationsList;
    public NotificationsAdapter(List<Notifications> notificationsList) {
        this.notificationsList = notificationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        NotificationsCardLayoutBinding binding = NotificationsCardLayoutBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding);
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
        public ViewHolder(NotificationsCardLayoutBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
        public void bind(Notifications notification){
            binding.notificationTitle.setText(notification.getNotificationTitle());
            binding.notificationBody.setText(notification.getNotificationBody());
        }
    }
}
