package com.example.edokmobile.ui.recipes;

import static androidx.core.view.VelocityTrackerCompat.clear;
import static java.util.Collections.addAll;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecipesFragment extends Fragment {

    protected OkHttpClient client = new OkHttpClient();
    private FragmentRecipesBinding binding;
    private ListView listView;
    private ImageView loadingAnimation;
    private Spinner spinner;
    private TextView text_view;
    private EditText searchText;
    String url = "https://j41kw20c-8000.euw.devtunnels.ms/recipe/page/true?sort=created_at&page=1&size=50";
    String item;
    boolean isFirstSelection = true; //флаг для отслеживания первого выбора

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listView = binding.listView;
        loadingAnimation = binding.loadingAnimation;
        spinner = binding.spinnerCategory;
        text_view = binding.textView7;
        searchText = binding.searchText;
        spinner.setVisibility(View.GONE);
        text_view.setVisibility(View.GONE);
        OkHTTPHandler handler = new OkHTTPHandler();
        Category_list category_list = new Category_list();
        handler.execute();
        category_list.execute();
        return root;
    }
    //ассинхронный поток
    public class OkHTTPHandler extends AsyncTask<Void,Void,ArrayList> { //что подаём на вход, что в середине, что возвращаем
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
            Request request = builder.url(url)
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
                    String price = jsonObject.getString("cooking_time"); //время приготовления рецепта
                    String img = "https://j41kw20c-8000.euw.devtunnels.ms/" + jsonObject.getString("face_img"); //картинка
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("recipeId", id);
                    map.put("recipeName", title);
                    map.put("recipeCategory", "Категория: " + category);
                    map.put("recipePrice", "Время готовки: " + price);
                    map.put("recipeImage", img);
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
            //передача значений
            String[] from = {"recipeName", "recipeCategory", "recipePrice","recipeImage"};
            int to[] = {R.id.textName,R.id.textCategory, R.id.textAutor,R.id.imageRecipe};

            //установка собственного адаптера
            MySimpleAdapter adapter = new MySimpleAdapter(requireContext().getApplicationContext(), s, R.layout.list_row_items, from, to);

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
            
            //взаимодействие со списком
            AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (isFirstSelection) {
                        isFirstSelection = false;
                        return;
                    }
                        Map<?, ?> itemMap = (Map<?, ?>) parent.getItemAtPosition(position);
                        item = (String) itemMap.get("categoryName");
                        //обработка случая, если ключ не найден или значение не является строкой
                        if (item == null) {
                            Log.e("Spinner", "Ключ не найден в Map, или значение не является строкой.");
                            return;
                        }
                        String text1 = searchText.getText().toString(); //текст из строки поиска
                        String text2 = item; //текст из категории
                        adapter.setFilterParams(text1, "recipeName", text2, "recipeCategory");
                    }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

            //установка слушателя изменения текста
            searchText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) { }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text1 = s.toString(); //текст из строки поиска
                    String text2 = item; //текст из категории
                    if (text2==null || text2 ==""){
                        text2= "Всё";
                    }
                    adapter.setFilterParams(text1, "recipeName", text2, "recipeCategory");
                }
            });

            spinner.setOnItemSelectedListener(itemSelectedListener);

            //установка адаптера
            listView.setAdapter(adapter);

            loadingAnimation.setVisibility(View.GONE);
            //остановка анимации
            loadingAnimation.clearAnimation();
            spinner.setVisibility(View.VISIBLE);
            text_view.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onCancelled() {
            //обработка отмены задачи
        }
    }
    //выпадающий список
    public class Category_list extends AsyncTask<Void,Void,ArrayList> {
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
            //запрос для вывода категорий
            Request.Builder builder_category = new Request.Builder(); //построитель запроса
            Request request_category = builder_category.url("https://j41kw20c-8000.euw.devtunnels.ms/category/")
                    .get() //тип запроса
                    .build();
            try {
                Response response = client.newCall(request_category).execute();
                JSONArray jsonArray = new JSONArray(response.body().string());//сначала массив элементов
                ArrayList<HashMap<String, Object>> list = new ArrayList<>(); //создание листа для значений
                HashMap<String, Object> map = new HashMap<>();
                map.put("categoryName", "Всё");
                list.add(map);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id"); //id категории
                    String title = jsonObject.getString("name"); //название категории
                    map = new HashMap<>();
                    map.put("categoryId", id);
                    map.put("categoryName", title);
                    list.add(map);
                }
                return list;
            } catch (IOException e) {
                Log.e("OkHTTPHandler", "Ошибка сети: " + e.getMessage());
                return new ArrayList<>(); // Возвращаем пустой список при ошибке
            } catch (JSONException e) {
                Log.e("OkHTTPHandler", "Ошибка JSON: " + e.getMessage());
                return new ArrayList<>(); // Возвращаем пустой список при ошибке
            } catch (Exception e) {
                Log.e("MyAsyncTask", "Ошибка в doInBackground", e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList s) {
            super.onPostExecute(s);
            String[] from_category = {"categoryName"};
            int to_category[] = {R.id.spinnerCategory};
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
            loadingAnimation.setVisibility(View.GONE);
            //остановка анимации
            loadingAnimation.clearAnimation();
        }
    }

    //собственный адаптер для рецептов
    public class MySimpleAdapter extends BaseAdapter {
        private List<Map<String, ?>> data;
        private List<Map<String, ?>> originalData;
        private String filterKey1;
        private String filterKey2;
        private String filterValue1;
        private String filterValue2;
        private int resource; // Ваш layout для одной строки
        private String[] from;
        private int[] to;

        public MySimpleAdapter(Context context, List<Map<String, ?>> data, int resource, String[] from, int[] to) {
            this.data = data;
            this.originalData = new ArrayList<>(data);
            this.resource = resource;
            this.from = from;
            this.to = to;
        }

        public void setFilterParams(String value1, String key1, String value2, String key2) {
            filterValue1 = value1;
            filterKey1 = key1;
            filterValue2 = value2;
            filterKey2 = key2;
            filterData();
            notifyDataSetChanged(); // Важно
        }

        private void filterData() {
            List<Map<String, ?>> filteredList = new ArrayList<>();

            if (filterValue1 == null || filterValue1.isEmpty()) {
                if (filterValue2 == null || filterValue2.isEmpty() || filterValue2.equals("Всё")) {
                    //оба фильтра пусты - выводим все
                    filteredList.addAll(originalData);
                } else {
                    //задана ТОЛЬКО категория
                    for (Map<String, ?> item : originalData) {
                        if (item.containsKey(filterKey2) && item.get(filterKey2).toString().toLowerCase().contains(filterValue2.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                }
            } else { //поиск не пустой
                if (filterValue2 == null || filterValue2.isEmpty() || filterValue2.equals("Всё")) {
                    //задан ТОЛЬКО поиск
                    for (Map<String, ?> item : originalData) {
                        if (item.containsKey(filterKey1) && item.get(filterKey1).toString().toLowerCase().contains(filterValue1.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                } else {
                    //заданы оба фильтра
                    for (Map<String, ?> item : originalData) {
                        if (item.containsKey(filterKey1) && item.get(filterKey1).toString().toLowerCase().contains(filterValue1.toLowerCase()) &&
                                item.containsKey(filterKey2) && item.get(filterKey2).toString().toLowerCase().contains(filterValue2.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                }
            }
            data = filteredList;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(resource, null);
            }

            Map<String, ?> item = data.get(position);
            for (int i = 0; i < from.length; i++) {
                int viewId = to[i];
                View itemView = view.findViewById(viewId);
                Object data = item.get(from[i]);

                if (itemView instanceof TextView) {
                    ((TextView) itemView).setText(data != null ? data.toString() : "");
                }
                else if (itemView instanceof ImageView && viewId == R.id.imageRecipe) { //обработка ImageView
                    String imageUrl = data != null ? data.toString() : "";
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(parent.getContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.group_23)
                                .error(R.drawable.group_23)
                                .into((ImageView) itemView);
                    }
                    else {
                        ((ImageView) itemView).setImageResource(R.drawable.group_23);
                    }
                }
                else {
                    //обработка других типов View, если необходимо
                }
            }
            return view;
        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
}

