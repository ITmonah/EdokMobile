package com.example.edokmobile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    private GoogleSignInAccount account;
    protected OkHttpClient client = new OkHttpClient();
    private String url = "http://10.0.2.2:8000/";
    private String accessToken;
    private ArrayList<HashMap<String, Object>> user_info;
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // При создании приложения, устанавливаем локаль из SharedPreferences
//        setAppLocale(this);
//    }
//
//    public void setAppLocale(Context context) {
//        // Получаем сохраненный язык
//        String languageCode = LocaleHelper.getSavedLanguage(context);
//        // Устанавливаем локаль
//        if (languageCode != null && !languageCode.isEmpty()) {
//            LocaleHelper.setLocale(context, languageCode); // Используем метод setLocale из LocaleHelper
//        }
//    }

    public GoogleSignInAccount getSomeVariable() {
        return account;
    }

    public void setSomeVariable(GoogleSignInAccount someVariable) {
        this.account = someVariable;
    }

    public void setAccessToken(String someVariable) {
        this.accessToken = someVariable;
    }

    public void setUserInfo(ArrayList<HashMap<String, Object>> someVariable) {
        this.user_info = someVariable;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public String getGlobalUrl() {
        return url;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public ArrayList<HashMap<String, Object>> getUserInfo() {
        return user_info;
    }

    //переменные для токена телефона
    private String token;
    private static final String TAG = "MyApplication";
    private static final String PREF_NAME = "MyPrefs";  // Название файла настроек
    private static final String KEY_FCM_TOKEN = "fcm_token"; // Ключ для хранения токена

    public String getToken() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        token = prefs.getString(KEY_FCM_TOKEN, null); // null - значение по умолчанию, если токена нет
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        //сохраняем токен в SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_FCM_TOKEN, token);
        editor.apply();
    }
}
