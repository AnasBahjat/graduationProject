package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.CustomJobCardViewBinding;
import com.example.graduationproject.databinding.TeacherReceivedRequestCardLayoutBinding;
import com.example.graduationproject.databinding.TeacherReceivedRequestsDialogLayoutBinding;
import com.example.graduationproject.listeners.OnAcceptDeclineTeacherRequestsListener;
import com.example.graduationproject.models.Job;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.models.TeacherReceivedRequest;

import java.util.List;

public class TeacherReceivedRequestAdapter extends RecyclerView.Adapter<TeacherReceivedRequestAdapter.ViewHolder>{

    public List<TeacherReceivedRequest> teacherReceivedRequestList;
    public TeacherReceivedRequestCardLayoutBinding binding ;
    public OnAcceptDeclineTeacherRequestsListener onAcceptDeclineTeacherRequestsClicked;

    public TeacherReceivedRequestAdapter(List<TeacherReceivedRequest> teacherReceivedRequestList,final OnAcceptDeclineTeacherRequestsListener onAcceptDeclineTeacherRequestsClicked){
        this.teacherReceivedRequestList=teacherReceivedRequestList;
        this.onAcceptDeclineTeacherRequestsClicked = onAcceptDeclineTeacherRequestsClicked;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = TeacherReceivedRequestCardLayoutBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding,onAcceptDeclineTeacherRequestsClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeacherReceivedRequest teacherReceivedRequest=teacherReceivedRequestList.get(position);
        holder.bind(teacherReceivedRequest);
    }

    @Override
    public int getItemCount() {
        return teacherReceivedRequestList.size();
    }

    public void filteredList(List<TeacherReceivedRequest> filteredReceivedRequests){
        this.teacherReceivedRequestList=filteredReceivedRequests;
        notifyDataSetChanged();
    }


    public void deleteItem(TeacherReceivedRequest teacherReceivedRequest){
        teacherReceivedRequestList.remove(teacherReceivedRequest);
        notifyDataSetChanged();
    }

    public TeacherReceivedRequest getItemById(int requestId){
        for(TeacherReceivedRequest trr : teacherReceivedRequestList){
            if(trr.getParentRequestId() == requestId){
                return trr;
            }
        }
        return null ;
    }

    public int getPosition(TeacherReceivedRequest item){
        for(int i=0;i < teacherReceivedRequestList.size();i++){
            if(teacherReceivedRequestList.get(i).getParentRequestId() == item.getParentRequestId()){
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TeacherReceivedRequestCardLayoutBinding binding;
        OnAcceptDeclineTeacherRequestsListener onAcceptDeclineTeacherRequestsListener;

        public ViewHolder(TeacherReceivedRequestCardLayoutBinding binding,OnAcceptDeclineTeacherRequestsListener onAcceptDeclineTeacherRequestsListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onAcceptDeclineTeacherRequestsListener=onAcceptDeclineTeacherRequestsListener;
        }
        public void bind(TeacherReceivedRequest teacherReceivedRequest){
            binding.parentNameTextView.setText(String.format("%s %s", teacherReceivedRequest.getParent().getFirstName(), teacherReceivedRequest.getParent().getFirstName()));

            StringBuilder phoneString = new StringBuilder();
            List<String> phoneList = teacherReceivedRequest.getParent().getPhoneNumbersList();
            for(String phone : phoneList){
                phoneString.append(phone).append("\n");
            }
            if (phoneString.length() > 0) {
                phoneString.setLength(phoneString.length() - 1);
            }

            binding.phoneNumberTextView.setText(phoneString);


            binding.coursesTextView.setText(teacherReceivedRequest.getTeacherPostRequest().getCourses());
            binding.dateTextView.setText(String.format("%s - %s", teacherReceivedRequest.getTeacherPostRequest().getStartDate(), teacherReceivedRequest.getTeacherPostRequest().getEndDate()));
            binding.daysTextView.setText(teacherReceivedRequest.getTeacherPostRequest().getAvailability());
            binding.timeTextView.setText(String.format("%s - %s", teacherReceivedRequest.getTeacherPostRequest().getStartTime(), teacherReceivedRequest.getTeacherPostRequest().getEndTime()));
            binding.locationTextView.setText(teacherReceivedRequest.getTeacherPostRequest().getLocation());
            binding.teachingMethodTextView.setText(teacherReceivedRequest.getTeacherPostRequest().getTeachingMethod());
            binding.acceptBtn.setOnClickListener(v->{
                onAcceptDeclineTeacherRequestsListener.onAcceptDeclineClicked(1,teacherReceivedRequest);
            });
            binding.declineBtn.setOnClickListener(b->{
                onAcceptDeclineTeacherRequestsListener.onAcceptDeclineClicked(0,teacherReceivedRequest);
            });
        }

    }
}
