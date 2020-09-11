package com.java.baohan.FragmentInterface;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RenderNode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.baohan.R;
import com.java.baohan.backend.CovidEvent;
import com.java.baohan.backend.NewsViewModel;
import com.java.baohan.model.News;
import com.java.baohan.ui.main.NewsActivity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FragmentInterface1_sub extends Fragment implements Serializable {
    private Context mcontext;
    private List<News> updateList;
    private RecycleAdapterDome adapterDome;
    private RecyclerView mRecyclerView;
    private NewsViewModel mNewsViewModel;

    private LiveData<List<News>> allNews;
    private List<News> cacheList;
    private List<CovidEvent> cacheEventList;
    private FragmentInterface1_sub This=this;
    private String keyWord;
    private int tag=0;

    public FragmentInterface1_sub() {}

    public FragmentInterface1_sub(String key){
        keyWord=key;
    }

    public FragmentInterface1_sub(LiveData<List<News>> allNews) { this.allNews = allNews; }

    public final String getKeyWord(){return keyWord;}

    public static FragmentInterface1_sub newInstance(String key) {
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        FragmentInterface1_sub f = new FragmentInterface1_sub();
        f.keyWord = key;
        f.setArguments(bundle);
        return f;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            Bundle b = getArguments();
            keyWord = b.getString("key");
        } else {
            keyWord = "";
        }
        mNewsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        switch (keyWord) {
            case "搜索":
                allNews = null;
                break;
            case "news":
                allNews = mNewsViewModel.getAllNews();
                cacheList = allNews.getValue();
                break;
            case "papers":
                allNews = mNewsViewModel.getAllPapers();
                cacheList = allNews.getValue();
                break;
            case "疫苗研发":
                if(CovidEvent.eventList != null)
                    cacheEventList = CovidEvent.getEventList(0);
                break;
            case "病毒研究":
                if(CovidEvent.eventList != null)
                    cacheEventList = CovidEvent.getEventList(1);
                break;
            case "临床治疗":
                if(CovidEvent.eventList != null)
                    cacheEventList = CovidEvent.getEventList(2);
                break;
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext = context;//mContext 是成员变量，上下文引用
    }

    public void onDetach() {
        super.onDetach();
        mcontext = null;
    }

    public void addData() {
        if(keyWord.equals("news")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mNewsViewModel.updateNews();
                }
            }).start();
        } else if (keyWord.equals("papers")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mNewsViewModel.updatePapers();
                }
            }).start();
        }
    }

    public void freshData(){
        if(keyWord.equals("news")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mNewsViewModel.retrieveOldNews();
                }
            }).start();
        } else if (keyWord.equals("papers")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mNewsViewModel.retrieveOldPapers();
                }
            }).start();
        }
    }

    public void search(String query){
        cacheList = mNewsViewModel.searchRecentNews(query);
        adapterDome.setList(cacheList);
        if(cacheList.isEmpty()) {
            Toast.makeText(getActivity(), "未搜索到结果", Toast.LENGTH_LONG).show();
        }
    }


    int curScrollPosition = 0;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //view
        View view=inflater.inflate(R.layout.news_layout,container,false);

        //recyclerView
        RecyclerView mRecyclerView=(RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mcontext,DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mcontext));
        //recyclerView - LinearLayout
        LinearLayoutManager manager = new LinearLayoutManager(mcontext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        //recyclerView - adapter
        adapterDome = new RecycleAdapterDome(mcontext, cacheList, cacheEventList);
        mRecyclerView.setAdapter(adapterDome);

        ConnectivityManager cm = (ConnectivityManager) getActivity() .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnected();

        if (allNews != null) {
            //update
            mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                @Override
                public void onLoadMore() {
                    freshData();
                }
                @Override
                public void onFresh() {
                    addData();
//                    LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//                    //获取最后一个完全显示的itemPosition
//                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
//                    curScrollPosition = manager.findFirstVisibleItemPosition();
                }
                @Override
                public void doSmthing(int pos) {
                    curScrollPosition = pos;
                }
            });

            allNews.observe(getViewLifecycleOwner(), new Observer<List<News>>() {
                @Override
                public void onChanged(List<News> news) {
                    if (!isConnected)
                        cacheList = news.stream().filter(e -> e.isRead).collect(Collectors.toList());
                    else
                        cacheList = news;
                    if (tag == 0) {
                        tag = 1;
                        if (cacheList.size() > 30)
                            adapterDome.setList(cacheList.subList(0, 30));
                        else
                            adapterDome.setList(cacheList);
                    } else {
                        adapterDome.setList(cacheList);
                        manager.scrollToPosition(curScrollPosition);
                    }
                }
            });
        }
        return view;
    }

    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }

    class RecycleAdapterDome extends RecyclerView.Adapter<RecycleAdapterDome.MyViewHolder> {
        private Context context;
        private List<News> list;
        private List<CovidEvent> covidList;
        private View inflater;

        //构造方法，传入数据
        public RecycleAdapterDome(Context context, List<News> list, List<CovidEvent> eventList) {
            this.context = context;
            this.list = list;
            this.covidList = eventList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //创建ViewHolder，返回每一项的布局
            inflater = LayoutInflater.from(context).inflate(R.layout.fragment_interface1_sub, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(inflater);
            return myViewHolder;
        }

//        public void addNews(News e)
//        {
//            list.add(e);
//            //notifyDataSetChanged();
//            notifyItemInserted(getItemCount());
//        }
//
//        public void addNews(List<News> l)
//        {
//            int s=l.size();
//            for(int i=0;i<s;i++) {
//                list.add(l.get(i));
//                notifyItemInserted(getItemCount());
//            }
//        }

        private SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        private SimpleDateFormat formatter_year = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            //将数据和控件绑定
            int MAX_TITLE_LEN = 16;
            if (list != null) {
                News n = list.get(position);
                String title_disp;
                if (n.languageId == 0)
                    MAX_TITLE_LEN *= 2;
                title_disp = n.title.length() < MAX_TITLE_LEN ? n.title : n.title.substring(0, MAX_TITLE_LEN);
                holder.title.setText(title_disp);
                holder.date.setText(formatter.format(n.time));
                if (n.isRead) {
                    holder.title.setBackgroundColor(Color.rgb(220, 220, 220));
                    holder.date.setBackgroundColor(Color.rgb(220, 220, 220));
                }
                holder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(mcontext, NewsActivity.class);
                        intent.putExtra("title", n.title);
                        intent.putExtra("time", formatter_year.format(n.time));
                        intent.putExtra("source", "来自：" + n.source);
                        intent.putExtra("content",n.content);
                        startActivityForResult(intent, 1);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(50);
                                } catch (Exception e) {}
                                mNewsViewModel.markRead(n);
                            }
                        }).start();
