package com.ngngteam.pocketwallet.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import com.ngngteam.pocketwallet.R;

/**
 * Created by Nikos on 2/3/2015.
 */
public class Themer {

    public static void setThemeToActivity(Activity act) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        try {
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_pink)) {
//                act.setTheme(R.style.AppThemePink);
                act.getWindow().setBackgroundDrawableResource(R.color.bg_pink);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_dark))) {
//                act.setTheme(R.style.AppThemeBlack);
                act.getWindow().setBackgroundDrawableResource(R.color.bg_dark);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_green))) {
//                act.setTheme(R.style.AppThemeGreen);
                act.getWindow().setBackgroundDrawableResource(R.color.bg_green);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_teal))) {
//                act.setTheme(R.style.AppThemeLightBlue);
                act.getWindow().setBackgroundDrawableResource(R.color.bg_teal);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_light))) {
//                act.setTheme(R.style.AppThemeWhite);
                act.getWindow().setBackgroundDrawableResource(R.color.bg_light);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBackgroundColor(Activity act, View view, boolean isRed) {
        if (isRed)
            view.setBackgroundColor(act.getResources().getColor(R.color.bpRed));
        else
            view.setBackgroundColor(act.getResources().getColor(R.color.YellowGreen));


    }

}
