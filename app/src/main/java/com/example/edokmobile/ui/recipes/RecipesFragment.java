package com.example.edokmobile.ui.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.edokmobile.DetailedActivity;
import com.example.edokmobile.MainActivity;
import com.example.edokmobile.MyApplication;
import com.example.edokmobile.R;
import com.example.edokmobile.databinding.FragmentRecipesBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipesFragment extends Fragment {

    protected OkHttpClient client  = new OkHttpClient();
    private FragmentRecipesBinding binding;
    private ListView listView;
    private ImageView loadingAnimation;
    private Spinner spinner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textDashboard;
        listView = binding.listView;
        loadingAnimation = binding.loadingAnimation;
        OkHTTPHandler handler = new OkHTTPHandler();
        //OkHTTPHandler_2 handler2 = new OkHTTPHandler_2();
        OkHTTPHandler_3 handler3 = new OkHTTPHandler_3();
        //handler2.execute();
        handler.execute();
        handler3.execute();
        return root;
    }
    //ассинхронный поток
    public class OkHTTPHandler extends AsyncTask<Void,Void,ArrayList> { //что подаём на вход, что в середине, что возвращаем

        //запуск экрана загрузки
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loadingAnimation.setVisibility(View.VISIBLE);
            AlphaAnimation animation = new AlphaAnimation(1f, 0f);
            animation.setDuration(750); //длительность анимации в миллисекундах
            animation.setRepeatCount(Animation.INFINITE); //повторять бесконечно
            animation.setRepeatMode(Animation.REVERSE); //переключать между видим и невидимым
            loadingAnimation.startAnimation(animation);
            //loadingAnimation.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.load_animation));
        }

        @Override
        protected ArrayList doInBackground(Void ... voids) { //действия в побочном потоке
            Request.Builder builder = new Request.Builder(); //построитель запроса
            Request request = builder.url("https://fakestoreapi.com/products")
                    .get() //тип запроса
                    .build();
            try {
                Response response = client.newCall(request).execute();
                JSONArray jsonArray = new JSONArray(response.body().string());//сначала массив элементов
                ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String title = jsonObject.getString("title"); //название рецепта
                    String category = jsonObject.getString("category"); //категория рецепта
                    String price = jsonObject.getString("price"); //цена рецепта
                    String img = jsonObject.getString("image"); //картинка
                    URL img_url = new URL(img);
                    InputStream inputStream = img_url.openStream();
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), image); //преображение bitmap в drawable
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("recipeName", title);
                    map.put("recipeCategory", "Категория: " + category);
                    map.put("recipePrice", "Цена: " + price);
                    map.put("recipeImage", drawable);
                    list.add(map);
                }
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
            String[] from = {"recipeName", "recipeCategory", "recipePrice","recipeImage"};
            int to[] = {R.id.textName,R.id.textCategory, R.id.textAutor,R.id.imageRecipe};
            SimpleAdapter simpleAdapter = new SimpleAdapter(requireContext().getApplicationContext(), s, R.layout.list_row_items, from, to);
            //определение, как SimpleAdapter должен устанавливать Drawable в ImageView
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view instanceof ImageView && data instanceof BitmapDrawable) {
                        ((ImageView) view).setImageDrawable((BitmapDrawable) data);
                        return true;
                    }
                    return false;
                }
            });
            listView.setAdapter(simpleAdapter);
            //открытие детального окна
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getContext(), "Подробнее", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity().getApplicationContext(), DetailedActivity.class);
                    String name = "Название рецепта";
                    intent.putExtra("name", name );
                    startActivity(intent);
                }
            });
            loadingAnimation.setVisibility(View.GONE);
            //остановка анимации
            loadingAnimation.clearAnimation();
        }
    }
    //ассинхронный поток 2
    public class OkHTTPHandler_2 extends AsyncTask<Void,Void,ArrayList> {
        //запуск экрана загрузки
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loadingAnimation.setVisibility(View.VISIBLE);
            AlphaAnimation animation = new AlphaAnimation(1f, 0f);
            animation.setDuration(750); //длительность анимации в миллисекундах
            animation.setRepeatCount(Animation.INFINITE); //повторять бесконечно
            animation.setRepeatMode(Animation.REVERSE); //переключать между видим и невидимым
            loadingAnimation.startAnimation(animation);
            //loadingAnimation.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.load_animation));
        }
        @Override
        protected ArrayList doInBackground(Void ... voids) { //действия в побочном потоке
            //запрос для вывода категорий
            Request.Builder builder_category = new Request.Builder(); //построитель запроса
            Request request_category = builder_category.url("http://127.0.0.1:8000/category/")
                    .get() //тип запроса
                    .build();
            try {
                Response response = client.newCall(request_category).execute();
                JSONArray jsonArray = new JSONArray(response.body().string());//сначала массив элементов
                ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject_category = jsonArray.getJSONObject(i);
                    String nameCategory = jsonObject_category.getString("name"); //название категории
                    HashMap<String, Object> map_category = new HashMap<>();
                    map_category.put("nameCategory", nameCategory);
                    list.add(map_category);
                }
                return list;
            } catch (IOException e) {
                Log.e("OkHTTPHandler", "Ошибка сети: " + e.getMessage());
                return new ArrayList<>(); // Возвращаем пустой список при ошибке
            } catch (JSONException e) {
                Log.e("OkHTTPHandler", "Ошибка JSON: " + e.getMessage());
                return new ArrayList<>(); // Возвращаем пустой список при ошибке
            }
        }
        @Override
        protected void onPostExecute(ArrayList s) { //действия после выполнения задач в фоне
            super.onPostExecute(s);
            String[] from_category = {"nameCategory"};
            int to_category[] = {R.id.spinnerCategory};
            //перенос значений к выпадающему списку
            spinner = binding.spinnerCategory;
            // Проверка на пустой список (необязательно, но рекомендуется)
            if (s.isEmpty()) {
                Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                loadingAnimation.setVisibility(View.GONE);
                // Остановите анимацию
                loadingAnimation.clearAnimation();
                return; // Предотвращаем дальнейшее выполнение, если список пуст
            }
            SimpleAdapter simpleAdapter_category = new SimpleAdapter(getContext().getApplicationContext(), s, android.R.layout.simple_spinner_item, from_category, to_category);
            simpleAdapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //визуализация списка
            spinner.setAdapter(simpleAdapter_category); //применяем адаптер к элементу spinner
            loadingAnimation.setVisibility(View.GONE);
            // Остановите анимацию
            loadingAnimation.clearAnimation();
        }
    }

    //ассинхронный поток 2
    public class OkHTTPHandler_3 extends AsyncTask<Void,Void,ArrayList> {
        @Override
        protected ArrayList doInBackground(Void ... voids) { //действия в побочном потоке
            //запрос для вывода категорий
            Request.Builder builder_category = new Request.Builder(); //построитель запроса
            Request request_category = builder_category.url("https://fakestoreapi.com/products/categories")
                    .get() //тип запроса
                    .build();
            try {
                Response response = client.newCall(request_category).execute();
                JSONArray jsonArray = new JSONArray(response.body().string());//сначала массив элементов
                ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                for (int i = 0; i < jsonArray.length(); i++) {
                    String nameCategory = jsonArray.getString(i); //название категории
                    HashMap<String, Object> map_category = new HashMap<>();
                    map_category.put("nameCategory", nameCategory);
                    list.add(map_category);
                }
                return list;
            } catch (IOException e) {
                Log.e("OkHTTPHandler", "Ошибка сети: " + e.getMessage());
                return new ArrayList<>(); // Возвращаем пустой список при ошибке
            } catch (JSONException e) {
                Log.e("OkHTTPHandler", "Ошибка JSON: " + e.getMessage());
                return new ArrayList<>(); // Возвращаем пустой список при ошибке
            }
        }
        @Override
        protected void onPostExecute(ArrayList s) {
            super.onPostExecute(s);
            String[] from_category = {"nameCategory"};
            int to_category[] = {R.id.spinnerCategory};
            spinner = binding.spinnerCategory;
            if (s.isEmpty()) {
                Toast.makeText(getContext(), "Нет данных", Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleAdapter simpleAdapter_category = new SimpleAdapter(requireContext().getApplicationContext(), s, R.layout.spinner_item, from_category, to_category);
            simpleAdapter_category.setDropDownViewResource(R.layout.spinner_item);
            if (spinner == null) {
                Toast.makeText(getContext(), "Список пуст", Toast.LENGTH_SHORT).show();
                return;
            }
            spinner.setAdapter(simpleAdapter_category);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

