package com.example.graduationproject.ui.register;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.ActivityRegisterBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.R;
import com.example.graduationproject.interfaces.RequestResult;
import com.example.graduationproject.models.Profile;
import org.json.JSONArray;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements RequestResult {
    private String firstnameStr,lastnameStr,emailStr,passwordStr,confPasswordStr,phoneNumberStr,phonePrefixStr,selectedDate;

    boolean dataValidFlag=false;
    private int genderSelected;
    int validBirthDateFlag=0;

    private ActivityRegisterBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        Database tt = new Database(this);
        tt.registerRetrofitRequest(new Profile("kkkk","Yaskksin","kkkkk@gmail.com","anas123123123","000000000000","2000-07-04","1","1"),this);
        wrapViews();
    }


    private void wrapViews(){
        binding.imageCalendar.setOnClickListener(v -> showDatePicker());


        if(binding.birthDateLayout.getEditText() != null){
            binding.birthDateLayout.getEditText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker();
                }
            });
        }


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.genderSpinner,R.layout.spinner_custom);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.genderSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> profileAdapter = ArrayAdapter.createFromResource(this,R.array.profileTypeSpinner,R.layout.spinner_custom);
        profileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.profileSpinner.setAdapter(profileAdapter);
    }



    @SuppressLint("ResourceAsColor")
    public void registerClicked(View view) {
        firstnameStr= Objects.requireNonNull(binding.firstname.getEditText()).getText().toString();
        lastnameStr= Objects.requireNonNull(binding.lastname.getEditText()).getText().toString() ;
        emailStr= Objects.requireNonNull(binding.email.getEditText()).getText().toString() ;
        passwordStr= Objects.requireNonNull(binding.password.getEditText()).getText().toString() ;
        confPasswordStr= Objects.requireNonNull(binding.passwordConfirm.getEditText()).getText().toString();
        phoneNumberStr= Objects.requireNonNull(binding.phoneNumber.getEditText()).getText().toString();
        phonePrefixStr= Objects.requireNonNull(binding.phonePrefix.getEditText()).getText().toString();
        if(firstnameStr.isEmpty()){
            binding.firstname.setError("* Fill in this field");
        }
        if(lastnameStr.isEmpty()){
            binding.lastname.setError("* Fill in this field");
        }
        if(emailStr.isEmpty()){
            binding.email.setError("* Fill in this field");
        }
        if(passwordStr.isEmpty()){
            binding.password.setError("* Fill in this field");
        }
        else if(passwordStr.length() < 10 || !containsTwoCases()){
            binding.password.setError("* Password  must be\n  at least 10 characters" +
                    "\n  one upper case\n  one lower case");
        }
        if(confPasswordStr.isEmpty()){
            binding.passwordConfirm.setError("* Fill in this field");
        }
        if(phoneNumberStr.isEmpty()){
            binding.phoneNumber.setError("* Fill in above fields");
        }

        if(phonePrefixStr.isEmpty()){
            binding.phonePrefix.setError("*");
        }


        if(checkContainSpace(firstnameStr.trim())){
            binding.firstname.setError("* First name should have only characters and no space");
        }
        if(checkContainSpace(lastnameStr.trim())){
            binding.lastname.setError("* Last name should have only characters and no space");
        }

        binding.firstname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.firstname.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.lastname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.lastname.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.passwordConfirm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.passwordConfirm.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.phoneNumber.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.phoneNumber.setError(null);
                binding.phonePrefix.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.phonePrefix.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.phoneNumber.setError(null);
                binding.phonePrefix.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if(passwordStr.length() > 25){
            binding.password.setError("Password length must be less than 25");
        }

        if(confPasswordStr.length() > 25){
            binding.passwordConfirm.setError("* Password length must be 8 - 25 characters");
        }

        if(!passwordStr.equals(confPasswordStr) && !passwordStr.isEmpty()){
            binding.password.setError("Passwords doesn't match");
            binding.passwordConfirm.setError("Passwords doesn't match");
        }

        if(binding.genderSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose Gender")){
            MyAlertDialog.showCustomAlertDialogSpinnerError(this,"Wrong gender value","Please Choose A Valid Gender Value");
            binding.genderTextView.setTextColor(Color.RED);
        }

        if(binding.profileSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose Type")){
            MyAlertDialog.showCustomAlertDialogSpinnerError(this,"Wrong type value","Please Choose A Valid Value");
            binding.profileTypeText.setTextColor(Color.RED);
        }

        binding.genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(binding.genderSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose Gander")){
                    binding.genderTextView.setTextColor(Color.RED);
                }
                else {
                    binding.genderTextView.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.genderTextView.setTextColor(Color.BLACK);
            }
        });

        binding.profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(binding.profileSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose Type")){
                    binding.profileTypeText.setTextColor(Color.RED);
                }
                else {
                    binding.profileTypeText.setTextColor(Color.BLACK);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Log.d(checkAll()+"","check all --> "+checkAll());
        if(checkAll()){
            binding.firstname.setError(null);
            binding.lastname .setError(null);
            binding.email.setError(null);
            binding.password.setError(null);
            binding.passwordConfirm.setError(null);
            binding.phonePrefix.setError(null);
            binding.phoneNumber.setError(null);
            binding.birthDateLayout.setError(null);
            Database insertNewProfile=new Database(getApplicationContext());
            String phoneNumber = phonePrefixStr+" "+phoneNumberStr;
            Log.d("---------->","------------->"+selectedDate);
            Profile profile=new Profile(firstnameStr.toLowerCase(),lastnameStr.trim(),emailStr.trim(),passwordStr,selectedDate,genderSelected+"",3+"",phoneNumber.trim());
           insertNewProfile.registerNewProfile(profile,this);
           // insertNewProfile.registerRetrofitRequest(profile,this);
            dataValidFlag=true ;
        }
        else if(!checkAll()){
            Toast.makeText(RegisterActivity.this,"ERROR ..",Toast.LENGTH_SHORT).show();
        }
    }



    private boolean checkAll(){
        String value=binding.genderSpinner.getSelectedItem().toString();
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

       /* Log.d("first name empty"+!firstnameStr.isEmpty(),"first name empty"+!firstnameStr.isEmpty());
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
        Log.d("check contains space last "+!checkContainSpace(lastnameStr)+"","check contains space last "+!checkContainSpace(lastnameStr));*/



        return (!firstnameStr.isEmpty() && !lastnameStr.isEmpty() && !emailStr.isEmpty()
                && !passwordStr.isEmpty() && !confPasswordStr.isEmpty() && !phoneNumberStr.isEmpty()
                && !phonePrefixStr.isEmpty() && passwordStr.equals(confPasswordStr) && isEmailValid() &&
                passwordStr.trim().length() > 10 && containsTwoCases() && gender && containsOnlyCharacters(firstnameStr) && containsOnlyCharacters(lastnameStr)
        && validBirthDateFlag != 0 && !checkContainSpace(firstnameStr.trim()) && !checkContainSpace(lastnameStr.trim())) ;
    }
    public boolean isEmailValid() {
        String emailFormat = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailFormat);
        Matcher matcher = pattern.matcher(emailStr);
        if(matcher.matches()){
            return true;
        }
        else {
            binding.email.setError("* Error Email Format");
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
            binding.progressBar.setVisibility(ProgressBar.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Toast.makeText(RegisterActivity.this,"Account created , you can sign in now",Toast.LENGTH_LONG).show();
                    finish();
                }
            },1500);

            //MyAlertDialog.showCustomAlerDialogForRegistrationDone(this);
        }
        else if(result == -2){
            binding.email.setError("Email already registered ...");
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
                            binding.birthDateLayout.setError("* You must be at least 18 years old");
                        }
                        else{
                            binding.birthDateLayout.setError(null);
                            validBirthDateFlag=1;
                        }
                        binding.birthDateLayout.getEditText().setText(dayOfMonth+"-"+(month+1)+"-"+year);
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }
}