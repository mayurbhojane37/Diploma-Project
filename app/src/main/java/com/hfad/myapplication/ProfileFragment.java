package com.hfad.myapplication;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText emailField;
    private Button emailButton,changePass;
    private EditText newPass,confirmPass;
    ImageView iv;
String enrollment;
    TextView tv;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        try {
            mAuth = FirebaseAuth.getInstance();

            Button logout = Objects.requireNonNull(getView()).findViewById(R.id.logout_button);
            final Button deleteAccount = getView().findViewById(R.id.delete_account);
            final Button changeMail = getView().findViewById(R.id.change_email);
            final Button changePassword = getView().findViewById(R.id.change_password);
            emailField = Objects.requireNonNull(getView()).findViewById(R.id.reset_email_field);
            emailButton = getView().findViewById(R.id.reset_email_button);
            ImageView editPic = getView().findViewById(R.id.edit_pic);
            //-----------------------------------------------------------------------------------


            //-----------------------------------------------------------------------------------

            newPass = Objects.requireNonNull(getView()).findViewById(R.id.reset_password);
            confirmPass = getView().findViewById(R.id.reset_password_confirm);
            changePass = getView().findViewById(R.id.reset_password_button);

            user = mAuth.getCurrentUser();
            if(user!=null){
                //There is a user.
                if(user.getDisplayName().toString().substring(0,5).equals("Admin"))
                enrollment = Objects.requireNonNull(user.getDisplayName()).substring(6);
                else
                    enrollment = Objects.requireNonNull(user.getDisplayName()).substring(9);
                logout.setVisibility(View.VISIBLE);
            }
            iv = getView().findViewById(R.id.account_pic);
            tv = getView().findViewById(R.id.username);
            //-------------------------------------------------------------------------------------

            DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("profile").child("dpImage");
            dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null)
                    {
                        Glide.with(getActivity()).load(dataSnapshot.getValue()).into(iv);
                        iv.setScaleType(ImageView.ScaleType.FIT_XY);

                    }

                    else Toast.makeText(getActivity(), "Please upload your profile picture! ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            dbf = FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("profile").child("name");
            dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null)
                    {
                        tv.setText(dataSnapshot.getValue().toString());

                    }

                    else Toast.makeText(getActivity(), "Please upload your profile Name! ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //-------------------------------------------------------------------------------------

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoutMethod();
                }
            });

            deleteAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAccountMethod();
                }
            });

            changeMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateLayoutEmail();
                }
            });

            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePasswordMethod();
                }
            });

            editPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectUserDp();
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
        }

    }
    private void hideSettings(){
        Objects.requireNonNull(getView()).findViewById(R.id.settings_layout).setVisibility(View.GONE);
    }

    private void unHideSettings(){
        Objects.requireNonNull(getView()).findViewById(R.id.settings_layout).setVisibility(View.VISIBLE);
    }


    private void updateLayoutEmail(){
        //User wants to change its mail.
        //We have to create a edit text for the user to enter his/her new email address.
        hideSettings();
        emailField.setVisibility(View.VISIBLE);
        emailButton.setVisibility(View.VISIBLE);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMail();
            }
        });
    }

    private void resetMail(){
        String newEmail = emailField.getText().toString().trim();
        user.updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                            unHideSettings();
		                    emailField.setVisibility(View.GONE);
		                    emailButton.setVisibility(View.GONE);
                        }
                        else {
                            Toast.makeText(getContext(), "Please Login again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void changePasswordMethod(){
        //User wants to change password.
        //We need to show them two edit text one for New password and confirm new password.
        updateLayoutPassword();
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword(){
        String mPassword = newPass.getText().toString().trim();
        String mConfirm = confirmPass.getText().toString().trim();
        if (mPassword.equals(mConfirm)){
            user.updatePassword(mPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(),"Password Reset Successful!", Toast.LENGTH_SHORT).show();
		unHideSettings();
		newPass.setVisibility(View.GONE);
		confirmPass.setVisibility(View.GONE);
		changePass.setVisibility(View.GONE);
                            }
                            else{
                                Toast.makeText(getContext(), "Please Login Again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            confirmPass.setText("");
            confirmPass.setBackgroundResource(R.color.coral);
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();

        }

    }

    private void updateLayoutPassword(){
        hideSettings();
        newPass.setVisibility(View.VISIBLE);
        confirmPass.setVisibility(View.VISIBLE);
        changePass.setVisibility(View.VISIBLE);
    }

    private void deleteAccountMethod(){
        hideSettings();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(),"User account deleted successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(),SignUpActivity.class));
                }
                else
                {
                    Toast.makeText(getContext(), "Please Login Again.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void logoutMethod(){

        mAuth.signOut();
        startActivity(new Intent(getContext(),LoginActivity.class));
    }

    private void selectUserDp()
    {
        Intent intent = new Intent(getActivity(),EditProfile.class);
        intent.putExtra("Enrollment",enrollment);
        startActivity(intent);
    }

}
