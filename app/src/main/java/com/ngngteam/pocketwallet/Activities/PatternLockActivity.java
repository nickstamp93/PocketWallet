package com.ngngteam.pocketwallet.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.patternview.PatternView;
import com.ngngteam.pocketwallet.R;
import com.ngngteam.pocketwallet.Utils.Themer;


public class PatternLockActivity extends Activity {

    private PatternView patternView;
    private TextView tvPatternTitle;
    private String patternString;

    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Themer.setThemeToActivity(this);
        setContentView(R.layout.activity_pattern_lock);

        patternView = (PatternView) findViewById(R.id.patternView);
        tvPatternTitle = (TextView) findViewById(R.id.tvPatternTitle);

        mode = (String) getIntent().getExtras().get("mode");
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
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_pattern_changed), Toast.LENGTH_SHORT).show();
                        PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this).edit().
                                putString(getString(R.string.pref_key_pattern), patternString).commit();

                        //TODO get confirmation from the user
                        finish();
                    }else{
                        tvPatternTitle.setText(R.string.textview_new_pattern);
                        patternString = null;
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_pattern_not_chaged), Toast.LENGTH_SHORT).show();
                        patternView.clearPattern();
                    }
                }
            });
        }else{
            patternString = PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this).
                    getString(getString(R.string.pref_key_pattern), "none");
            patternView.setOnPatternDetectedListener(new PatternView.OnPatternDetectedListener() {
                @Override
                public void onPatternDetected() {
                    if (patternString.equals(patternView.getPatternString())) {
                        //correct pattern
                        //launch overview activity
                        Intent intent = new Intent(getApplicationContext(), OverviewActivity.class);
                        startActivity(intent);
                        //and destroy this one
                        finish();
                    }else{
                        tvPatternTitle.setText(R.string.textview_wrong_pattern);
                        tvPatternTitle.setTextColor(getResources().getColor(R.color.red));
                        patternView.clearPattern();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //if this was edit mode
        if(mode.equals("edit")){
            //and if pattern not set
            if(PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this).
                    getString(getString(R.string.pref_key_pattern), "none").equals("none")){
                //disable the startup lock
                PreferenceManager.getDefaultSharedPreferences(PatternLockActivity.this)
                        .edit()
                        .putBoolean(getResources().getString(R.string.pref_key_password), false)
                        .commit();
                //an exit
                finish();
            }
        }
    }
}