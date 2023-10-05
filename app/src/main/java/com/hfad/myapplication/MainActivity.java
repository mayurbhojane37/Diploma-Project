package com.hfad.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private String type_of_account,enrollment;
//    Button sign_up,login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);







        BottomNavigationView navBar = findViewById(R.id.navigation);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(this, "A user " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            type_of_account = Objects.requireNonNull(user.getDisplayName()).substring(0, 8);
            enrollment =  Objects.requireNonNull(user.getDisplayName()).substring(9);
            Toast.makeText(this, "The type of account is" + type_of_account, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No user found!!", Toast.LENGTH_SHORT).show();
            navBar.setEnabled(false);
        }

        //-------------------------------------------------------------------------------------

        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("profile").child("dpImage");
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    ImageView iv = findViewById(R.id.imageView);
                    Glide.with(getApplicationContext()).load(dataSnapshot.getValue()).into(iv);
                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                }

                //else Toast.makeText(getApplicationContext(), "User DP not and name not set", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dbf = FirebaseDatabase.getInstance().getReference("UserProfileSyllabus").child(enrollment).child("profile").child("name");
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                {
                    TextView tv = findViewById(R.id.usernameTv);
                    tv.setText(dataSnapshot.getValue().toString());

                }

                //else Toast.makeText(getApplicationContext(), "Please upload your profile Name! ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //-------------------------------------------------------------------------------------







//        login = findViewById(R.id.login_button);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //The above code simply sets the toolbar we have created as the action bar bcz we have
        //removed the action bar.

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //The toggle we just created is actually the three parallel line button that we use to open our navigation drawer.

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //This will take care of rotating the hamburger icon.

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
            navigationView.setCheckedItem(R.id.home);
        }
        //The above code checks if the activity is started is started for the first time. If yes then it sets the first option fragment as the display.


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (type_of_account.equals("Students")) {
                    switch (menuItem.getItemId()) {
                        case R.id.home:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
                            break;
                        case R.id.notes:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NotesStudentFragment()).commit();
                            break;
                        case R.id.ass:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AssignmentFragment()).commit();
                            break;
                        case R.id.account:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                            break;
                    }
                } else if (type_of_account.equals("Teachers")) {
                    switch (menuItem.getItemId()) {
                        case R.id.home:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
                            break;
                        case R.id.notes:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Create_Notes()).commit();
                            break;
                        case R.id.ass:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AssignmentFragmentTeacher()).commit();
                            break;
                        case R.id.account:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                            break;
                    }
                }
                return true;
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home_option:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;//One f**king break statement.

            case R.id.nav_faq:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OptionFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);//This closes the drawer once you click a menu item.
                break;

            case R.id.nav_rate_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Rateus_fragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_feedback:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Feedback_Fragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_about_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutUs_Fragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;


            case R.id.nav_terms:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotesFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_privacy_policy:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PrivacyPolicy()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_auth:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;

//            case R.id.nav_share:
//                Uri uri = Uri.parse("http://www.instagram.com/the_unorthodox70");
//                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//                startActivity(intent);
//                drawer.closeDrawer(GravityCompat.START);
//                break;
//
//            case R.id.nav_send:
//                uri = Uri.parse("http://m.facebook.com/profile.php?id=100010848547826&ref=content_filter");
//                intent = new Intent(Intent.ACTION_VIEW,uri);
//                startActivity(intent);
//                drawer.closeDrawer(GravityCompat.START);
//                break;

        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //In the above function we check what menu item is selected and if any one is selected then we load the respective fragment.
    //We implement a specific method from NavigationView interface for writing this method.

    //Now what if the drawer is open and the user presses the back button .
    //The app will go on to the previous screen making our original home page hidden and inaccessible.
    //To avoid that we simply override the onBackPressed() method.
    public void callAssign(View view) {

        if(type_of_account.equals("Students"))
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AssignmentFragment()).commit();
        else if(type_of_account.equals("Teachers"))
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UploadAssignmentQuestion()).commit();
    }
    public void callMcq(View view)
    {
        if(type_of_account.equals("Students"))
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StudentMcqFragment()).commit();
        }
        else if (type_of_account.equals("Teachers"))
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TeacherMcqFragment()).commit();
        }
    }

    public void callGetNotes(View view){
        goToTeacherUploadNotes();
    }

    private void goToTeacherUploadNotes(){
        Intent intent = new Intent(MainActivity.this, TeacherUploadNotes.class);
        intent.putExtra("AccountType",type_of_account);
        startActivity(intent);
    }

    public void callSyllabus(View v)
    {
        Intent syllIntent = new Intent(MainActivity.this,ActivityForSyllabus.class);
        startActivity(syllIntent);
    }
}

