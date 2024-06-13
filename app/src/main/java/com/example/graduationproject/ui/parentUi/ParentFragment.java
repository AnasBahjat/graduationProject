package com.example.graduationproject.ui.parentUi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.graduationproject.R;
import com.example.graduationproject.databinding.FragmentParentBinding;
import com.example.graduationproject.databinding.FragmentTeacherBinding;

public class ParentFragment extends Fragment {


    FragmentParentBinding binding ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentParentBinding.inflate(getLayoutInflater(),container,false);
        initialize();
        return binding.getRoot();
    }

    private void initialize(){

    }
}