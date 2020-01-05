package com.example.si7atic;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParentProfileActivity extends AppCompatActivity {
    TextView mAddress, mMobile, mChildren, mName;
    String name, mobile, address, children, image;
    CircleImageView mPhoto;
    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;
    RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile);
        mAddress = findViewById(R.id.m_parent_address);
        mChildren = findViewById(R.id.m_parent_children);
        mName = findViewById(R.id.m_parent_name);
        mPhoto = findViewById(R.id.m_parent_image);
        mMobile = findViewById(R.id.m_parent_number);
        mRelativeLayout=findViewById(R.id.m_parent_profile_relative_layout);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON).setAllowRotation(true).setSnapRadius(50).setAllowFlipping(true).setAllowCounterRotation(true)
                        .start(ParentProfileActivity.this);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Read from the database
        String uid = mCurrentUser.getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid); //Getting root reference
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                address = Objects.requireNonNull(dataSnapshot.child("address").getValue()).toString();
                mobile = Objects.requireNonNull(dataSnapshot.child("mobile").getValue()).toString();
                children = Objects.requireNonNull(dataSnapshot.child("number of children").getValue()).toString();
                image = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                mName.setText(name.toUpperCase());
                mMobile.setText("PHONE: " + mobile);
                mAddress.setText("ADDRESS: " + address);
                mChildren.setText("NUMBER OF CHILDREN " + children);
                Picasso.get().load(image).into(mPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void handleEditProfile(View view) {
        Pair[] pairs =new Pair[4];
        pairs[0]=new Pair<View , String>(mRelativeLayout, "layoutTransition");
        pairs[1]=new Pair<View , String>(mAddress, "AddressTransition");
        pairs[2]=new Pair<View , String>(mName, "nameTransition");
        pairs[3]=new Pair<View , String>(mMobile, "phoneTransition");
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("mobile", mobile);
        intent.putExtra("address", address);
        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(ParentProfileActivity.this, pairs);
        startActivity(intent,options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Picasso.get().load(resultUri).into(mPhoto);
                final String uid = mCurrentUser.getUid();
                final StorageReference filePath = FirebaseStorage.getInstance().getReference()
                        .child(uid + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid); //Getting root reference
                                mUserDatabase.child("image").setValue(uri.toString());
                                Toast.makeText(getApplicationContext(), "done!!!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("the error is :", exception.getMessage());
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("hoho", error.getMessage());
            }
        }
    }
}