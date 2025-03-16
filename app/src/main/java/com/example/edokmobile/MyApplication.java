package com.example.edokmobile;

import android.app.Application;
import android.content.Context;

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
    private String url = "https://zkjxvmvq-8000.euw.devtunnels.ms/";
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

    //функции для токена телефона
    private MutableLiveData<String> token = new MutableLiveData<>();

    public void setToken(String myToken){
        token.postValue(myToken);
    }

    public LiveData<String> getToken(){
        return token;
    }
}
