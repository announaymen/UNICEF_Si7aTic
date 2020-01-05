package com.example.si7atic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText mLoginEmail, mLoginPassword;
    TextView mLabel;
    Button mLoginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginEmail = findViewById(R.id.m_login_email);
        mLoginPassword = findViewById(R.id.m_login_password);
        mLoginButton  = findViewById(R.id.m_login_button);
        mLabel=findViewById(R.id.m_layout_login_label);
        mAuth = FirebaseAuth.getInstance();

    }

    public void signIn(View view) {
        String email = mLoginEmail.getText().toString();
        String password = mLoginPassword.getText().toString();
        Toast.makeText(getApplicationContext(), "signing in with "+email+ " "+password,
                Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Authentication done.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMain();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void goToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    public void goToRegister(View view) {
        Pair[] pairs =new Pair[4];
        pairs[0]=new Pair<View , String>(mLoginEmail, "emailTransition");
        pairs[1]=new Pair<View , String>(mLoginPassword, "passwordTransition");
        pairs[2]=new Pair<View , String>(mLoginButton, "buttonTransition");
        pairs[3]=new Pair<View , String>(mLabel, "labelTransition");
        Intent intent = new Intent(this,RegisterActivity.class);
        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
        startActivity(intent,options.toBundle());
    }
}
