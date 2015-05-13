package com.ngngteam.pocketwallet.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ngngteam.pocketwallet.R;

/**
 * Created by Nikos on 2/3/2015.
 */
public class Themer {

    public static void setThemeToActivity(Activity act) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        try {
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.background_material_dark)) == act.getResources().getColor(R.color.background_material_dark)) {
                act.setTheme(R.style.Theme_Appcompat_custom_dark);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.background_material_dark)) == act.getResources().getColor(R.color.background_material_light))) {
                act.setTheme(R.style.Theme_Appcompat_custom_light);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
