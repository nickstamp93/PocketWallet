package com.ngngteam.pocketwallet.Utils;

import android.app.Activity;
import android.app.DialogFragment;
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
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.background_material_light)) == act.getResources().getColor(R.color.background_material_dark)) {
                act.setTheme(R.style.Theme_Appcompat_custom_dark);
            } else {
                act.setTheme(R.style.Theme_Appcompat_custom_light);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public static int getThemeOfActivity(Activity act){
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
       try {
           if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.background_material_light)) == act.getResources().getColor(R.color.background_material_dark)) {
               return R.style.Theme_Appcompat_custom_dark;
           } else {
               return R.style.Theme_Appcompat_custom_light;
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return -1;
   }


}
