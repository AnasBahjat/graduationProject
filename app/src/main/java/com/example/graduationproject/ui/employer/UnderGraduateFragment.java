package com.example.graduationproject.ui.employer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.graduationproject.adapters.UnderGraduateJobsAdapters;
import com.example.graduationproject.databinding.FragmentUnderGraduateBinding;
import com.example.graduationproject.models.Job;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnderGraduateFragment extends Fragment {

    private FragmentUnderGraduateBinding binding ;
    private UnderGraduateJobsAdapters adapter ;
    private RecyclerView recyclerView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUnderGraduateBinding.inflate(getLayoutInflater(),container,false);
        init();
        return binding.getRoot();
    }

    private void init(){

        binding.jobsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       List<Job> test = new ArrayList<>();
       test.add(new Job(1,"Naseem","category 1"));
       test.add(new Job(2,"aNAS","category 2"));
       test.add(new Job(3,"donia","category 3"));
       test.add(new Job(4,"donia","category 4"));
       test.add(new Job(5,"سشيشسيشس","category 4"));




        adapter = new UnderGraduateJobsAdapters(test,getContext());
        binding.jobsRecyclerView.setAdapter(adapter);
    }
}