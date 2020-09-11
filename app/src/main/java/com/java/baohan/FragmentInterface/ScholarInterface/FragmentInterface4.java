package com.java.baohan.FragmentInterface.ScholarInterface;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
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

    private FragmentInterface4() {}

    public static FragmentInterface4 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FragmentInterface4();
        }
        return INSTANCE;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Scholar> livingScholars = Scholar.getAliveScholars().entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().numViewed - e1.getValue().numViewed)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        fragments.add(ScholarListFragment.newInstance(livingScholars));

        List<Scholar> deadScholars = Scholar.getDeadScholars().entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().numViewed - e1.getValue().numViewed)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        fragments.add(ScholarListFragment.newInstance(deadScholars));
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scholars, container, false);
        TabLayout tab = root.findViewById(R.id.tab_scholars);
        ViewPager pager = (ViewPager) root.findViewById(R.id.pager_scholars);
        ScholarFragmentAdapter adapter = new ScholarFragmentAdapter(this.getActivity().getSupportFragmentManager(), getContext(), fragments);
        pager.setAdapter(adapter);
        tab.setupWithViewPager(pager);
        for(int i = 0; i < titles.length; i++)
            tab.getTabAt(i).setText(titles[i]);

//        root.findViewById(R.id.scholar_loading_panel).setVisibility(View.GONE);

        return root;
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
