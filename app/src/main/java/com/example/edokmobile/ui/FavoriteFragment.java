package com.example.edokmobile.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.edokmobile.DetailedActivity;
import com.example.edokmobile.LocaleHelper;
import com.example.edokmobile.MyApplication;
import com.example.edokmobile.R;
import com.example.edokmobile.databinding.FragmentFavoriteBinding;
import com.example.edokmobile.databinding.FragmentProfileBinding;
import com.example.edokmobile.databinding.FragmentRecipesBinding;
import com.example.edokmobile.ui.recipes.RecipesFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;
    protected OkHttpClient client = new OkHttpClient();
    private ListView listView;
    private ImageView loadingAnimation;
    String url;
    private boolean isFragmentVisible = false; //флаг, показывающий, виден ли фрагмент
    private FavoriteRecipes favoriteRecipesTask;
    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
        //FavoriteRecipes handler = new FavoriteRecipes();
        //handler.execute();
        //favoriteRecipesTask = new FavoriteRecipes();
        //favoriteRecipesTask.execute();
        // Отменяем задачу, если она еще выполняется
        if (favoriteRecipesTask != null && !favoriteRecipesTask.isCancelled()) {
            favoriteRecipesTask.cancel(true);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listView = binding.listViewFavorite;
        loadingAnimation = binding.loadingAnimation;
        listView.setVisibility(View.GONE);
        url = ((MyApplication) requireActivity().getApplication()).getGlobalUrl();
        //FavoriteRecipes handler = new FavoriteRecipes();
        //handler.execute();
        return root;
    }

    //ассинхронный поток
    public class FavoriteRecipes extends AsyncTask<Void,Void, ArrayList> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        //запуск экрана загрузки
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlphaAnimation animation = new AlphaAnimation(1f, 0f);
            animation.setDuration(750); //длительность анимации в миллисекундах
            animation.setRepeatCount(Animation.INFINITE); //повторять бесконечно
            animation.setRepeatMode(Animation.REVERSE); //переключать между видим и невидимым
            loadingAnimation.startAnimation(animation);
        }
        @Override
        protected ArrayList doInBackground(Void ... voids) { //действия в побочном потоке
            if ( isCancelled()){
                return null;
            }
            int retryCount = 0;
            String lang = LocaleHelper.getSavedLanguage(getContext());
            while (retryCount < MAX_RETRIES) {
                Request.Builder builder = new Request.Builder(); //построитель запроса
                Request request = builder.url(url + "recipe/page/true/favorite?sort=created_at&lang_code="+lang+"&page=1&size=50")
                        .header("Authorization", "Bearer " + ((MyApplication) requireContext().getApplicationContext()).getAccessToken())
                        .get() //тип запроса
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONObject jsonObject_start = new JSONObject(response.body().string());//сначала объект элементов
                    JSONArray jsonArray = jsonObject_start.getJSONArray("items");//массив элементов "items"
                    ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("id"); //id рецепта
                        String title = jsonObject.getString("name"); //название рецепта
                        JSONObject category_object = jsonObject.getJSONObject("category"); //категория рецепта
                        String category = category_object.getString("name");
                        JSONObject autor_object = jsonObject.getJSONObject("user"); //автор рецепта
                        String autor = autor_object.getString("name");
                        String cooking_time = jsonObject.getString("cooking_time"); //время приготовления рецепта
                        String img = url + jsonObject.getString("face_img"); //картинка
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("recipeId", id);
                        map.put("recipeName", title);
                        map.put("recipeAutor", autor);
                        map.put("recipeCategory", getResources().getString(R.string.detailed_category) + " " + category);
                        map.put("recipeCookingTime", cooking_time + "мин.");
                        map.put("recipeImage", img);
                        list.add(map);
                    }
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
            //передача значений
            String[] from = {"recipeName", "recipeCategory", "recipeAutor","recipeCookingTime", "recipeImage"};
            int to[] = {R.id.textName,R.id.textCategory, R.id.detailPrice,R.id.textDataPublish,R.id.imageRecipe};

            //установка собственного адаптера
            SimpleAdapter adapter = new SimpleAdapter(requireContext().getApplicationContext(), s, R.layout.list_row_items, from, to);
            //определение для картинок
            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    //if (view instanceof ImageView && data instanceof BitmapDrawable) {
                    if (view.getId() == R.id.imageRecipe) {
                        String imageUrl = data.toString();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(getContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.group_23)
                                    .error(R.drawable.group_23)
                                    .into((ImageView) view);
                        } else {
                            ((ImageView) view).setImageResource(R.drawable.group_23);
                        }
                        return true;
                    }
                    return false; // Для других View не обрабатываем
                }
            });
            //открытие детального окна
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), DetailedActivity.class);
                    Map<?, ?> itemMap = (Map<?, ?>) adapterView.getItemAtPosition(i);
                    String item = (String) itemMap.get("recipeId");
                    intent.putExtra("recipe", item); //запоминание отдельного рецепта
                    startActivity(intent);
                }
            });

            //установка адаптера
            listView.setAdapter(adapter);
            //остановка анимации
            loadingAnimation.setVisibility(View.GONE);
            loadingAnimation.clearAnimation();
            listView.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onCancelled() {
            //обработка отмены задачи
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void loadData() {
        favoriteRecipesTask = new FavoriteRecipes();
        favoriteRecipesTask.execute();
    }
}