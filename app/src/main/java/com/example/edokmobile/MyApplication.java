package com.example.edokmobile;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    private GoogleSignInAccount account;
    protected OkHttpClient client = new OkHttpClient();
    private String url = "https://5jfh958c-8081.euw.devtunnels.ms/";
    private String accessToken;
    private ArrayList<HashMap<String, Object>> user_info;

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

}
