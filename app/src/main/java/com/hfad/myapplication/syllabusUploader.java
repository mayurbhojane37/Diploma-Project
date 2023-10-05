package com.hfad.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class syllabusUploader extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Profile";
    private static final String TABLE_USERS = "Usersyllabus";
    private static final String KEY_ID = "id";
    private static final String COL1 = "Java";
    private static final String COL2 = "C";
    private static final String COL3 = "Cpp";
    private static final String COL4 = "Csharp";
    private static final String COL5 = "JavaScript";
    String userName;
    int userId;
   public byte[] imageByte;
    private Context ct;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String enrollment,Query;

    syllabusUploader(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        ct = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =  "CREATE TABLE   Usersyllabus (  id   INTEGER PRIMARY KEY,  Java   BLOB, C BLOB , Cpp BLOB , Csharp BLOB, JavaScript BLOB )";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
    public int addUserSyllabus(String SubjectName, Bitmap bmp)
    {
        SQLiteDatabase db = this.getWritableDatabase();


        ByteArrayOutputStream imgByte = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100,imgByte);
        byte[] imgArray = imgByte.toByteArray();

        if(CheckIsDataAlreadyInDBorNot())
        {
            ContentValues values = new ContentValues();
            values.put(KEY_ID,enrollment);
            values.put(SubjectName, imgArray);
            int i = (int)db.update(TABLE_USERS,values,KEY_ID+" = "+enrollment,null);
            db.close(); // Closing database connection
            return i;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(KEY_ID,enrollment);
            values.put(SubjectName, imgArray);
            int i = (int)db.insert(TABLE_USERS,null,values);
            db.close(); // Closing database connection
            return i;
        }


    }
    public int deleteUserData(int Key_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete(TABLE_USERS, KEY_ID + " = ?",
                new String[] { String.valueOf(Key_id) });
        db.close();
        return res;

    }
    public void getUserSyllabus(String subject)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlSelect = "SELECT "+subject+"  FROM "+TABLE_USERS+" WHERE "+KEY_ID+" = "+enrollment;
        Cursor cr = db.rawQuery(sqlSelect,new String[]{});
        if(cr.moveToFirst())
        {
            imageByte = cr.getBlob(0);
        }
        if(!cr.isClosed())
        {
            cr.close();
        }

    }

    public boolean CheckIsDataAlreadyInDBorNot() {

        boolean flag=false;
        if (user != null) {
            enrollment = Objects.requireNonNull(user.getDisplayName()).substring(9);
            SQLiteDatabase sqldb = this.getReadableDatabase();

            Query = "Select * from " + TABLE_USERS + " where " + KEY_ID + " = " +enrollment;
            Cursor cursor = sqldb.rawQuery(Query, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                flag=false;
            }
            else
            {
                cursor.close();
                flag = true;
            }


        }
        return flag;

    }
}
