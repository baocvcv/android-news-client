package com.java.baohan.FragmentInterface;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.java.baohan.backend.NewsViewModel;
import com.java.baohan.model.News;
import com.java.baohan.ui.main.NewsActivity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FragmentInterface1_sub extends Fragment implements Serializable {
    private Context mcontext;
    private List list=new ArrayList<News>();
    private List<News> updateList;
    private RecycleAdapterDome adapterDome;
    private RecyclerView mRecyclerView;
    private NewsViewModel mNewsViewModel;
    private LiveData<List<News>> allNews;
    private FragmentInterface1_sub This=this;
    private String keyWord;
    private int tag=0;


    public FragmentInterface1_sub(String key){
        keyWord=key;
    }

    public final String getKeyWord(){return keyWord;}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        allNews = mNewsViewModel.getAllNews();
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

        mNewsViewModel.updateNews();
        adapterDome.setList(list);
    }

    public void freshData(){
        mNewsViewModel.retrieveOldNews();
        adapterDome.setList(list);
    }

    public void getData(){

        list=mNewsViewModel.searchRecentNews("纽约");
    }


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
        adapterDome = new RecycleAdapterDome(mcontext, allNews.getValue());
        mRecyclerView.setAdapter(adapterDome);


        //update
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                addData();
            }
            @Override
            public void onFresh() {
                freshData();
            }
        });


        allNews.observe(getViewLifecycleOwner(), new Observer<List<News>>() {
            @Override
            public void onChanged(List<News> news) {
                    list = news;
                    if(tag==0) {
                        tag = 1;
                        adapterDome.setList(news.subList(0, 12));
                    }
            }
        });
        return view;
    }

    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }

    class RecycleAdapterDome extends RecyclerView.Adapter<RecycleAdapterDome.MyViewHolder> {
        private Context context;
        private List<News> list;
        private View inflater;

        //构造方法，传入数据
        public RecycleAdapterDome(Context context, List<News> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //创建ViewHolder，返回每一项的布局
            inflater = LayoutInflater.from(context).inflate(R.layout.fragment_interface1_sub, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(inflater);
            return myViewHolder;
        }

        public void addNews(News e)
        {
            list.add(e);
            //notifyDataSetChanged();
            notifyItemInserted(getItemCount());
        }

        public void addNews(List<News> l)
        {
            int s=l.size();
            for(int i=0;i<s;i++) {
                list.add(l.get(i));
                notifyItemInserted(getItemCount());
            }
        }

        private SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        private SimpleDateFormat formatter_year = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            //将数据和控件绑定
            final int MAX_TITLE_LEN = 16;
            News n = list.get(position);
            String title_disp = n.title.length() < MAX_TITLE_LEN ? n.title : n.title.substring(0, MAX_TITLE_LEN);
            holder.title.setText(title_disp);
            holder.date.setText(formatter.format(n.time));
            if(n.isRead) {
                holder.title.setBackgroundColor(Color.rgb(220, 220, 220));
                holder.date.setBackgroundColor(Color.rgb(220, 220, 220));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mNewsViewModel.markRead(n);
                    Intent intent = new Intent(mcontext, NewsActivity.class);
                    intent.putExtra("title", n.title);
                    intent.putExtra("time", formatter_year.format(n.time));
                    intent.putExtra("content",n.content);
                    startActivityForResult(intent, 1);
                    notifyDataSetChanged();
//                    Toast.makeText(context, n.id, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            //返回Item总条数
            if (list == null) {
                return 0;
            }
            return list.size();
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

        public List<News> getList(){return list;}
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
}


