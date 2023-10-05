package com.hfad.myapplication;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Feedback_Fragment extends Fragment {
     EditText feedback;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(Objects.requireNonNull(view), savedInstanceState);
        Button button = Objects.requireNonNull(getView()).findViewById(R.id.button2);
       feedback = Objects.requireNonNull(getView()).findViewById(R.id.feedback);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFeedback();
            }
        });
    }

    public void submitFeedback()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("feedback_and_rating");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String suggestions = feedback.getText().toString();
        String enrollment =  Objects.requireNonNull(user.getDisplayName()).substring(9);
        db.child(enrollment).child("feedback").setValue(suggestions);
        Toast.makeText(getContext(), "Thankyou! We will improvise.", Toast.LENGTH_SHORT).show();
    }

}
