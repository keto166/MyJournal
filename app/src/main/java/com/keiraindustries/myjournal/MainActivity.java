package com.keiraindustries.myjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.keiraindustries.myjournal.Activities.BlogEntryListActivity;
import com.keiraindustries.myjournal.Data.JournalData;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null) {
                    loginSuccess();
                }else {
                    loginFailed();
                }

            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        mAuth.signInWithEmailAndPassword("ryanto166@gmail.com","GTFrde112")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            loginSuccess();
                        } else {
                            loginFailed();
                        }
                    }
                });



    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    public void loginSuccess() {
        JournalData.getInstance().initialize();
        Intent i = new Intent(this,BlogEntryListActivity.class);
        startActivity(i);
        finish();
    }

    public void loginFailed() {

        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }
}
