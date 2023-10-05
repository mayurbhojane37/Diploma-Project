package com.hfad.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ActivityForGettingGrades extends Activity {
    String en;
    Spinner sp1,sp2,sp3;
    String[] subList;
    String[] divlist;
    String[] ass;
    String vishay,tukdi,assSelected;
    ArrayAdapter adp;
    DatabaseReference db;
    TextView obtMarks,comments;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_for_grades_and_comments);
        Intent intent = getIntent();
        en = intent.getStringExtra("Enrollment");
        sp1 = findViewById(R.id.division);
        sp2 = findViewById(R.id.subject);
        sp3 = findViewById(R.id.ass);
        obtMarks = findViewById(R.id.obtainedMarks);
        comments = findViewById(R.id.commentsgiven);
        subList=new String[]{"Select Subject","Java","Java II","JavaScript","PHP","Android","Microprocessor programm","Webdesign","Data Structures","Computer Security"};
        divlist=new String[]{"Select Division","G1","G2","G3","H1","H2","H3","N1","N2","N3"};

        ArrayAdapter<CharSequence> adp = ArrayAdapter.createFromResource(this, R.array.division, android.R.layout.simple_spinner_item);
        sp1.setAdapter(adp);


        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tukdi = parent.getItemAtPosition(position).toString();
                if(tukdi.equals("Select Division"))
                {
                    Toast.makeText(ActivityForGettingGrades.this, "Select Division first!", Toast.LENGTH_SHORT).show();
                    sp2.setEnabled(false);
                }
                else if(tukdi.compareTo("G3")==0||tukdi.compareTo("H3")==0||tukdi.compareTo("N3")==0)
                {
                    ArrayAdapter<CharSequence> adp = ArrayAdapter.createFromResource(getApplicationContext(), R.array.G3, android.R.layout.simple_spinner_item);
                    sp2.setAdapter(adp);
                    sp2.setEnabled(true);
                }
                else if(tukdi.compareTo("G2")==0||tukdi.compareTo("H2")==0||tukdi.compareTo("N2")==0)
                {
                    ArrayAdapter<CharSequence> adp = ArrayAdapter.createFromResource(getApplicationContext(), R.array.G2, android.R.layout.simple_spinner_item);
                    sp2.setAdapter(adp);
                    sp2.setEnabled(true);
                }
                else if(tukdi.compareTo("G1")==0||tukdi.compareTo("H1")==0||tukdi.compareTo("N1")==0)
                {
                    ArrayAdapter<CharSequence> adp = ArrayAdapter.createFromResource(getApplicationContext(), R.array.G1, android.R.layout.simple_spinner_item);
                    sp2.setAdapter(adp);
                    sp2.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vishay = parent.getItemAtPosition(position).toString();

                db = FirebaseDatabase.getInstance().getReference("Student/"+tukdi+"/"+vishay+"/"+en);
                Toast.makeText(ActivityForGettingGrades.this, tukdi+" "+vishay+" "+en, Toast.LENGTH_SHORT).show();
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ass = new String[(int)dataSnapshot.getChildrenCount()];
                        int index = 0;
                        for(DataSnapshot dsp : dataSnapshot.getChildren())
                        {
                            ass[index++] = dsp.getKey();
                        }
                        ArrayAdapter adp = new ArrayAdapter<>(ActivityForGettingGrades.this, android.R.layout.simple_spinner_dropdown_item, ass);
                        sp3.setAdapter(adp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    public void fetchGC(View v)
    {
        try {
            if(tukdi.length() == 0) Toast.makeText(this, "Division not found", Toast.LENGTH_SHORT).show();
            else if (vishay.length() == 0)Toast.makeText(this,"Subject not Found",Toast.LENGTH_SHORT).show();
            else if (en.length() == 0) Toast.makeText(this, "Teacher has not given your assignment any grades", Toast.LENGTH_SHORT).show();
            else if (assSelected.length() == 0) Toast.makeText(this, "Assignment not selected", Toast.LENGTH_SHORT).show();
            else {
                db = FirebaseDatabase.getInstance().getReference("Student").child(tukdi).child(vishay).child(en).child(assSelected).child("comments");
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null)
                        comments.setText(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                        else
                            Toast.makeText(ActivityForGettingGrades.this, "Teacher has not commented.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Database Exception "+e, Toast.LENGTH_SHORT).show();
        }

        try {
            if(tukdi.length() == 0) Toast.makeText(this, "Division not found", Toast.LENGTH_SHORT).show();
            else if (vishay.length() == 0)Toast.makeText(this,"Subject not Found",Toast.LENGTH_SHORT).show();
            else if (en.length() == 0) Toast.makeText(this, "Teacher has not given your assignment any grades", Toast.LENGTH_SHORT).show();
            else if (assSelected.length() == 0) Toast.makeText(this, "Assignment not selected", Toast.LENGTH_SHORT).show();
            else {
                db = FirebaseDatabase.getInstance().getReference("Student").child(tukdi).child(vishay).child(en).child(assSelected).child("marks");
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null)
                        obtMarks.setText(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                        Toast.makeText(ActivityForGettingGrades.this, "Teacher has not given marks", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Database Exception "+e, Toast.LENGTH_SHORT).show();
        }

    }
}
