package com.example.graduationproject.ui.afterLogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.graduationproject.database.Database;
import com.example.graduationproject.R;
import com.example.graduationproject.databinding.ActivityAfterLoginBinding;
import com.example.graduationproject.ui.employer.UnderGraduateFragment;
import com.example.graduationproject.ui.login.LoginActivity;

public class AfterLoginActivity extends AppCompatActivity {
    private Database database;

    private ActivityAfterLoginBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAfterLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    private void initialize(){
       loadFragment(new UnderGraduateFragment());
    }


    private void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragments_container,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void logoutOnClick(View view) {
        String email=getIntent().getStringExtra("email");
        database.updateLogout(email);
        Intent intent=new Intent(AfterLoginActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}