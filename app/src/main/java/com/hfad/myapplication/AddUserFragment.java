package com.hfad.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Objects;

public class AddUserFragment extends Fragment {
    private EditText id_field;
    private String type_of_account;
    private long count;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_user_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(Objects.requireNonNull(view), savedInstanceState);

        id_field = Objects.requireNonNull(getView()).findViewById(R.id.id_of_user);

        Button addButton = getView().findViewById(R.id.addID);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUsersID();
            }
        });

        RadioButton teacher_radio = getView().findViewById(R.id.teacher_radio_admin);
        RadioButton student_radio = getView().findViewById(R.id.student_radio_admin);
        teacher_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_of_account = "Teachers";
                Toast.makeText(getContext(), type_of_account, Toast.LENGTH_SHORT).show();
            }
        });
        student_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_of_account = "Students";
                Toast.makeText(getContext(), type_of_account, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUsersID(){
        try {
            Toast.makeText(getActivity(), "addUsersId Called", Toast.LENGTH_SHORT).show();
        final String id = id_field.getText().toString().trim();
        if (!(type_of_account.length()==0 || id.length()==0)) {
            DatabaseReference dbImage = FirebaseDatabase.getInstance().getReference().child("UserIDs").child(type_of_account);
            dbImage.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    count = dataSnapshot.getChildrenCount();
                    Toast.makeText(getContext(), "Total no of Users "+ count, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            count++;
            dbImage.child(String.valueOf(count)).setValue(id).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "ID "+id+" added successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
        }
        catch (Exception e){
            Toast.makeText(getContext(),"You can not proceed. Contact the App Admin", Toast.LENGTH_SHORT).show();
        }
    }
}