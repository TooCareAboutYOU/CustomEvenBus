package com.event.eventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.event.EventBus;
import com.event.Subscribe;
import com.event.ThreadMode;
import com.event.custom_eventbus_master.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView mTextView;

    private ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView=findViewById(R.id.txt_response);

        mExecutorService= Executors.newSingleThreadExecutor();

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        EventBus.getDefault().post(new FriendBean("哈哈哈",18));
//                    }
//                }).start();

//                mExecutorService.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        EventBus.getDefault().post(new FriendBean("哈哈哈",18));
//                    }
//                });

                EventBus.getDefault().post(new FriendBean("哈哈哈",18));

            }
        });

    }

    /**
     *
     * @param content
     */
    @Subscribe(ThreadMode.MainThread)
    public void OnEvent(final FriendBean content){
//        Log.i(TAG, "receive: "+content.toString()+"\n"+Thread.currentThread().getName());
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mTextView.setText(content.toString());
//
//            }
//        });

        mTextView.setText(content.toString());

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
