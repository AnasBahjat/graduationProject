package com.example.graduationproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.ParentReceivedRequestsCardLayoutBinding;
import com.example.graduationproject.listeners.OnAcceptDeclineParentRequestsListener;
import com.example.graduationproject.listeners.OnAcceptDeclineTeacherRequestsListener;
import com.example.graduationproject.models.Parent;
import com.example.graduationproject.models.ParentReceivedRequest;
import com.example.graduationproject.ui.parentUi.ParentFragment;

import java.util.List;

public class ParentReceivedRequestAdapter extends RecyclerView.Adapter<ParentReceivedRequestAdapter.MyViewHolder>{



    public List<ParentReceivedRequest> parentReceivedRequestList ;
    public ParentReceivedRequestsCardLayoutBinding binding ;
    public OnAcceptDeclineParentRequestsListener onAcceptDeclineClicked;

    public ParentReceivedRequestAdapter(List<ParentReceivedRequest> parentReceivedRequestList,final OnAcceptDeclineParentRequestsListener onAcceptDeclineClicked){
        this.parentReceivedRequestList = parentReceivedRequestList;
        this.onAcceptDeclineClicked=onAcceptDeclineClicked;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ParentReceivedRequestsCardLayoutBinding.inflate(inflater,parent,false);
        return new MyViewHolder(binding,onAcceptDeclineClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ParentReceivedRequest parentReceivedRequest = parentReceivedRequestList.get(position);
        holder.bind(parentReceivedRequest);
    }

    @Override
    public int getItemCount() {
        return parentReceivedRequestList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ParentReceivedRequestsCardLayoutBinding binding;
        OnAcceptDeclineParentRequestsListener onAcceptDeclineParentRequestsListener;

        public MyViewHolder(ParentReceivedRequestsCardLayoutBinding binding,OnAcceptDeclineParentRequestsListener onAcceptDeclineParentRequestsListener) {
            super(binding.getRoot());
            this.binding=binding;
            this.onAcceptDeclineParentRequestsListener = onAcceptDeclineParentRequestsListener;
        }
        public void bind(ParentReceivedRequest parentReceivedRequest){
            String phoneString = "";
            binding.teacherNameTextView.setText(parentReceivedRequest.getTeacher().getTeacherName());
            List<String> phoneList = parentReceivedRequest.getTeacher().getPhoneNumbersList();
            for(int i = 0 ; i <phoneList.size();i++){
                if(i + 1 != phoneList.size() - 1){
                    phoneString += phoneList.get(i)+" ,";
                }
                else {
                    phoneString+= phoneList.get(i);
                }
            }
            binding.phoneNumberTextView.setText(phoneString);
            binding.coursesTextView.setText(parentReceivedRequest.getTeacherMatchModel().getCourses());
            binding.dateTextView.setText(String.format("%s - %s", parentReceivedRequest.getTeacherMatchModel().getStartDate(), parentReceivedRequest.getTeacherMatchModel().getEndDate()));
            binding.daysTextView.setText(parentReceivedRequest.getTeacherMatchModel().getChoseDays());
            binding.timeTextView.setText(String.format("%s - %s", parentReceivedRequest.getTeacherMatchModel().getStartTime(), parentReceivedRequest.getTeacherMatchModel().getEndTime()));
            binding.locationTextView.setText(parentReceivedRequest.getTeacherMatchModel().getLocation());
            binding.teachingMethodTextView.setText(parentReceivedRequest.getTeacherMatchModel().getTeachingMethod());
            binding.priceTextView.setText(String.format("%s$  -  %s", parentReceivedRequest.getTeacherMatchModel().getPriceMinimum(), parentReceivedRequest.getTeacherMatchModel().getPriceMaximum()));
            binding.acceptBtn.setOnClickListener(z->{
                onAcceptDeclineParentRequestsListener.onParentAcceptDeclineClicked(1,parentReceivedRequest);
            });
            binding.declineBtn.setOnClickListener(v->{
                onAcceptDeclineParentRequestsListener.onParentAcceptDeclineClicked(0,parentReceivedRequest);
            });
        }
    }
}
