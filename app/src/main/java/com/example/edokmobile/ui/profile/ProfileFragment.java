package com.example.edokmobile.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.edokmobile.LocaleHelper;
import com.example.edokmobile.MyApplication;
import com.example.edokmobile.R;
import com.example.edokmobile.databinding.FragmentProfileBinding;
import com.example.edokmobile.ui.home.HomeFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView name;
    private TextView email;
    private TextView count_recipes;
    private Spinner spinner_lang;
    private TextView raiting;
    private ImageView avatar;
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
    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible = false;
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
            currentLanguageCode = "en"; // Устанавливаем "en" по умолчанию
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
                    initialLanguageName = "English"; // Значение по умолчанию
                    break;
            }
        } else {
            // Обработка случая, когда контекст null
            initialLanguageName = "English"; // Или другое значение по умолчанию
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
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}