package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.databinding.CustomJobCardViewBinding;
import com.example.graduationproject.models.Job;

import java.util.List;

public class UnderGraduateJobsAdapters extends RecyclerView.Adapter<UnderGraduateJobsAdapters.ViewHolder>{
    public CustomJobCardViewBinding binding;
    private List<Job> jobsList;
    private final Context context ;

    public UnderGraduateJobsAdapters(List<Job> jobsList, Context context){
        this.jobsList=jobsList;
        this.context=context;
    }
     @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CustomJobCardViewBinding binding = CustomJobCardViewBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Job job = jobsList.get(position);
        holder.bind(job,context);
    }

    @Override
    public int getItemCount() {
        return jobsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final CustomJobCardViewBinding binding;

        public ViewHolder(CustomJobCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Job job,Context context){
            binding.jobTitle.setText(job.getJobTitle());
            binding.teacherRate.setText("000000000");
        }
    }
}
