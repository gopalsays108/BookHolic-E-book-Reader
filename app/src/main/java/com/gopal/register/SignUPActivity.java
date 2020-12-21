package com.gopal.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gopal.ebookapp.MainActivity;
import com.gopal.ebookapp.R;

import java.util.HashMap;

public class SignUPActivity extends AppCompatActivity {

    private EditText userName;
    private EditText email;
    private EditText password;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private FrameLayout registerBtn;
    private DatabaseReference databaseReference;
    private String uid;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_u_p );

        userName = findViewById( R.id.userName );
        email = findViewById( R.id.emailRegister );
        password = findViewById( R.id.passwordRegister );

        progressBar = findViewById( R.id.progressBarRegister );
        registerBtn = findViewById( R.id.loginBtn );

        registerBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredential();
            }
        } );
    }

    private void checkCredential() {

        String emailPatterns = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
        String name = userName.getText().toString();
        String eMail = email.getText().toString();
        String pass = password.getText().toString();

        if (name.isEmpty()) {
            userName.setError( "Enter Name" );
            userName.requestFocus();
        } else if (eMail.isEmpty()) {
            email.setError( "Enter E-mail" );
            email.requestFocus();
        } else if (!eMail.matches( emailPatterns )) {
            Toast.makeText( this, "Email Pattern is Wrong", Toast.LENGTH_SHORT ).show();
        } else if (pass.isEmpty()) {
            password.setError( "Enter Password" );
            password.requestFocus();
        } else if (pass.length() < 6) {
            Toast.makeText( this, "Password Length should be at least 6 character", Toast.LENGTH_LONG ).show();
        } else {
            progressBar.setVisibility( View.VISIBLE );
            registerBtn.setEnabled( false );
            startRegistration( name, eMail, pass );
        }
    }

    private void startRegistration(final String name, String eMail, String pass) {

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword( eMail, pass )
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            firebaseUser = firebaseAuth.getCurrentUser();

                            if (firebaseUser != null) {
                                uid = firebaseUser.getUid();

                                databaseReference = FirebaseDatabase.getInstance().getReference( "Users" ).child( uid );

                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put( "name", name );
                                userMap.put( "image", "default" );

                                databaseReference.setValue( userMap )
                                        .addOnCompleteListener( new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    progressBar.setVisibility( View.INVISIBLE );
                                                    registerBtn.setEnabled( true );
                                                    mainIntent();
                                                } else {
                                                    if (task.getException() != null) {
                                                        progressBar.setVisibility( View.INVISIBLE );
                                                        registerBtn.setEnabled( true );
                                                        String error = task.getException().getLocalizedMessage();
                                                        Toast.makeText( SignUPActivity.this, error, Toast.LENGTH_SHORT ).show();
                                                    }
                                                }

                                            }
                                        } );
                            }

                        } else {
                            if (task.getException() != null) {
                                progressBar.setVisibility( View.INVISIBLE );
                                registerBtn.setEnabled( true );
                                String error = task.getException().getLocalizedMessage();
                                Toast.makeText( SignUPActivity.this, error, Toast.LENGTH_SHORT ).show();
                            }
                        }

                    }
                } );

    }

    private void mainIntent() {
        Intent mainIntent = new Intent( getApplicationContext(), MainActivity.class );
        mainIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity( mainIntent );
        finish();
    }
}