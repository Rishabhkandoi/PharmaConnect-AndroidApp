package com.example.shailesh.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Jarvis on 19-03-2018.
 */

public class Logout extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        signOut();
    }

    @SuppressLint("RestrictedApi")
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Login.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(Logout.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}
