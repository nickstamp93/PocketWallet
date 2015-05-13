package com.ngngteam.pocketwallet.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.patternview.PatternView;
import com.ngngteam.pocketwallet.R;


public class PatternLockActivity extends AppCompatActivity {

    private PatternView patternView;
    private TextView tvPatternTitle;
    private String patternString;

    private String mode;

    Context context;
    LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Themer.setThemeToActivity(this);
        setContentView(R.layout.activity_pattern_lock);


        inflater = getLayoutInflater();

        context = this;


        patternView = (PatternView) findViewById(R.id.patternView);
        tvPatternTitle = (TextView) findViewById(R.id.tvPatternTitle);

        mode = (String) getIntent().getExtras().get("mode");

        Log.i("nikos" , "Pattern created--->mode:" + mode);
        //if we are in create mode
        if (mode.equals("edit")) {
            //enter the create title
            tvPatternTitle.setText(R.string.textview_new_pattern);
            patternString = null;
            patternView.setOnPatternDetectedListener(new PatternView.OnPatternDetectedListener() {
                @Override
                public void onPatternDetected() {
                    //get the pattern in the first time
                    if (patternString == null) {
                        patternString = patternView.getPatternString();
                        patternView.clearPattern();
                        //and change the title for confirmation
                        tvPatternTitle.setText(R.string.textview_confirm_pattern);
                        return;
                    }
                    if (patternString.equals(patternView.getPatternString())) {


                        View actionBarButtons = inflater.inflate(R.layout.custom_action_bar_buttons, new LinearLayout(context), false);
                        View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
                        cancelActionView.setOnClickListener(actionBarListener);
                        View doneActionView = actionBarButtons.findViewById(R.id.action_done);
                        doneActionView.setOnClickListener(actionBarListener);

                        getSupportActionBar().setHomeButtonEnabled(false);
                        getSupportActionBar().setDisplayShowHomeEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setDisplayShowTitleEnabled(false);

                        getSupportActionBar().setDisplayShowCustomEnabled(true);
                        getSupportActionBar().setCustomView(actionBarButtons);


//                        finish();
                    } else {
                        tvPatternTitle.setText(R.string.textview_new_pattern);
                        patternString = null;
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_pattern_not_chaged), Toast.LENGTH_SHORT).show();
                        patternView.clearPattern();
                    }
                }
            });
        } else {
            patternString = PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this).
                    getString(getString(R.string.pref_key_pattern), "none");
            patternView.setOnPatternDetectedListener(new PatternView.OnPatternDetectedListener() {
                @Override
                public void onPatternDetected() {
                    if (patternString.equals(patternView.getPatternString())) {
                        //correct pattern
                        //launch overview activity
                        Intent intent = new Intent(getApplicationContext(), NewOverviewActivity.class);
                        startActivity(intent);
                        //and destroy this one
                        finish();
                    } else {
                        tvPatternTitle.setText(R.string.textview_wrong_pattern);
                        tvPatternTitle.setTextColor(getResources().getColor(R.color.red));
                        patternView.clearPattern();
                    }
                }
            });
        }
    }

    private View.OnClickListener actionBarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.action_done) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_pattern_changed), Toast.LENGTH_SHORT).show();
                PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this).edit().
                        putString(getString(R.string.pref_key_pattern), patternString).commit();

                finish();
            } else {
                if (PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this).
                        getString(getString(R.string.pref_key_pattern), "none").equals("none")) {
                    //disable the startup lock
                    PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this)
                            .edit()
                            .putBoolean(getResources().getString(R.string.pref_key_password), false)
                            .commit();
                }
            }
            finish();
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //if this was edit mode
        if (mode.equals("edit")) {
            //and if pattern not set
            if (PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this).
                    getString(getString(R.string.pref_key_pattern), "none").equals("none")) {
                //disable the startup lock
                PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this)
                        .edit()
                        .putBoolean(getResources().getString(R.string.pref_key_password), false)
                        .commit();
                //an exit
            }
        }
        finish();
    }
}
