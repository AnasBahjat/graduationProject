package com.example.graduationproject.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.graduationproject.ui.afterLogin.AfterLoginActivity;

import java.util.Objects;

public class BroadcastHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            if(context instanceof AfterLoginActivity){

            }
    }
}
