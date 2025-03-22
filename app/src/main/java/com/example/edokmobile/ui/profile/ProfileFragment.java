package com.example.edokmobile.ui.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.edokmobile.DetailedActivity;
import com.example.edokmobile.EnterToAppActivity;
import com.example.edokmobile.LocaleHelper;
import com.example.edokmobile.MyApplication;
import com.example.edokmobile.R;
import com.example.edokmobile.databinding.FragmentProfileBinding;
import com.example.edokmobile.ui.home.HomeFragment;
import com.example.edokmobile.ui.recipes.RecipesFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    protected OkHttpClient client = new OkHttpClient();
    private TextView name;
    private TextView email;
    private TextView count_recipes;
    private Spinner spinner_lang;
    private TextView raiting;
    private ImageView avatar;
    private Button button_delete_user;
    private SharedPreferences mSettings;
    public String APP_PREFERENCES_COUNTER = "counter";
    public static final String APP_PREFERENCES = "mysettings";
    private String currentLanguageCode; // Код текущего языка (ru, en, fr)
    private String initialLanguageName;   // Название текущего языка (Русский, English, Français)
    private Context context;
    String url;
    private ArrayList<HashMap<String, Object>> list;
    boolean first_run=true;
    private boolean isFragmentVisible = false; //флаг, показывающий, виден ли фрагмент
    private User_delete userTask;
    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;
        button_delete_user.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
        button_delete_user.setEnabled(false);
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mSettings = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        name = binding.textView77;
        email = binding.textView9;
        count_recipes = binding.textView4;
        raiting = binding.textView5;
        avatar = binding.imageView14;
        button_delete_user = binding.buttonDeleteUser;
        button_delete_user.setEnabled(false);
        url = ((MyApplication) requireActivity().getApplication()).getGlobalUrl();
        list = ((MyApplication) requireActivity().getApplication()).getUserInfo();
        HashMap<String, Object> user = list.get(0);
        name.setText((String) user.get("name"));
        email.setText((String) user.get("email"));
        count_recipes.setText(getResources().getString(R.string.profile_count_recipies) + " " + (String) user.get("count_r"));
        raiting.setText(getResources().getString(R.string.profile_raiting) + " " + (String) user.get("raiting"));
        String url_pic = (String) user.get("image");
        String imageUrl = url + url_pic;

        spinner_lang = binding.spinner;
        currentLanguageCode = LocaleHelper.getSavedLanguage(getActivity().getApplicationContext());

        // 2. Устанавливаем язык по умолчанию, если ничего не сохранено
        if (currentLanguageCode == null || currentLanguageCode.isEmpty()) {
            currentLanguageCode = "ru"; // Устанавливаем "en" по умолчанию
        }
        context = getContext(); // Получаем контекст

        if (context != null) { // Убеждаемся, что контекст не null
            currentLanguageCode = LocaleHelper.getSavedLanguage(context);
            // Преобразуем код языка в имя
            switch (currentLanguageCode) {
                case "ru":
                    initialLanguageName = "Русский";
                    break;
                case "en":
                    initialLanguageName = "English";
                    break;
                case "fr":
                    initialLanguageName = "Français";
                    break;
                default:
                    initialLanguageName = "Русский"; // Значение по умолчанию
                    break;
            }
        } else {
            // Обработка случая, когда контекст null
            initialLanguageName = "Русский"; // Или другое значение по умолчанию
        }

        String[] langs={"Русский","English","Français"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,langs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_lang.setAdapter(adapter);

        int initialPosition = -1;
        for (int i = 0; i < langs.length; i++) {
            if (langs[i].equals(initialLanguageName)) {
                initialPosition = i;
                break;
            }
        }
        // Выбираем начальный язык (если он найден)
        if (initialPosition >= 0) {
            spinner_lang.setSelection(initialPosition, false); // false чтобы избежать вызова onItemSelected
        }

        spinner_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (first_run) {
                    first_run = false;
                    return;
                }
                String selectedLanguageName = parent.getItemAtPosition(position).toString();
                String selectedLanguageCode;
                switch (selectedLanguageName) {
                    case "Русский":
                        selectedLanguageCode = "ru";
                        break;
                    case "English":
                        selectedLanguageCode = "en";
                        break;
                    case "Français":
                        selectedLanguageCode = "fr";
                        break;
                    default:
                        selectedLanguageCode = "en";
                        break;
                }
                LocaleHelper.changeLoc(getActivity(), selectedLanguageCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не выбрано
            }
        });

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.group_23) // опционально, пока изображение загружается
                .error(R.drawable.group_23) // опционально, если загрузка изображения не удалась
                .into(avatar);

        button_delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.profile_delete_account_question);
                //кнопка "да"
                builder.setPositiveButton(R.string.profile_delete_account_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadData();
                    }
                });
                //кнопка "нет"
                builder.setNegativeButton(R.string.profile_delete_account_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }

    public class User_delete extends AsyncTask<Void,Void, Response> { //что подаём на вход, что в середине, что возвращаем
        private static final int MAX_RETRIES = 3;  // Максимальное количество попыток
        private static final int INITIAL_DELAY = 1000; // Начальная задержка (1 секунда)
        @Override
        protected Response doInBackground(Void ... voids) { //действия в побочном потоке
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                Request.Builder builder = new Request.Builder(); //построитель запроса
                Request request_delete = builder.url(url + "user/my_profile_delete")
                        .header("Authorization", "Bearer " + ((MyApplication) requireContext().getApplicationContext()).getAccessToken())
                        .delete() //тип запроса
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
                Toast.makeText(requireContext().getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                return;
            }
            if (response.isSuccessful()) {
                Toast toast_acc = Toast.makeText(requireContext().getApplicationContext(), getResources().getString(R.string.profile_delete_account_successfully), Toast.LENGTH_LONG);
                toast_acc.show();
                Intent intent = new Intent(getActivity().getApplicationContext(), EnterToAppActivity.class);
                startActivity(intent);
            }
            else {
                Toast toast_acc = Toast.makeText(requireContext().getApplicationContext(), getResources().getString(R.string.not_profile_delete_account_error), Toast.LENGTH_LONG);
                toast_acc.show();
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void loadData() {
        userTask = new User_delete();
        userTask.execute();
    }
}