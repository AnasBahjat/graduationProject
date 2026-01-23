package com.example.graduationproject.modelView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.graduationproject.database.Database;
import com.example.graduationproject.models.Notifications;

import java.util.List;

public class DatabaseViewModel extends AndroidViewModel {
    public DatabaseViewModel(@NonNull Application application, Database database,String email) {
        super(application);
     //   private LiveData<List<Notifications>> = database.getNotifications(email);
    }
}
