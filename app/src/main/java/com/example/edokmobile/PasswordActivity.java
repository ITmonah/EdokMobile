package com.example.edokmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

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
                if (email_input.getText().toString().trim().matches("")){
                    next_btn.setEnabled(true);
                    next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                    Toast toast_acc = Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_to_app_hint_email), Toast.LENGTH_SHORT);
                    toast_acc.show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email_input.getText().toString().trim()).matches()) {
                    next_btn.setEnabled(true);
                    next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_email_hint), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password_input.getText().toString().trim().matches("")){
                    next_btn.setEnabled(true);
                    next_btn.setText(getResources().getString(R.string.enter_to_app_next));
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
                        next_btn.setEnabled(true);
                        next_btn.setText(getResources().getString(R.string.enter_to_app_next));
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
    public class OkHTTPHandler extends AsyncTask<Void,Void, OkHTTPHandler.RegistrationResult> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        public final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        //вспомогательный класс для передачи результатов из doInBackground в onPostExecute
        public class RegistrationResult {
            boolean isSuccessful;
            String errorMessage;
            List<Map<String, String>> errorDetails;
        }
        @Override
        protected RegistrationResult doInBackground(Void ... voids) { //действия в побочном потоке
            RegistrationResult result = new RegistrationResult();
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
                    result.isSuccessful = response.isSuccessful();
                    if (!result.isSuccessful) {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            String jsonString = responseBody.string();
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.has("detail")) {
                                    JSONArray detailArray = jsonObject.getJSONArray("detail");
                                    result.errorDetails = new ArrayList<>();
                                    for (int i = 0; i < detailArray.length(); i++) {
                                        JSONObject detailObject = detailArray.getJSONObject(i);
                                        String msg = detailObject.getString("msg");
                                        JSONArray locArray = detailObject.getJSONArray("loc");
                                        String loc = locArray.getString(0);
                                        Map<String, String> detailMap = new HashMap<>();
                                        detailMap.put("msg", msg);
                                        detailMap.put("loc", loc);
                                        result.errorDetails.add(detailMap);
                                    }
                                } else {
                                    result.errorMessage = "Ошибка разбора ответа сервера: отсутствует поле 'detail'";
                                }

                            } catch (JSONException e) {
                                result.errorMessage = "Ошибка парсинга JSON: " + e.getMessage();
                                e.printStackTrace();
                            }
                        } else {
                            result.errorMessage = "Пустой ответ от сервера";
                        }
                    }
                    return result;
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
            result.isSuccessful = false;
            return null; // Достигли максимума попыток, вернули null
        }
        @Override
        protected void onPostExecute(RegistrationResult result) { //действия после выполнения задач в фоне
            super.onPostExecute(result);
            if (result == null) {
                //ошибка соединения
                next_btn.setEnabled(true);
                next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (result.errorMessage != null) {
                next_btn.setEnabled(true);
                next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                Toast.makeText(getApplicationContext(), result.errorMessage, Toast.LENGTH_LONG).show();
                return;
            }
            if (result.isSuccessful) {
                Intent intent = new Intent(getApplicationContext(), EnterToAppActivity.class);
                startActivity(intent);
            }else {
                //обработка ошибок от сервера
                if (result.errorDetails != null) {
                    for (Map<String, String> detail : result.errorDetails) {
                        String msg = detail.get("msg");
                        String loc = detail.get("loc");
                        if (loc.contains("email") && msg.equals("Email уже существует")) {
                            next_btn.setEnabled(true);
                            next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_already_exists), Toast.LENGTH_LONG).show(); // Замените на строковые ресурсы
                        } else if (loc.contains("name") && msg.equals("Никнейм уже существует")) {
                            next_btn.setEnabled(true);
                            next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.username_already_exists), Toast.LENGTH_LONG).show(); // Замените на строковые ресурсы
                        } else {
                            next_btn.setEnabled(true);
                            next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_enter_error) + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    next_btn.setEnabled(true);
                    next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_profile_delete_account_error), Toast.LENGTH_LONG).show();
                }
            }
        }
        @Override
        protected void onCancelled() {
            //обработка отмены задачи
        }
    }
}