package com.hfad.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class SignUpActivity extends AppCompatActivity {

    String type_of_account;
    String email;
    String password, c_password;
    String id;
    String key_of_matched_id;
    Button submit;
    RadioButton teacher_radio, student_radio, admin_radio;
    EditText email_field, pass_field, id_field, confirm_field;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser newUser;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        email_field = findViewById(R.id.email_field);
        pass_field = findViewById(R.id.password_field);
        id_field = findViewById(R.id.id_field);
        confirm_field = findViewById(R.id.confirm_password_field);
        submit = findViewById(R.id.sign_up_button_final);

        teacher_radio = findViewById(R.id.teacher_radio);
        student_radio = findViewById(R.id.student_radio);
        admin_radio = findViewById(R.id.admin_radio);


        teacher_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_of_account = "Teachers";
                Toast.makeText(SignUpActivity.this, type_of_account, Toast.LENGTH_SHORT).show();
            }
        });
        student_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_of_account = "Students";
                Toast.makeText(SignUpActivity.this, type_of_account, Toast.LENGTH_SHORT).show();
            }
        });
        admin_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_of_account = "Admin";
                Toast.makeText(SignUpActivity.this, type_of_account, Toast.LENGTH_SHORT).show();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStuff();
            }
        });
    }

    public void checkStuff() {
        if (validateForm()) {
            try {
                getListOfIDs();
            }
            catch(Exception exception){
                Toast.makeText(this, "Exception ="+exception, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Form not valid", Toast.LENGTH_SHORT).show();
            disableFields(true);
        }
    }

    public void createNewUser(String key_of_matched_id) {
        //Toast.makeText(this, "Finishing account creation with key "+key_of_matched_id, Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Sign Up successful", Toast.LENGTH_SHORT).show();
                    newUser = mAuth.getCurrentUser();
                    try {
                        configureUser();
                    }
                    catch(Exception exc)
                    {
                        Toast.makeText(SignUpActivity.this, "Wait for a minute", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Sign in fails and the user is informed about it.
                    Toast.makeText(SignUpActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void configureUser() {
        try {
            Toast.makeText(this, "Configuring the User details", Toast.LENGTH_SHORT).show();
            if (!type_of_account.equals("Admin")) {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(type_of_account + " " + id).build();
                newUser.updateProfile(profile);
            } else {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(type_of_account+" "+id).build();
                newUser.updateProfile(profile);
            }
            Toast.makeText(this, "Configuring User done", Toast.LENGTH_SHORT).show();
        }
        catch (Exception exx){
            Toast.makeText(this, "Could not configure user "+exx, Toast.LENGTH_SHORT).show();
        }
        try {
            updateUI();
        }
        catch
        (Exception e){
            Toast.makeText(this, "Reopen Your App", Toast.LENGTH_SHORT).show();
        }
    }

    /*public void signOut(){
        mAuth.signOut();
    }*/

    public boolean validateForm() {
        Toast.makeText(this, "Validating Details", Toast.LENGTH_SHORT).show();
        email = email_field.getText().toString().trim();
        password = pass_field.getText().toString().trim();
        id = id_field.getText().toString().trim();
        c_password = confirm_field.getText().toString().trim();

        disableFields(false);
        if (type_of_account.length() == 0) {
            Toast.makeText(this, "Select a type of Account", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() == 0 && c_password.length() == 0) {
            Toast.makeText(this, "Password required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(c_password)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.length() == 0) {
            Toast.makeText(this, "This is the email - > " + email, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Please Enter a Valid Email Address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (id.length() == 0) {
            Toast.makeText(this, "Enter an ID ", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*if(!temp){
            Toast.makeText(this, "Unable to find that ID", Toast.LENGTH_SHORT).show();
        }*/

        //Toast.makeText(this, "Details seem okay!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void getListOfIDs() {
        try {

            //Toast.makeText(this, "Checking your ID", Toast.LENGTH_SHORT).show();
            Query myUserIdList = database.getReference().child("UserIDs").child(type_of_account);

            myUserIdList.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        boolean flag=false;
                        for (DataSnapshot userIDs : dataSnapshot.getChildren()) {

                            if (id.equals(Objects.requireNonNull(userIDs.getValue()).toString().trim())) {
                                flag = true;
                                try {
                                    key_of_matched_id = Objects.requireNonNull(userIDs.getKey()).trim();

//                                mAppIdReference.child(key_of_matched_id).removeValue();
                                    createNewUser(key_of_matched_id);
//                            Toast.makeText(SignUpActivity.this, "Matched = " + matched, Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception ex) {
                                    Toast.makeText(SignUpActivity.this, "Retrying!!!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "Just a sec", Toast.LENGTH_SHORT).show();
                            }

                        }
                        if(!flag)
                        {
                            Toast.makeText(SignUpActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ne) {
                        Toast.makeText(SignUpActivity.this, "Exception ->" + ne, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SignUpActivity.this, "Some problem occurred!!!", Toast.LENGTH_SHORT).show();
                }
            });}
        catch (Exception e){
            Toast.makeText(this, "Please restart the app", Toast.LENGTH_SHORT).show();
        }

    }

    public void updateUI() {
        try {
            //Toast.makeText(this, "Updating your UI", Toast.LENGTH_SHORT).show();
            if (newUser != null) {
                if(type_of_account.equals("Admin")){
                    //Toast.makeText(this, "Loading Your UI Admin", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, AdminActivity.class));
                }
                else{
                    //Toast.makeText(this, "Loading Your UI " + type_of_account, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                }
            } else {
                Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Reopen your app!", Toast.LENGTH_SHORT).show();
        }
    }


    public void disableFields(boolean status) {
        Toast.makeText(this, "Fields have been disabled", Toast.LENGTH_SHORT).show();
        email_field.setEnabled(status);
        confirm_field.setEnabled(status);
        submit.setEnabled(status);
        pass_field.setEnabled(status);
        id_field.setEnabled(status);
        teacher_radio.setEnabled(status);
        student_radio.setEnabled(status);
        admin_radio.setEnabled(status);
    }

}

//We check while signing up that the user is legitimate user by checking a predefined user id that is
//in our database. While signing up the user is required to produce any preexisting and non occupied
// registered IDs to guarantee himself/herself as a legitimate user.
