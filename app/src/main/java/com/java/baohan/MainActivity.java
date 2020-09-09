package com.java.baohan;

import android.app.Application;
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

import com.java.baohan.backend.CovidEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {

    // ViewModel used to update news and manipulate news database
    private NewsViewModel mNewsViewModel;

    private Application app;

    //the following four Interface:
    //fragment1:News
    //fragment2:Epidemic data
    //fragment3:Epidemic map
    //fragment4:Relative scholar
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private FragmentInterface1 fragment1;
    private FragmentInterface2 fragment2 = FragmentInterface2.getInstance();
    private FragmentInterface3 fragment3 = FragmentInterface3.getInstance();
    private FragmentInterface4 fragment4 = FragmentInterface4.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewmodel
        mNewsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        setContentView(R.layout.activity_main);
        //initView();
        fragment1 = FragmentInterface1.getInstance(mNewsViewModel);

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


        // Required startup tasks for backend
        app = this.getApplication();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // load covid event list
                CovidEvent.loadEventList(app);

                // update news
                mNewsViewModel.updatePapers();
                mNewsViewModel.updateNews();

                // cache pandemic data
                PandemicData.updateDataCache();

                // download scholar info
                Scholar.cacheScholars();

                System.out.println("Finished startup tasks...");
            }
        }).start();


        // interfaces
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Network activity must not be run in the main thread
                // so it is necessary to create a new thread to run update tasks
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // update news and papers
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
                        System.out.println("Searching for 新冠");
                        for (News n : result) {
                            System.out.println("Search result: " + n);
                        }
                        System.out.println("Got " + result.size() + " results");
//                        mNewsViewModel.markRead(result.get(1)); // can mark the news as read with this

                        // view search history
                        System.out.print("Search history: ");
                        System.out.println(String.join(", ", mNewsViewModel.getSearchHistory()));

                        // get scholars
                        ConcurrentHashMap<String, Scholar> aliveScholars = Scholar.getAliveScholars();
//                        for (Map.Entry<String, Scholar> entry: aliveScholars.entrySet()) {
//                            Scholar s = entry.getValue();
//                            System.out.println(s.name_zh + ": " + String.join("^", s.affiliation));
//                        }
                        System.out.println("There are " + aliveScholars.size() + " alive scholars");


                        // request for knowledge graph
                        List<KnowledgeNode> nodes = KnowledgeGraph.search("病毒");
//                        for(KnowledgeNode n: nodes) {
//                            System.out.println(n);
//                        }
                        System.out.println("Search result contains " + nodes.size() + "entries.");


                        // Covid event list
                        List<String> classes = CovidEvent.getClassNames();
                        System.out.println("Class names: " + String.join(", ", classes));
                        List<CovidEvent> class1 = CovidEvent.getEventList(1);
                        for(CovidEvent e: class1)
                            System.out.println(e);
                        System.out.println("Class " + classes.get(1) + " has " + class1.size() + " events.");
                    }
                }).start();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


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


        // pandemic data interface
        PandemicData pdData = new PandemicData();
//        DataEntry entry = pdData.getCountryData().get("China").get(0); // will crash
//        System.out.println(entry);

    }
}