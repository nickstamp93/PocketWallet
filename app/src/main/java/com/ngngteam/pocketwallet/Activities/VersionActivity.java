package com.ngngteam.pocketwallet.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

public class VersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.changelog_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

}
