package com.example.edokmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.edokmobile.ui.recipes.RecipesFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailedActivity extends AppCompatActivity {

    protected OkHttpClient client  = new OkHttpClient();
    private TextView detailName;
    private TextView detailDes;
    private TextView detailPrice;
    private ImageView detailImage;
    private LinearLayout linearLayout;
    private TextView steps;
    String recipe_id;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        detailName = findViewById(R.id.detailName);
        detailDes = findViewById(R.id.detailDes);
        detailPrice = findViewById(R.id.detailPrice);
        detailImage = findViewById(R.id.detailImage);
        linearLayout = findViewById(R.id.main);
        steps = findViewById(R.id.detailDes3);
        url = ((MyApplication) getApplication()).getGlobalUrl();
        Intent intent = getIntent();
        recipe_id = intent.getStringExtra("recipe");
        OkHTTPHandler handler = new OkHTTPHandler();
        handler.execute();
    }

    //ассинхронный поток
    public class OkHTTPHandler extends AsyncTask<Void,Void, ArrayList> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        @Override
        protected ArrayList doInBackground(Void ... voids) { //действия в побочном потоке
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                Request.Builder builder = new Request.Builder(); //построитель запроса
                Request request = builder.url(url + "recipe/" + recipe_id)
                        .get() //тип запроса
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());//сначала объект элементов
                    ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                    String title = jsonObject.getString("name"); //название рецепта
                    JSONObject category_object = jsonObject.getJSONObject("category"); //категория рецепта
                    String category = category_object.getString("name");
                    JSONObject autor_object = jsonObject.getJSONObject("user"); //автор рецепта
                    String autor = autor_object.getString("name");
                    String img = url + jsonObject.getString("face_img"); //картинка
                    JSONArray steps_array = jsonObject.getJSONArray("steps"); //шаги рецепта
                    for (int i = 0; i < steps_array.length(); i++) {
                        JSONObject jsonObject_step = steps_array.getJSONObject(i);
                        String number = jsonObject_step.getString("number"); //номер шага
                        String info = jsonObject_step.getString("info"); //текст шага
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("stepNumber", number);
                        map.put("stepInfo", info);
                        list.add(map);
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("recipeName", title);
                    map.put("recipeCategory", category);
                    map.put("recipeAutor", autor);
                    map.put("recipeImage", img);
                    list.add(map);
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
                    Log.e("OkHTTPHandler", "JSON error: " + e.getMessage());
                    return null;
                }
            }
            return null; // Достигли максимума попыток, вернули null
        }
        @Override
        protected void onPostExecute(ArrayList s) { //действия после выполнения задач в фоне
            super.onPostExecute(s);
            ArrayList <HashMap<String, Object>> filteredList = (ArrayList<HashMap<String, Object>>) s.stream()
                    .limit(s.size() -1) //вывожу все элементы, кроме последнего
                    .collect(Collectors.toCollection(ArrayList::new));
            if (s != null && s.size() > 0) {
                HashMap<String, Object> recipe = (HashMap<String, Object>) s.get(s.size()-1); //берём последний элемент
                detailName.setText((String) recipe.get("recipeName"));
                detailDes.setText((String) recipe.get("recipeCategory"));
                detailPrice.setText((String) recipe.get("recipeAutor"));
                String stepsArr = "";
                for (int i = 0; i < filteredList.size(); i++) {
                    stepsArr += i+1 + "." + filteredList.get(i).get("stepInfo") + "\n\n";
                }
                stepsArr += "Приятного аппетита!";
                steps.setText(stepsArr);
                Glide.with(getApplicationContext())
                        .load(recipe.get("recipeImage"))
                        .placeholder(R.drawable.like)
                        .error(R.drawable.group_23)
                        .into(detailImage);
            }
        }
    }
}