package com.example.graduationproject.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.material.textfield.TextInputEditText;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.ActivityLoginBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.R;
import com.example.graduationproject.ui.register.RegisterActivity;
import com.example.graduationproject.interfaces.RequestResult;
import com.example.graduationproject.ui.afterLogin.AfterLoginActivity;
import com.google.android.material.textfield.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements RequestResult {
    private Database database;
    private ActivityLoginBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
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
        }
        else if(message.equals("wrong password")){
            binding.passwordText.setError("Wrong password ");
        }
        else if(message.equals("ERROR")){
        }
        else {
                try {
                    Intent intent=new Intent(this, AfterLoginActivity.class);
                    JSONObject jsonObject=loginSuccessData.getJSONObject(0);
                    String email=jsonObject.getString("email");
                    String firstName=jsonObject.getString("firstname");
                    String lastName=jsonObject.getString("lastname");
                    String password=jsonObject.getString("password");
                    String idNumber=jsonObject.getString("IDnumber");
                    String birthDate=jsonObject.getString("birthDate");
                    String phoneNumber=jsonObject.getString("phoneNumber");
                    String city=jsonObject.getString("city");
                    String country=jsonObject.getString("country");



                    // start activity based on profile type ,,,

                    Log.d("----->"+jsonObject.getInt("signedIn"),"----->"+jsonObject.getInt("signedIn"));
                    Log.d(email,email);

                    if(jsonObject.getInt("signedIn")==1){
                        MyAlertDialog.showCustomAlertDialogLoginError(this,"Error Sign In","Your Account Is Already Signed In from another device ..");
                    }
                    else {
                        database.updateLogin(email);
                        intent.putExtra("email", email);
                        intent.putExtra("firstname", firstName);
                        intent.putExtra("lastname", lastName);
                        intent.putExtra("password", password);
                        intent.putExtra("birthDate", birthDate);
                        intent.putExtra("phonenumber", phoneNumber);
                        intent.putExtra("city", city);
                        intent.putExtra("country", country);
                        intent.putExtra("idNumber", idNumber);
                        startActivity(intent);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
        }
    }
}