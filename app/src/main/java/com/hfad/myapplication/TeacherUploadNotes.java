package com.hfad.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

/*import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;*/

import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class TeacherUploadNotes extends Activity {
    Spinner  subject, TeacherName;
    TextView Title;
    EditText newTeacher;
    DatabaseReference dbImage;
    StorageReference storeImage;
    ProgressDialog progressDialog = null;
    Uri uri;
    ArrayAdapter<String> adapter1, adapter2;
    final int SELECT_PDF = 1;
    String[] Teacher;
    String[] subs;
//    String[] divs;
    String[] starting;
    String teacherSelected = "", selectSub;
    long notesCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_teacher_upload_notes);
            
            Intent old_intent = getIntent();
            String typeDeAccount = old_intent.getStringExtra("AccountType");
            assert typeDeAccount != null;
            updateTheUI(typeDeAccount);
            
            subject = findViewById(R.id.spinner1);
            TeacherName = findViewById(R.id.spinner);
            newTeacher = findViewById(R.id.editText3);
            Title = findViewById(R.id.textView);
            //divs = new String[]{"G1","H1","N1","G2","H2","N2","G3","H3","N3"};
            starting = new String[]{"select subject first"};
            subs = new String[]{"Select Subject", "Java", "Java II", "JavaScript", "PHP", "Android", "Microprocessor programm", "Webdesign", "Data Structures", "Computer Security"};
            adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subs);
            subject.setAdapter(adapter1);
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
//            TextPaint paint = Title.getPaint();
//            float width = paint.measureText(Title.getText().toString());
//            Shader textShader = new LinearGradient(0, 0, width, Title.getTextSize(), new int[]
//                    {
//                            Color.BLACK,
//                            Color.rgb(80, 27, 228),
//                            Color.rgb(131, 94, 236)
//                    }, null, Shader.TileMode.CLAMP);
//
//            Title.getPaint().setShader(textShader);
            subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectSub = parent.getItemAtPosition(position).toString();
                    Toast.makeText(TeacherUploadNotes.this, selectSub, Toast.LENGTH_SHORT).show();

                    //Now here we try to say that if selectedSubject is anything but the text "Select Subject" which means some sub is selected.
                    if (!selectSub.equals("Select Subject")) {
                    //We make sure that the DBImage now points to Teacher/WhateverSubjectThatIsSelected.
                        //We also add a listener to it to make sure we record a change in the database.
                        dbImage = FirebaseDatabase.getInstance().getReference("Teacher/" + selectSub);
                        dbImage.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int i = 0;
                                Teacher = new String[(int) dataSnapshot.getChildrenCount() + 1];
                                Teacher[i++] = "Select Teacher";
                                //Above two lines of code retrieve the names of Teacher that have created Notes earlier.
                                //In the first of them what we do is we create an array called Teachers which will have teachers names.
                                //Note now it is empty, we only have defined the array yet and not initialized it.
                                //The size of array is the children count at the data snapshot : Teachers/Subject.
                                //Then in the second line, we insert the String "Select Teacher" in Teacher array for the sake of Spinner's appearance.
                                //So this is how Teachers[] looks like Teachers[] = {" ","Select Teacher","","","",""};

                                Toast.makeText(TeacherUploadNotes.this, "" + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                                for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                                    Teacher[i++] = dsp.getKey();
                                }

                                //In the above lines only we have filled the names of Teachers in the String array.
                                //Now the array looks like : Teachers[] = {"","Select Teacher","TeacherName1","TeacherName2",..."TeacherNameN"}.
                                adapter2 = new ArrayAdapter<>(TeacherUploadNotes.this, android.R.layout.simple_spinner_dropdown_item, Teacher);
                                TeacherName.setAdapter(adapter2);
                                //Here we simply create an adapter and populate our spinner, explanation of the above two lines is beyond the scope of this document.
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

            //The below code is for handling the spinner. We get the element of a spinner that is selected and convert it to a string for later use.
            TeacherName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    teacherSelected = parent.getItemAtPosition(position).toString();
                    Toast.makeText(TeacherUploadNotes.this, teacherSelected, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
        }


    }

    public void showMoreOptions(View view) {    //teacherSelected=null;
        //We change the color of link just for an artistic effect.
        findViewById(R.id.textView3).setBackgroundResource(R.color.ana_blue);
        TeacherName.setVisibility(View.INVISIBLE);
    //If the name of the teacher does not appear in spinner then we ask the user to enter the name of the teacher himself, which is disgusting.
        newTeacher.setVisibility(View.VISIBLE);
    }

    public void uploadToDb(View view) {
        try {
            if (teacherSelected.equals("Select Teacher")) {
                newTeacher.setVisibility(View.VISIBLE);
                teacherSelected = newTeacher.getText().toString().trim();
                //This code works when there is no element in the spinner to select and the default selection is the text "Select Teacher".
                //What we now do is that we get the teacher name from asking the user to enter the name into an EditText.

            }

            Intent pickImage = new Intent(Intent.ACTION_GET_CONTENT);
            pickImage.setType("application/pdf");
            startActivityForResult(pickImage, SELECT_PDF);
        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }


        //Toast.makeText(MainActivity.this, teacherSelected, Toast.LENGTH_SHORT).show();

    }

    public void getFromDb(View view) {
        if (teacherSelected.equals("Select Teacher")) {
            newTeacher.setVisibility(View.VISIBLE);
            teacherSelected = newTeacher.getText().toString().trim();
            //This code works when there is no element in the spinner to select and the default selection is the text "Select Teacher".
            //What we now do is that we get the teacher name from asking the user to enter the name into an EditText.
            goToListOfNotes(teacherSelected);
        }
        else{
            teacherSelected = teacherSelected.trim();
            goToListOfNotes(teacherSelected);
        }


    }

    private void goToListOfNotes(String teacher){
        Intent showListOfNotes = new Intent(this, ListOfNotes.class);
        showListOfNotes.putExtra("TeacherName", teacher);
        showListOfNotes.putExtra("Subject", selectSub);
        startActivity(showListOfNotes);
        //Here we pass the name of the teacher and subject to List of Notes activity which simply show the existing notes available.
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PDF && data != null && data.getData() != null) {
        //Here we are uploading the PDF to the Firebase Database.
            try {
                uri = data.getData();
                progressDialog.show();
                Toast.makeText(this, teacherSelected, Toast.LENGTH_SHORT).show();
                dbImage = FirebaseDatabase.getInstance().getReference("Teacher").child(selectSub).child(teacherSelected).child("Notes");
                storeImage = FirebaseStorage.getInstance().getReference("Teacher").child(selectSub).child(teacherSelected).child("Notes");
                dbImage.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        notesCount = dataSnapshot.getChildrenCount() + 1;
                        Toast.makeText(TeacherUploadNotes.this, "" + notesCount, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                StorageReference str = storeImage.child("Note " + notesCount + ".pdf");
                str.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(TeacherUploadNotes.this, "File uploaded", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uri.isComplete()) ;
                                Uri url = uri.getResult();
                                assert url != null;
                                Upload upload = new Upload(String.valueOf(notesCount), url.toString());
                                dbImage.child("Notes " + notesCount).setValue(upload);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeacherUploadNotes.this, "File not uploaded because " + e, Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setProgress(1);
                        progressDialog.setMessage("Uploaded..... " + (int) progress + "%");
                    }
                });
                TeacherName.setVisibility(View.VISIBLE);
                newTeacher.setText("");
                newTeacher.setVisibility(View.INVISIBLE);


            } catch (Exception e) {
                Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
            }

        }
    }


    
    private void updateTheUI(String userType){
        if(userType.equals("Students")){
            findViewById(R.id.upload_button_notes).setVisibility(View.GONE);
        }
        else if (!userType.equals("Teachers")){
            Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TeacherUploadNotes.this,MainActivity.class));
        }
}

}

