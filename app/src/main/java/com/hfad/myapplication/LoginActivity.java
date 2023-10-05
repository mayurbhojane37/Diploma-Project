package com.hfad.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText email_field_login, pass;
    String email, password;
    private FirebaseAuth mAuth;
    Button login;
    TextView nau;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_field_login = findViewById(R.id.email_field_login);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();
        //------------------------------------------------------------------------------------
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
//--------------------------------------------------------------------------------------------
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        nau = findViewById(R.id.notUser);

        nau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });
    }

    private void signIn() {
        email = email_field_login.getText().toString();
        password = pass.getText().toString();
        if (!validateForm(email, password)) {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Sign in has been done !!!     :)
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        //Sign in is not successful:<
                        Toast.makeText(LoginActivity.this, "Sign up not successful", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }

    private boolean validateForm(String email, String passkey) {
        if (email.length() == 0) return false;
        return passkey.length() != 0;
    }

    private void updateUI(FirebaseUser user) {
        try {
            if (user != null) {
                if (!Objects.requireNonNull(user.getDisplayName()).substring(0,5).equals("Admin"))   {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    this.finish();
                }
                else{
                    startActivity(new Intent(LoginActivity.this,AdminActivity.class));
                   this.finish();
                }
            }
        }catch (Exception e)
        {

            //Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }


    }

    public void sendPasswordReset(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email_field_login.getText().toString().trim();
        if(!emailAddress.equals(""))
        {
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Password Reset Link has been sent to your email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else Toast.makeText(this, "Please enter an email Address first", Toast.LENGTH_SHORT).show();

    }

    public void goToSignUp()
    {
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
    }

}
