package com.example.graduationproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.databinding.CustomTeacherMatchingLayout2Binding;
import com.example.graduationproject.databinding.CustomTeacherMatchingLayoutBinding;
import com.example.graduationproject.listeners.TeacherMatchCardClickListener;
import com.example.graduationproject.models.TeacherMatchModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchingTeacherAdapter extends RecyclerView.Adapter<MatchingTeacherAdapter.MyViewHolder>{
    List<TeacherMatchModel> teacherMatchModelList;
    Context context;
    TeacherMatchCardClickListener teacherMatchCardClickListener;


    public MatchingTeacherAdapter(List<TeacherMatchModel> teacherMatchModelList, Context context, final TeacherMatchCardClickListener teacherMatchCardClickListener){
        this.teacherMatchModelList=teacherMatchModelList;
        this.context=context;
        this.teacherMatchCardClickListener=teacherMatchCardClickListener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        CustomTeacherMatchingLayout2Binding binding = CustomTeacherMatchingLayout2Binding.inflate(inflater,parent,false);
        return new MyViewHolder(binding,context,teacherMatchCardClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TeacherMatchModel teacherMatchModel=teacherMatchModelList.get(position);
        holder.bind(teacherMatchModel);
    }

    @Override
    public int getItemCount() {
        return teacherMatchModelList.size();
    }


    public void filteredList(List<TeacherMatchModel> teacherMatchModelList){
        this.teacherMatchModelList=teacherMatchModelList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CustomTeacherMatchingLayout2Binding binding;
        Context context;
        TeacherMatchCardClickListener teacherMatchCardClickListener;
        public MyViewHolder(CustomTeacherMatchingLayout2Binding binding,Context context,TeacherMatchCardClickListener teacherMatchCardClickListener) {
            super(binding.getRoot());
            this.binding=binding;
            this.context=context;
            this.teacherMatchCardClickListener = teacherMatchCardClickListener;
        }
        public void bind(TeacherMatchModel teacherMatchModel){
            binding.childNameTextView.setText(teacherMatchModel.getChildren().getChildName());
            binding.coursesTextView.setText(teacherMatchModel.getCourses());
            binding.locationTextView.setText(teacherMatchModel.getLocation());
            binding.teachingMethodTextView.setText(teacherMatchModel.getTeachingMethod());
            binding.timeTextView.setText(teacherMatchModel.getStartTime()+" - "+teacherMatchModel.getEndTime());

            binding.matchTeacherCardView.setOnClickListener(cl->{
                cardClicked(teacherMatchModel);
            });
        }

        private void cardClicked(TeacherMatchModel modelClicked){
            teacherMatchCardClickListener.onCardClicked(modelClicked);
        }
    }
}
