package com.keiraindustries.myjournal.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.keiraindustries.myjournal.Data.JournalData;
import com.keiraindustries.myjournal.Data.JournalManifest;
import com.keiraindustries.myjournal.R;

public class TestActivity extends AppCompatActivity {

    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testButton = (Button) findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalData.getInstance().syncWithStorage();
            }
        });
        JournalData.getInstance().initialize();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
