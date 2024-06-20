package com.example.graduationproject.ui.teacherUi;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.graduationproject.databinding.ConfirmPhoneNumberBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PopUpWindows {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static Long timeOutSeconds = 60L;
    private static PhoneAuthProvider.ForceResendingToken resendingToken;
    private static String verificationCode;
    private static boolean flag ;
    private static Context context ;
    private static Activity activity;
    public static boolean showConfirmPhoneNumberPopupWindow(Context context,Activity activity, LayoutInflater layoutInflater,String phoneNumber){
        PopUpWindows.activity=activity;
        PopUpWindows.context=context;
        Log.d("phone ----------> "+phoneNumber,"phone ----------> "+phoneNumber);
        ConfirmPhoneNumberBinding confirmPhoneNumberBinding = ConfirmPhoneNumberBinding.inflate(layoutInflater);
        int width = 1150;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        PopupWindow confirmPhoneNumberPopupWindow = new PopupWindow(confirmPhoneNumberBinding.getRoot(),width,height,true);
        confirmPhoneNumberPopupWindow.showAtLocation(confirmPhoneNumberBinding.otpParentLayout, Gravity.CENTER,0,0);
        confirmPhoneNumberBinding.confirmBtn.setOnClickListener(ad->{
            flag = authPhoneNumber(confirmPhoneNumberBinding,phoneNumber);
        });
        return flag ;
    }
    private static boolean authPhoneNumber(ConfirmPhoneNumberBinding confirmPhoneNumberBinding,String phoneNumber ){
        Log.d("phone ----------> "+phoneNumber,"phone ----------> "+phoneNumber);
        flag = false ;
            PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(timeOutSeconds, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            flag = true ;
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            flag = false ;
                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            verificationCode = s;
                            resendingToken=forceResendingToken;
                            Toast.makeText(context.getApplicationContext(),"OTP sent to the entered phone number",Toast.LENGTH_SHORT).show();
                        }
                    });
            return flag ;
        }
}
