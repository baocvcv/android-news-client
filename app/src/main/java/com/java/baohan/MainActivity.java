package com.java.baohan;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.java.baohan.backend.NewsViewModel;
import com.java.baohan.model.News;
import com.java.baohan.ui.main.SectionsPagerAdapter;

import com.java.baohan.FragmentInterface.FragmentInterface1;
import com.java.baohan.FragmentInterface.FragmentInterface2;
import com.java.baohan.FragmentInterface.FragmentInterface3;
import com.java.baohan.FragmentInterface.FragmentInterface4;
import com.java.baohan.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NewsViewModel mNewsViewModel;
    //the following four Interface:
    //fragment1:News
    //fragment2:Epidemic data
    //fragment3:Epidemic map
    //fragment4:Relative scholar
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private FragmentInterface1 fragment1=FragmentInterface1.getInstance();
    private FragmentInterface2 fragment2=new FragmentInterface2();
    private FragmentInterface3 fragment3=new FragmentInterface3();
    private FragmentInterface4 fragment4=new FragmentInterface4();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initView();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);
        fragmentList.add(fragment4);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),fragmentList);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mNewsViewModel.retrieveOldNews();
                    }
                });
                thread.start();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mNewsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mNewsViewModel.updateNews();
            }
        });

        mNewsViewModel.getAllNews().observe(this, new Observer<List<News>>() {
            @Override
            public void onChanged(@Nullable final List<News> news) {
                for (News n : news)
                    System.out.println(n);
                System.out.println("******Currently have " + news.size() + " news!***********");
            }
        });
        t.start();
    }
}