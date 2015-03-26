package myexpenses.ng2.com.myexpenses.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import myexpenses.ng2.com.myexpenses.R;

/**
 * Created by Nikos on 2/3/2015.
 */
public class Themer {

    public static void setThemeToActivity(Activity act) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        try {
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_pink)) {
                act.setTheme(R.style.AppThemePink);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_dark))) {
                act.setTheme(R.style.AppThemeBlack);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_green))) {
                act.setTheme(R.style.AppThemeGreen);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_teal))) {
                act.setTheme(R.style.AppThemeLightBlue);
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_light))) {
                act.setTheme(R.style.AppThemeWhite);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setButtonTextColor(Activity act, View view, boolean isButton) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_pink))) {
            if (!isButton)
                ((EditText) view).setTextColor(act.getResources().getColor(R.color.black));
            else
                ((Button) view).setTextColor(act.getResources().getColor(R.color.black));
        }
    }

    public static void setTextviewTextColor(Activity act, View view){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_light))) {
            ((TextView) view).setTextColor(act.getResources().getColor(R.color.black));
        }else{
            ((TextView) view).setTextColor(act.getResources().getColor(R.color.white));
        }

    }

    public static void setBackgroundColor(Activity act, View view, boolean isRed) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_dark))
                || (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_teal))
                || (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_light))) {
            if (isRed)
                ((Button) view).setBackgroundColor(act.getResources().getColor(R.color.red));
            else
                ((Button) view).setBackgroundColor(act.getResources().getColor(R.color.YellowGreen));

        }


    }

    public static void setPieBackgroundColor(Activity act, View view) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        try {
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_pink)) {
                ((MagnificentChart) view).setChartBackgroundColor(act.getResources().getColor(R.color.action_bar_pink));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_dark))) {
                ((MagnificentChart) view).setChartBackgroundColor(act.getResources().getColor(R.color.action_bar_dark));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_green))) {
                ((MagnificentChart) view).setChartBackgroundColor(act.getResources().getColor(R.color.action_bar_green));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_teal))) {
                ((MagnificentChart) view).setChartBackgroundColor(act.getResources().getColor(R.color.action_bar_teal));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_light))) {
                ((MagnificentChart) view).setChartBackgroundColor(act.getResources().getColor(R.color.backround_pie_light));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLinearLayoutBackround(Activity act , View view){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        try {
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_pink)) {
//                ((LinearLayout) view).setBackgroundColor(act.getResources().getColor(R.color.action_bar_pink));
                GradientDrawable d = (GradientDrawable) ((LinearLayout) view).getBackground();
                d.setColor(act.getResources().getColor(R.color.action_bar_pink));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_dark))) {
//                ((LinearLayout) view).setBackgroundColor(act.getResources().getColor(R.color.action_bar_pink));
                GradientDrawable d = (GradientDrawable) ((LinearLayout) view).getBackground();
                d.setColor(act.getResources().getColor(R.color.action_bar_dark));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_green))) {
//                ((LinearLayout) view).setBackgroundColor(act.getResources().getColor(R.color.action_bar_pink));
                GradientDrawable d = (GradientDrawable) ((LinearLayout) view).getBackground();
                d.setColor(act.getResources().getColor(R.color.action_bar_green));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_teal))) {
//                ((LinearLayout) view).setBackgroundColor(act.getResources().getColor(R.color.action_bar_pink));
                GradientDrawable d = (GradientDrawable) ((LinearLayout) view).getBackground();
                d.setColor(act.getResources().getColor(R.color.action_bar_teal));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_light))) {
//                ((LinearLayout) view).setBackgroundColor(act.getResources().getColor(R.color.action_bar_pink));
                GradientDrawable d = (GradientDrawable) ((LinearLayout) view).getBackground();
                d.setColor(act.getResources().getColor(R.color.backround_pie_light));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setDrawerBackground(Activity act , View view){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        try {
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_pink)) {
                ((ListView) view).setBackgroundColor(act.getResources().getColor(R.color.bg_pink));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_dark))) {
                ((ListView) view).setBackgroundColor(act.getResources().getColor(R.color.bg_dark));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_green))) {
                ((ListView) view).setBackgroundColor(act.getResources().getColor(R.color.bg_green));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_teal))) {
                ((ListView) view).setBackgroundColor(act.getResources().getColor(R.color.bg_teal));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_light))) {
                ((ListView) view).setBackgroundColor(act.getResources().getColor(R.color.action_bar_dark));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
