package com.java.baohan.FragmentInterface.ScholarInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.java.baohan.R;
import com.java.baohan.backend.Scholar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ScholarDetailFragment extends Fragment {

    private Scholar scholar;

    private static Gson gson = new GsonBuilder().create();

//    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    public ScholarDetailFragment() {}

    public ScholarDetailFragment(Scholar scholar) { this.scholar = scholar; }

    public static ScholarDetailFragment newInstance(Scholar scholar) {
        String jsonList = gson.toJson(scholar);
        ScholarDetailFragment f = new ScholarDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("scholar", jsonList);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle b = getArguments();
            scholar = gson.fromJson(b.getString("scholar"), Scholar.class);
        } else {
            scholar = new Scholar();
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.scholar_detail, container, false);
//        ScholarListFragment.parseScholar(root.findViewById(R.id.brief_in_detail), scholar);

        return root;
    }
}
