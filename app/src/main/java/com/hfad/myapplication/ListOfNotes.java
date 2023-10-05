package com.hfad.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ListOfNotes extends Activity {
    ListView listView;
    DatabaseReference dbf;
    String[] uploadsStr;
    String pdf_url;
    List<Upload> uploadedPdfs;
    String sub, teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_list_of_notes);
            Intent i = getIntent();
            listView = findViewById(R.id.NotesLists);//change
            sub = i.getStringExtra("Subject");
            teacher = i.getStringExtra("TeacherName");
            Toast.makeText(this, sub + " " + teacher, Toast.LENGTH_SHORT).show();
            uploadedPdfs = new ArrayList<>();
            dbf = FirebaseDatabase.getInstance().getReference("Teacher/" + sub + "/" + teacher + "/Notes");
            viewPdfFileName();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    DatabaseReference retriveChild = dbf.child(uploadsStr[position]).child("document");//change
                    retriveChild.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                pdf_url = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                Toast.makeText(ListOfNotes.this, pdf_url, Toast.LENGTH_SHORT).show();
                                Intent openPdfFile = new Intent();
                                openPdfFile.setDataAndType(Uri.parse(pdf_url), Intent.ACTION_VIEW);


                                startActivity(openPdfFile);
                            } catch (Exception e) {
                                //Toast.makeText(ListOfNotes.this, ""+e, Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
        }


    }

    private void viewPdfFileName() {
        dbf.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int Size = (int) dataSnapshot.getChildrenCount();
                int index = 0;
                uploadsStr = new String[Size];
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    uploadsStr[index++] = post.getKey();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploadsStr) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView myText = view.findViewById(android.R.id.text1);
                        myText.setTextColor(Color.BLACK);
                        return super.getView(position, convertView, parent);
                    }
                };
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
