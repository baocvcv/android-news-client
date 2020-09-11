package com.java.baohan.FragmentInterface;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.baohan.R;
import com.java.baohan.backend.CovidEvent;
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

    public FragmentInterface1() { }

    public static FragmentInterface1 getInstance() {
        if (instance == null) {
            instance = new FragmentInterface1();
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
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        fragmentList.clear();
        fragmentList.add(FragmentInterface1_sub.newInstance("搜索"));
        fragmentList.add(FragmentInterface1_sub.newInstance("news"));
        fragmentList.add(FragmentInterface1_sub.newInstance("papers"));
        fragmentList.add(FragmentInterface1_sub.newInstance("疫苗研发"));
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

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(40, 2, 2, 40);

        View hll = view.findViewById(R.id.history_list_layout);
        LinearLayout searchHistoryLayout = view.findViewById(R.id.history_list);
        EditText searchInput = view.findViewById(R.id.search_text_fg1);
        searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    List<String> history = newsViewModel.getSearchHistory();
                    searchHistoryLayout.removeAllViews();
                    for(int i = 0; i < 10 && i < history.size(); i++) {
                        TextView tv = new TextView(view.getContext());
                        tv.setText(history.get(i));
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        tv.setPadding(2, 2 , 2, 2);
                        tv.setBackgroundColor(Color.rgb(212,232,246));
                        tv.setLayoutParams(p);
                        searchHistoryLayout.addView(tv);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String s = tv.getText().toString();
                                fragmentList.get(0).search(s);
                                searchInput.setText(s);
                                searchInput.clearFocus();
                                hll.setVisibility(View.GONE);
                                ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                                viewPager.setCurrentItem(0);
                                //TODO
                            }
                        });
                    }
                    hll.setVisibility(View.VISIBLE);
                } else {
                    hll.setVisibility(View.GONE);
                }
            }
        });

        searchButton=view.findViewById(R.id.search_button_fg1);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = searchInput.getText().toString().trim();
                if(!s.isEmpty()) {
                    fragmentList.get(0).search(s);
                    searchInput.clearFocus();
                    ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                    viewPager.setCurrentItem(0);
                    //TODO
                }
            }
        });

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
                intent.putExtra("num",(sizeList-1)+"");

                for(int i = 1;i<sizeList;i++){
                    String na="list"+String.valueOf(i);
                    intent.putExtra("list"+(i-1),fragmentList.get(i).getKeyWord());
                }

                startActivityForResult(intent, 1);
            }
        });

        Button btnCancel = view.findViewById(R.id.btn_cancel_search);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput.clearFocus();
                ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        if(fragmentList.size() > 1)
            viewPager.setCurrentItem(1);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent)
    {
        super.onActivityResult(requestCode,resultCode,intent);
        if(resultCode==3) {
            switch (requestCode) {
                case 1:
                    while(fragmentList.size() > 1){
                        fragmentList.remove(1);
                        tableList.remove(1);
                    }
                    for(FragmentInterface1_sub f: fragmentList) {
                        System.out.println(f.getKeyWord());
                    }
                    String tmp = intent.getStringExtra("num");
//                    System.out.println(tmp+"-------------------------------------------------------------------");
                    //Toast.makeText(getActivity(),tmp+"",Toast.LENGTH_SHORT).show();
                    int sizeList = Integer.parseInt(tmp);
                    for(int i=0;i<sizeList;i++)
                    {
                        String tabel=intent.getStringExtra("list"+i);
                        tableList.add(tabel);
                        fragmentList.add(FragmentInterface1_sub.newInstance(tabel));
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
