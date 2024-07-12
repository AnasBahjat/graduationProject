package com.example.graduationproject.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.material.textfield.TextInputEditText;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.database.SharedPreferencesManager;
import com.example.graduationproject.databinding.ActivityLoginBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.ui.parentUi.ParentActivity;
import com.example.graduationproject.ui.register.RegisterActivity;
import com.example.graduationproject.interfaces.RequestResult;
import com.example.graduationproject.ui.teacherUi.TeacherActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements RequestResult {
    private Database database;
    private ActivityLoginBinding binding ;
    private SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth ;
    String currentUserPassword="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        emptyTheInputs();
    }

    private void emptyTheInputs(){
        binding.emailEditText.setText(null);
        binding.passwordEditText.setText(null);
    }
    private void initialize(){
        binding.rememberMeBtn.setChecked(false);
        database=new Database(this);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        auth = FirebaseAuth.getInstance();
        checkIfDataSaved();

    }

    private void checkIfDataSaved(){
        if(sharedPreferencesManager.getSavedEmail() != null && sharedPreferencesManager.getSavedPassword() != null){
            binding.rememberMeBtn.setChecked(true);
            binding.emailEditText.setText(sharedPreferencesManager.getSavedEmail());
            binding.passwordEditText.setText(sharedPreferencesManager.getSavedPassword());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "logged out ...", Toast.LENGTH_SHORT).show();
        checkIfDataSaved();
    }

    public void loginClicked(View view) {
        binding.loginProgressBar.setVisibility(View.VISIBLE);

        if(binding.rememberMeBtn.isChecked() &&
                !binding.emailEditText.getText().toString().isEmpty() &&
                !binding.passwordEditText.getText().toString().isEmpty()){
            sharedPreferencesManager.saveLoginDate(binding.emailEditText.getText().toString().trim(),
                    binding.passwordEditText.getText().toString());
        }

        else {
            sharedPreferencesManager.removeLoginData();
        }

        if(binding.emailText.getEditText().getText().toString().isEmpty()){
            binding.emailText.setError("* Fill in this field");
        }
        if(binding.passwordText.getEditText().getText().toString().isEmpty()){
            binding.passwordText.setError("* Fill in this field");
        }

        binding.emailText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.emailText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.passwordText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //binding.passwordText.setError(null);
                if (s.length() > 0) {
                    binding.passwordText.setError(null);
                    binding.passwordText.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(!binding.emailText.getEditText().getText().toString().isEmpty() && !binding.passwordText.getEditText().getText().toString().isEmpty()){
            // binding.loginProgressBar.setVisibility(View.VISIBLE);
            currentUserPassword = binding.passwordText.getEditText().getText().toString();
            database.loginCheck(binding.emailText.getEditText().getText().toString(),binding.passwordText.getEditText().getText().toString(),this);

        }
    }

    public void createAccountClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        binding.loginProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(int result) {

    }

    @Override
    public void onLoginSuccess(String message,JSONArray loginSuccessData) {
        if(message.equals("email does not exist")){
            binding.emailText.setError("Email is not registered");
            binding.loginProgressBar.setVisibility(View.GONE);
        }
        else if(message.equals("wrong password")){
            binding.loginProgressBar.setVisibility(View.GONE);
            binding.passwordText.setError("Wrong password ");
        }
        else if(message.equals("ERROR")){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Login Error","Something went wrong , please try again later");
            binding.loginProgressBar.setVisibility(View.GONE);
        }
        else if(message.equals("volleyError")){
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Login Error","Network Error , please check your internet connection ..");
            binding.loginProgressBar.setVisibility(View.GONE);
        }
        else {
            binding.loginProgressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject=loginSuccessData.getJSONObject(0);

                    if(jsonObject.getString("profileType").equals("1")){
                        Intent intent=new Intent(this, TeacherActivity.class);
                        String email=jsonObject.getString("email");
                        String firstName=jsonObject.getString("firstname");
                        String lastName=jsonObject.getString("lastname");
                        String password = jsonObject.getString("password");
                        String birthDate=jsonObject.getString("birthDate");
                        String profileType = jsonObject.getString("profileType");

                        intent.putExtra("email",email);
                        intent.putExtra("firstName",firstName);
                        intent.putExtra("lastName",lastName);
                        intent.putExtra("password",password);
                        intent.putExtra("birthDate",birthDate);
                        intent.putExtra("profileType",profileType);
                        intent.putExtra("accountDone",jsonObject.getString("doneInformation"));
                        binding.loginProgressBar.setVisibility(View.VISIBLE);






                        auth.signInWithEmailAndPassword(email.toLowerCase().trim(),currentUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.loginProgressBar.setVisibility(View.GONE);
                                            startActivity(intent);
                                        }
                                    },1500);
                                }
                                else {
                                    MyAlertDialog.showCustomAlertDialogSpinnerError(LoginActivity.this,"Error signing in","Please try again");
                                }
                            }
                        });





                    }
                    else {
                        Intent intent = new Intent(LoginActivity.this, ParentActivity.class);
                        String email=jsonObject.getString("email");
                        String firstName=jsonObject.getString("firstname");
                        String lastName=jsonObject.getString("lastname");
                        String password=jsonObject.getString("password");
                        String birthDate=jsonObject.getString("birthDate");
                        String profileType = jsonObject.getString("profileType");
                        intent.putExtra("email",email);
                        intent.putExtra("firstName",firstName);
                        intent.putExtra("lastName",lastName);
                        intent.putExtra("password",password);
                        intent.putExtra("birthDate",birthDate);
                        intent.putExtra("profileType",profileType);
                        intent.putExtra("accountDone",jsonObject.getString("doneInformation"));
                        binding.loginProgressBar.setVisibility(View.VISIBLE);
                        auth.signInWithEmailAndPassword(email.toLowerCase().trim(),currentUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.loginProgressBar.setVisibility(View.GONE);
                                            startActivity(intent);
                                        }
                                    },1500);
                                }
                                else {
                                    MyAlertDialog.showCustomAlertDialogSpinnerError(LoginActivity.this,"Error signing in","Please try again");
                                }
                            }
                        });
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
        }
    }
}