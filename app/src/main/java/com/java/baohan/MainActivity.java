package com.java.baohan;

import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Intent;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.java.baohan.backend.CovidEvent;
import com.java.baohan.backend.KnowledgeGraph;
import com.java.baohan.backend.KnowledgeNode;
import com.java.baohan.backend.NewsViewModel;
import com.java.baohan.backend.PandemicData;
import com.java.baohan.backend.Scholar;
import com.java.baohan.model.News;
import com.java.baohan.ui.main.SectionsPagerAdapter;

import com.java.baohan.FragmentInterface.FragmentInterface1;
import com.java.baohan.FragmentInterface.dataInterface.FragmentInterface2;
import com.java.baohan.FragmentInterface.EntityInterface.FragmentInterface3;
import com.java.baohan.FragmentInterface.ScholarInterface.FragmentInterface4;

import java.util.ArrayList;
import java.util.List;
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
    private FragmentInterface1 fragment1 = FragmentInterface1.getInstance();
    private FragmentInterface2 fragment2 = FragmentInterface2.getInstance();
    private FragmentInterface3 fragment3 = FragmentInterface3.getInstance();
    private FragmentInterface4 fragment4 = FragmentInterface4.getInstance();

    private void initTasks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // update news
                mNewsViewModel.updatePapers();
                mNewsViewModel.updateNews();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // cache pandemic data
                PandemicData.updateDataCache();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.detach(fragment2);
                fragmentTransaction.attach(fragment2);
                fragmentTransaction.commit();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // download scholar info
                Scholar.cacheScholars();
                while (Scholar.executor.getTaskCount() != Scholar.executor.getCompletedTaskCount()) {
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                    }
                }
                fragment4.loadData();
                if (getSupportFragmentManager().getFragments().contains(fragment4)) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.detach(fragment4);
                    fragmentTransaction.attach(fragment4);
                    fragmentTransaction.commit();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create viewmodel
        mNewsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);

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

        app = this.getApplication();

        // load covid event list
        CovidEvent.loadEventList(app);

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
                        //for (News n : result) {
                        //    System.out.println("Search result: " + n);
                        //}
                        //System.out.println("Got " + result.size() + " results");
//                        mNewsViewModel.markRead(result.get(1)); // can mark the news as read with this
                        result = mNewsViewModel.searchRecentNews("疫苗");
                        System.out.println("Search for 疫苗 " + " got " + result.size() + " results");

                        // view search history
                        //System.out.print("Search history: ");
                        //System.out.println(String.join(", ", mNewsViewModel.getSearchHistory()));

                        // get scholars
                        //ConcurrentHashMap<String, Scholar> aliveScholars = Scholar.getAliveScholars();
//                        for (Map.Entry<String, Scholar> entry: aliveScholars.entrySet()) {
//                            Scholar s = entry.getValue();
//                            System.out.println(s.name_zh + ": " + String.join("^", s.affiliation));
//                        }
                        //System.out.println("There are " + aliveScholars.size() + " alive scholars");


                        // request for knowledge graph
                        List<KnowledgeNode> nodes = KnowledgeGraph.search("病毒");
//                        for(KnowledgeNode n: nodes) {
//                            System.out.println(n);
//                        }
                        System.out.println("Search result contains " + nodes.size() + " entries.");


                        // Covid event list
                        List<String> classes = CovidEvent.getClassNames();
                        System.out.println("Class names: " + String.join(", ", classes));
                        List<CovidEvent> class1 = CovidEvent.getEventList(1);
//                        for(CovidEvent e: class1)
//                            System.out.println(e);
                        System.out.println("Class " + classes.get(1) + " has " + class1.size() + " events.");
                        System.out.println("There are " + CovidEvent.eventList.size() + " classes");
                    }

                }).start();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isConnected()) {
            // Required startup tasks for backend
            initTasks();
        } else {
            Toast.makeText(getApplicationContext(), "无网络连接", Toast.LENGTH_LONG).show();
        }
    }



    boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode,resultCode,intent);
        Fragment fragment = FragmentInterface1.getInstance();
        String tmp = "";
        if(intent != null && intent.hasExtra("num"))
            tmp = intent.getStringExtra("num");
//        Toast.makeText(this,tmp+"",Toast.LENGTH_SHORT).show();
        fragment.onActivityResult(requestCode,resultCode,intent);
    }


//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if ( v instanceof EditText) {
//                Rect outRect = new Rect();
//                v.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
//                    Log.d("focus", "touchevent");
//                    v.clearFocus();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event);
//    }
}