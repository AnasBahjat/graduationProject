package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout firstname,lastname,email,password,confPassword,phoneNumber,phonePrefix;
    String firstnameStr,lastnameStr,emailStr,passwordStr,confPasswordStr,phoneNumberStr,phonePrefixStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        wrapViews();
    }

    private void wrapViews(){
        firstname=findViewById(R.id.firstname);
        lastname=findViewById(R.id.lastname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confPassword=findViewById(R.id.passwordConfirm);
        phoneNumber=findViewById(R.id.phoneNumber);
        phonePrefix=findViewById(R.id.phonePrefix);
    }

    @SuppressLint("ResourceAsColor")
    public void registerClicked(View view) {
        firstnameStr=firstname.getEditText().getText().toString();
        lastnameStr=lastname.getEditText().getText().toString() ;
        emailStr=email.getEditText().getText().toString() ;
        passwordStr=password.getEditText().getText().toString() ;
        confPasswordStr=confPassword.getEditText().getText().toString();
        phoneNumberStr=phoneNumber.getEditText().getText().toString();
        phonePrefixStr=phonePrefix.getEditText().getText().toString();
        if(firstnameStr.isEmpty()){
            firstname.setError("* Fill in this field");
        }
        if(lastnameStr.isEmpty()){
            lastname.setError("* Fill in this field");
        }
        if(emailStr.isEmpty()){
            email.setError("* Fill in this field");
        }
        if(passwordStr.isEmpty()){
            password.setError("* Fill in this field");
        }
        if(confPasswordStr.isEmpty()){
            confPassword.setError("* Fill in this field");
        }
        if(phoneNumberStr.isEmpty()){
            phoneNumber.setError("* Fill in above fields");
        }

        if(phonePrefixStr.isEmpty()){
            phonePrefix.setError("*");
        }

        firstname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstname.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lastname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastname.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email.setError(null);
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
                password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumber.setError(null);
                phonePrefix.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phonePrefix.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumber.setError(null);
                phonePrefix.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(passwordStr.length() > 25){
            password.setError("Password length must be less than 25");
        }

        if(confPasswordStr.length() > 25){
            confPassword.setError("Password length must be less than 25");
        }

        if(!passwordStr.equals(confPasswordStr) && !passwordStr.isEmpty()){
            password.setError("Passwords doesn't match");
            confPassword.setError("Passwords doesn't match");
        }

        if(checkAll()){
            Toast.makeText(RegisterActivity.this,"Registered ..",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(RegisterActivity.this,"ERROR ..",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkAll(){
        if (!firstnameStr.isEmpty() && !lastnameStr.isEmpty() && !emailStr.isEmpty()
        && !passwordStr.isEmpty() && !confPasswordStr.isEmpty()&& !phoneNumberStr.isEmpty()
                && !phonePrefixStr.isEmpty() && passwordStr.equals(confPasswordStr) && !isEmailValid()) {
            return true;
        }
        return false;
    }
    public boolean isEmailValid() {
        String emailFormat = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailFormat);
        Matcher matcher = pattern.matcher(emailStr);
        if(matcher.matches()){
            return true;
        }
        else {
            email.setError("* Error Email Format");
            return false ;
        }
    }
}