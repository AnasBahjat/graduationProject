package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graduationproject.models.CustomChildData;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<CustomChildData> {
    private Context context;
    private List<CustomChildData> children;

    public CustomSpinnerAdapter(Context context, List<CustomChildData> children) {
        super(context, android.R.layout.simple_spinner_item, children);
        this.context = context;
        this.children = children;
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        CustomChildData child = children.get(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(child.toString());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        CustomChildData child = children.get(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(child.toString());
        return convertView;
    }
}