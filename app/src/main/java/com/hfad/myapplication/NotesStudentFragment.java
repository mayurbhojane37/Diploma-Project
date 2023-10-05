package com.hfad.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.Objects;

public class NotesStudentFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notes_student_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(Objects.requireNonNull(view), savedInstanceState);

        Button fetch = Objects.requireNonNull(getView()).findViewById(R.id.fetch_notes_student);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTeacherUploadNotes();
            }
        });
    }

    private void goToTeacherUploadNotes(){
        String type = "Students";
        Intent intent = new Intent(getContext(),TeacherUploadNotes.class);
        intent.putExtra("AccountType",type);
        startActivity(intent);
    }

}
