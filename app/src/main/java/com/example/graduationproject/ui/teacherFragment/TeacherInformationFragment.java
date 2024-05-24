package com.example.graduationproject.ui.teacherFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.graduationproject.R;
import com.example.graduationproject.databinding.FragmentTeacherInformationBinding;

public class TeacherInformationFragment extends Fragment {
    private FragmentTeacherInformationBinding binding ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentTeacherInformationBinding.inflate(getLayoutInflater(),container,false);
        init();
        return (binding.getRoot());
    }
    private void init(){

    }
}