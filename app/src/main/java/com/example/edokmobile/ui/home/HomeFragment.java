package com.example.edokmobile.ui.home;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.edokmobile.DetailedActivity;
import com.example.edokmobile.MyApplication;
import com.example.edokmobile.R;
import com.example.edokmobile.databinding.FragmentHomeBinding;
import com.example.edokmobile.ui.recipes.RecipesFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    protected OkHttpClient client = new OkHttpClient();
    private FragmentHomeBinding binding;
    private ImageView loadingAnimation;
    private RecyclerView topUsers;
    private RecyclerView topRecipes;
    private FrameLayout frameLayout;
    String url;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadingAnimation = binding.loadingAnimation;
        frameLayout = binding.frameEdok;
        topUsers = binding.topUsers; //топ 3 пользователей
        topRecipes = binding.topRecipes; //топ 3 рецептов
        topUsers.setVisibility(View.GONE);
        topRecipes.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        url = ((MyApplication) requireActivity().getApplication()).getGlobalUrl();
        TopRecipes handler_recipes = new TopRecipes();
        handler_recipes.execute();
        TopUsers handler_users = new TopUsers();
        handler_users.execute();
        return root;
    }

    //вывод топ 3 рецептов
    public class TopRecipes extends AsyncTask<Void,Void, ArrayList> { //что подаём на вход, что в середине, что возвращаем
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
            Request.Builder builder = new Request.Builder(); //построитель запроса
            Request request = builder.url(url + "recipe/top/")
                    .get() //тип запроса
                    .build();
            try {
                Response response = client.newCall(request).execute();
                JSONArray jsonArray = new JSONArray(response.body().string());//сначала массив элементов
                ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id"); //id пользователя
                    String title = jsonObject.getString("name"); //имя пользователя
                    String raiting = jsonObject.getString("raiting"); //рейтинг
                    String img = url + jsonObject.getString("face_img"); //картинка
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("topId", id);
                    map.put("topName", title);
                    map.put("topRaiting", raiting);
                    map.put("topImage", img);
                    list.add(map);
                }
                return list;
            } catch (IOException e) {
                Log.e("OkHTTPHandler", "Ошибка сети: " + e.getMessage());
            } catch (JSONException e) {
                Log.e("OkHTTPHandler", "Ошибка JSON: " + e.getMessage());
            } catch (Exception e) {
                Log.e("MyAsyncTask", "Ошибка в doInBackground", e);
                return null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList s) { //действия после выполнения задач в фоне
            super.onPostExecute(s);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            topRecipes.setLayoutManager(layoutManager);
            topRecipes.setAdapter(new MyRecyclerViewAdapter(s));

            //остановка анимации
            loadingAnimation.setVisibility(View.GONE);
            loadingAnimation.clearAnimation();
            frameLayout.setVisibility(View.VISIBLE);
            topRecipes.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onCancelled() {
            //обработка отмены задачи
        }
    }

    //вывод топ 3 пользователей
    public class TopUsers extends AsyncTask<Void,Void, ArrayList> { //что подаём на вход, что в середине, что возвращаем
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
            Request.Builder builder = new Request.Builder(); //построитель запроса
            Request request = builder.url(url + "user/top")
                    .get() //тип запроса
                    .build();
            try {
                Response response = client.newCall(request).execute();
                JSONArray jsonArray = new JSONArray(response.body().string());//сначала массив элементов
                ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id"); //id пользователя
                    String title = jsonObject.getString("name"); //имя пользователя
                    String raiting = jsonObject.getString("raiting"); //рейтинг
                    String img = url + jsonObject.getString("img_avatar"); //картинка
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("topId", id);
                    map.put("topName", title);
                    map.put("topRaiting", raiting);
                    map.put("topImage", img);
                    list.add(map);
                }
                return list;
            } catch (IOException e) {
                Log.e("OkHTTPHandler", "Ошибка сети: " + e.getMessage());
            } catch (JSONException e) {
                Log.e("OkHTTPHandler", "Ошибка JSON: " + e.getMessage());
            } catch (Exception e) {
                Log.e("MyAsyncTask", "Ошибка в doInBackground", e);
                return null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList s) { //действия после выполнения задач в фоне
            super.onPostExecute(s);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            topUsers.setLayoutManager(layoutManager);
            topUsers.setAdapter(new MyRecyclerViewAdapter(s));

            //остановка анимации
            loadingAnimation.setVisibility(View.GONE);
            loadingAnimation.clearAnimation();
            frameLayout.setVisibility(View.VISIBLE);
            topUsers.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onCancelled() {
            //обработка отмены задачи
        }
    }

    //создание своего адаптера
    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

        private ArrayList<HashMap<String, Object>> data;

        public MyRecyclerViewAdapter(ArrayList<HashMap<String, Object>> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.top_items, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            HashMap<String, Object> item = data.get(position);
            holder.textViewName.setText((String) item.get("topName"));
            holder.textViewRaiting.setText("Рейтинг: " + item.get("topRaiting"));
            //обработка изображения:
            Object imageObject = item.get("topImage");
            if (imageObject instanceof String) {
                Glide.with(holder.imageTop.getContext())
                        .load((String) imageObject)
                        .into(holder.imageTop);
            } else {
                holder.imageTop.setImageResource(R.drawable.group_23); // Заполнитель
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            TextView textViewRaiting;
            ImageView imageTop;

            public MyViewHolder(View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewRaiting = itemView.findViewById(R.id.textViewRaiting);
                imageTop = itemView.findViewById(R.id.imageTop);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}