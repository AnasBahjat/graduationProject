package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout firstname,lastname,email,password,confPassword,phoneNumber,phonePrefix;
    String firstnameStr,lastnameStr,emailStr,passwordStr,confPasswordStr,phoneNumberStr,phonePrefixStr;
    FirebaseAuth firebaseAuth ;
    boolean dataValidFlag=false;
    private Spinner genderSpinner ;
    private int genderSelected;
    private String[] genderItems={"Choose Gender","Male","Female"};
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
        genderSpinner=findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.genderSpinner,R.layout.spinner_custom);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

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
        else if(passwordStr.length() < 10 || !containsTwoCases()){
            password.setError("* Password  must be\n  at least 10 characters" +
                    "\n  one upper case\n  one lower case");
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
            firstname.setError(null);
            lastname.setError(null);
            email.setError(null);
            password.setError(null);
            confPassword.setError(null);
            phonePrefix.setError(null);
            phoneNumber.setError(null);
          //  Toast.makeText(RegisterActivity.this,"Registered ..",Toast.LENGTH_SHORT).show();

            Database insertNewProfile=new Database(getApplicationContext());
            String phoneNumber = phonePrefixStr+" "+phoneNumberStr;
            Profile profile=new Profile(firstnameStr,lastnameStr,emailStr,passwordStr,"12345555","2000-04-07",genderSelected,3,phoneNumber,"Ramallah","Palestine");
            int successFlag=insertNewProfile.registerNewProfile(profile);
            Log.d("Here in register activity","here in register activity");

            Log.d("-----------> "+successFlag,"------------->"+successFlag);
            if(successFlag == 1){
                finish();
            }
            else if(successFlag == -2){
                email.setError("Email already registered ...");
            }
            dataValidFlag=true ;
        }
        else if(!checkAll()){
            Toast.makeText(RegisterActivity.this,"ERROR ..",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkAll(){
        String value=genderSpinner.getSelectedItem().toString();
        boolean gender=true;
        if(value.equalsIgnoreCase("Choose gender")){
            gender=false;
        }
        else if(value.equals("Male")){
            genderSelected=1;
        }
        else if(value.equals("Female")){
            genderSelected=0;
        }
        Log.d("firstname--> "+firstnameStr.isEmpty(),"");
        Log.d("lastname--> "+lastnameStr.isEmpty(),"");
        Log.d("emailStr--> "+emailStr.isEmpty(),"");
        Log.d("passwordStr--> "+passwordStr.isEmpty(),"");
        Log.d("confPasswordStr--> "+confPasswordStr.isEmpty(),"");
        Log.d("phoneNumberStr--> "+phoneNumberStr.isEmpty(),"");
        Log.d("phonePrefixStr--> "+phonePrefixStr.isEmpty(),"");
        Log.d("passwordStr equlas --> "+passwordStr.equals(confPasswordStr),"");
        Log.d("passwordStr length > 10 --> "+(passwordStr.trim().length() > 10),"");
        Log.d("containsTwoCases()--> "+containsTwoCases(),"");
        Log.d("gender--> "+gender,"");


        return (!firstnameStr.isEmpty() && !lastnameStr.isEmpty() && !emailStr.isEmpty()
                && !passwordStr.isEmpty() && !confPasswordStr.isEmpty() && !phoneNumberStr.isEmpty()
                && !phonePrefixStr.isEmpty() && passwordStr.equals(confPasswordStr) && isEmailValid() &&
                passwordStr.trim().length() > 10 && containsTwoCases() && gender) ;
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

    public boolean containsTwoCases(){
        boolean uppercaseFlag=false,lowercaseFlag=false;
        for (char ch : passwordStr.toCharArray()){
            if(Character.isUpperCase(ch))
                uppercaseFlag=true;
            if(Character.isLowerCase(ch))
                lowercaseFlag=true;
            if (uppercaseFlag && lowercaseFlag) {
                break;
            }
        }
        return uppercaseFlag && lowercaseFlag;
    }
}