package com.example.graduationproject.messaging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.graduationproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SettingMessagingActivity extends AppCompatActivity {
    ImageView setprofile;
    EditText setname, setstatus;
    Button donebut;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri setImageUri;
    String email, password, currentName, currentStatus, currentProfilePic;
    ProgressDialog progressDialog;
    String profiletype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        setprofile = findViewById(R.id.settingprofile);
        setname = findViewById(R.id.settingname);
        donebut = findViewById(R.id.donebutt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if each child exists before trying to access its value
                if (snapshot.child("mail").getValue() != null) {
                    email = snapshot.child("mail").getValue().toString();
                }
                if (snapshot.child("password").getValue() != null) {
                    password = snapshot.child("password").getValue().toString();
                }
                if (snapshot.child("userName").getValue() != null) {
                    currentName = snapshot.child("userName").getValue().toString();
                    setname.setText(currentName);
                }
                if (snapshot.child("profilepic").getValue() != null) {
                    currentProfilePic = snapshot.child("profilepic").getValue().toString();
                    Picasso.get().load(currentProfilePic).into(setprofile);
                }
                if (snapshot.child("status").getValue() != null) {
                    currentStatus = snapshot.child("status").getValue().toString();
                    setstatus.setText(currentStatus);
                }
                if (snapshot.child("prfiletype").getValue() != null) {
                    profiletype = snapshot.child("prfiletype").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
                Toast.makeText(SettingMessagingActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        setprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });
//
        donebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = setname.getText().toString();
                String status = setstatus.getText().toString();

                // Check if any changes are made
                if (name.equals(currentName) && status.equals(currentStatus) && setImageUri == null) {
                    Toast.makeText(SettingMessagingActivity.this, "No changes were made", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();

                    if (setImageUri != null) {
                        storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String finalImageUri = uri.toString();
                                            saveUserData(reference, name, email, password, finalImageUri, status, profiletype);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            handleStorageError(e);
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingMessagingActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        saveUserData(reference, name, email, password, currentProfilePic, status, profiletype);
                    }
                }
            }
        });
    }

    private void saveUserData(DatabaseReference reference, String name, String email, String password, String imageUri, String status, String profileType) {
        Users users = new Users(auth.getUid(), name, email, password, imageUri, status, profileType);
        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(SettingMessagingActivity.this, "Data is saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingMessagingActivity.this, ChatMainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SettingMessagingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleStorageError(Exception e) {
        progressDialog.dismiss();
        if (e instanceof StorageException) {
            StorageException se = (StorageException) e;
            if (se.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                Toast.makeText(SettingMessagingActivity.this, "File not found at the specified location.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingMessagingActivity.this, "An error occurred: " + se.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SettingMessagingActivity.this, "An unknown error occurred.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && data != null && data.getData() != null) {
            setImageUri = data.getData();
            setprofile.setImageURI(setImageUri);
        }
    }
}
