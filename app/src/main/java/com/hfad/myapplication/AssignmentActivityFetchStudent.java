package com.hfad.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AssignmentActivityFetchStudent extends Activity {
    Spinner subject,TeacherName;
    TextView Title,NameNotThere;
    EditText newTeacher;
    DatabaseReference dbImage;
    ProgressDialog progressDialog=null;

    Button getQButton ;
    ArrayAdapter<String> adapter1,adapter2;
    String[] Teacher;
    String[] subs;
    String[] starting;
    String teacherSelected="",selectSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.act_view_ass_question);

            subject=findViewById(R.id.spinner1);
            TeacherName=findViewById(R.id.spinner);
            newTeacher= findViewById(R.id.editText3);
            NameNotThere = findViewById(R.id.name_not_found_TV);
            getQButton = findViewById(R.id.getQuestionButton);
            Title=findViewById(R.id.textView);
            starting =new String[]{"select subject first"};
            subs = new String[]{"Select Subject","Java","Java II","JavaScript","PHP","Android","Microprocessor programm","Webdesign","Data Structures","Computer Security"};
            adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,subs);
            subject.setAdapter(adapter1);
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            TextPaint paint = Title.getPaint();

            float width= paint.measureText(Title.getText().toString());
            Shader textShader = new LinearGradient(0,0,width,Title.getTextSize(),new int[]
                    {
                            Color.BLACK,
                            Color.rgb(80,27,228),
                            Color.rgb(131,94,236)
                    },null, Shader.TileMode.CLAMP);

            Title.getPaint().setShader(textShader);
            subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectSub=parent.getItemAtPosition(position).toString();
                    Toast.makeText(AssignmentActivityFetchStudent.this,selectSub, Toast.LENGTH_SHORT).show();
                    if(!selectSub.equals("Select Subject"))
                    {
                        dbImage=FirebaseDatabase.getInstance().getReference("Teacher").child(selectSub);
                        dbImage.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int i=0;
                                Teacher=new String[(int)dataSnapshot.getChildrenCount()+1];
                                Teacher[i++]="Select Teacher";
                                Toast.makeText(AssignmentActivityFetchStudent.this, ""+dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                                for(DataSnapshot dsp : dataSnapshot.getChildren())
                                {
                                    Teacher[i++]=dsp.getKey();
                                }
                                adapter2 = new ArrayAdapter<>(AssignmentActivityFetchStudent.this, android.R.layout.simple_spinner_dropdown_item,Teacher);
                                TeacherName.setAdapter(adapter2);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            Toast.makeText(this, teacherSelected, Toast.LENGTH_SHORT).show();
            TeacherName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    teacherSelected=parent.getItemAtPosition(position).toString();
                    Toast.makeText(AssignmentActivityFetchStudent.this, teacherSelected, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
        catch (Exception e)
        {
            Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show();
        }

        getQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFromDb();
            }
        });

        NameNotThere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions();
            }
        });

    }

    public void showMoreOptions()
    {    //teacherSelected=null;
        TeacherName.setVisibility(View.INVISIBLE);

        newTeacher.setVisibility(View.VISIBLE);
    }

    public void getFromDb()
    {
        Intent showListOfNotes= new Intent(this,ListOfAssignments.class);
        showListOfNotes.putExtra("TeacherName",teacherSelected);
        showListOfNotes.putExtra("Subject",selectSub);
        startActivity(showListOfNotes);
    }

}
