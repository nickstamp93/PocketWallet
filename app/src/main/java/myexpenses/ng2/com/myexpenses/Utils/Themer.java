package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 2/3/2015.
 */
public class Themer {

    public static void setThemeToActivity(Activity act) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        try {
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_teal)) == act.getResources().getColor(R.color.bg_pink)) {
                act.setTheme(R.style.AppThemeFuchsia);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_teal)) == act.getResources().getColor(R.color.bg_dark))) {
                act.setTheme(R.style.AppThemeBlack);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_teal)) == act.getResources().getColor(R.color.bg_green))) {
                act.setTheme(R.style.AppThemeGreen);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_teal)) == act.getResources().getColor(R.color.Orange))) {
                act.setTheme(R.style.AppThemeOrange);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_teal)) == act.getResources().getColor(R.color.bg_teal))) {
                act.setTheme(R.style.AppThemeTeal);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_teal)) == act.getResources().getColor(R.color.white))) {
                act.setTheme(R.style.AppThemeWhite);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTextColor(Activity act, View view, boolean isButton) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_teal)) == act.getResources().getColor(R.color.bg_pink))) {
            if (!isButton)
                ((EditText) view).setTextColor(act.getResources().getColor(R.color.black));
            else
                ((Button) view).setTextColor(act.getResources().getColor(R.color.black));
        }
    }

    public static void setBackgroundColor(Activity act, View view, boolean isRed) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_teal)) == act.getResources().getColor(R.color.bg_dark))
                || (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_teal)) == act.getResources().getColor(R.color.bg_teal))) {
            if (isRed)
                ((Button) view).setBackgroundColor(act.getResources().getColor(R.color.red));
            else
                ((Button) view).setBackgroundColor(act.getResources().getColor(R.color.YellowGreen));

        }

    }



}
