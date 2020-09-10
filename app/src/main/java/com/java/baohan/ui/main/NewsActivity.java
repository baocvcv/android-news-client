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
    News news;
    Button buttonGoBack;
    TextView titleView;
    TextView timeView;
    TextView contentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_reader);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String time = intent.getStringExtra("time");
        String content="        "+ intent.getStringExtra("content");



        buttonGoBack=(Button) findViewById(R.id.go_back);
        titleView=(TextView) findViewById(R.id.title);
        timeView=(TextView) findViewById(R.id.time);
        contentView =(TextView) findViewById(R.id.context);

        titleView.setText(title);
        timeView.setText(time);
        contentView.setText(content);
        contentView.setMovementMethod(ScrollingMovementMethod.getInstance());

        buttonGoBack.setOnClickListener(new View.OnClickListener() {

            public void onClick (View v){
                finish();
            }
        });

    }

}
