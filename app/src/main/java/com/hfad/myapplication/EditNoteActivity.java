package com.hfad.myapplication;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditNoteActivity extends AppCompatActivity {
    private static final int STORAGE_CODE=1000;
    EditText e1;
    Button b1,open,upload_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_layout);

        Intent intent = getIntent();
        String rawNote = "rawNote";
        String msgText = intent.getStringExtra(rawNote);

        e1 = findViewById(R.id.editableNotesView);
        e1.setText(msgText);
        b1=findViewById(R.id.button);
        open=findViewById(R.id.button2);
        upload_button= findViewById(R.id.upload_note_button_teacher);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(ActivityCompat.checkSelfPermission(EditNoteActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions,STORAGE_CODE);
                       // ActivityCompat.requestPermissions(EditNoteActivity.this,,1);


                    }else {
                        savePDF();

                    }
                }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(e1.getText()==null){
                    Toast.makeText(EditNoteActivity.this, "Enter some value in edit text", Toast.LENGTH_SHORT).show();
                }
                else {
                    shareDoc();
                }
            }
        });

        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_note();
            }
        });
    }
    public void savePDF()
    {
        Document mDoc=new Document();
        String mFileName=new SimpleDateFormat("yyyymmdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath= Environment.getExternalStorageDirectory() + "/" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc , new FileOutputStream(mFilePath));
            mDoc.open();
            String mText= e1.getText().toString();
            mDoc.addAuthor("Mayur Bhojane");
            mDoc.add(new Paragraph(mText));
            mDoc.close();
            Toast.makeText(this, mFileName + ".pdf\nis saved to \n "+mFilePath , Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                savePDF();

            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void shareDoc(){
        Intent i=new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        String sharebody=e1.getText().toString();
        String sharesub="Your Subject Here";
        i.putExtra(Intent.EXTRA_SUBJECT,sharesub);
        i.putExtra(Intent.EXTRA_TEXT,sharebody);
        startActivity(Intent.createChooser(i,"Share Using"));
    }

    private void upload_note(){
        Intent intent = new Intent(this, TeacherUploadNotes.class);
        intent.putExtra("AccountType","Teachers");
        startActivity(intent);
    }
}
