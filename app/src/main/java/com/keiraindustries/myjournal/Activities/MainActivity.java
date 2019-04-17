package com.keiraindustries.myjournal.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.keiraindustries.myjournal.Data.JournalData;
import com.keiraindustries.myjournal.R;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }


    @Override
    protected void onResume() {
        super.onResume();

        try {
            File temp = getFilesDir();
            JournalData.getInstance().setFilesDir(getFilesDir().getCanonicalPath());
            temp = getFilesDir();
        }
        catch (Exception e) {

        }

        Intent i = new Intent(this,BlogEntryListActivity.class);
        startActivity(i);
        finish();
    }

}
