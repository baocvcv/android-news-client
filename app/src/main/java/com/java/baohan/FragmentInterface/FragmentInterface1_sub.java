package com.java.baohan.FragmentInterface;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.baohan.R;

public class FragmentInterface1_sub extends Fragment {
    private Context mcontext;
    private String data[] = {"震惊！美国疫情失控或引发局部冲突？","齐心抗新冠，众志可成城","卫生部：深入贯彻落实中央有关新冠疫情的八项指示","有关北京市高校学生出入的相关规定","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a","a"};


    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext = context;//mContext 是成员变量，上下文引用
    }

    public void onDetach() {
        super.onDetach();
        mcontext = null;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.news_layout,container,false);
        ListView listView = (ListView) view.findViewById(R.id.listview);//在视图中找到ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mcontext,android.R.layout.simple_list_item_1,data);//新建并配置ArrayAapeter
        listView.setAdapter(adapter);
        return view;
    }

    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }
}
