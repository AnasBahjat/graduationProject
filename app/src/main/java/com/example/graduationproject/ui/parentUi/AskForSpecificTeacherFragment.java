package com.example.graduationproject.ui.parentUi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduationproject.R;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.FragmentAskForSpecificTeacherBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.GetParentChildren;
import com.example.graduationproject.models.Children;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AskForSpecificTeacherFragment extends Fragment   {

    FragmentAskForSpecificTeacherBinding binding ;
    Database database ;
    String parentEmail ;
    ArrayAdapter<String> adapter ;
    Context context ;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAskForSpecificTeacherBinding.inflate(getLayoutInflater(),container,false);
        getParentInformation();
        init();
        return binding.getRoot();
    }

    private void getParentInformation(){
        if(getArguments() != null){
            parentEmail = getArguments().getString("email");
        }
        else {
            parentEmail="";
        }
    }

    private void init(){
        binding.childrenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context,binding.childrenSpinner.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getChildrenFromParentActivity(List<Children> childrenList,List<String> childrenSpinner){
        if(childrenList.isEmpty()){
            MyAlertDialog.errorDialog(context);
        }
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,childrenSpinner);
        binding.childrenSpinner.setAdapter(adapter);

    }

}