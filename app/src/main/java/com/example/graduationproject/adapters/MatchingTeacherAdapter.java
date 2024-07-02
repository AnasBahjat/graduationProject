package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.databinding.CustomTeacherMatchingLayoutForParentBinding;
import com.example.graduationproject.listeners.TeacherMatchCardClickListener;
import com.example.graduationproject.models.TeacherMatchModel;

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
        CustomTeacherMatchingLayoutForParentBinding binding = CustomTeacherMatchingLayoutForParentBinding.inflate(inflater,parent,false);
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
        CustomTeacherMatchingLayoutForParentBinding binding;
        Context context;
        TeacherMatchCardClickListener teacherMatchCardClickListener;
        public MyViewHolder(CustomTeacherMatchingLayoutForParentBinding binding,Context context,TeacherMatchCardClickListener teacherMatchCardClickListener) {
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
            binding.priceTextView.setText(teacherMatchModel.getPriceMinimum()+"$"+" - "+teacherMatchModel.getPriceMaximum()+"$");
            binding.dateTextView.setText(teacherMatchModel.getStartDate()+"  -  "+teacherMatchModel.getEndDate());
            binding.matchTeacherCardView.setOnClickListener(cl->{
                cardClicked(teacherMatchModel);
            });
        }

        private void cardClicked(TeacherMatchModel modelClicked){
            teacherMatchCardClickListener.onCardClicked(modelClicked);
        }
    }
}
