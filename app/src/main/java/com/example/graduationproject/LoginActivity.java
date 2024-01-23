package com.example.graduationproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.*;
public class LoginActivity extends AppCompatActivity {
    private TextInputLayout email;
    private TextInputLayout password;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.emailText);
        password=findViewById(R.id.passwordText);
        loginBtn=findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getEditText().getText().toString().isEmpty()){
                    email.setError("Fill in this field");
                }
                if(password.getEditText().getText().toString().isEmpty()){
                    password.setError("Fill in this field");
                }

                email.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        email.setError("");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                password.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        password.setError("");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                if(!email.getEditText().getText().toString().isEmpty() && !password.getEditText().getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this,"Login done",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}