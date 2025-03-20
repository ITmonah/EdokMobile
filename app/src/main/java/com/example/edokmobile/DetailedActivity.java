package com.example.edokmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailedActivity extends AppCompatActivity {

    protected OkHttpClient client  = new OkHttpClient();
    private TextView detailName;
    private TextView detailDes;
    private TextView detailPrice;
    private ImageView detailImage;
    private TextView detailLikes;
    private TextView detailDizlikes;
    private ImageButton Likes;
    private ImageButton Dizlikes;
    private TextView steps;
    private TextView time;
    private TextView ingredients;
    private MediaPlayer mediaPlayer;
    String recipe_id;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed);
        time = findViewById(R.id.textDataPublish);
        detailName = findViewById(R.id.detailName);
        detailDes = findViewById(R.id.detailDes);
        detailPrice = findViewById(R.id.detailPrice);
        detailImage = findViewById(R.id.detailImage);
        detailLikes = findViewById(R.id.detailLikes);
        detailDizlikes = findViewById(R.id.detailDizlikes);
        Likes = findViewById(R.id.buttonLikes); //кнопка лайка
        Dizlikes = findViewById(R.id.buttonDizlikes); //кнопка дизлайка
        steps = findViewById(R.id.detailDes3);
        ingredients = findViewById(R.id.textIngredients);
        url = ((MyApplication) getApplication()).getGlobalUrl();
        Intent intent = getIntent();
        recipe_id = intent.getStringExtra("recipe");
        OkHTTPHandler handler = new OkHTTPHandler();
        handler.execute();
        OkHTTPButton okHTTPButton = new OkHTTPButton();
        okHTTPButton.execute();
        mediaPlayer = MediaPlayer.create(this, R.raw.like);
        Likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTag = (int) Likes.getTag(); // получаем tag
                int currentTag_diz = (int) Dizlikes.getTag(); // получаем tag дизлайка
                if (currentTag == R.drawable.baseline_favorite_border_24) {
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(0); // Перематываем к началу (если звук уже играл)
                        mediaPlayer.start();
                    }
                    Likes.setImageResource(R.drawable.baseline_favorite_24);
                    Dizlikes.setImageResource(R.drawable.baseline_thumb_down_off_alt_24);
                    String count_likes = detailLikes.getText().toString();
                    detailLikes.setText((String) String.valueOf(Integer.parseInt(count_likes) + 1 ) );
                    //уменьшение количества дизлайков, если до лайка был нажат дизлайк
                    if (currentTag_diz == R.drawable.baseline_thumb_down_alt_24){
                        String count_dizlikes = detailDizlikes.getText().toString();
                        detailDizlikes.setText((String) String.valueOf(Integer.parseInt(count_dizlikes) - 1 ) );
                    }
                    Likes.setTag(R.drawable.baseline_favorite_24);
                    Dizlikes.setTag(R.drawable.baseline_thumb_down_off_alt_24);

                    OkHTTPLike okHTTPLike = new OkHTTPLike();
                    okHTTPLike.execute();
                } else {
                    Likes.setImageResource(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setImageResource(R.drawable.baseline_thumb_down_off_alt_24);
                    String count_likes = detailLikes.getText().toString();
                    detailLikes.setText((String) String.valueOf(Integer.parseInt(count_likes) - 1 ) );

                    Likes.setTag(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setTag(R.drawable.baseline_thumb_down_off_alt_24);

                    OkHTTPDelete okHTTPDelete = new OkHTTPDelete();
                    okHTTPDelete.execute();
                }
            }
        });
        Dizlikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentTag = (int) Dizlikes.getTag(); // получаем tag
                int currentTag_like = (int) Likes.getTag(); // получаем tag лайка
                if (currentTag == R.drawable.baseline_thumb_down_off_alt_24) {
                    Likes.setImageResource(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setImageResource(R.drawable.baseline_thumb_down_alt_24);
                    String count_dizlikes = detailDizlikes.getText().toString();
                    detailDizlikes.setText((String) String.valueOf(Integer.parseInt(count_dizlikes) + 1 ));
                    //уменьшение количества лайков, если до дизлайка был нажат лайк
                    if (currentTag_like == R.drawable.baseline_favorite_24){
                        String count_likes = detailLikes.getText().toString();
                        detailLikes.setText((String) String.valueOf(Integer.parseInt(count_likes) - 1 ) );
                    }
                    Likes.setTag(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setTag(R.drawable.baseline_thumb_down_alt_24);

                    OkHTTPDizlike okHTTPDizlike = new OkHTTPDizlike();
                    okHTTPDizlike.execute();
                } else {
                    Likes.setImageResource(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setImageResource(R.drawable.baseline_thumb_down_off_alt_24);
                    String count_dizlikes = detailDizlikes.getText().toString();
                    detailDizlikes.setText((String) String.valueOf(Integer.parseInt(count_dizlikes) - 1 ));

                    Likes.setTag(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setTag(R.drawable.baseline_thumb_down_off_alt_24);

                    OkHTTPDelete okHTTPDelete = new OkHTTPDelete();
                    okHTTPDelete.execute();
                }
            }
        });
        Likes.setEnabled(false);
        Dizlikes.setEnabled(false);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Освобождаем ресурсы MediaPlayer, когда Activity уничтожается
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    //ассинхронный поток
    public class OkHTTPHandler extends AsyncTask<Void,Void, ArrayList> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        @Override
        protected ArrayList doInBackground(Void ... voids) { //действия в побочном потоке
            int retryCount = 0;
            String lang = LocaleHelper.getSavedLanguage(getApplicationContext());
            while (retryCount < MAX_RETRIES) {
                Request.Builder builder = new Request.Builder(); //построитель запроса
                Request request = builder.url(url + "recipe/" + recipe_id+"?lang_code="+lang)
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
                    String time = jsonObject.getString("cooking_time"); //время готовки
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
                    JSONArray ingredients_array = jsonObject.getJSONArray("counts"); //ингредиенты рецепта
                    for (int i = 0; i < ingredients_array.length(); i++) {
                        JSONObject jsonObject_ingredient = ingredients_array.getJSONObject(i);
                        String count = jsonObject_ingredient.getString("count"); //количество ингредиента
                        JSONObject ingredient_object = jsonObject_ingredient.getJSONObject("ingredient"); //название ингредиента
                        String name = ingredient_object.getString("name");
                        JSONObject SYS_object = jsonObject_ingredient.getJSONObject("system_of_calc"); //система исчисления
                        String sys = SYS_object.getString("name");
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("ingrCount", count);
                        map.put("ingrName", name);
                        map.put("ingrSys", sys);
                        list.add(map);
                    }
                    String likes = jsonObject.getString("likes"); //лайки
                    String dizlikes = jsonObject.getString("dizlikes"); //дизлайки
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("recipeName", title);
                    map.put("recipeCategory", category);
                    map.put("recipeAutor", autor);
                    map.put("recipeImage", img);
                    map.put("recipeTime", time);
                    map.put("recipeLikes", likes);
                    map.put("recipeDizlikes", dizlikes);
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
            if (s == null) {
                //ошибка соединения
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList <HashMap<String, Object>> filteredList = (ArrayList<HashMap<String, Object>>) s.stream()
                    .limit(s.size() -1) //вывожу все элементы, кроме последнего
                    .collect(Collectors.toCollection(ArrayList::new));
            if (s != null && s.size() > 0) {
                HashMap<String, Object> recipe = (HashMap<String, Object>) s.get(s.size()-1); //берём последний элемент
                detailName.setText((String) recipe.get("recipeName"));
                detailDes.setText((String) recipe.get("recipeCategory"));
                detailPrice.setText((String) recipe.get("recipeAutor"));
                detailLikes.setText((String) recipe.get("recipeLikes"));
                detailDizlikes.setText((String) recipe.get("recipeDizlikes"));
                time.setText((String) recipe.get("recipeTime") + " " + getResources().getString(R.string.time_time_minutes_text));
                String stepsArr = ""; //шаги
                for (int i = 0; i < filteredList.size(); i++) {
                    if (filteredList.get(i).get("stepInfo")!=null){
                        stepsArr += i + 1 + ". " + filteredList.get(i).get("stepInfo");
                        if (i < filteredList.size() - 1) {
                            stepsArr += "\n\n";  //добавляем перенос строки, если не последний
                        }
                    }
                }
                steps.setText(stepsArr);
                String ingredientsArr = ""; //ингредиенты
                for (int i = 0; i < filteredList.size(); i++) {
                    if (filteredList.get(i).get("ingrName")!=null){
                        ingredientsArr += filteredList.get(i).get("ingrName") + " " + filteredList.get(i).get("ingrCount") + " " + filteredList.get(i).get("ingrSys");
                        if (i < filteredList.size() - 1) {
                            ingredientsArr += "\n";  //добавляем перенос строки, если не последний
                        }
                    }
                }
                ingredients.setText(ingredientsArr);
                Glide.with(getApplicationContext())
                        .load(recipe.get("recipeImage"))
                        .placeholder(R.drawable.baseline_wallpaper_24)
                        .error(R.drawable.group_23)
                        .into(detailImage);
            }
            Likes.setEnabled(true);
            Dizlikes.setEnabled(true);
        }
    }

    //кнопки
    public class OkHTTPButton extends AsyncTask<Void,Void, ArrayList> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        @Override
        protected ArrayList doInBackground(Void ... voids) { //действия в побочном потоке
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                Request.Builder builder = new Request.Builder(); //построитель запроса
                //запрос на лайк и дизлайк
                Request request_score = builder.url(url + "score/info/" + recipe_id)
                        .header("Authorization", "Bearer " + ((MyApplication) getApplication()).getAccessToken())
                        .get() //тип запроса
                        .build();
                try {
                    //запрос на лайк и дизлайк
                    Response response_score = client.newCall(request_score).execute();
                    JSONObject jsonObject_score = new JSONObject(response_score.body().string()); //сначала объект элементов
                    ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                    String like_score = jsonObject_score.getString("status_like"); //статус лайка у рецепта
                    String dizlike_score = jsonObject_score.getString("status_dizlike"); //статус дизлайка у рецепта
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("scoreLike", like_score);
                    map.put("scoreDizlike", dizlike_score);
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
            if (s == null) {
                //ошибка соединения
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (s != null && s.size() > 0) {
                HashMap<String, Object> recipe = (HashMap<String, Object>) s.get(0);
                String like = (String) recipe.get("scoreLike");
                String dizlike = (String) recipe.get("scoreDizlike");
                if (Objects.equals(like, "1") && Objects.equals(dizlike, "0")){
                    Likes.setImageResource(R.drawable.baseline_favorite_24);
                    Dizlikes.setImageResource(R.drawable.baseline_thumb_down_off_alt_24);

                    Likes.setTag(R.drawable.baseline_favorite_24);
                    Dizlikes.setTag(R.drawable.baseline_thumb_down_off_alt_24);
                }
                else if (Objects.equals(like, "0") && Objects.equals(dizlike, "1")){
                    Likes.setImageResource(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setImageResource(R.drawable.baseline_thumb_down_alt_24);

                    Likes.setTag(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setTag(R.drawable.baseline_thumb_down_alt_24);
                }
                else{
                    Likes.setImageResource(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setImageResource(R.drawable.baseline_thumb_down_off_alt_24);

                    Likes.setTag(R.drawable.baseline_favorite_border_24);
                    Dizlikes.setTag(R.drawable.baseline_thumb_down_off_alt_24);
                }
            }
            Likes.setEnabled(true);
            Dizlikes.setEnabled(true);
        }
    }

    //отправка в бд
    public class OkHTTPLike extends AsyncTask<Void,Void, Response> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        @Override
        protected Response doInBackground(Void ... voids) { //действия в побочном потоке
            Likes.setEnabled(false);
            Dizlikes.setEnabled(false);
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("score", recipe_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
                Request.Builder builder = new Request.Builder(); //построитель запроса
                Request request_like = builder.url(url + "score/like/" + recipe_id)
                        .header("Authorization", "Bearer " + ((MyApplication) getApplication()).getAccessToken())
                        .post(body) //тип запроса
                        .build();
                try {
                    //запрос на лайк и дизлайк
                    Response response_like = client.newCall(request_like).execute();
                    return response_like;
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
                }
            }
            return null; // Достигли максимума попыток, вернули null
        }
        @Override
        protected void onPostExecute(Response response) { //действия после выполнения задач в фоне
            super.onPostExecute(response);
            if (response == null) {
                //ошибка соединения
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                return;
            }
            Likes.setEnabled(true);
            Dizlikes.setEnabled(true);
        }
    }

    public class OkHTTPDizlike extends AsyncTask<Void,Void, Response> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        @Override
        protected Response doInBackground(Void ... voids) { //действия в побочном потоке
            Likes.setEnabled(false);
            Dizlikes.setEnabled(false);
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("score", recipe_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
                Request.Builder builder = new Request.Builder(); //построитель запроса
                Request request_dizlike = builder.url(url + "score/dizlike/" + recipe_id)
                        .header("Authorization", "Bearer " + ((MyApplication) getApplication()).getAccessToken())
                        .post(body) //тип запроса
                        .build();
                try {
                    Response response_dizlike = client.newCall(request_dizlike).execute();
                    return response_dizlike;
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
                }
            }
            return null; // Достигли максимума попыток, вернули null
        }
        @Override
        protected void onPostExecute(Response response) { //действия после выполнения задач в фоне
            super.onPostExecute(response);
            if (response == null) {
                //ошибка соединения
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                return;
            }
            Likes.setEnabled(true);
            Dizlikes.setEnabled(true);
        }
    }

    public class OkHTTPDelete extends AsyncTask<Void,Void, Response> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        @Override
        protected Response doInBackground(Void ... voids) { //действия в побочном потоке
            Likes.setEnabled(false);
            Dizlikes.setEnabled(false);
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("score", recipe_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
                Request.Builder builder = new Request.Builder(); //построитель запроса
                Request request_delete = builder.url(url + "score/no/" + recipe_id)
                        .header("Authorization", "Bearer " + ((MyApplication) getApplication()).getAccessToken())
                        .delete(body) //тип запроса
                        .build();
                try {
                    Response response_delete = client.newCall(request_delete).execute();
                    return response_delete;
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
                }
            }
            return null; // Достигли максимума попыток, вернули null
        }
        @Override
        protected void onPostExecute(Response response) { //действия после выполнения задач в фоне
            super.onPostExecute(response);
            if (response == null) {
                //ошибка соединения
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                return;
            }
            Likes.setEnabled(true);
            Dizlikes.setEnabled(true);
        }
    }
}

