package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.json.JSONArray;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements RequestResult{
    private TextInputLayout firstname,lastname,email,password,confPassword,phoneNumber,phonePrefix,
            genderSpinnerLayout,city,country,birthDateLayout,idNumberLayout;
    private ScrollView scrollView;
    private TextView genderTextView ;
    private String firstnameStr,lastnameStr,emailStr,passwordStr,confPasswordStr,phoneNumberStr,phonePrefixStr,idNumberStr,selectedDate;

    private ImageView showCalendarImage ;
    FirebaseAuth firebaseAuth ;
    boolean dataValidFlag=false;
    private Spinner genderSpinner ;
    private int genderSelected;
    int validBirthDateFlag=0;
    private String[] genderItems={"Choose Gender","Male","Female"};
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.testfile);
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
        genderSpinnerLayout=findViewById(R.id.genderLayout);
        city=findViewById(R.id.city);
        country=findViewById(R.id.country);
        showCalendarImage=findViewById(R.id.imageCalendar);
        birthDateLayout=findViewById(R.id.birthDateLayout);
        idNumberLayout=findViewById(R.id.idNumber);
        progressBar=findViewById(R.id.progressBar);
        scrollView=findViewById(R.id.scrollView);
        genderTextView=findViewById(R.id.genderTextView);
        final ImageView hintArrow=findViewById(R.id.hint_arrow);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(scrollView.getChildAt(0).getBottom() <= (scrollView.getHeight() + scrollView.getScrollY())){
                    hintArrow.setVisibility(View.GONE);
                }
                else {
                    hintArrow.setVisibility(View.VISIBLE);
                }
            }
        });
        showCalendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        birthDateLayout.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });


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
        idNumberStr=idNumberLayout.getEditText().getText().toString();
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


        if(city.getEditText().getText().toString().isEmpty()){
            city.setError("* Fill in above field");
        }

        if(country.getEditText().getText().toString().isEmpty()){
            country.setError("* Fill in above field");
        }


        if(idNumberStr.isEmpty()){
            idNumberLayout.setError("* Fill in above field");
        }

        if(idNumberStr.length() < 8){
            idNumberLayout.setError("* Enter A valid ID number");
        }


        if(checkContainSpace(firstnameStr.trim())){
            firstname.setError("* First name should have only characters and no space");
        }
        if(checkContainSpace(lastnameStr.trim())){
            lastname.setError("* Last name should have only characters and no space");
        }

        idNumberLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                idNumberLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        city.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                city.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        country.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                country.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        if(passwordStr.length() > 25){
            password.setError("Password length must be less than 25");
        }

        if(confPasswordStr.length() > 25){
            confPassword.setError("* Password length must be 8 - 25 characters");
        }

        if(!passwordStr.equals(confPasswordStr) && !passwordStr.isEmpty()){
            password.setError("Passwords doesn't match");
            confPassword.setError("Passwords doesn't match");
        }

        if(genderSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose Gender")){
            MyAlertDialog.showCustomAlertDialogSpinnerError(this,"Wrong gender value","Please Choose A Valid Gender Value");
            genderTextView.setTextColor(Color.RED);
        }

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(genderSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose Gander")){
                    genderTextView.setTextColor(Color.RED);
                }
                else {
                    genderTextView.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                genderTextView.setTextColor(Color.BLACK);
            }
        });


        Log.d(checkAll()+"","check all --> "+checkAll());
        if(checkAll()){
            firstname.setError(null);
            lastname.setError(null);
            email.setError(null);
            password.setError(null);
            confPassword.setError(null);
            phonePrefix.setError(null);
            phoneNumber.setError(null);
            city.setError(null);
            country.setError(null);
            birthDateLayout.setError(null);
            Database insertNewProfile=new Database(getApplicationContext());
            String phoneNumber = phonePrefixStr+" "+phoneNumberStr;
            Log.d("---------->","------------->"+selectedDate);
            Profile profile=new Profile(firstnameStr,lastnameStr,emailStr,passwordStr,idNumberStr,selectedDate,genderSelected,3,phoneNumber,city.getEditText().getText().toString(),country.getEditText().getText().toString());
            insertNewProfile.registerNewProfile(profile,this);
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

        Log.d("first name empty"+!firstnameStr.isEmpty(),"first name empty"+!firstnameStr.isEmpty());
        Log.d("last name empty"+!lastnameStr.isEmpty(),"last name empty"+!lastnameStr.isEmpty());
        Log.d("email empty"+!emailStr.isEmpty(),"email  empty"+!emailStr.isEmpty());
        Log.d("password empty"+!passwordStr.isEmpty(),"password  empty"+!passwordStr.isEmpty());
        Log.d("conf password empty"+!confPasswordStr.isEmpty(),"confPassword  empty"+!confPasswordStr.isEmpty());
        Log.d("phone empty"+!phoneNumberStr.isEmpty(),"phoneNumber  empty"+!phoneNumberStr.isEmpty());
        Log.d("phone prefix empty"+!phonePrefixStr.isEmpty(),"phonePrefix  empty"+!phonePrefixStr.isEmpty());
        Log.d(" password matches"+passwordStr.equals(confPasswordStr)," password matches"+passwordStr.equals(confPasswordStr));
        Log.d("valid email"+isEmailValid(),"valid email "+isEmailValid());
        Log.d("Password length "+passwordStr.trim().length(),"Password length "+passwordStr.trim().length());
        Log.d("contains two cases "+containsTwoCases(),"contains two cases "+containsTwoCases());
        Log.d("gender value 1 male 0 female "+gender,"gender value 1 male 0 female "+gender);
        Log.d(containsOnlyCharacters(firstnameStr)+"","Contains only characters first"+containsOnlyCharacters(firstnameStr));
        Log.d(!city.getEditText().getText().toString().isEmpty()+"","City empty "+!city.getEditText().getText().toString().isEmpty());
        Log.d(!country.getEditText().getText().toString().isEmpty()+"","country empty "+!country.getEditText().getText().toString().isEmpty());
        Log.d(validBirthDateFlag+"","birth date flag "+validBirthDateFlag);
        Log.d(!idNumberStr.isEmpty()+"","id number  "+!idNumberStr.isEmpty());
        Log.d("check contains space first "+!checkContainSpace(firstnameStr)+"","check contains space first "+!checkContainSpace(firstnameStr));
        Log.d("check contains space last "+!checkContainSpace(lastnameStr)+"","check contains space last "+!checkContainSpace(lastnameStr));



        return (!firstnameStr.isEmpty() && !lastnameStr.isEmpty() && !emailStr.isEmpty()
                && !passwordStr.isEmpty() && !confPasswordStr.isEmpty() && !phoneNumberStr.isEmpty()
                && !phonePrefixStr.isEmpty() && passwordStr.equals(confPasswordStr) && isEmailValid() &&
                passwordStr.trim().length() > 10 && containsTwoCases() && gender && containsOnlyCharacters(firstnameStr) && containsOnlyCharacters(lastnameStr)
        && !city.getEditText().getText().toString().isEmpty() && !country.getEditText().getText().toString().isEmpty()
        && validBirthDateFlag != 0 && !idNumberStr.isEmpty() && !checkContainSpace(firstnameStr.trim()) && !checkContainSpace(lastnameStr.trim())) ;
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

    private boolean checkContainSpace(String string) {
        if(string.contains(" ")){
            return true;
        }
        return false;
    }

    public boolean containsOnlyCharacters(String input) {
        String regex = "^[a-zA-Z ]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
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

    @Override
    public void onSuccess(int result) {
        Log.d("Here in register activity"+result,"here in register activity"+result);
        if(result == 1){
            progressBar.setVisibility(ProgressBar.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Toast.makeText(RegisterActivity.this,"Account created , you can sign in now",Toast.LENGTH_LONG).show();
                    finish();
                }
            },1500);

            //MyAlertDialog.showCustomAlerDialogForRegistrationDone(this);
        }
        else if(result == -2){
            email.setError("Email already registered ...");
        }
        else if (result==0){
            Toast.makeText(this,"Error registration",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoginSuccess(String message, JSONArray loginSuccessData) {

    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        int currentYear = LocalDate.now().getYear();
        selectedDate=year +"-"+ (month+1) + dayOfMonth;
        validBirthDateFlag=1;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = year +"-"+ (month+1) + "-" +dayOfMonth;
                        if(currentYear - year < 18){
                            birthDateLayout.setError("* You must be at least 18 years old");
                        }
                        else{
                            birthDateLayout.setError(null);
                            validBirthDateFlag=1;
                        }
                        birthDateLayout.getEditText().setText(dayOfMonth+"-"+(month+1)+"-"+year);
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }
}