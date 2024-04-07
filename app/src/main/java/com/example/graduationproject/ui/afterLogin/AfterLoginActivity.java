package com.example.graduationproject.ui.afterLogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.graduationproject.database.Database;
import com.example.graduationproject.R;
import com.example.graduationproject.ui.login.LoginActivity;

public class AfterLoginActivity extends AppCompatActivity {
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_after_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database=new Database(this);
    }

    public void logoutOnClick(View view) {
        String email=getIntent().getStringExtra("email");
        database.updateLogout(email);
        Intent intent=new Intent(AfterLoginActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}