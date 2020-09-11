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
                    msg = content.substring(0, 20);
                } else {
                    msg = content;
                }
                share_intent.putExtra(Intent.EXTRA_TEXT, msg);//添加分享内容
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, title);
                startActivity(share_intent);

//                    Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);

//                    platform.setPlatformActionListener(new PlatformActionListener() {
//                        @Override
//                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                            switch (i) {
//                                case Platform.ACTION_AUTHORIZING:
//                                    Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
//                                    //获取分享参数
//                                    SinaWeibo.ShareParams params = new SinaWeibo.ShareParams();
//                                    String sTxt = "http://www.baidu.com/互联";//转成url编码
//                                    try {
//                                        sTxt += URLEncoder.encode("互联", "utf-8");
//                                    } catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();
//                                    }
//                                    params.setText("测试指定平台分享 @1611 zsp 分享地址：" + sTxt);
//                                    platform.share(params); //开始分享
//                                    platform.showUser(null);
//                                    break;
//
//                                case Platform.ACTION_SHARE:
//                                    Log.d("zsp", "分享成功");
//                                    Toast.makeText(getApplication(), "分享成功", Toast.LENGTH_SHORT).show();
//                                    break;
//                                case Platform.ACTION_USER_INFOR:
//                                    break;
//                            }
//                        }
//
//                        @Override
//                        public void onError(Platform platform, int i, Throwable throwable) {
//
//                        }
//
//                        @Override
//                        public void onCancel(Platform platform, int i) {
//
//                        }
//                    });
//                    platform.authorize();//分享授权
//                }
            }
        });
    }

}
