package com.example.si7atic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    String name, number, address;
    EditText mNumber, mAddress, mName;
    @SuppressLint("WrongViewCast")
    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mNumber = findViewById(R.id.m_edit_profile_mobile);
        mAddress = findViewById(R.id.m_edit_profile_address);
        mName = findViewById(R.id.m_edit_profile_name);
        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("mobile");
        address = getIntent().getStringExtra("address");
        mName.setText(name);
        mAddress.setText(address);
        mNumber.setText(number);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());
        Toast.makeText(getApplicationContext(), name + "  " + number + "  " + address, Toast.LENGTH_LONG).show();
    }

    public void saveChanges(View view) {
        String name = mName.getText().toString(), address = mAddress.getText().toString(), number = mNumber.getText().toString();
        mUserDatabase.child("name").setValue(name);
        mUserDatabase.child("address").setValue(address);
        mUserDatabase.child("mobile").setValue(number);
        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View, String>(mAddress, "AddressTransition");
        pairs[1] = new Pair<View, String>(mName, "nameTransition");
        pairs[2] = new Pair<View, String>(mNumber, "phoneTransition");
        Intent intent = new Intent(this, ParentProfileActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(EditProfileActivity.this, pairs);
        startActivity(intent, options.toBundle());
        finish();

    }
}