package com.java.baohan.FragmentInterface;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.baohan.R;

import java.util.ArrayList;
import java.util.List;

//Fragment for news
public class FragmentInterface1 extends Fragment {
    private static FragmentInterface1 instance = null;
    private FragmentInterface1() {    }
    public static FragmentInterface1 getInstance() {
        if (instance == null) {
            instance = new FragmentInterface1();
        }
        return instance;
    }

    private ViewPager viewPager;
    private TabLayout tabLayout;
    String[] tvTabs;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private Context mcontext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext = context;//mContext 是成员变量，上下文引用

    }

    public void onDetach() {
        super.onDetach();
        mcontext = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentList.add(new FragmentInterface1_sub());
        fragmentList.add(new FragmentInterface1_sub());
        fragmentList.add(new FragmentInterface1_sub());
        fragmentList.add(new FragmentInterface1_sub());
        fragmentList.add(new FragmentInterface1_sub());
        fragmentList.add(new FragmentInterface1_sub());
        fragmentList.add(new FragmentInterface1_sub());
        fragmentList.add(new FragmentInterface1_sub());
        fragmentList.add(new FragmentInterface1_sub());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_interface1,container,false);

        viewPager=(ViewPager)view.findViewById(R.id.viewPager);

        tabLayout=view.findViewById(R.id.tab_layout);

        viewPager.setAdapter(new MyAdapter(mcontext,getChildFragmentManager(), fragmentList));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        return view;
    }

    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }


    public class MyAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList=new ArrayList<Fragment>();
        private Context mContext;
        private final int[] TAB_TITLES = new int[]{R.string.tab_text1_sub1, R.string.tab_text1_sub2,R.string.tab_text1_sub3
                , R.string.tab_text1_sub4, R.string.tab_text1_sub5,R.string.tab_text1_sub6,
                R.string.tab_text1_sub7, R.string.tab_text1_sub8,R.string.tab_text1_sub9, R.string.tab_text1_sub10};



        public MyAdapter(Context context,FragmentManager fm) {
            super(fm);
            mContext=context;
        }
        public MyAdapter(Context context,FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
            mContext=context;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(TAB_TITLES[position]);
        }


        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }



}
