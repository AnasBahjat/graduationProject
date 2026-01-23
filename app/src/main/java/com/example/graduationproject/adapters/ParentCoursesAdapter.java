package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.ParentCoursesCardLayoutBinding;
import com.example.graduationproject.listeners.OnParentCourseClickedListener;
import com.example.graduationproject.models.Course;
import com.example.graduationproject.utils.Constants;

import java.util.List;

public class ParentCoursesAdapter extends RecyclerView.Adapter<ParentCoursesAdapter.MyViewHolder>{

    List<Course> parentCoursesList ;
    ParentCoursesCardLayoutBinding binding ;
    Context context;
    OnParentCourseClickedListener onParentCourseClickedListener ;
    public ParentCoursesAdapter(List<Course> parentCoursesList, Context context , final OnParentCourseClickedListener onParentCourseClickedListener){
        this.parentCoursesList=parentCoursesList;
        this.context=context;
        this.onParentCourseClickedListener=onParentCourseClickedListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = ParentCoursesCardLayoutBinding.inflate(inflater,parent,false);
        return new MyViewHolder(binding,context,onParentCourseClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Course course = parentCoursesList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return parentCoursesList.size();
    }

    public void filter(List<Course> filteredCourse){
        parentCoursesList = filteredCourse;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ParentCoursesCardLayoutBinding binding ;
        Context context ;
        OnParentCourseClickedListener onParentCourseClickedListener;
        public MyViewHolder(ParentCoursesCardLayoutBinding binding,Context context,OnParentCourseClickedListener onParentCourseClickedListener){
            super(binding.getRoot());
            this.binding=binding;
            this.context=context;
            this.onParentCourseClickedListener=onParentCourseClickedListener;
        }

        public void bind(Course course){
            binding.dateTextView.setText(String.format("%s  -  %s", course.getStartDate(), course.getEndDate()));
            binding.timeTextView.setText(String.format("%s  -  %s", course.getStartTime(), course.getEndTime()));
            binding.daysTextView.setText(course.getDays());
            binding.coursesTextView.setText(course.getCourses());
            binding.childNameTextView.setText(course.getChild().getChildName());
            binding.teacherNameTextView.setText(course.getTeacher().getTeacherName());
            binding.teachingMethodTextView.setText(course.getTeachingMethod());
            binding.parentCourseCard.setOnClickListener(z->{
                onParentCourseClickedListener.onParentCourseClicked(course);
            });
        }
    }
}
