package com.ngliaxl.encrypt.manage;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ngliaxl.encrypt.R;
import com.ngliaxl.encrypt.util.FileUtils;

public class FileViewActivity extends AppCompatActivity {


    private TextView mTvContent;
    private String mCurrentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);
        mCurrentPath = getIntent().getStringExtra("extra_file_view_path");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(FileUtils.getFileNameWithoutExtension(mCurrentPath));
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mTvContent = (TextView) findViewById(R.id.tv_file_content);

        readFile();
    }


    private void readFile() {
        new Thread("readFileThread") {
            @Override
            public void run() {
                StringBuilder fileContent = FileUtils.readFile(mCurrentPath, "UTF-8");
                runOnUiThreadContent(fileContent);
            }
        }.start();
    }

    private void runOnUiThreadContent(final StringBuilder content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvContent.setText(content.toString());
            }
        });
    }


}
