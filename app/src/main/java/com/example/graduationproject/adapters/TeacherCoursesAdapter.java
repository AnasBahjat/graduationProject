package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.TeacherCoursesCardLayoutBinding;
import com.example.graduationproject.listeners.TeacherCourseClickListener;
import com.example.graduationproject.models.Course;

import java.util.List;

public class TeacherCoursesAdapter extends RecyclerView.Adapter<TeacherCoursesAdapter.MyViewHolder>{
    Context context;
    List<Course> coursesList;
    TeacherCoursesCardLayoutBinding binding;
    TeacherCourseClickListener teacherCourseClickListener;

    public TeacherCoursesAdapter(List<Course> coursesList , Context context,final TeacherCourseClickListener teacherCourseClickListener){
        this.coursesList = coursesList;
        this.context = context;
        this.teacherCourseClickListener = teacherCourseClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = TeacherCoursesCardLayoutBinding.inflate(inflater,parent,false);
        return new MyViewHolder(binding,context,teacherCourseClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Course course = coursesList.get(position);
        holder.bind(course);

    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TeacherCoursesCardLayoutBinding teacherCoursesCardLayoutBinding;
        Context context ;
        TeacherCourseClickListener teacherCourseClickListener;

        public MyViewHolder(TeacherCoursesCardLayoutBinding binding,Context context,TeacherCourseClickListener teacherCourseClickListener) {
            super(binding.getRoot());
            this.teacherCoursesCardLayoutBinding = binding;
            this.context=context;
            this.teacherCourseClickListener = teacherCourseClickListener;
        }

        public void bind(Course course){
            teacherCoursesCardLayoutBinding.dateTextView.setText(String.format("%s  -  %s", course.getStartDate(), course.getEndDate()));
            teacherCoursesCardLayoutBinding.timeTextView.setText(String.format("%s  -  %s", course.getStartTime(), course.getEndTime()));
            teacherCoursesCardLayoutBinding.daysTextView.setText(course.getDays());
            teacherCoursesCardLayoutBinding.childNameTextView.setText(course.getChild().getChildName());
            if(course.getChild().getChildAge().equalsIgnoreCase("1")){
                teacherCoursesCardLayoutBinding.childAgeTextView.setText(String.format("%s Year", course.getChild().getChildAge()));
            }
            else {
                teacherCoursesCardLayoutBinding.childAgeTextView.setText(String.format("%s Years", course.getChild().getChildAge()));
            }

            String gender = "Male";
            if(course.getChild().getChildGender() == 0){
                gender = "Female";
            }
            teacherCoursesCardLayoutBinding.childGenderTextView.setText(gender);


            if(course.getChild().getGrade() == 1){
                teacherCoursesCardLayoutBinding.childGradeTextView.setText(String.format("%dst Grade", course.getChild().getGrade()));
            }
            else if(course.getChild().getGrade() == 2){
                teacherCoursesCardLayoutBinding.childGradeTextView.setText(String.format("%dnd Grade", course.getChild().getGrade()));
            }
            else {
                teacherCoursesCardLayoutBinding.childGradeTextView.setText(String.format("%dth Grade", course.getChild().getGrade()));
            }
            teacherCoursesCardLayoutBinding.teacherCourseCard.setOnClickListener(c->{
                teacherCourseClickListener.onTeacherCourseClicked(course);
            });
        }
    }
}
