package com.example.edokmobile;

import android.annotation.SuppressLint;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

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
    protected OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password);

        next_btn = findViewById(R.id.next_btn);
        password_input = findViewById(R.id.PasswordInput);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_input.getText().toString().trim().matches("")){
                    Toast toast_acc = Toast.makeText(getApplicationContext(), "Введите пароль", Toast.LENGTH_SHORT);
                    toast_acc.show();
                }
                else {
                    if (password_input.getText().toString().trim().length() > 3 && password_input.getText().toString().trim().length() <= 20){
                        OkHTTPHandler okHTTPHandler = new OkHTTPHandler();
                        okHTTPHandler.execute();
                    }
                    else {
                        Toast toast_acc = Toast.makeText(getApplicationContext(), "Пароль слишком длинный/короткий", Toast.LENGTH_SHORT);
                        toast_acc.show();
                    }
                }
            }
        });
    }
    public class OkHTTPHandler extends AsyncTask<Void,Void, Response> { //что подаём на вход, что в середине, что возвращаем
        public final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        @Override
        protected Response doInBackground(Void ... voids) { //действия в побочном потоке
            if ( isCancelled()){
                return null;
            }
            GoogleSignInAccount account = ((MyApplication) getApplication()).getSomeVariable();
            try  {
                JSONObject jo = new JSONObject();
                jo.put("name", account.getDisplayName().toString());
                jo.put("mail", account.getEmail().toString());
                jo.put("password", password_input.getText().toString().trim());
                RequestBody formBody = RequestBody.create(JSON, String.valueOf(jo));
                Request.Builder builder = new Request.Builder(); //построитель запроса
                Request request = builder.url("https://j41kw20c-8000.euw.devtunnels.ms/user/reg")
                        .post(formBody) //тип запроса
                        .build();
                Response response = client.newCall(request).execute();
                return response;
            } catch (IOException e) {
                Log.e("OkHTTPHandler", "Ошибка сети: " + e.getMessage());
            } catch (Exception e) {
                Log.e("MyAsyncTask", "Ошибка в doInBackground", e);
                return null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Response response) { //действия после выполнения задач в фоне
            super.onPostExecute(response);
            if (response.isSuccessful()) {
                GoogleSignInAccount account = ((MyApplication) getApplication()).getSomeVariable();
                String name = account.getDisplayName();
                String email = account.getEmail();
                Toast toast_acc = Toast.makeText(getApplicationContext(), "Привет, " + name + "\n" +"(" + email + ")", Toast.LENGTH_LONG);
                toast_acc.show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast toast_acc = Toast.makeText(getApplicationContext(), "Не удалось войти", Toast.LENGTH_LONG);
                toast_acc.show();
            }
        }
        @Override
        protected void onCancelled() {
            //обработка отмены задачи
        }
    }
}