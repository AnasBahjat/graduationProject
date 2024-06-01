package com.example.graduationproject.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

//import com.google.android.material.textfield.TextInputEditText;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.ActivityLoginBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.R;
import com.example.graduationproject.ui.register.RegisterActivity;
import com.example.graduationproject.interfaces.RequestResult;
import com.example.graduationproject.ui.afterLogin.AfterLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.*;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LoginActivity extends AppCompatActivity implements RequestResult {
    private Database database;
    private ActivityLoginBinding binding ;

    private ActivityResultLauncher<String> resultLauncher= registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            isGranted-> {
        if(isGranted){

        }
        else {

        }
    });

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
        database=new Database(this);
    }

    public void loginClicked(View view) {
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
                binding.emailText.setError("");
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
                binding.passwordText.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(!binding.emailText.getEditText().getText().toString().isEmpty() && !binding.passwordText.getEditText().getText().toString().isEmpty()){
            binding.loginProgressBar.setVisibility(View.VISIBLE);
            database.loginCheck(binding.emailText.getEditText().getText().toString(),binding.passwordText.getEditText().getText().toString(),this);

        }
    }

    public void createAccountClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
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
            MyAlertDialog.showCustomAlertDialogLoginError(this,"Login Error","Something went wrong , please try again later");
            binding.loginProgressBar.setVisibility(View.GONE);
        }
        else {
            binding.loginProgressBar.setVisibility(View.GONE);
                try {
                    Intent intent=new Intent(this, AfterLoginActivity.class);
                    JSONObject jsonObject=loginSuccessData.getJSONObject(0);
                    String email=jsonObject.getString("email");
                    String firstName=jsonObject.getString("firstname");
                    String lastName=jsonObject.getString("lastname");
                    String password=jsonObject.getString("password");
                    String birthDate=jsonObject.getString("birthDate");
                    String phoneNumber=jsonObject.getString("phoneNumber");
                    String profileType = jsonObject.getString("profileType");

                    intent.putExtra("email",email);
                    intent.putExtra("firstName",firstName);
                    intent.putExtra("lastName",lastName);
                    intent.putExtra("password",password);
                    intent.putExtra("birthDate",birthDate);
                    intent.putExtra("phoneNumber",phoneNumber);
                    intent.putExtra("profileType",profileType);
                    intent.putExtra("accountDone",jsonObject.getString("doneInformation"));
                    startActivity(intent);

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
        }
    }
}