package com.example.shailesh.dashboard;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shailesh.dashboard.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Provider;

/**
 * Created by Jarvis on 19-03-2018.
 */

public class MyProfile extends AppCompatActivity{

    ImageView imageView;
    TextView textName, textEmail;
    FirebaseAuth mAuth;
    int p;
    DatabaseReference mDatabase;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        sp = getSharedPreferences("MYPREF",0);
        p = sp.getInt("Provider",-1);

        Log.d ("MyProfile.this", "Provider: " +p);

        if (p==2) {
            retrieveInfoGoogle();
        }

        else if (p==1) {
            retrieveInfoFacebook();
        }

        else if(p==0)
            retrieveInfoEmail();

        else if(p==-1)
        {
            Toast.makeText(this, "Not Authenticated with any provider", Toast.LENGTH_SHORT).show();
        }
    }

    public void retrieveInfoGoogle ()
    {
        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.profile_photo);
        textName = findViewById(R.id.username);
        textEmail = findViewById(R.id.display_name);


        FirebaseUser user = mAuth.getCurrentUser();

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imageView);

        textName.setText(user.getDisplayName());
        textEmail.setText(user.getEmail());
    }

    public void retrieveInfoEmail ()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        textName = findViewById(R.id.username);
        textEmail = findViewById(R.id.display_name);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String string = dataSnapshot.getValue(String.class);

                Log.d ("MyProfile.this", "Data: " +string);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void retrieveInfoFacebook ()
    {
        imageView = findViewById(R.id.profile_photo);
        textName = findViewById(R.id.username);
        textEmail = findViewById(R.id.display_name);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // User Name
        textName.setText(mFirebaseUser.getDisplayName());

        // User ID
        Log.d ("MyProfile.this", "FB UID: " +mFirebaseUser.getUid());

        // Email-ID
        textEmail.setText(mFirebaseUser.getEmail());

        // User-Profile (if available)
        Glide.with(this)
                .load(mFirebaseUser.getPhotoUrl())
                .into(imageView);
    }
}