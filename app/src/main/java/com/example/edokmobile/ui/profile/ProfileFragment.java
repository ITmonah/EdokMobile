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
import com.example.edokmobile.MyApplication;
import com.example.edokmobile.R;
import com.example.edokmobile.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

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
    String url;
    private ArrayList<HashMap<String, Object>> list;
    boolean first_run=true;

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
        String[] langs={"Русский","English","Français"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,langs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_lang.setAdapter(adapter);

//        spinner_lang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (parent.getItemAtPosition(position).toString()){
//                    case "Русский":
//                        changeLoc(getActivity(),"ru");
//                        break;
//                    case "English":
//                        changeLoc(getActivity(),"en");
//                        break;
//                    case "Français":
//                        changeLoc(getActivity(),"fr");
//                        break;
//                }
//            }
//        });
        spinner_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(first_run){
                    first_run=false;
                    return;
                }
                switch (parent.getItemAtPosition(position).toString()){
                    case "Русский":
                        changeLoc(getActivity(),"ru");
                        break;
                    case "English":
                        changeLoc(getActivity(),"en");
                        break;
                    case "Français":
                        changeLoc(getActivity(),"fr");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.group_23) // опционально, пока изображение загружается
                .error(R.drawable.group_23) // опционально, если загрузка изображения не удалась
                .into(avatar);
        return root;
    }

    public void changeLoc(Activity activity, String langCode){
        Locale locale=new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        android.content.res.Configuration conf = resources.getConfiguration();
        conf.locale = new Locale(langCode.toLowerCase());
        resources.updateConfiguration(conf, dm);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_COUNTER, langCode);
        editor.apply();
        activity.finish();
        startActivity(getActivity().getIntent());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}