package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements RequestResult{
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
    }

    public void loginClicked(View view) {
        if(email.getEditText().getText().toString().isEmpty()){
            email.setError("* Fill in this field");
        }
        if(password.getEditText().getText().toString().isEmpty()){
            password.setError("* Fill in this field");
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
           // Toast.makeText(LoginActivity.this,"Login done",Toast.LENGTH_SHORT).show();
            Database loginRequest=new Database(this);
            loginRequest.loginCheck(email.getEditText().getText().toString(),password.getEditText().getText().toString(),this);

        }
    }

    public void createAccountClicked(View view) {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSuccess(int result) {

    }

    @Override
    public void onLoginSuccess(String message, JSONArray loginSuccessData) {
        if(message.equals("email does not exist")){
            email.setError("Email is not registered");
        }
        else if(message.equals("wrong password")){
            password.setError("Wrong password ");
        }
        else if(message.equals("ERROR")){
        }
        else {
           for(int i=0;i<loginSuccessData.length();i++){
                try {
                    JSONObject jsonObject=loginSuccessData.getJSONObject(i);
                    String firstName=jsonObject.getString("firstname");
                    String lastName=jsonObject.getString("lastname");
                    String password=jsonObject.getString("password");
                    String birthDate=jsonObject.getString("birthDate");
                    Log.d("1---> firstname = "+firstName,"1---> firstname = "+firstName);
                    Log.d("2---> lastname = "+lastName,"2---> lastname = "+lastName);
                    Log.d("3---> password = "+password,"3---> password = "+password);
                    Log.d("4---> birthDate = "+birthDate,"4---> password = "+birthDate);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}