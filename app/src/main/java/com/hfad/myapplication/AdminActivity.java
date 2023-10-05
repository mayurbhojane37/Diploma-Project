package com.hfad.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    public void addUserId(View view){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddUserFragment()).commit();
    }

    public void clearDatabase(View view){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DeleteFragment()).commit();
    }


    public void handleProfile(View view){
        //Open Profile Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
    }
}

