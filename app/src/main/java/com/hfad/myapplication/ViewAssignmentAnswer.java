package com.hfad.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ViewAssignmentAnswer extends AppCompatActivity {

    String enrollSelected, sub, div;
    ListView listView;
    DatabaseReference dbf;
    String[] uploadsStr;
    List<Upload> uploadedPdfs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_assignment_answer);

            Intent i = getIntent();
            enrollSelected = i.getStringExtra("enroll");
            sub = i.getStringExtra("subject");
            div = i.getStringExtra("division");

            Toast.makeText(this, enrollSelected, Toast.LENGTH_SHORT).show();
            listView = findViewById(R.id.list_view_item_answers);

            Toast.makeText(this, sub + " " + div, Toast.LENGTH_SHORT).show();
            uploadedPdfs = new ArrayList<>();
            dbf = FirebaseDatabase.getInstance().getReference("Student/" + div + "/" + sub + "/" + enrollSelected);
            viewEnrollAssignment();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(ViewAssignmentAnswer.this,ViewOrRateAssignment.class);
                    intent.putExtra("div",div);
                    intent.putExtra("sub",sub);
                    intent.putExtra("enroll",enrollSelected);
                    intent.putExtra("assSelected",uploadsStr[position]);
                    startActivity(intent);

                }
            });
        } catch (Exception e) {
            Toast.makeText(ViewAssignmentAnswer.this, "" + e, Toast.LENGTH_LONG).show();
        }

    }

    private void viewEnrollAssignment() {
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
                        myText.setTextSize(20);
                        myText.setPadding(0,10,0,10);
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
