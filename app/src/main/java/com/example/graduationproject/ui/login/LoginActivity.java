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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements RequestResult {
    private Database database;
    private ActivityLoginBinding binding ;
    private SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth ;
    DatabaseReference databaseReference ;
    ProgressDialog progressDialog;
 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
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
            binding.loginProgressBar.setVisibility(View.GONE);
            binding.emailText.setError("* Fill in this field");
        }
        if(binding.passwordText.getEditText().getText().toString().isEmpty()){
            binding.loginProgressBar.setVisibility(View.GONE);
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

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.loginProgressBar.setVisibility(View.GONE);
                                startActivity(intent);
                            }
                        },1500);
                        try{
                            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        if (user != null) {
                                            String userId = user.getUid();
                                            databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(userId);
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        String userName = snapshot.child("name").getValue(String.class);
                                                        // Intent intent = new Intent(login.this, MainActivity.class);
                                                        //  startActivity(intent);
                                                        finish();
                                                    } else {
                                                        //   Toast.makeText(login.this, "User data not found", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    //  Toast.makeText(login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } else {
                                        //  Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        catch (Exception exc){
                            Toast.makeText(LoginActivity.this, "Wrong with firebase ...", Toast.LENGTH_SHORT).show();
                            Log.d("Error -----> "+exc.getMessage(), "Error -----> "+exc.getMessage());
                        }

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

                        try{
                            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        if (user != null) {
                                            String userId = user.getUid();
                                            databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(userId);
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        String userName = snapshot.child("name").getValue(String.class);
                                                        // Intent intent = new Intent(login.this, MainActivity.class);


                                                        //  startActivity(intent);
                                                        finish();
                                                    } else {
                                                        //   Toast.makeText(login.this, "User data not found", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    //  Toast.makeText(login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } else {
                                        //  Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        catch (Exception exception){
                            Toast.makeText(LoginActivity.this, "Wrong with firebase ...", Toast.LENGTH_SHORT).show();
                            Log.d("Error -----> "+exception.getMessage(), "Error -----> "+exception.getMessage());
                        }


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.loginProgressBar.setVisibility(View.GONE);
                                startActivity(intent);
                            }
                        },1500);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
        }
    }
}