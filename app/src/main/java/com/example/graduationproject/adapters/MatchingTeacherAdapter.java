package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.CustomTeacherMatchingLayoutBinding;
import com.example.graduationproject.models.TeacherMatchModel;

import java.util.ArrayList;
import java.util.List;

public class MatchingTeacherAdapter extends RecyclerView.Adapter<MatchingTeacherAdapter.MyViewHolder> {
    List<TeacherMatchModel> teacherMatchModelList;
    List<TeacherMatchModel> teacherMatchModelListFull;
    Context context;


    public MatchingTeacherAdapter(List<TeacherMatchModel> teacherMatchModelList,Context context){
        this.teacherMatchModelList=teacherMatchModelList;
        this.teacherMatchModelListFull = new ArrayList<>(teacherMatchModelList);
        this.context=context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        CustomTeacherMatchingLayoutBinding binding = CustomTeacherMatchingLayoutBinding.inflate(inflater,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TeacherMatchModel teacherMatchModel=teacherMatchModelList.get(position);
        holder.bind(teacherMatchModel,context);
    }

    @Override
    public int getItemCount() {
        return teacherMatchModelList.size();
    }

    public void search(String searchText){
        teacherMatchModelList.clear();
        if(searchText.isEmpty()){
            teacherMatchModelList.addAll(teacherMatchModelListFull);
        }
        else {
            String text = searchText.toLowerCase();
            for(TeacherMatchModel model : teacherMatchModelList){
                if(model.getParentEmail().toLowerCase().contains(text) || (""+model.getCustomChildData().getChildGrade()).toLowerCase().contains(text)||
                model.getCustomChildData().getChildName().toLowerCase().contains(text) || model.getChoseDays().toLowerCase().contains(text) || model.getCourses().toLowerCase().contains(text)
                || model.getLocation().toLowerCase().contains(text) || model.getTeachingMethod().toLowerCase().contains(text)
                || model.getStartTime().toLowerCase().contains(text) || model.getEndTime().toLowerCase().contains(text)
                || model.getChildren().getChildAge().toLowerCase().contains(text) || (""+model.getChildren().getChildGender()).toLowerCase().contains(text)){
                    teacherMatchModelList.add(model);
                }
            }
            notifyItemRangeChanged(0,teacherMatchModelList.size());
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CustomTeacherMatchingLayoutBinding binding;
        public MyViewHolder(CustomTeacherMatchingLayoutBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
        public void bind(TeacherMatchModel teacherMatchModel,Context context){
            binding.childGradeTextView.setText(teacherMatchModel.getChildren().getGrade()+"");
            binding.coursesTextView.setText(teacherMatchModel.getCourses());
            binding.eachDaysTextView.setText(teacherMatchModel.getChoseDays());
            binding.locationTextView.setText(teacherMatchModel.getLocation());
            binding.eachDaysTextView.setText(teacherMatchModel.getStartTime()+" - "+teacherMatchModel.getEndTime());
        }
    }
}
