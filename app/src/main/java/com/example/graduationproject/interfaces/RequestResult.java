package com.example.graduationproject.interfaces;

import org.json.JSONArray;

public interface RequestResult {
    void onSuccess(int result);
    void onLoginSuccess(String message,JSONArray loginSuccessData);

}
