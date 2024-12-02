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
            //сюда надо передавать значения
            String[] from = {"recipeName", "recipeCategory", "recipePrice","recipeImage"};
            int to[] = {R.id.textName,R.id.textCategory, R.id.textAutor,R.id.imageRecipe};
//            SimpleAdapter simpleAdapter = new SimpleAdapter(requireContext().getApplicationContext(), s, R.layout.list_row_items, from, to);
//            //определение для картинок
//            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
//                @Override
//                public boolean setViewValue(View view, Object data, String textRepresentation) {
//                    if (view.getId() == R.id.imageRecipe) {
//                        String imageUrl = data.toString();
//                        if (imageUrl != null && !imageUrl.isEmpty()) {
//                            Glide.with(getContext())
//                                    .load(imageUrl)
//                                    .placeholder(R.drawable.group_23)
//                                    .error(R.drawable.group_23)
//                                    .into((ImageView) view);
//                        } else {
//                            ((ImageView) view).setImageResource(R.drawable.group_23);
//                        }
//                        return true;
//                    }
//                    return false;
//                }
//            });

            //собственный адаптер
            MySimpleAdapter adapter = new MySimpleAdapter(requireContext().getApplicationContext(), s, R.layout.list_row_items, from, to);
            //определение для картинок
            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
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
                    return false;
                }
            });
            listView.setAdapter(adapter);
            //listView.setAdapter(simpleAdapter);
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

            //если в текстовом поле есть текст, выполняем фильтрацию
            if(!searchText.getText().toString().isEmpty())
                adapter.getFilter().filter(searchText.getText().toString());
            //установка слушателя изменения текста
            searchText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) { }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                //при изменении текста выполняем фильтрацию
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s.toString());
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
                    if (position == 0){
                        adapter.getFilter();
                    }
                    else{
                        Map<?, ?> itemMap = (Map<?, ?>) parent.getItemAtPosition(position);
                        String item = (String) itemMap.get("categoryName");
                        //обработка случая, если ключ не найден или значение не является строкой
                        if (item == null) {
                            Log.e("Spinner", "Ключ не найден в Map, или значение не является строкой.");
                            return;
                        }
                        adapter.getFilter().filter(item);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
            spinner.setOnItemSelectedListener(itemSelectedListener);
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

    public class MySimpleAdapter extends SimpleAdapter {
        private List<Map<String, ?>> originalData;
        private List<Map<String, ?>> filteredData;
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.originalData = new ArrayList<>(data);
            this.filteredData = new ArrayList<>(data);
        }
        @Override
        public int getCount() {
            return filteredData.size(); //возвращаем размер отфильтрованного списка
        }

        @Override
        public Object getItem(int position) {
            return filteredData.get(position); //возвращаем элемент из отфильтрованного списка
        }

        @Override
        public long getItemId(int position) {
            return position; //или любой другой подходящий идентификатор
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //используется filteredData
            return super.getView(position, convertView, parent);
        }
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        results.values = originalData; //восстанавливаем исходный список
                        results.count = originalData.size();
                    } else {
                        String filterString = constraint.toString().toLowerCase();
                        List<Map<String, ?>> filteredList = new ArrayList<>();
                        for (Map<String, ?> item : originalData) {
                            boolean matched = false;
                            String[] from = {"recipeName"};
                            for (String key : from) {
                                if (item.containsKey(key)) { //Проверка на существование ключа
                                    String value = item.get(key).toString().toLowerCase();
                                    if (value.contains(filterString)) {
                                        matched = true;
                                        break;
                                    }
                                }
                            }
                            if (matched) {
                                filteredList.add(item);
                            }
                        }
                        results.values = filteredList;
                        results.count = filteredList.size();
                    }
                    return results;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredData = (List<Map<String, ?>>) results.values;
                    notifyDataSetChanged(); //обновляем адаптер
                }
            };
        }
    }
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
}

