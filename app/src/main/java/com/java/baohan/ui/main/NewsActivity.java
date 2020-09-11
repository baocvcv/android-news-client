package com.java.baohan.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.baohan.MainActivity;
import com.java.baohan.R;
import com.java.baohan.model.News;

public class NewsActivity extends AppCompatActivity {
    Button buttonGoBack;
    TextView titleView;
    TextView timeView;
    TextView sourceView;
    TextView contentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_reader);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String time = intent.getStringExtra("time");
        String content="        "+ intent.getStringExtra("content");
        String source = intent.getStringExtra("source");



        buttonGoBack=(Button) findViewById(R.id.go_back);
        titleView=(TextView) findViewById(R.id.title);
        timeView=(TextView) findViewById(R.id.time);
        contentView =(TextView) findViewById(R.id.context);

        titleView.setText(title);
        timeView.setText(time);
        contentView.setText(content);
        contentView.setMovementMethod(ScrollingMovementMethod.getInstance());

        sourceView = findViewById(R.id.news_source);
        sourceView.setText(source);

        buttonGoBack.setOnClickListener(new View.OnClickListener() {

            public void onClick (View v){
                finish();
            }
        });

        Button btn = findViewById(R.id.button_share);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                share_intent.setType("text/plain");//设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_SUBJECT, title);//添加分享内容标题
                String msg;
                if(content.length() > 20) {
                    msg = content.substring(0, 60);
                } else {
                    msg = content;
                }
                share_intent.putExtra(Intent.EXTRA_TEXT, msg);//添加分享内容
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, title);
                startActivity(share_intent);
            }
        });
    }

}
