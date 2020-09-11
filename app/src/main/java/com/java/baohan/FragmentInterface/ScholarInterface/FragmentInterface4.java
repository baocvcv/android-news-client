package com.java.baohan.FragmentInterface.ScholarInterface;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.baohan.FragmentInterface.dataInterface.ChartFragmentAdapter;
import com.java.baohan.R;
import com.java.baohan.backend.Scholar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Fragment for relative scholar
public class FragmentInterface4 extends Fragment {

    private static FragmentInterface4 INSTANCE = null;

    ArrayList<Fragment> fragments = new ArrayList<>();

    private static final String[] titles = new String[]{"高关注学者", "追忆学者"};

    public static FragmentInterface4 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FragmentInterface4();
        }
        return INSTANCE;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scholars, container, false);
        if(fragments.isEmpty()) {
            return root;
        }

        TabLayout tab = root.findViewById(R.id.tab_scholars);
        ViewPager pager = (ViewPager) root.findViewById(R.id.pager_scholars);
        ScholarFragmentAdapter adapter = new ScholarFragmentAdapter(getChildFragmentManager(), getContext(), fragments);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
        for(int i = 0; i < titles.length; i++)
            tab.getTabAt(i).setText(titles[i]);

        root.findViewById(R.id.scholar_loading_panel).setVisibility(View.GONE);
        return root;
    }

    public void loadData() {
        fragments.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List < Scholar > deadScholars = Scholar.getDeadScholars().entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().numViewed - e1.getValue().numViewed)
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());
                fragments.add(ScholarListFragment.newInstance(deadScholars));
            }
        }).start();

        List<Scholar> livingScholars = Scholar.getAliveScholars().entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().numViewed - e1.getValue().numViewed)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        fragments.add(0, ScholarListFragment.newInstance(livingScholars));
    }

    class ScholarFragmentAdapter extends FragmentPagerAdapter {

        Context context;
        List<Fragment> fragments;

        public ScholarFragmentAdapter(FragmentManager fm, Context context, List<Fragment> fragments) {
            super(fm);
            this.context = context;
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

    }
}
