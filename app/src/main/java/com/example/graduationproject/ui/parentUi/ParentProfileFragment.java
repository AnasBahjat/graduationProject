package com.example.graduationproject.ui.parentUi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.graduationproject.R;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.FragmentParentBinding;
import com.example.graduationproject.databinding.TempParentProfileFragmentBinding;
import com.example.graduationproject.listeners.OnProfileDataFetchListener;

import org.json.JSONArray;

public class ParentProfileFragment extends Fragment implements OnProfileDataFetchListener {

    private TempParentProfileFragmentBinding binding ;
    private Database database = new Database(getContext());
    private String email ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = TempParentProfileFragmentBinding.inflate(getLayoutInflater(),container,false);
        init();
        return binding.getRoot();
    }

    private void init(){
        getPassedData();
        database.getCurrentProfileData(email,this);
    }

    private void getPassedData(){
        if(getArguments() != null){
            email = getArguments().getString("email");
        }
        else {
            email="";
        }
    }

    @Override
    public void onProfileDataFetched(int flag, JSONArray profileData) {

    }
}