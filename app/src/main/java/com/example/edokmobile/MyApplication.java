package com.example.edokmobile;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    private GoogleSignInAccount account;
    protected OkHttpClient client = new OkHttpClient();
    private String url = "https://j41kw20c-8000.euw.devtunnels.ms/";

    public GoogleSignInAccount getSomeVariable() {
        return account;
    }

    public void setSomeVariable(GoogleSignInAccount someVariable) {
        this.account = someVariable;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public String getGlobalUrl() {
        return url;
    }

}
