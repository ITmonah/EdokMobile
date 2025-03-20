package com.example.edokmobile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EnterToAppActivity extends AppCompatActivity {
    private Button next_btn;
    private EditText password_input;
    private EditText login_input;
    protected OkHttpClient client = new OkHttpClient();
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_to_app);
        url = ((MyApplication) getApplication()).getGlobalUrl();
        next_btn = findViewById(R.id.next_btn);
        password_input = findViewById(R.id.PasswordInput);
        login_input = findViewById(R.id.UserInput);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login_input.getText().toString().trim().matches("")) {
                    Toast toast_acc = Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_to_app_hint_email), Toast.LENGTH_SHORT);
                    toast_acc.show();
                } else {
                    if (password_input.getText().toString().trim().matches("")) {
                        Toast toast_acc = Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_to_app_hint_password), Toast.LENGTH_SHORT);
                        toast_acc.show();
                    } else {
                        next_btn.setEnabled(false);
                        next_btn.setText(getResources().getString(R.string.enter_to_app_waiting_btn));
                        OkHTTPHandler okHTTPHandler = new OkHTTPHandler();
                        okHTTPHandler.execute();
                    }
                }
            }
        });
    }

    public class OkHTTPHandler extends AsyncTask<Void, Void, ArrayList> { //что подаём на вход, что в середине, что возвращаем
        public final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)

        @Override
        protected ArrayList doInBackground(Void... voids) { //действия в побочном потоке
            if (isCancelled()) {
                return null;
            }
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                try {
                    JSONObject jo = new JSONObject();
                    jo.put("mail", login_input.getText().toString().trim());
                    jo.put("pwd", password_input.getText().toString().trim());
                    RequestBody formBody = RequestBody.create(JSON, String.valueOf(jo));
                    Request.Builder builder = new Request.Builder(); //построитель запроса
                    Request request = builder.url(url + "user/login")
                            .post(formBody) //тип запроса
                            .build();
                    Response response = client.newCall(request).execute();
                    ArrayList<Object> res = new ArrayList<>();
                    if (response.isSuccessful()) {
                        JSONObject jsonObject_start = null;
                        String js_body = response.body().string();
                        jsonObject_start = new JSONObject(js_body);
                        String token = jsonObject_start.getString("access_token");
                        res.add(token);
                    }else{
                        res.add("error"); }
                    return res;
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
        protected void onPostExecute(ArrayList arrayList) { //действия после выполнения задач в фоне
            super.onPostExecute(arrayList);
            if (arrayList == null) {
                //ошибка соединения
                next_btn.setEnabled(true);
                next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                return;
            }
            String token=arrayList.get(0).toString();
            if(token!="error"){
                ((MyApplication) getApplicationContext()).setAccessToken(token);
                OkHTTPHandler_User okHTTPHandler_user = new OkHTTPHandler_User();
                okHTTPHandler_user.execute();
            } else {
                next_btn.setEnabled(true);
                next_btn.setText(getResources().getString(R.string.enter_to_app_next));
                Toast toast_acc = Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_to_app_not_enter_error), Toast.LENGTH_LONG);
                toast_acc.show();
            }
        }
        @Override
        protected void onCancelled() {
            //обработка отмены задачи
        }
    }


    public class OkHTTPHandler_User extends AsyncTask<Void, Void, ArrayList> { //что подаём на вход, что в середине, что возвращаем
        public final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)

        @Override
        protected ArrayList<HashMap<String, Object>> doInBackground(Void... voids) { //действия в побочном потоке
            if (isCancelled()) {
                return null;
            }
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                try {
                    Request.Builder builder = new Request.Builder(); //построитель запроса
                    Request request = builder.url(url + "user/me")
                            .header("Authorization", "Bearer " + ((MyApplication) getApplication()).getAccessToken())
                            .get() //тип запроса
                            .build();
                    Response response = client.newCall(request).execute();
                    HashMap<String, Object> map = new HashMap<>();
                    if (response.isSuccessful()) {
                        JSONObject jsonObject_start = null;
                        jsonObject_start = new JSONObject(response.body().string());
                        String name = jsonObject_start.getString("username");
                        String email = jsonObject_start.getString("email");
                        String count_r = jsonObject_start.getString("count_r");
                        String raiting = jsonObject_start.getString("raiting");
                        String image = jsonObject_start.getString("image");

                        map.put("name", name);
                        map.put("email", email);
                        map.put("count_r", count_r);
                        map.put("raiting", raiting);
                        map.put("image", image);
                        map.put("error", false);
                    }else{
                        map.put("error", true);
                    }
                    ArrayList<HashMap<String, Object>> list=new ArrayList<>();
                    list.add(0,map);
                    return list;
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
                    throw new RuntimeException(e);
                }
            }
            return null; // Достигли максимума попыток, вернули null
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) { //действия после выполнения задач в фоне
            super.onPostExecute(arrayList);
            HashMap<String, Object> map = (HashMap<String, Object>) arrayList.get(0);
            if (map.get("error").equals(false)) {
            ((MyApplication) getApplicationContext()).setUserInfo(arrayList);
            Toast toast_acc = Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_to_app_welcome_toast), Toast.LENGTH_LONG);
            toast_acc.show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            } else {
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