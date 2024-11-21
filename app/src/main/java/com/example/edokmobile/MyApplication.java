package com.example.edokmobile;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MyApplication extends Application {

    private GoogleSignInAccount account;

    public GoogleSignInAccount getSomeVariable() {
        return account;
    }

    public void setSomeVariable(GoogleSignInAccount someVariable) {
        this.account = someVariable;
    }
}
