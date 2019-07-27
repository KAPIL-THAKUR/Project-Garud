package com.example.projectgarud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.core.content.ContextCompat.startActivity;

public class NotificationReceiver extends BroadcastReceiver {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    public void onReceive(Context context, Intent intent) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            final DatabaseReference databaseReference = firebaseDatabase.getReference("Project Garud Users").child(firebaseAuth.getUid()).child("Safe Person");
            SafePerson safePerson1 = new SafePerson("Yes");
            databaseReference.setValue(safePerson1);
        }

        String message = intent.getStringExtra("toastMessage");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
