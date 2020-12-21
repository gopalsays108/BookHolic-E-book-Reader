package com.gopal.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.gopal.ebookapp.MainActivity;
import com.gopal.ebookapp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private FirebaseAuth firebaseAuth;
    private FrameLayout loginBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        email = findViewById( R.id.emailRegister );
        password = findViewById( R.id.passwordRegister );
        loginBtn = findViewById( R.id.loginBtn );
        TextView needNewAccount = findViewById( R.id.needNewAccount );
        progressBar = findViewById( R.id.progressBarRegister );
        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener( v -> verifyCredential() );

        needNewAccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext() , SignUPActivity.class ) );
            }
        } );
    }

    private void verifyCredential() {

        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String emailPatterns = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

        if (userEmail.isEmpty()) {
            email.setError( "Enter Email" );
            email.requestFocus();
        } else if (!userEmail.matches( emailPatterns )) {
            Toast.makeText( this, "Email pattern is wrong", Toast.LENGTH_SHORT ).show();
        } else if (userPassword.isEmpty()) {
            password.setError( "Enter Password" );
            password.requestFocus();
        } else {

            loginBtn.setEnabled( false );
            progressBar.setVisibility( View.VISIBLE );
            startLogin( userEmail, userPassword );

        }

    }

    private void startLogin(String userEmail, String userPassword) {

        firebaseAuth.signInWithEmailAndPassword( userEmail, userPassword )
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Intent mainIntent = new Intent( getApplicationContext(), MainActivity.class );
                            startActivity( mainIntent );
                            finish();

                            loginBtn.setEnabled( true );
                            progressBar.setVisibility( View.INVISIBLE );
                        } else {
                            if (task.getException() != null) {

                                loginBtn.setEnabled( true );
                                progressBar.setVisibility( View.INVISIBLE );
                                String error = task.getException().getLocalizedMessage();
                                Toast.makeText( LoginActivity.this, error, Toast.LENGTH_SHORT ).show();
                            }
                        }

                    }
                } );
    }
}