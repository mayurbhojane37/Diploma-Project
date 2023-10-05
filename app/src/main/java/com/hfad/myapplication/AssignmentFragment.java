package com.hfad.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;


public class AssignmentFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assignment_student, container, false);
    }

    private final int SELECT = 1;
    private Spinner spin1, spin2, spin_num;
    private EditText title_field;
    private String selectSub, division="G1";
    private String enroll;
    private String assNumber, assTitle;

    private Button uploadPdf;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button confirm_button;
        Button upload_selection_button,fetchAssQuestions,getGrades;
        {
            super.onViewCreated(view, savedInstanceState);
            spin1 = Objects.requireNonNull(getView()).findViewById(R.id.div);
            spin2 = getView().findViewById(R.id.selsub);
            uploadPdf = getView().findViewById(R.id.upload_button_asf);
            //------------------------------------------------------------
            //------------------------------------------------------------
            spin_num = getView().findViewById(R.id.Assnum);
            title_field = getView().findViewById(R.id.titleOfAss);
            confirm_button = getView().findViewById(R.id.confirm_button_student);
            upload_selection_button = getView().findViewById(R.id.upload_assignment_student);
            fetchAssQuestions = getView().findViewById(R.id.fetch_assignment_student_button);
            getGrades = getView().findViewById(R.id.getGrades);

        }//Getting all the widgets from the layout file.
        checkForUser();//Checking who the user is and fetching his/her enrollment number
        handleSpinners();//Function that initializes spinners and handles their events.

        getGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToGetGrades();
            }
        });
        uploadPdf.setVisibility(View.INVISIBLE);

        uploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToDb();
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDetailsToUser();
            }
        });

        upload_selection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectionDisplay(1);
            }
        });
        fetchAssQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectionDisplay(2);
            }
        });

    }

    private void setSelectionDisplay(int choice){
        if(choice == 1){
            Objects.requireNonNull(getView()).findViewById(R.id.help_layout).setVisibility(View.GONE);
            getView().findViewById(R.id.constraint_layout_fas).setVisibility(View.VISIBLE);

        }
        else if(choice == 2){
            Intent intent = new Intent(getContext(),AssignmentActivityFetchStudent.class);
            startActivity(intent);
        }
    }

    private void uploadToDb(){
        //validations are to be done here and after that open this intent
        Intent pickImage = new Intent(Intent.ACTION_GET_CONTENT);
        pickImage.setType("application/pdf");
        startActivityForResult(pickImage, SELECT);
        uploadPdf.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        StorageReference storeImage;
        Uri uri;

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT) {
            assert data != null;
            uri = data.getData();
            /*progressDialog.setProgress(1);
            progressDialog.show();*/

            final DatabaseReference dbImage = FirebaseDatabase.getInstance().getReference("Student").child(division).child(selectSub).child(enroll).child(assNumber);
            storeImage = FirebaseStorage.getInstance().getReference("Student").child(division).child(selectSub).child(enroll).child(assNumber);
            Toast.makeText(getContext(), "Uploading your File...", Toast.LENGTH_LONG).show();
            StorageReference str = storeImage.child(assNumber + ".pdf");
            assert uri != null;
            str.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "File Uploaded!!!", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uri.isComplete());
                            Uri url = uri.getResult();
                            assert url != null;
                            Upload upload = new Upload(assTitle, url.toString());
                            dbImage.setValue(upload);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "File not uploaded because " + e, Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
/*
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setProgress(2);
                    progressDialog.setMessage("Uploaded..... " + (int) progress + "%");*/
                }
            });
        }
    }

    private void confirmDetailsToUser() {
        assTitle = title_field.getText().toString();
        if (testDetails()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setMessage("Are these details valid:\n Enrollment Number : " + enroll + "\n Division : " + division + "\n Subject :" + selectSub+"\n Assignment Number :"+assNumber+"\n Assignment Title :"+assTitle);
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Toast.makeText(getContext(), "You clicked yes button", Toast.LENGTH_LONG).show();
                    uploadPdf.setVisibility(View.VISIBLE);
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Redo the process!", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else{
            Toast.makeText(getContext(), "Some problem occurred!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.division, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin1.setAdapter(adapter);
        spin1.setOnItemSelectedListener(this);



        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSub = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.Numbers,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_num.setAdapter(adapter2);
        spin_num.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assNumber = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void checkForUser() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            enroll = Objects.requireNonNull(user.getDisplayName()).substring(9);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        division = adapterView.getItemAtPosition(i).toString();
        if(division.compareTo("G3")==0||division.compareTo("N3")==0||division.compareTo("H3")==0)
        {

            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.G3, android.R.layout.simple_spinner_item);

            spin2.setAdapter(adapter1);
        }

        else if(division.compareTo("G2")==0||division.compareTo("N2")==0||division.compareTo("H2")==0)
        {
            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.G2, android.R.layout.simple_spinner_item);

            spin2.setAdapter(adapter1);
        }
        else if(division.compareTo("G1")==0||division.compareTo("N1")==0||division.compareTo("H1")==0)
        {
            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.G1, android.R.layout.simple_spinner_item);

            spin2.setAdapter(adapter1);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private boolean testDetails() {
        assTitle = title_field.getText().toString();
        if (TextUtils.isEmpty(division)) {
            Toast.makeText(getContext(), "Please select a division", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(selectSub)) {
            Toast.makeText(getContext(), "Please select a subject", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(enroll)) {
            Toast.makeText(getContext(), "Please Login again!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(assNumber)) {
            Toast.makeText(getContext(), "Please Enter the Assignment Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(assTitle)) {
            Toast.makeText(getContext(), "Please Enter the Title", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void jumpToGetGrades()
    {

        Toast.makeText(getActivity(), "get Grades Called", Toast.LENGTH_SHORT).show();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String enrollment = Objects.requireNonNull(user.getDisplayName()).substring(9);
            Intent intent = new Intent(getActivity(),ActivityForGettingGrades.class);
            intent.putExtra("Enrollment",enrollment);
            startActivity(intent);
        }



    }

}




