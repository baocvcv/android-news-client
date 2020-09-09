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
import androidx.loader.content.AsyncTaskLoader;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.java.baohan.backend.DataEntry;
import com.java.baohan.backend.KnowledgeGraph;
import com.java.baohan.backend.KnowledgeNode;
import com.java.baohan.backend.NewsViewModel;
import com.java.baohan.backend.PandemicData;
import com.java.baohan.backend.Scholar;
import com.java.baohan.model.News;
import com.java.baohan.ui.main.SectionsPagerAdapter;

import com.java.baohan.FragmentInterface.FragmentInterface1;
import com.java.baohan.FragmentInterface.FragmentInterface2;
import com.java.baohan.FragmentInterface.FragmentInterface3;
import com.java.baohan.FragmentInterface.FragmentInterface4;
import com.java.baohan.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    // ViewModel used to update news and manipulate news database
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

        /*
        Demonstration of NewsViewModel
         */
        // Update news / paper
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Network activity must not be run in the main thread
                // so it is necessary to create a new thread to run update tasks
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        The following must be run in a new thread
                         */
                        // retrieves new news and papers
//                        mNewsViewModel.updateNews(); // updates news
//                        mNewsViewModel.updatePapers(); // updates papers

                        // retrieves old news and papers
//                        mNewsViewModel.retrieveOldNews(); // retrieves old news
//                        mNewsViewModel.retrieveOldPapers(); // retrieves old papers

                        // mark news as read
//                        News n = mNewsViewModel.getAllNews().getValue().get(0);
//                        mNewsViewModel.markRead(n);

                        // search
                        List<News> result = mNewsViewModel.searchRecentNews("新冠");
                        System.out.println("******************search result **************");
                        for (News n : result) {
                            System.out.println("Search result: " + n);
                        }
                        System.out.println("searched : " + result.size());
//                        mNewsViewModel.markRead(result.get(1)); // can mark the news as read with this
                        // view search history
                        System.out.print("Search history: ");
                        System.out.println(String.join(", ", mNewsViewModel.getSearchHistory()));

                        // get scholars
                        ConcurrentHashMap<String, Scholar> aliveScholars = Scholar.getAliveScholars();
                        for (Map.Entry<String, Scholar> entry: aliveScholars.entrySet()) {
                            Scholar s = entry.getValue();
                            System.out.println(s.name_zh + ": " + String.join("^", s.affiliation));
                        }
                        System.out.println(aliveScholars.size());
                    }
                });
                thread.start();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Create viewmodel
        mNewsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        // Observes the change in News data, can be used to update UI elements
        mNewsViewModel.getAllNews().observe(this, new Observer<List<News>>() {
            @Override
            public void onChanged(@Nullable final List<News> news) {
//                for (News n : news)
//                    System.out.println(n);
                System.out.println("News updated! Currently have " + news.size() + " news!");
            }
        });
        mNewsViewModel.getAllPapers().observe(this, new Observer<List<News>>() {
            @Override
            public void onChanged(List<News> news) {
                System.out.println("Currently have " + news.size() + " papers!");
            }
        });

        // Can run this when app starts to update news
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mNewsViewModel.updateNews();
            }
        });
        t.start();

        // search
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<News> result = mNewsViewModel.searchRecentNews("新冠");
                System.out.println("******************search result **************");
                for (News n : result) {
                    System.out.println("Search result: " + n);
                }
                System.out.println("searched : " + result.size());
            }
        });
        t.start();

        // pandemic data interface
        PandemicData pdData = new PandemicData();
//        DataEntry entry = pdData.getCountryData().get("China").get(0); // will crash
//        System.out.println(entry);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<KnowledgeNode> nodes = KnowledgeGraph.search("病毒");
                for(KnowledgeNode n: nodes) {
                    System.out.println(n);
                }

                Scholar.cacheScholars();
            }
        }).start();

    }

    class Pipe {
        boolean taskFinished;
    }
}