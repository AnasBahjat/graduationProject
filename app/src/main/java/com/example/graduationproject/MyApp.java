package com.example.graduationproject;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags("en")
        );
    }
}