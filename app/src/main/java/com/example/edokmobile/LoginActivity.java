package com.example.edokmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private Button login_btn;
    private Button reg_btn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        String languageCode = LocaleHelper.getSavedLanguage(getApplicationContext());
        if (languageCode != null && !languageCode.isEmpty()) {
            LocaleHelper.setLocale(this, languageCode);
        }
        setContentView(R.layout.activity_login);
        login_btn = (android.widget.Button) findViewById(R.id.button2);
        reg_btn = (android.widget.Button) findViewById(R.id.button3);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = gsc.getSignInIntent();
//                GoogleSignInLauncher.launch(intent);
                Intent intent = new Intent(getApplicationContext(), EnterToAppActivity.class);
                startActivity(intent);
            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> GoogleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (account != null) {
                    ((MyApplication) getApplicationContext()).setSomeVariable(account);
                    Intent intent = new Intent(getApplicationContext(),PasswordActivity.class);
                    startActivity(intent);
                }
            } catch (ApiException e) {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_enter_error), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    });
}