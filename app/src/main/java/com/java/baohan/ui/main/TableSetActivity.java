package com.java.baohan.ui.main;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.java.baohan.FragmentInterface.FragmentInterface1;
import com.java.baohan.FragmentInterface.FragmentInterface1_sub;
import com.java.baohan.MainActivity;
import com.java.baohan.R;
import com.java.baohan.backend.CovidEvent;

import java.util.ArrayList;
import java.util.List;


public class TableSetActivity extends AppCompatActivity {
    private ArrayList<String> fragmentListIn = new ArrayList<String>();
    private ArrayList<String> fragmentListOut = new ArrayList<String>();
    private ArrayList<String> standardList = new ArrayList<String>();
    private Button buttonGoback;
    private ListView listView1;
    private ListView listView2;

    public void intiStandardList(){
        standardList.add("搜索");
        standardList.add("收藏");
        standardList.add("news");
        standardList.add("papers");
        List<String> classes = CovidEvent.getClassNames();

        standardList.add(classes.get(0));
        standardList.add(classes.get(1));
        standardList.add(classes.get(2));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checked_layout);

        intiStandardList();

        listView1 = findViewById(R.id.listview1);
        listView2 = findViewById(R.id.listview2);
        buttonGoback = findViewById(R.id.checked_goback);

        Intent intent=getIntent();
        String tmp = intent.getStringExtra("num");

        int sizeList = Integer.parseInt(tmp);
        for(int i=0;i<sizeList;i++)
            fragmentListIn.add(intent.getStringExtra("list"+i));

        for(int i=0;i<standardList.size();i++)
        {
            if(fragmentListIn.contains(standardList.get(i))==false)
                fragmentListOut.add(standardList.get(i));
        }


        buttonGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(TableSetActivity.this, MainActivity.class);
                Intent intent=new Intent();
                int sizeList=fragmentListIn.size();
                intent.putExtra("num",sizeList+"");
                int i=0;
                for(;i<sizeList;i++) {
                    String na = "list" + String.valueOf(i);
                    intent.putExtra("list" + i, fragmentListIn.get(i));
                }
                setResult(3,intent);
                finish();
            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fragmentListIn);
        listView1.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fragmentListOut);
        listView2.setAdapter(adapter2);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String res=fragmentListIn.get(i);
                fragmentListOut.add(res);
                fragmentListIn.remove(i);
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String res=fragmentListOut.get(i);
                fragmentListIn.add(res);
                fragmentListOut.remove(i);
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
        });

    }


}
