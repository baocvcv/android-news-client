package com.java.baohan.FragmentInterface;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.baohan.R;
import com.java.baohan.backend.NewsViewModel;
import com.java.baohan.model.News;

import java.util.ArrayList;
import java.util.List;

public class FragmentInterface1_sub extends Fragment {
    private Context mcontext;
    private List list;
    private List<News> updateList;
    private RecycleAdapterDome adapterDome;
    private RecyclerView mRecyclerView;
    private NewsViewModel mNewsViewModel;
    private LiveData<List<News>> allNews;
    private FragmentInterface1_sub This=this;


    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext = context;//mContext 是成员变量，上下文引用
    }

    public void onDetach() {
        super.onDetach();
        mcontext = null;
    }

    public void updatedNews(int command){
        int x=list.size();
        //command:1-add new;2-add used;3-delete
    }

    public void addDataAt(int position, String data) {
        List<News> updateList=new ArrayList<News>();

        NetThread mNetThread=new NetThread();
        mNetThread.start();
        try{
            mNetThread.join();
            if(updateList.size()>0)
                list.add(updateList.get(0).title);
            else
                list.add(data);
            adapterDome.notifyItemInserted(position);}
        catch (InterruptedException e){}


    }

    public class NetThread extends Thread implements Runnable{
        private int com;
        public void run() {
            mNewsViewModel.updateNews();
            updateList = mNewsViewModel.searchRecentNews("新冠");
        }
    }

    public void onCreated()
    {
        mNewsViewModel=new ViewModelProvider(this).get(NewsViewModel.class);;
        allNews = mNewsViewModel.getAllNews();
    }



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.news_layout,container,false);

        RecyclerView mRecyclerView=(RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mcontext,DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mcontext));//这里用线性显示 类似于listview


        list=new ArrayList<String>();
        list.add("震惊！美国疫情失控或引发局部冲突？");
        list.add("齐心抗新冠，众志可成城");
        list.add("卫生部：深入贯彻落实中央有关新冠疫情的八项指示");
        list.add("震惊！美国疫情失控或引发局部冲突？");
        adapterDome = new RecycleAdapterDome(mcontext,list);
        mRecyclerView.setAdapter(adapterDome);
        LinearLayoutManager manager = new LinearLayoutManager(mcontext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        FloatingActionButton button=view.findViewById(R.id.fab1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDataAt(list.size(),"hi"+list.size());
            }
        });



        return view;
    }

    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mNewsViewModel=new ViewModelProvider(This).get(NewsViewModel.class);;
                allNews = mNewsViewModel.getAllNews();
                allNews.observe(This.getViewLifecycleOwner(), new Observer<List<News>>() {
                    @Override
                    public void onChanged(List<News> news) {

                    }
                });

            }
        });

    }


}

class RecycleAdapterDome extends RecyclerView.Adapter<RecycleAdapterDome.MyViewHolder> {
    private Context context;
    private List<String> list;
    private View inflater;

    //构造方法，传入数据
    public RecycleAdapterDome(Context context, List<String> list) {
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

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //将数据和控件绑定
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        //返回Item总条数
        return list.size();
    }

    //内部类，绑定控件
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view);
        }
    }
/*
    public class DataAdapter extends RecyclerView.Adapter {
        Context context;
        List<DataBean.Cls> list;
        ItemClickListem itemClickListem;
        FooterViewHolder footerViewHolder;
        public MORE_STATUS status = LOADED;

        public DataAdapter(Context context, List<DataBean.Cls > list){
            this.context =context;
            this.list=list;

        }

        public void setItemClickListem(ItemClickListem itemClickListem) {
            this.itemClickListem = itemClickListem;
        }
        public void setStatus(MORE_STATUS status){

            this.status=status;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (i==1){
                View view= LayoutInflater.from(context).inflate(R.layout.item_progress,viewGroup,false);
                footerViewHolder=new FooterViewHolder(view);
                return footerViewHolder;
            }else {
                View view= LayoutInflater.from(context).inflate(R.layout.rv_item,viewGroup,false);
                DataViewHolder dataViewHolder=new DataViewHolder(view);
                return dataViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (i!=list.size()){
                ((DataViewHolder)viewHolder).tv1.setText(list.get(i).getClsId());
                ((DataViewHolder)viewHolder).tv2.setText(list.get(i).getClsName());
                ((DataViewHolder)viewHolder).rv_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    /*Intent intent=new Intent(context, MainActivity2.class);
                    context.startActivity(intent);
                        itemClickListem.OnCli();
                    }
                });
            }else {
                footerViewHolder.setData(status);
            }

        }

        @Override
        public int getItemViewType(int position) {
            if (position==list.size()){
                return 1;
            }else {
                return 0;
            }

        }

        @Override
        public int getItemCount() {
            return list.size()+1;
        }

        class DataViewHolder extends RecyclerView.ViewHolder {
            public TextView tv1,tv2;
            public LinearLayout rv_item;
            public DataViewHolder(@NonNull View itemView) {
                super(itemView);
                tv1=itemView.findViewById(R.id.weather_city);
                tv2=itemView.findViewById(R.id.weather_low);
                rv_item=itemView.findViewById(R.id.rv_item);
            }
        }
        public  class FooterViewHolder extends RecyclerView.ViewHolder{
            public  ProgressBar pb;
            public  TextView tv;
            public FooterViewHolder(@NonNull View itemView) {
                super(itemView);
                pb=itemView.findViewById(R.id.progressbar);
                tv=itemView.findViewById(R.id.zzjz);

            }
            public void setData(MORE_STATUS status){
                switch (status){
                    case LOADING:
                        pb.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.VISIBLE);
                        tv.setText("正在加载");
                        break;
                    case LOADED:
                        pb.setVisibility(View.GONE);
                        tv.setVisibility(View.GONE);
                        break;
                    case LOAD_COMPLETED:
                        pb.setVisibility(View.GONE);
                        tv.setVisibility(View.VISIBLE);
                        tv.setText("已经到底了");
                        break;

                }
            }
        }

        public interface ItemClickListem{
            public void OnCli();
        }

        public enum MORE_STATUS{
            LOADING,
            LOADED,
            LOAD_COMPLETED,
            REFLASHING,
            REFLASHED;
        }


    }*/
}
