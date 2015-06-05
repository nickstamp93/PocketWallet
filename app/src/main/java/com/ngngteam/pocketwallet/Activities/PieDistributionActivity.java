package com.ngngteam.pocketwallet.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;

public class PieDistributionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Themer.setThemeToActivity(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pie_distribution);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

}
