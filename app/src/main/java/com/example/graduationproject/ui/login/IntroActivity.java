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
import com.example.graduationproject.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {
    private Animation top,right,left;
    private ActivityIntroBinding binding;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_intro);
        setAnimation();
    }
    private void setAnimation(){
        top= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        right=AnimationUtils.loadAnimation(this,R.anim.right_animation);
        left=AnimationUtils.loadAnimation(this,R.anim.left_animation);

        binding.logoImg.setAnimation(top);
        binding.logoText.setAnimation(right);
        binding.since2024.setAnimation(left);

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