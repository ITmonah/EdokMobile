package com.example.edokmobile.ui.recipes;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.edokmobile.R;
import com.example.edokmobile.databinding.FragmentRecipesBinding;

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

public class RecipesFragment extends Fragment {

    protected OkHttpClient client = new OkHttpClient();
    private FragmentRecipesBinding binding;
    private ListView listView;
    private ImageView loadingAnimation;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textDashboard;
        listView = binding.listView;
        loadingAnimation = binding.loadingAnimation;
        OkHTTPHandler handler = new OkHTTPHandler();
        handler.execute();
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
                    String price = jsonObject.getString("price"); //цена рецепта
                    String img = jsonObject.getString("image"); //картинка
                    URL img_url = new URL(img);
                    InputStream inputStream = img_url.openStream();
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), image); //преображение bitmap в drawable
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("recipeName", title);
                    map.put("recipePrice", price);
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
            String[] from = {"recipeName", "recipePrice","recipeImage"};
            int to[] = {R.id.textName, R.id.textAutor,R.id.imageRecipe};
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext().getApplicationContext(), s, R.layout.list_row_items, from, to);
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
            loadingAnimation.setVisibility(View.GONE);
            // Остановите анимацию
            loadingAnimation.clearAnimation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

