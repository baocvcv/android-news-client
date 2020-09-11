package com.java.baohan.FragmentInterface.ScholarInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.java.baohan.R;
import com.java.baohan.backend.Scholar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ScholarListFragment extends Fragment {

    private List<Scholar> scholars;

    private static Gson gson = new GsonBuilder().create();

    private static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private LinearLayout scholar_list;
    private ScrollView detail_scroll;
    private ScrollView list_scroll;

    private ScrollView scrollView;

    private int scrollY;

    public static ScholarListFragment newInstance(List<Scholar> scholars) {
        String jsonList = gson.toJson(scholars);
        ScholarListFragment f = new ScholarListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("scholars", jsonList);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle b = getArguments();
            scholars = gson.fromJson(b.getString("scholars"), new TypeToken<ArrayList<Scholar>>(){}.getType());
        } else {
            scholars = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.scholar_list, container, false);
        detail_scroll = root.findViewById(R.id.scholar_detail_scroll);
        list_scroll = root.findViewById(R.id.scholar_list_scroll);

        scholar_list = root.findViewById(R.id.scholar_list);
        scholar_list.removeAllViews();
        for(Scholar s: scholars) {
            View v = inflater.inflate(R.layout.scholar_brief, container, false);
            parseScholar(v, s);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    setScrollY();
                    View disp = root.findViewById(R.id.scholar_detail_display);
                    parseDetail(disp, s);
//                    disp.setVisibility(View.VISIBLE);
                    list_scroll.setVisibility(View.GONE);
                    detail_scroll.setVisibility(View.VISIBLE);
                    detail_scroll.scrollTo(0, detail_scroll.getTop());
                }
            });
            scholar_list.addView(v);
        }
        return root;
    }

    private void setScrollY() {
        scrollY = scrollView.getScrollY();
        System.out.println("ScrollY = " + scrollY);
    }

    private void restoreScrollY() {
        scrollView.scrollTo(0, scrollY);
        System.out.println("Set scrollview to " + scrollY);
    }

    private void parseScholar(View root, Scholar s) {
//        View root = inflater.inflate(R.layout.scholar_brief, container, false);
        ImageView avatar = root.findViewById(R.id.scholar_avatar);
        if(s.hasAvatar) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    while(Scholar.executor.getCompletedTaskCount() != Scholar.executor.getTaskCount()) {
                        try {
                            Thread.sleep(5);
                        } catch (Exception e) {}
                    }
                    avatar.setImageBitmap(s.avatar);
                }
            });
        } else {
            avatar.setVisibility(View.GONE);
        }

        TextView name = root.findViewById(R.id.scholar_name);
        if(s.name_zh != null && !s.name_zh.isEmpty())
            name.setText(s.name_zh);
        else
            name.setText(s.name_en);


        TextView position = root.findViewById(R.id.scholar_position);
        position.setText(String.join("、", s.position));

        TextView affi = root.findViewById(R.id.scholar_affiliation);
        affi.setText(String.join("/", s.affiliation));

        // indices
        TextView index = root.findViewById(R.id.scholar_hindex);
        index.setText(String.format("%d", (int)s.hindex));
        index = root.findViewById(R.id.scholar_citation);
        index.setText(String.format("%d", s.citations));
        index = root.findViewById(R.id.scholar_activity);
        index.setText(String.format("%.1f", s.activity));
        index = root.findViewById(R.id.scholar_social);
        index.setText(String.format("%.1f", s.sociability));
        index = root.findViewById(R.id.scholar_publication);
        index.setText(String.format("%d", s.publications));

//        return root;
    }

    private void parseDetail(View root, Scholar s) {
        View brief = root.findViewById(R.id.brief_in_detail);
        parseScholar(brief, s);

        if(s.bio != null) {
            TextView bio = root.findViewById(R.id.scholar_bio);
            bio.setText(s.bio);
        } else {
            root.findViewById(R.id.scholar_bio_layout).setVisibility(View.GONE);
        }

        if(s.work != null) {
            TextView bio = root.findViewById(R.id.scholar_work);
            bio.setText(s.work);
        } else {
            root.findViewById(R.id.scholar_work_layout).setVisibility(View.GONE);
        }

        if(s.edu != null) {
            TextView education = root.findViewById(R.id.scholar_edu);
            education.setText(s.edu);
        } else {
            root.findViewById(R.id.scholar_edu_layout).setVisibility(View.GONE);
        }

        if(s.homepage != null) {
            TextView home = root.findViewById(R.id.scholar_home);
            home.setText(s.homepage);
        } else {
            root.findViewById(R.id.scholar_home_layout).setVisibility(View.GONE);
        }

        Button btnBack = root.findViewById(R.id.back_to_scholar_list);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                root.setVisibility(View.GONE);
                detail_scroll.setVisibility(View.GONE);
                list_scroll.setVisibility(View.VISIBLE);
//                restoreScrollY();
            }
        });
    }
}
