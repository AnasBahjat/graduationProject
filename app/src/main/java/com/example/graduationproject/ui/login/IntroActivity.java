package com.example.graduationproject.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;

public class IntroActivity extends AppCompatActivity {
    private Animation top,right,left;

    private ImageView img;
    private TextView text,since2024;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        top= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        right=AnimationUtils.loadAnimation(this,R.anim.right_animation);
        left=AnimationUtils.loadAnimation(this,R.anim.left_animation);

        img=findViewById(R.id.logoImg);
        text=findViewById(R.id.logoText);
        since2024=findViewById(R.id.since2024);



        img.setAnimation(top);
        text.setAnimation(right);
        since2024.setAnimation(left);

        Handler handler=new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}