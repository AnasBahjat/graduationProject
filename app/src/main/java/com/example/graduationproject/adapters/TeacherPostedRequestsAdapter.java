package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.CustomTeacherMatchingLayoutForTeacherBinding;
import com.example.graduationproject.databinding.TeacherPostedRequestCardLayoutBinding;
import com.example.graduationproject.listeners.TeacherPostRequestClickListener;
import com.example.graduationproject.models.TeacherPostRequest;

import java.util.List;

public class TeacherPostedRequestsAdapter extends RecyclerView.Adapter<TeacherPostedRequestsAdapter.MyViewHolder> {
    List<TeacherPostRequest> teacherPostRequestList ;
    Context context;
    CustomTeacherMatchingLayoutForTeacherBinding binding;
    TeacherPostRequestClickListener listener;

    public TeacherPostedRequestsAdapter(List<TeacherPostRequest> postedRequestsDataList,Context context,TeacherPostRequestClickListener listener){
        this.teacherPostRequestList = postedRequestsDataList;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CustomTeacherMatchingLayoutForTeacherBinding binding = CustomTeacherMatchingLayoutForTeacherBinding.inflate(inflater,parent,false);
        return new MyViewHolder(binding,context,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TeacherPostRequest requestModel= teacherPostRequestList.get(position);
        holder.bind(requestModel);
    }

    @Override
    public int getItemCount() {
        return teacherPostRequestList.size();
    }

    public void filteredList(List<TeacherPostRequest> filteredPostedRequestList){
        this.teacherPostRequestList=filteredPostedRequestList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CustomTeacherMatchingLayoutForTeacherBinding binding;
        Context context;

        TeacherPostRequestClickListener listener ;
        public MyViewHolder(CustomTeacherMatchingLayoutForTeacherBinding binding, Context context, TeacherPostRequestClickListener listener){
            super(binding.getRoot());
            this.binding=binding;
            this.context=context;
            this.listener = listener;
        }
        public void bind(TeacherPostRequest requestModel){
            binding.coursesTextView.setText(requestModel.getCourses());
            binding.locationTextView.setText(requestModel.getLocation());
            binding.teachingMethodTextView.setText(requestModel.getTeachingMethod());
            binding.availableTextView.setText(requestModel.getAvailability());
            if(requestModel.getDuration().equalsIgnoreCase("1"))
                binding.durationTextView.setText(requestModel.getDuration()+" Month");
            else
                binding.durationTextView.setText(requestModel.getDuration()+" Months");


            binding.teacherPostJobLayout.setOnClickListener(b->{
                listener.onTeacherPostClicked(requestModel);
            });

        }
    }
}
