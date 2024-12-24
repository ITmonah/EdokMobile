package com.example.edokmobile.ui.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView name;
    private TextView email;
    private ImageView avatar;
    private ArrayList<HashMap<String, Object>> list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        name = binding.textView77;
        email = binding.textView9;
        avatar = binding.imageView14;
        list = ((MyApplication) requireActivity().getApplication()).getUserInfo();
        HashMap<String, Object> user = list.get(0);
        name.setText((String) user.get("name"));
        email.setText((String) user.get("email"));
//        String imageUrl = account.getPhotoUrl().toString();
//
//        Glide.with(this)
//                .load(imageUrl)
//                .placeholder(R.drawable.group_23) // опционально, пока изображение загружается
//                .error(R.drawable.group_23) // опционально, если загрузка изображения не удалась
//                .into(avatar);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}