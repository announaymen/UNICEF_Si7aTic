package com.example.si7atic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    EditText mRegisterMobile, mRegisterName, mRegisterEmail, mRegisterPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRegisterName = findViewById(R.id.m_register_name);
        mRegisterMobile = findViewById(R.id.m_register_mobile);
        mRegisterEmail = findViewById(R.id.m_register_email);
        mRegisterPassword = findViewById(R.id.m_register_password);
        mAuth = FirebaseAuth.getInstance();
    }

    public void handleRegister(View view) {
        String email = mRegisterEmail.getText().toString();
        String password = mRegisterPassword.getText().toString();
        String name = mRegisterName.getText().toString();
        String mobile = mRegisterMobile.getText().toString();
        Toast.makeText(getApplicationContext(), "registering with " + email + " anddd " + password, Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(mobile))
            register_user(name, mobile, email, password);
    }

    public void register_user(final String name, final String mobile, final String email, String password) {
        Toast.makeText(getApplicationContext(), "registering with " + email + " and " + password, Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                            String uid =  Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("mobile", mobile);
                            userMap.put("email", email);
                            userMap.put("address", "street X city Y");
                            userMap.put("number of children", "0");
                            userMap.put("image", "image_link");
                            userMap.put("thumb_image", "default");
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        goToMain();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "saving data failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void goToLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
