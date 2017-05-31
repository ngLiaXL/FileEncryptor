package com.ngliaxl.encrypt.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ngliaxl.encrypt.FileManageActivity;
import com.ngliaxl.encrypt.R;
import com.ngliaxl.encrypt.util.DialogUtil;
import com.ngliaxl.encrypt.util.ExternalStorageUtils;
import com.ngliaxl.encrypt.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileCreateActivity extends AppCompatActivity {


    private EditText mEtContent;
    private String mCurrentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_create);
        mCurrentPath = getIntent().getStringExtra("extra_current_path");

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
            ab.setTitle(mCurrentPath);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mEtContent = (EditText) findViewById(R.id.et_file_content);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.file_save) {
            DialogUtil.showEditDialog(this, "取消", "确认", "请输入文件名", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) v;
                    String fileName = editText.getText().toString().trim();
                    String fileContent = mEtContent.getText().toString();
                    createFile(fileName, fileContent);

                }
            });
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private void createFile(String fileName, final String fileContent) {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(this, "请输入文件名", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(fileContent)) {
            Toast.makeText(this, "请输入文件内容", Toast.LENGTH_LONG).show();
            return;
        }

        final String fullFilePath = mCurrentPath + File.separator + fileName + ".txt";

        new Thread("createFileThread") {
            @Override
            public void run() {
                try {
                    FileUtils.write(new File(fullFilePath), new StringBuffer(fileContent));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThreadFinish();
            }
        }.start();


    }

    private void runOnUiThreadFinish() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileCreateActivity.this, "文件成功创建成功", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

}
