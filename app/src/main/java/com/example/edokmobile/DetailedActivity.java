package com.example.edokmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.edokmobile.ui.recipes.RecipesFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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
    String recipe_id;
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
        Intent intent = getIntent();
        recipe_id = intent.getStringExtra("recipe");
        OkHTTPHandler handler = new OkHTTPHandler();
        handler.execute();
    }

    //ассинхронный поток
    public class OkHTTPHandler extends AsyncTask<Void,Void, ArrayList> { //что подаём на вход, что в середине, что возвращаем
        @Override
        protected ArrayList doInBackground(Void ... voids) { //действия в побочном потоке
            Request.Builder builder = new Request.Builder(); //построитель запроса
            Request request = builder.url("https://fakestoreapi.com/products/" + recipe_id)
                    .get() //тип запроса
                    .build();
            try {
                Response response = client.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());//сначала объект элементов
                ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                String title = jsonObject.getString("title"); //название рецепта
                String description = jsonObject.getString("description"); //описание рецепта
                String price = jsonObject.getString("price"); //цена рецепта
                String img = jsonObject.getString("image"); //картинка
                URL img_url = new URL(img);
                InputStream inputStream = img_url.openStream();
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                BitmapDrawable drawable = new BitmapDrawable(getResources(), image); //преображение bitmap в drawable
                HashMap<String, Object> map = new HashMap<>();
                map.put("recipeName", title);
                map.put("recipeDescription", description);
                map.put("recipePrice", price);
                map.put("recipeImage", drawable);
                list.add(map);
                return list;
            } catch (IOException e) {
                Log.e("OkHTTPHandler", "Ошибка сети: " + e.getMessage());
            } catch (JSONException e) {
                Log.e("OkHTTPHandler", "Ошибка JSON: " + e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList s) { //действия после выполнения задач в фоне
            super.onPostExecute(s);
            //сюда надо передавать значения
            if (s != null && s.size() > 0) {
                HashMap<String, Object> recipe = (HashMap<String, Object>) s.get(0); // Берем первый и единственный элемент
                detailName.setText((String) recipe.get("recipeName"));
                detailDes.setText((String) recipe.get("recipeDescription"));
                detailPrice.setText((String) recipe.get("recipePrice"));
                detailImage.setImageDrawable((BitmapDrawable) recipe.get("recipeImage"));
            }
        }
    }
}