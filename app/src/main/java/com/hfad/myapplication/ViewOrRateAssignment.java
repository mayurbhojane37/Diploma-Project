package com.hfad.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ViewOrRateAssignment extends Activity {
    ImageView view,rate;
    String div,sub,enroll,ass,pdfurl;
    DatabaseReference dbf;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_or_give_grades);
        Intent i = getIntent();
        div = i.getStringExtra("div");
        sub = i.getStringExtra("sub");
        enroll = i.getStringExtra("enroll");
        ass = i.getStringExtra("assSelected");

        dbf = FirebaseDatabase.getInstance().getReference("Student/"+div+"/"+sub+"/"+enroll+"/"+ass+"/document");

        view = findViewById(R.id.seeAss);
        rate = findViewById(R.id.rateView);
    }
    public void viewAss(View v)
    {
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pdfurl= Objects.requireNonNull(dataSnapshot.getValue()).toString();

                Intent openPdfFile= new Intent();
                openPdfFile.setDataAndType(Uri.parse(pdfurl),Intent.ACTION_VIEW);


                startActivity(openPdfFile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void rateAss(View v)
    {
        Intent intent = new Intent(this,ActivityForRating.class);
        intent.putExtra("dbRef","Student/"+div+"/"+sub+"/"+enroll+"/"+ass+"");
        startActivity(intent);
    }
}