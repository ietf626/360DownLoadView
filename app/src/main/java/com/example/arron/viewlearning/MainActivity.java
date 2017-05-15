package com.example.arron.viewlearning;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.arron.viewlearning.widgets.DownloadView;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mDownLoadView.isLoading())
                mDownLoadView.setProgress(progress++);
            if (progress <= 100)
                mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    };
    private DownloadView mDownLoadView;
    private int progress;
    private TextView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDownLoadView = (DownloadView) findViewById(R.id.downLoadView);

        btn = (TextView) findViewById(R.id.pause);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDownLoadView.isLoading())
                    mDownLoadView.stop();
                else
                    mDownLoadView.start();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownLoadView.cancel();
            }
        });
        mHandler.sendEmptyMessageDelayed(0, 1000);
        mDownLoadView.setOnProgressChangeListener(new DownloadView.OnProgressChangeListener() {
            @Override
            public void onPause() {
                btn.setText("继续");
            }

            @Override
            public void onContinue() {
                btn.setText("暂停");
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
    }
}
