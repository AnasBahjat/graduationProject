package com.example.graduationproject.errorHandling;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.graduationproject.R;

public class MyAlertDialog {

    public static void showCustomAlertDialogLoginError(Context context, String title, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog_builder, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.baseline_error_24);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        titleTextView.setText(title);
        errorTextView.setText(errorMessage);
        builder.setView(view);

        builder.setPositiveButton("OK", null);


        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void showCustomAlertDialogSpinnerError(Context context, String title, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog_builder, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.baseline_error_24);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        titleTextView.setText(title);
        errorTextView.setText(errorMessage);
        builder.setView(view);

        builder.setPositiveButton("OK", null);


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("ResourceAsColor")
    public static void showCustomAlerDialogForRegistrationDone(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog_builder, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.icon_done);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        titleTextView.setText("Registration Done");
        errorTextView.setText("Account created successfully ");
        errorTextView.setTextColor(R.color.green);
        builder.setView(view);

        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showCustomDialogForTeacherAccountConfirmed(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog_builder, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.icon_done);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        titleTextView.setText(context.getString(R.string.teacherAccountConfirmedTitle));
        errorTextView.setText(context.getString(R.string.teacherAccountConfirmedMsg));
        errorTextView.setTextColor(context.getColor(R.color.green));
        builder.setView(view);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void teacherAccountAlreadyConfirmed(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog_builder, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.icon_done);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        titleTextView.setText(context.getString(R.string.teacherAccountConfirmedTitleAlready));
        errorTextView.setText(context.getString(R.string.teacherAccountConfirmedMsgAlready));
        errorTextView.setTextColor(context.getColor(R.color.green));
        builder.setView(view);

        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showCustomDialogForParentAccountConfirmed(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog_builder, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.icon_done);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        titleTextView.setText(context.getString(R.string.teacherAccountConfirmedTitle));
        errorTextView.setText(context.getString(R.string.parentAccountConfirmedMsg));
        errorTextView.setTextColor(context.getColor(R.color.green));
        builder.setView(view);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }




}