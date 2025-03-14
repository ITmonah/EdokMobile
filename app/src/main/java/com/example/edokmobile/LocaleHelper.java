package com.example.edokmobile;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.Log;

import java.util.Locale;

public class LocaleHelper { //  Создадим отдельный класс для вспомогательных методов

    private static final String APP_PREFERENCES = "settings"; // Используйте константу
    private static final String APP_PREFERENCES_COUNTER = "language"; // Используйте константу
    private static SharedPreferences mSettings;

    // Ваша функция для изменения локали (остается без изменений)
    public static void changeLoc(Activity activity, String langCode) {
        mSettings = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        android.content.res.Configuration conf = activity.getResources().getConfiguration();
        conf.locale = new Locale(langCode.toLowerCase());
        activity.getResources().updateConfiguration(conf, activity.getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_COUNTER, langCode);
        editor.apply();
        activity.finish();
        activity.startActivity(activity.getIntent());
    }

    // Функция для получения сохраненной локали
    public static String getSavedLanguage(Context context) {
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return mSettings.getString(APP_PREFERENCES_COUNTER, "en");
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = activity.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(new LocaleList(locale));
        } else {
            config.locale = locale;
        }

        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
    }
}
