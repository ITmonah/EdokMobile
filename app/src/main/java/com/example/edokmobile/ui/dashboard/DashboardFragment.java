package com.example.edokmobile.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.edokmobile.R;
import com.example.edokmobile.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ListView listView;

    // creating  a String type array (fruitNames)
    // which contains names of different fruits' images
    String fruitNames[] = {"Banana", "Grape", "Guava", "Mango", "Orange", "Watermelon"};

    // creating an Integer type array (fruitImageIds) which
    // contains IDs of different fruits' images
    int fruitImageIds[] = {R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_dashboard_black_24dp};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;

        listView = binding.listView;
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < fruitNames.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("fruitName", fruitNames[i]);
            map.put("fruitImage", fruitImageIds[i]);
            list.add(map);
        }
        String[] from = {"fruitName", "fruitImage"};
        int to[] = {R.id.textView, R.id.imageRecipe};
        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext().getApplicationContext(), list, R.layout.list_row_items, from, to);
        listView.setAdapter(simpleAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}