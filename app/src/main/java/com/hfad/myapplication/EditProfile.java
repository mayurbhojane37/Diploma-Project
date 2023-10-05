package com.hfad.myapplication;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;

import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfile extends AppCompatActivity {
    EditText teacherName;
    String teacher, enrollment;
    ImageView userDp;
    Uri uri;
    Bitmap bitmap;
    DatabaseReference dbf;
    StorageReference srf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);//change
        Intent intent = getIntent();
        enrollment = intent.getStringExtra("Enrollment");
        teacherName = findViewById(R.id.editText);
        userDp = findViewById(R.id.view);


        dbf= FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("profile");
        srf= FirebaseStorage.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("profile").child("ProfilePhoto.jpg");
    }
    public void uploadData(View view)
    {
        //TO-DO
        try {
            Toast.makeText(this, "Uploading profilePic", Toast.LENGTH_SHORT).show();
            srf.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();

                    while (!uri.isComplete());

                    Uri url= uri.getResult();
                    if(!teacherName.getText().toString().equals(""))
                    {
                        uploadForProfilePic dp = new uploadForProfilePic(url.toString(),teacherName.getText().toString());
                        dbf.setValue(dp);
                        Toast.makeText(EditProfile.this, "Profile picture uploaded", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(EditProfile.this, "Oops!Give Your Name as well", Toast.LENGTH_SHORT).show();



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, ""+e, Toast.LENGTH_SHORT).show();

                }
            });


        }catch (Exception e)
        {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }

    }


    public  void selectUserDp(View view)
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
        {
            Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},4);
        }
        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, 4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(resultCode==RESULT_OK &&data!=null&&data.getData()!=null)
            {
                uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                Toast.makeText(this, ""+bitmap, Toast.LENGTH_SHORT).show();
                userDp.setImageBitmap(bitmap);
                userDp.setScaleType(ImageView.ScaleType.FIT_XY);
            }else
                Toast.makeText(this, "not able to choose", Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }

    }


}




