package com.example.si7atic;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Si7aTic extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
