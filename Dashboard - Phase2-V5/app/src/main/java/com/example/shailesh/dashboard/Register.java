package com.example.shailesh.dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shailesh.dashboard.Utils.FirebaseMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Register extends AppCompatActivity {

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;
    private Context mContext;
    private String email, fname, lname, password;

    private ProgressBar progressBar;
    private EditText Fname, Lname, Email, Password;
    private TextView text, linkSignIn;
    private Button register;

    private String append=" ";
    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        text = (TextView) findViewById(R.id.pleaseWait2);
        Fname = (EditText) findViewById(R.id.Fname);
        Lname = (EditText) findViewById(R.id.Lname);
        Email = (EditText) findViewById(R.id.Email2);
        Password = (EditText) findViewById(R.id.Pass2);
        register = (Button) findViewById(R.id.register);
        mContext = Register.this;
        firebaseMethods = new FirebaseMethods(mContext);

        progressBar.setVisibility(View.GONE);
        text.setVisibility(View.GONE);

        setupFirebaseAuth();

        linkSignIn = (TextView) findViewById(R.id.linkSignIn);
        linkSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linkSignIn.setPaintFlags(linkSignIn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname = Fname.getText().toString();
                lname = Lname.getText().toString();
                email = Email.getText().toString();
                password = Password.getText().toString();

                if (isStringNull(fname) || isStringNull(lname) || isStringNull(email) || isStringNull(password))
                    Toast.makeText(Register.this, "All field required.", Toast.LENGTH_SHORT).show();
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Register.this", "createUserWithEmail:success");
                                        //FirebaseUser user = mAuth.getCurrentUser();
                                        progressBar.setVisibility(View.GONE);
                                        text.setVisibility(View.GONE);
                                        Toast.makeText(mContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        progressBar.setVisibility(View.GONE);
                                        text.setVisibility(View.GONE);
                                        Log.w("Register.this", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    private boolean isStringNull(String string) {
        if(string == null || string.equals(""))
            return true;
        else
            return false;
    }

    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d("activity_home", "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d("activity_home", "onAuthStateChanged:signed_in:" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Make Sure Username is unique

                                if (firebaseMethods.checkIfUsernameExists(email, dataSnapshot)) {
                                    Toast.makeText(Register.this, "Email already exists..!!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "OnDataChange: username already exists. Appending random string to name." + append);
                                }

                            String username = fname + "." + lname + append;

                            //Add new user to the database
                            Log.d(TAG, "Email: " + email + ", username: " +username);
                            firebaseMethods.addNewUser(email, username);

                            mAuth.signOut();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    // User is signed out
                    Log.d("activity_home", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
