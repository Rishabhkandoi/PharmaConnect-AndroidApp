package com.example.shailesh.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {

    //Facebook
    private FirebaseAuth.AuthStateListener fAuthStateListener;
    private FirebaseAuth fAuth;
    LoginButton loginButton;
    CallbackManager mCallbackManager;
    String TAG1 = "Hey";
    String name;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar progressBar;
    private EditText UserName, Password;
    private TextView text, linkSignUp;

    // For Google
    private static final String TAG = "LoginActivity";
    @SuppressLint("StaticFieldLeak")
    public static GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    SharedPreferences sharedPreferences;
    public static Boolean loggedin = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        text = (TextView) findViewById(R.id.pleaseWait);
        UserName = (EditText) findViewById(R.id.Email);
        Password = (EditText) findViewById(R.id.Pass);

        progressBar.setVisibility(View.GONE);
        text.setVisibility(View.GONE);

        setupFirebaseAuth();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null)
        {
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        }

        findViewById(R.id.signInbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.signInbtn:
                        userSignIn();
                        break;
                }
            }
        });

        init();

        fAuth = FirebaseAuth.getInstance();

        fAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser f_user = firebaseAuth.getCurrentUser();
                updateUI_f(f_user);
            }
        };

        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.fbloginbutton);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(getApplication(), "LOGINSUCCESSFUL", Toast.LENGTH_SHORT).show();

                //Login Successful
                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);

                SharedPreferences sharedPref = getSharedPreferences("SHAREDPREFERENCE_USER_PROFILE", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("USERLOGINTYPE", "facebook");
                editor.apply();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "LOGINFAILED", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isStringNull(String string) {
        if (string.equals(""))
            return true;
        else
            return false;
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    public void init() {
        //initialise button for logging in
        Button login = (Button) findViewById(R.id.Login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = UserName.getText().toString();
                String password = Password.getText().toString();

                if (isStringNull(email) || isStringNull(password))
                    Toast.makeText(Login.this, "All fields required to be filled.", Toast.LENGTH_SHORT).show();
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Login.this", "signInWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();

                                sharedPreferences = getSharedPreferences("MYPREF", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("Provider", 0);
                                editor.apply();

                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();

                                progressBar.setVisibility(View.GONE);
                                text.setVisibility(View.GONE);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                text.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        linkSignUp = (TextView) findViewById(R.id.linkSignUp);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linkSignUp.setPaintFlags(linkSignUp.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

                Intent intent  = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d("activity_home", "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d("activity_home", "onAuthStateChanged:signed_in:" + user.getUid());
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
        fAuth.addAuthStateListener(fAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (fAuthStateListener != null) {
            fAuth.removeAuthStateListener(fAuthStateListener);
        }
    }

    /****************************************************************************************************
     ************************************** FOR FACEBOOK AUTH *********************************************
     ****************************************************************************************************/



    private void handleFacebookAccessToken(AccessToken token) {
        // ...
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        else {
                            Toast.makeText(Login.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                            Log.d ("Login.this", "Authentication successful");
                            sharedPreferences = getSharedPreferences("MYPREF", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Provider", 1);
                            editor.apply();
                        }
                    }
                });
    }
// [END auth_with_facebook]

    private void updateUI_f(FirebaseUser user) {
        if (user != null) {
            //Code here for what you want to do after login
        }
    }


    private void FacebookSignOut() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        } else {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {

                    LoginManager.getInstance().logOut();

                }
            }).executeAsync();
        }
    }


    /****************************************************************************************************
     ************************************** FOR GOOGLE AUTH *********************************************
     ****************************************************************************************************/

    /*
   =================================================================================================
     userSignIn() - The primary signin process starts with this function
   =================================================================================================
    */
    private void userSignIn() {
        @SuppressLint("RestrictedApi") Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /*
  =================================================================================================
    updateUI() - To change the activity once the user has signed in
  =================================================================================================
   */
    public void updateUI(FirebaseUser account) {
        if(account != null) {
            Log.d(TAG, "updateUI: User is SIGNED IN.");

            // Get the profile details of the signed in user
            final String personName = account.getDisplayName();
            final String personEmail = account.getEmail();
            final String UId = account.getUid();
            final Uri personPhoto = account.getPhotoUrl();

            // Creating a bundle with all the user info and passing
            // it to the next activity
            final Bundle user_info_bundle = new Bundle();
            user_info_bundle.putString("displayname",personName);
            user_info_bundle.putString("email",personEmail);
            user_info_bundle.putString("id",UId);
            user_info_bundle.putString("photo",personPhoto.toString()); // This is originally of type Uri


            // Changing to HomeActivity - The actual objective of this function updateUI()
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtras(user_info_bundle);
            startActivity(intent);
            finish();
        }
        else
        {
            // User is not signed in....Don't change the activity
            Log.d(TAG, "updateUI: User is SIGNED OUT.");
        }
    }


    /*
  =================================================================================================
   onActivityResult() - Due to startActivityForResult() call in the function userSignIn()
  =================================================================================================
   */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            progressBar.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
            @SuppressLint("RestrictedApi") Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        //Facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*
  =================================================================================================
   handleSignInResult() - Supporting function for onActivityResult()
  =================================================================================================
   */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // This is where we know that the user has signed in successfully through Google Account
            // We now authenticate this user account with our Firebase Authentication
            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "handleSignInResult: failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "handleSignInResult: failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
            updateUI(null);
        }
    }


    /*
  =================================================================================================
   firebaseAuthWithGoogle() - Authenticating the Google SignedIn user to our Firebase Application
  =================================================================================================
   */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        // Getting the auth credential from the signed in account
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Firebase Sign in successful, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            sharedPreferences = getSharedPreferences("MYPREF", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Provider", 2);
                            editor.apply();

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // Firebase sign in failed, display a message to the user.
                            // This may happen when the administrator blocks an account
                            // on his application
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            // Deauth() from Google SignedIn account on device
                            Login.mGoogleSignInClient.signOut();
                            progressBar.setVisibility(View.GONE);
                            text.setVisibility(View.GONE);
                            updateUI(null);
                        }
                    }
                });
    }

}
