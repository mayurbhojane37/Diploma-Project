package com.hfad.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityForRating extends Activity {
    Spinner listOfmarks;
    ArrayAdapter adp;
    EditText comments;
    DatabaseReference dbMarks,dbComments;
    String path;
    String marksObtained;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rates_and_comments);
        listOfmarks = findViewById(R.id.marks);
        final String[] marks ={"Marks","1","1.5","2","2.5","3","3.5","4.0","4.5","5.0","5.5","6.0","6.5","7.0","7.5","8.0","8.5","9.0","9.5","10.0"};
        adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,marks);
        listOfmarks.setAdapter(adp);
        comments = findViewById(R.id.comments);
        Intent i = getIntent();
        path = i.getStringExtra("dbRef");
        listOfmarks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                marksObtained = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }
    public  void  uploadRemarks(View v)
    {
        dbMarks = FirebaseDatabase.getInstance().getReference(path).child("marks");
        if(!marksObtained.equals("Marks"))
        {
            dbMarks.setValue(marksObtained);
            dbComments = FirebaseDatabase.getInstance().getReference(path).child("comments");
            dbComments.setValue(comments.getText().toString());

            Toast.makeText(this, "Rates and Comments Submitted", Toast.LENGTH_SHORT).show();
        }

        else
            Toast.makeText(this, "Give proper marks!", Toast.LENGTH_SHORT).show();


    }
}