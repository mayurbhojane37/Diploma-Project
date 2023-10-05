package com.hfad.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class ActivityForSyllabus extends AppCompatActivity {
    Spinner sp;
    ArrayAdapter adp;
    String[] subs;
    String selectedSubject,enrollment;
    ImageView subSyllabus;
    TextView addSubs;
    syllabusUploader sup;
    View java,c,cpp,js;
    View bvJava,bvC,bvCpp,bvJs;  //bv stands for background view
    Bitmap bitmap;
    Uri uri;
    byte []imageInBytes;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference dbf;
    StorageReference srf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_syllabus);
        //-----------------------     Accessing back views    -------------------------
        bvJava = findViewById(R.id.view9);
        bvC = findViewById(R.id.view3);
        bvCpp = findViewById(R.id.view7);
        bvJs = findViewById(R.id.view8);

        mAuth= FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(this, "A user " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            enrollment = Objects.requireNonNull(user.getDisplayName()).substring(9);

        }
        //-----------------------------------------------------------------------------


        java = findViewById(R.id.java);
        java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJavaSyllabus();
            }
        });

        c = findViewById(R.id.c);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCSyllabus();
            }
        });

        cpp = findViewById(R.id.cpp);
        cpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCppSyllabus();
            }
        });

        js = findViewById(R.id.js);
        js.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJavascriptSyllabus();
            }
        });

        subSyllabus = findViewById(R.id.syllabusDisplay);
        sp = findViewById(R.id.subjects);
        addSubs = findViewById(R.id.addSubs);
        sup = new syllabusUploader(ActivityForSyllabus.this);


        sp.setDropDownVerticalOffset(-15);
        subs= new String[]{"Select Subject","Java","C","Cpp","JavaScript"};
        adp = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,subs);
        sp.setAdapter(adp);

    }
    public void addNewSubjectSyllabus(View view)
    {
        sp.setVisibility(View.VISIBLE);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!parent.getItemAtPosition(position).toString().equals("Select Subject"))
                {
                    selectedSubject = parent.getItemAtPosition(position).toString();
                    Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    galleryintent.setType("image/*");
                    startActivityForResult(galleryintent, 4);
                }
                else Toast.makeText(ActivityForSyllabus.this, "Please Select the subject", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 4 &&resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            try {

                uri = data.getData();
                dbf = FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("syllabus").child(selectedSubject.toLowerCase());
                srf = FirebaseStorage.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("syllabus").child(selectedSubject+".jpg");
                srf.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uri.isComplete());

                        Uri url= uri.getResult();
                        uploadForProfilePic dp = new uploadForProfilePic(url.toString(),selectedSubject.toLowerCase());
                        dbf.setValue(dp);
                        Toast.makeText(ActivityForSyllabus.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
//                Toast.makeText(ActivityForSyllabus.this, ""+bitmap, Toast.LENGTH_SHORT).show();
//                int i = sup.addUserSyllabus(selectedSubject,bitmap);
//                subSyllabus.setImageBitmap(bitmap);
//                subSyllabus.setScaleType(ImageView.ScaleType.FIT_XY);

            }catch (Exception e)
            {
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }


        }
    }
    public void getJavaSyllabus()
    {

        bvJava.setBackground(getDrawable(R.drawable.add));
        bvC.setBackground(getDrawable(R.drawable.subjects));
        bvCpp.setBackground(getDrawable(R.drawable.subjects));
        bvJs.setBackground(getDrawable(R.drawable.subjects));

        subSyllabus.setImageBitmap(null);
        Toast.makeText(this, "Java", Toast.LENGTH_SHORT).show();
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("syllabus").child("java").child("dpImage");
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    Glide.with(ActivityForSyllabus.this).load(dataSnapshot.getValue()).into(subSyllabus);
                    subSyllabus.setScaleType(ImageView.ScaleType.FIT_XY);

                }

                else Toast.makeText(ActivityForSyllabus.this, "Please upload your profile picture! ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void getCSyllabus()
    {
        bvJava.setBackground(getDrawable(R.drawable.subjects));
        bvC.setBackground(getDrawable(R.drawable.add));
        bvCpp.setBackground(getDrawable(R.drawable.subjects));
        bvJs.setBackground(getDrawable(R.drawable.subjects));

        subSyllabus.setImageBitmap(null);
        Toast.makeText(this, "C", Toast.LENGTH_SHORT).show();
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("syllabus").child("c").child("dpImage");
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    Glide.with(ActivityForSyllabus.this).load(dataSnapshot.getValue()).into(subSyllabus);
                    subSyllabus.setScaleType(ImageView.ScaleType.FIT_XY);

                }

                else Toast.makeText(ActivityForSyllabus.this, "Please upload your profile picture! ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getCppSyllabus()
    {

        bvJava.setBackground(getDrawable(R.drawable.subjects));
        bvC.setBackground(getDrawable(R.drawable.subjects));
        bvCpp.setBackground(getDrawable(R.drawable.add));
        bvJs.setBackground(getDrawable(R.drawable.subjects));

        subSyllabus.setImageBitmap(null);
        Toast.makeText(this, "Cpp", Toast.LENGTH_SHORT).show();
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("syllabus").child("cpp").child("dpImage");
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    Glide.with(ActivityForSyllabus.this).load(dataSnapshot.getValue()).into(subSyllabus);
                    subSyllabus.setScaleType(ImageView.ScaleType.FIT_XY);

                }

                else Toast.makeText(ActivityForSyllabus.this, "Please upload your profile picture! ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void getJavascriptSyllabus()
    {
        bvJava.setBackground(getDrawable(R.drawable.subjects));
        bvC.setBackground(getDrawable(R.drawable.subjects));
        bvCpp.setBackground(getDrawable(R.drawable.subjects));
        bvJs.setBackground(getDrawable(R.drawable.add));

        subSyllabus.setImageBitmap(null);
        Toast.makeText(this, "JavaScript", Toast.LENGTH_SHORT).show();
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("syllabus").child("javascript").child("dpImage");
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    Glide.with(ActivityForSyllabus.this).load(dataSnapshot.getValue()).into(subSyllabus);
                    subSyllabus.setScaleType(ImageView.ScaleType.FIT_XY);

                }

                else Toast.makeText(ActivityForSyllabus.this, "Please upload your profile picture! ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}