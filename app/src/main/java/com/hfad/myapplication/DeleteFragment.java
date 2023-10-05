package com.hfad.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class DeleteFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText passField;
    TextView mail,passkey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.delete_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(Objects.requireNonNull(view), savedInstanceState);
        passField = Objects.requireNonNull(getView()).findViewById(R.id.ask_passkey);
        Button deleteButton = getView().findViewById(R.id.delete_database);
        mail = getView().findViewById(R.id.ask_mail);

        mAuth = FirebaseAuth.getInstance();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTheAdmin();
            }
        });

    }

    private void checkTheAdmin() {


            if (mail.getText().toString().equals("") || passField.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter Valid Credentials", Toast.LENGTH_SHORT).show();
                return;
            }
            AuthCredential credential = EmailAuthProvider.getCredential(mail.getText().toString(), passField.getText().toString());

// Prompt the user to re-provide their sign-in credentials

            try {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                        if (task.isSuccessful()) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setMessage("Are you sure, You want to delete all the database.\nThis is only recommended at the end of a semester!");
                            alertDialogBuilder.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Toast.makeText(getContext(), "You clicked yes button", Toast.LENGTH_LONG).show();
                                            wipeAllData();
                                        }
                                    });

                            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "You have cancelled the Operation.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else {
                            Toast.makeText(getActivity(), "email of password was incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }catch(Exception e) {

            }
        }

    private void wipeAllData()
    {
        try {
            DatabaseReference dbImage = FirebaseDatabase.getInstance().getReference();
            dbImage.child("Teacher").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "You have cleared Teachers' data", Toast.LENGTH_SHORT).show();
                }
            });
            dbImage.child("Student").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "You have cleared Students' data", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(getContext(), "There is a server issue, Please try again later", Toast.LENGTH_SHORT).show();
        }
        }
}