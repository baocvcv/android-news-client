package com.java.baohan.FragmentInterface;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.baohan.R;
import com.java.baohan.backend.NewsViewModel;
import com.java.baohan.ui.main.NewsActivity;
import com.java.baohan.ui.main.TableSetActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

//Fragment for news
public class FragmentInterface1 extends Fragment {
    private static FragmentInterface1 instance = null;
    private NewsViewModel newsViewModel;
    private FragmentInterface1() { }

    public FragmentInterface1(NewsViewModel m) {
        newsViewModel = m;
    }

    public static FragmentInterface1 getInstance(NewsViewModel m) {
        if (instance == null) {
            instance = new FragmentInterface1(m);
        }
        return instance;
    }

    public static FragmentInterface1 getInstance() {
        if (instance == null) {
            return null;
        }
        return instance;
    }

    private ViewPager viewPager;
    private TabLayout tabLayout;
    String[] tvTabs;
    private List<FragmentInterface1_sub> fragmentList = new ArrayList<FragmentInterface1_sub>();
    private ArrayList<String> tableList=new ArrayList<String>();
    private Context mcontext;
    private RelativeLayout setButton;
    private RelativeLayout searchButton;
    private MyAdapter mAdapter;

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
        fragmentList.clear();
        fragmentList.add(new FragmentInterface1_sub("搜索"));
        fragmentList.add(new FragmentInterface1_sub("news"));
        fragmentList.add(new FragmentInterface1_sub("papers"));
        fragmentList.add(new FragmentInterface1_sub("收藏"));
        for(int i=0;i<fragmentList.size();i++)
        {
            tableList.add(fragmentList.get(i).getKeyWord());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_interface1,container,false);

        viewPager=(ViewPager)view.findViewById(R.id.viewPager);
        setButton=view.findViewById(R.id.set_button);
        searchButton=view.findViewById(R.id.search_button_fg1);

        tabLayout=view.findViewById(R.id.tab_layout);
        mAdapter= new MyAdapter(mcontext,getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        setButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                int sizeList=fragmentList.size();
                Intent intent = new Intent(getActivity(), TableSetActivity.class);
                intent.putExtra("num",sizeList+"");

                int i=0;
                for(;i<sizeList;i++){
                    String na="list"+String.valueOf(i);
                    intent.putExtra("list"+i,fragmentList.get(i).getKeyWord());
                }

                startActivityForResult(intent, 1);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent)
    {
        super.onActivityResult(requestCode,resultCode,intent);
        if(resultCode==3) {
            switch (requestCode) {
                case 1:
                    fragmentList.clear();
                    tableList.clear();
                    String tmp = intent.getStringExtra("num");
                    System.out.println(tmp+"-------------------------------------------------------------------");
                    //Toast.makeText(getActivity(),tmp+"",Toast.LENGTH_SHORT).show();
                    int sizeList = Integer.parseInt(tmp);
                    for(int i=0;i<sizeList;i++)
                    {
                        String tabel=intent.getStringExtra("list"+i);
                        tableList.add(tabel);
                        fragmentList.add(new FragmentInterface1_sub(tabel));
                    }

                    mAdapter.notifyDataSetChanged();
                    break;

            }
        }
    }

    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }


    public class MyAdapter extends FragmentPagerAdapter {

        List<FragmentInterface1_sub> fragmentList=new ArrayList<FragmentInterface1_sub>();
        private Context mContext;
        public MyAdapter(Context context,FragmentManager fm) {
            super(fm);
            mContext=context;
        }
        public MyAdapter(Context context,FragmentManager fm, List<FragmentInterface1_sub> fragmentList) {
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
            return tableList.get(position);
        }


        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }



}
