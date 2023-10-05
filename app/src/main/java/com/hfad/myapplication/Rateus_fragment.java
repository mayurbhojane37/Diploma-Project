package com.hfad.myapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import android.view.LayoutInflater;


import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Rateus_fragment extends Fragment {

    private RatingBar ratingBar;
    private Button button;
    final

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View rootView=inflater.inflate(R.layout.fragment_rateus, container, false);
        ratingBar=(RatingBar) rootView.findViewById(R.id.ratingBar);
        button=(Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String r=String.valueOf(ratingBar.getRating());

                DatabaseReference db = FirebaseDatabase.getInstance().getReference("feedback_and_rating");
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String enrollment =  Objects.requireNonNull(user.getDisplayName()).substring(9);
                db.child(enrollment).child("rating").setValue(r);

                Toast.makeText(getContext(), "Thankyou! ", Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }


}
