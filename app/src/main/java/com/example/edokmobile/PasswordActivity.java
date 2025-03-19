package com.example.edokmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class PasswordActivity extends AppCompatActivity {
    private Button next_btn;
    private EditText password_input;
    private EditText name_input;
    private EditText email_input;
    private String token_user;
    protected OkHttpClient client = new OkHttpClient();
    String url;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password);
        url = ((MyApplication) getApplication()).getGlobalUrl();
        next_btn = findViewById(R.id.next_btn);
        password_input = findViewById(R.id.PasswordInput);
        name_input = findViewById(R.id.NameInput);
        email_input = findViewById(R.id.EmailInput);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_input.getText().toString().trim().matches("")){
                    Toast toast_acc = Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_to_app_hint_password), Toast.LENGTH_SHORT);
                    toast_acc.show();
                }
                else {
                    if (password_input.getText().toString().trim().length() > 5 && password_input.getText().toString().trim().length() <= 20){
                        next_btn.setEnabled(false);
                        next_btn.setText(getResources().getString(R.string.waiting_text_btn));
                        OkHTTPHandler okHTTPHandler = new OkHTTPHandler();
                        okHTTPHandler.execute();
                    }
                    else {
                        Toast toast_acc = Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_error_len), Toast.LENGTH_SHORT);
                        toast_acc.show();
                    }
                }
            }
        });
        token_user = ((MyApplication) getApplication()).getToken();
        requestNotificationPermission();
    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            //запрашиваем разрешение
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
    }
    public class OkHTTPHandler extends AsyncTask<Void,Void, Response> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        public final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        @Override
        protected Response doInBackground(Void ... voids) { //действия в побочном потоке
            if ( isCancelled()){
                return null;
            }
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                try  {
                    JSONObject jo = new JSONObject();
                    jo.put("name", name_input.getText().toString().trim());
                    jo.put("mail", email_input.getText().toString().trim());
                    jo.put("password", password_input.getText().toString().trim());
                    jo.put("token_phone", token_user);
                    RequestBody formBody = RequestBody.create(JSON, String.valueOf(jo));
                    Request.Builder builder = new Request.Builder(); //построитель запроса
                    Request request = builder.url(url + "user/reg")
                            .post(formBody) //тип запроса
                            .build();
                    Response response = client.newCall(request).execute();
                    return response;
                } catch (IOException e) {
                    Log.e("OkHTTPHandler", "Network error: " + e.getMessage());
                    retryCount++;
                    if (retryCount >= MAX_RETRIES) {
                        Log.e("OkHTTPHandler", "Max retries reached, request failed.");
                        return null;
                    }
                    try {
                        Thread.sleep(INITIAL_DELAY * retryCount); // экспоненциальная задержка
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        Log.e("OkHTTPHandler", "Thread interrupted.");
                        return null;
                    }
                } catch (JSONException e) {
                    Log.e("OkHTTPHandler", "JSON error: " + e.getMessage());
                    return null;
                }
            }
            return null; // Достигли максимума попыток, вернули null
        }
        @Override
        protected void onPostExecute(Response response) { //действия после выполнения задач в фоне
            super.onPostExecute(response);
            if (response.isSuccessful()) {
                Intent intent = new Intent(getApplicationContext(), EnterToAppActivity.class);
                startActivity(intent);
            }
            else {
                next_btn.setEnabled(false);
                next_btn.setText(getResources().getString(R.string.waiting_text_btn));
                Toast toast_acc = Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_to_app_not_enter_error), Toast.LENGTH_LONG);
                toast_acc.show();
            }
        }
        @Override
        protected void onCancelled() {
            //обработка отмены задачи
        }
    }
}