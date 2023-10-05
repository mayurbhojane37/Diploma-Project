package com.hfad.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.util.Objects;

public class UploadAssignmentQuestion extends Fragment {
    private Spinner TeacherName;
    private EditText newTeacher;
    private DatabaseReference dbImage;
    private ProgressDialog progressDialog=null;
    private ArrayAdapter<String> adapter2;
    private final int SELECT_PDF=1;
    private String[] Teacher;
    private String teacherSelected="",selectSub;
    private long assCount=0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_assignment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            super.onViewCreated(view, savedInstanceState);
            Spinner subject = Objects.requireNonNull(getView()).findViewById(R.id.spinner1);
            TeacherName=getView().findViewById(R.id.spinner);
            newTeacher= getView().findViewById(R.id.editText3);
            TextView title = getView().findViewById(R.id.textView);
            Button upload = getView().findViewById(R.id.uploadbutton);
            Button fetch = getView().findViewById(R.id.downloadbutton);
            TextView tv = getView().findViewById(R.id.more);

            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadToDb();
                }
            });

            fetch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFromDb();
                }
            });

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreOptions();
                }
            });

            String[] subs = new String[]{"Select Subject", "Java", "Java II", "JavaScript", "PHP", "Android", "Microprocessor programm", "Webdesign", "Data Structures", "Computer Security"};
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, subs);
            subject.setAdapter(adapter1);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            TextPaint paint = title.getPaint();
            float width= paint.measureText(title.getText().toString());
            Shader textShader = new LinearGradient(0,0,width, title.getTextSize(),new int[]
                    {
                            Color.BLACK,
                            Color.rgb(80,27,228),
                            Color.rgb(131,94,236)
                    },null, Shader.TileMode.CLAMP);

            title.getPaint().setShader(textShader);
            subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectSub=parent.getItemAtPosition(position).toString();
                    Toast.makeText(getActivity(),selectSub, Toast.LENGTH_SHORT).show();
                    if(!selectSub.equals("Select Subject"))
                    {
                        dbImage= FirebaseDatabase.getInstance().getReference("Teacher/"+selectSub);
                        dbImage.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int i=0;
                                Teacher=new String[(int)dataSnapshot.getChildrenCount()+1];
                                Teacher[i++]="Select Teacher";
                                Toast.makeText(getActivity(), ""+dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                                for(DataSnapshot dsp : dataSnapshot.getChildren())
                                {

                                    Teacher[i++]=dsp.getKey();
                                }
                                adapter2 = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, Teacher);
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
            Toast.makeText(getActivity(), teacherSelected, Toast.LENGTH_SHORT).show();
            TeacherName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    teacherSelected=parent.getItemAtPosition(position).toString();
                    Toast.makeText(getActivity(), teacherSelected, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });





        }catch (Exception e)
        {
            Toast.makeText(getActivity(), ""+e, Toast.LENGTH_LONG).show();
        }
    }
    private void showMoreOptions()
    {    //teacherSelected=null;
        TeacherName.setVisibility(View.INVISIBLE);

        newTeacher.setVisibility(View.VISIBLE);
    }
    private void uploadToDb() {
        try {
            if (teacherSelected.equals("Select Teacher")) {
                newTeacher.setVisibility(View.VISIBLE);
                teacherSelected = newTeacher.getText().toString().trim();
            }

            Intent pickImage = new Intent(Intent.ACTION_GET_CONTENT);
            pickImage.setType("application/pdf");
            startActivityForResult(pickImage, SELECT_PDF);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
        }
    }
    private void getFromDb()
    {
        Intent showListOfNotes= new Intent(getActivity(),ListOfAssignments.class);
        showListOfNotes.putExtra("TeacherName",teacherSelected);
        showListOfNotes.putExtra("Subject",selectSub);
        startActivity(showListOfNotes);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_PDF&&data!=null&&data.getData()!=null)
        {


            try {
                Uri uri = data.getData();
                progressDialog.show();
                Toast.makeText(getActivity(), teacherSelected, Toast.LENGTH_SHORT).show();
                dbImage = FirebaseDatabase.getInstance().getReference("Teacher").child(selectSub).child(teacherSelected).child("Assignment");
                StorageReference storeImage = FirebaseStorage.getInstance().getReference("Teacher").child(selectSub).child(teacherSelected).child("Assignment");
                dbImage.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        assCount=dataSnapshot.getChildrenCount()+1;
                        Toast.makeText(getActivity(), ""+assCount, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                StorageReference str= storeImage.child("Assignment Question"+assCount+".pdf");
                str.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getActivity(), "File uploaded", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uri.isComplete());
                                Uri url= uri.getResult();
                                assert url != null;
                                Upload upload = new Upload(String.valueOf(assCount),url.toString());
                                dbImage.child("Assignment Question "+assCount).setValue(upload);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "File not uploaded because "+e, Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        double progress= (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setProgress(1);
                        progressDialog.setMessage("Uploaded..... "+(int)progress+"%");
                    }
                });
                TeacherName.setVisibility(View.VISIBLE);
                newTeacher.setText("");
                newTeacher.setVisibility(View.INVISIBLE);




            }catch (Exception e)
            {
                Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
            }

        }


    }


}
