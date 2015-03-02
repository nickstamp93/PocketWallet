package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 2/3/2015.
 */
public class Themer {

    public static void setThemeToActivity(Activity act) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.black)) == act.getResources().getColor(R.color.Fuchsia)) {
                act.setTheme(R.style.AppThemeFuchsia);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.black)) == act.getResources().getColor(R.color.black))) {
                act.setTheme(R.style.AppThemeBlack);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.black)) == act.getResources().getColor(R.color.green))) {
                act.setTheme(R.style.AppThemeGreen);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.black)) == act.getResources().getColor(R.color.Orange))) {
                act.setTheme(R.style.AppThemeOrange);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.black)) == act.getResources().getColor(R.color.teal))) {
                act.setTheme(R.style.AppThemeTeal);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.black)) == act.getResources().getColor(R.color.white))) {
                act.setTheme(R.style.AppThemeWhite);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
