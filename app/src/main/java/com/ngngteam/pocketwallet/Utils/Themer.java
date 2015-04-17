package com.ngngteam.pocketwallet.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ngngteam.pocketwallet.Extra.MagnificentChart;
import com.ngngteam.pocketwallet.R;

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


    public static void setTextviewTextColor(Activity act, View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_light))) {
            ((TextView) view).setTextColor(act.getResources().getColor(R.color.black));
        } else {
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

    public static void setBackgroundColorCard(Activity act, View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
        try {
            if (prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_pink)) {
                GradientDrawable d = (GradientDrawable) view.getBackground();
                d.setColor(act.getResources().getColor(R.color.action_bar_pink));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_dark))) {
                GradientDrawable d = (GradientDrawable) view.getBackground();
                d.setColor(act.getResources().getColor(R.color.action_bar_dark));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_green))) {
                GradientDrawable d = (GradientDrawable) view.getBackground();
                d.setColor(act.getResources().getColor(R.color.action_bar_green));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_teal))) {
                GradientDrawable d = (GradientDrawable) view.getBackground();
                d.setColor(act.getResources().getColor(R.color.action_bar_teal));
            } else if ((prefs.getInt("pref_key_theme", act.getResources().getColor(R.color.bg_dark)) == act.getResources().getColor(R.color.bg_light))) {
                GradientDrawable d = (GradientDrawable) view.getBackground();
                d.setColor(act.getResources().getColor(R.color.backround_pie_light));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setDrawerBackground(Activity act, View view) {
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
                ((ListView) view).setBackgroundColor(act.getResources().getColor(R.color.drawer_light));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