//                    Toast.makeText(context, n.id, Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (covidList != null) {
                CovidEvent n = covidList.get(position);
                String title_disp = n.title.length() < MAX_TITLE_LEN ? n.title : n.title.substring(0, MAX_TITLE_LEN);
                holder.title.setText(title_disp);
                holder.date.setText(formatter.format(n.date));
//                if (n.isRead) {
//                    holder.title.setBackgroundColor(Color.rgb(220, 220, 220));
//                    holder.date.setBackgroundColor(Color.rgb(220, 220, 220));
//                }
                holder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(mcontext, NewsActivity.class);
                        intent.putExtra("title", "Event");
                        intent.putExtra("time", formatter_year.format(n.date));
                        intent.putExtra("source", "");
                        intent.putExtra("content", n.title + "\n" + "来自：" + n.url);
                        startActivityForResult(intent, 1);
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            //返回Item总条数
            if (list == null && covidList == null) {
                return 0;
            }
            if (list != null)
                return list.size();
            return covidList.size();
        }

        //内部类，绑定控件
        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView date;

            public MyViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.news_title_brief);
                date = (TextView) itemView.findViewById(R.id.news_date_brief);
            }
        }

        public void setList(List<News> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void setCovidList(List<CovidEvent> list) {
            this.covidList = list;
            notifyDataSetChanged();
        }
        public List<News> getList(){return list;}
        public List<CovidEvent> getCovidList() {return covidList;}
    }

}


abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    //用来标记是否正在向上滑动
    private boolean isSlidingUpward = false;
    private boolean isSlidingDownward = false;
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//        doSmthing(manager.findFirstCompletelyVisibleItemPosition());
        doSmthing(manager.findLastVisibleItemPosition());
        // 当不滑动时
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //获取最后一个完全显示的itemPosition
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int firstItemPosition = manager.findFirstVisibleItemPosition();
            int itemCount = manager.getItemCount();

            // 判断是否滑动到了最后一个item，并且是向上滑动
            if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                //加载更多
                onLoadMore();
            }
            else if(firstItemPosition ==0 && isSlidingDownward){
                onFresh();
            }
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
        isSlidingUpward = dy > 0;
        isSlidingDownward= dy < 0;
    }
    /**
     * 加载更多回调
     */
    public abstract void onFresh();
    public abstract void onLoadMore();
    public abstract void doSmthing(int curPos);
}


