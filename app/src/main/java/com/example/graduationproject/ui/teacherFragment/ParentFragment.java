package com.example.graduationproject.ui.teacherFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.StudentAdapter;
import com.example.graduationproject.models.Parent;
import com.example.graduationproject.models.Student;

import java.util.ArrayList;
import java.util.List;

public class ParentFragment extends Fragment {

    private TextView parentName;
    private RecyclerView studentRecyclerView;
    private StudentAdapter studentAdapter;
    private Parent parent;

    public ParentFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent, container, false);

        parentName = view.findViewById(R.id.parentName);
        studentRecyclerView = view.findViewById(R.id.studentRecyclerView);

        // Initialize parent object (this should be passed or retrieved from a ViewModel)
        parent = new Parent("John Doe", "johndoe@example.com", "123456", "Science", null, getDummyStudents());

        parentName.setText(parent.getName());

        studentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentAdapter = new StudentAdapter(parent.getStdlist(), this::onStudentSelected);
        studentRecyclerView.setAdapter(studentAdapter);

        return view;
    }

    private List<Student> getDummyStudents() {

        List<Student> test = new ArrayList<>();

        test.add(new Student("Alice", "123456", "johndoe@example.com"));
        test.add( new Student("Bob", "123456", "johndoe@example.com"));
        return test;
    }

    private void onStudentSelected(Student student) {
        // Handle student selection
        // For example, you can navigate to a detail fragment or display a message
         Toast.makeText(getContext(), "Selected: " + student.getName(), Toast.LENGTH_SHORT).show();
    }
}
