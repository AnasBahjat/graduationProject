package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.CustomTeacherMatchingLayout2Binding;
import com.example.graduationproject.listeners.ParentPostRequestClickListener;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;

import java.util.List;

public class PostedTeacherRequestsAdapter extends RecyclerView.Adapter<PostedTeacherRequestsAdapter.MyViewHolder> {
    List<TeacherMatchModel> postedRequestsDataList ;
    Context context;
    CustomTeacherMatchingLayout2Binding binding;
    ParentPostRequestClickListener listener;

    public PostedTeacherRequestsAdapter(List<TeacherMatchModel> postedRequestsDataList,Context context,ParentPostRequestClickListener listener){
        this.postedRequestsDataList = postedRequestsDataList;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CustomTeacherMatchingLayout2Binding binding = CustomTeacherMatchingLayout2Binding.inflate(inflater,parent,false);
        return new MyViewHolder(binding,context,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TeacherMatchModel requestModel= postedRequestsDataList.get(position);
        holder.bind(requestModel);
    }

    @Override
    public int getItemCount() {
        return postedRequestsDataList.size();
    }

    public void filteredList(List<TeacherMatchModel> filteredPostedRequestList){
        this.postedRequestsDataList=filteredPostedRequestList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CustomTeacherMatchingLayout2Binding binding;
        Context context;

        ParentPostRequestClickListener listener ;
        public MyViewHolder(CustomTeacherMatchingLayout2Binding binding,Context context,ParentPostRequestClickListener listener){
            super(binding.getRoot());
            this.binding=binding;
            this.context=context;
            this.listener = listener;
        }
        public void bind(TeacherMatchModel requestModel){
            binding.childNameTextView.setText(requestModel.getChildren().getChildName());
            binding.coursesTextView.setText(requestModel.getCourses());
            binding.locationTextView.setText(requestModel.getLocation());
            binding.teachingMethodTextView.setText(requestModel.getTeachingMethod());
            binding.timeTextView.setText(requestModel.getStartTime()+" - "+requestModel.getEndTime());

            binding.matchTeacherCardView.setOnClickListener(x->{
                listener.onClick(requestModel);
            });
        }
    }
}
