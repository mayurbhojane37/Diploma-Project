package com.hfad.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserDataUploader extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Profile";
    private static final String TABLE_USERS = "Users";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DP = "UserDP";
    public String userName,enrollOfUser;
    public byte[] imageByte;
    Context ct;
    SQLiteDatabase db = this.getWritableDatabase();
    UserDataUploader(Context context)
    {

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        ct=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =  "CREATE TABLE " + TABLE_USERS + "(" + KEY_ID + " VARCHAR2, " + KEY_NAME + " TEXT, "+KEY_DP+" BLOB )";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
    public int addUserData(String name, Bitmap bmp,String enroll)
    {

        enrollOfUser = enroll;
        ByteArrayOutputStream imgByte = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100,imgByte);
        byte[] imgArray = imgByte.toByteArray();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Contact Name
        values.put(KEY_DP,imgArray);
            if(CheckIsDataAlreadyInDBorNot())
            {
                SQLiteDatabase db2 = this.getWritableDatabase();
                values.put(KEY_NAME, name); // Contact Name
                values.put(KEY_DP,imgArray);
                int i = (int)db2.update(TABLE_USERS,values,KEY_ID+" = "+enrollOfUser,null);
                db2.close();
                return i;
            }
            else
            {   SQLiteDatabase db2 = this.getWritableDatabase();
                values.put(KEY_ID,enrollOfUser);
                values.put(KEY_NAME, name); // Contact Name
                values.put(KEY_DP,imgArray);
                int i = (int)db2.insert(TABLE_USERS,null,values);
                db2.close();
                return i;
            }


    }
//    int deleteUserData(int Key_id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int res = db.delete(TABLE_USERS, KEY_ID + " = ?",
//                new String[] { String.valueOf(Key_id) });
//        db.close();
//        return res;
//
//    }
    public int getUserInfo(String key_id)
    {

        enrollOfUser=key_id;


        if(CheckIsDataAlreadyInDBorNot())
        { String sqlSelect = "SELECT * FROM "+TABLE_USERS+" WHERE "+KEY_ID+" = "+key_id;
            SQLiteDatabase db2 = this.getReadableDatabase();
            Cursor cr = db.rawQuery(sqlSelect,new String[]{});
            if(cr.moveToFirst())
            {
                int userId = cr.getInt(0);
                userName = cr.getString(1);
                imageByte = cr.getBlob(2);
            }
            if(!cr.isClosed())
            {
                cr.close();
            }

            return 1;
        }

return 2;
    }

        public boolean CheckIsDataAlreadyInDBorNot() {

            String selectString = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_ID + " =?";

            // Add the String you are searching by here.
            // Put it in an array to avoid an unrecognized token error
            Cursor cursor = db.rawQuery(selectString, new String[] {enrollOfUser});

            boolean hasObject = false;
            if(cursor.moveToFirst()){
                hasObject = true;
            }

            cursor.close();

            return hasObject;
        }




}
