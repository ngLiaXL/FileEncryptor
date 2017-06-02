package com.ngliaxl.encrypt.manage;

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

import com.ngliaxl.encrypt.R;
import com.ngliaxl.encrypt.event.UpdateEvent;
import com.ngliaxl.encrypt.util.DialogUtil;
import com.ngliaxl.encrypt.util.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

public class FileEditActivity extends AppCompatActivity {


    private EditText mEtContent;
    private String mCurrentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_edit);
        mCurrentPath = getIntent().getStringExtra("extra_file_path");

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
        mEtContent = (EditText) findViewById(R.id.et_file_content);

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
                mEtContent.setText(content);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.file_save) {
            String fileContent = mEtContent.getText().toString();
            editFile(fileContent);
        } else if (menuItem.getItemId() == R.id.file_delete) {
            DialogUtil.showConfirmDialog(this, "", "确认删除?", "取消", "确认", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileUtils.deleteFile(mCurrentPath);
                    EventBus.getDefault().post(new UpdateEvent());
                    Toast.makeText(FileEditActivity.this, "文件删除成功", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

        }
        return super.onOptionsItemSelected(menuItem);
    }


    private void editFile( final String fileContent) {

        if (TextUtils.isEmpty(fileContent)) {
            Toast.makeText(this, "请输入文件内容", Toast.LENGTH_LONG).show();
            return;
        }

        final String fullFilePath = mCurrentPath;

        new Thread("createFileThread") {
            @Override
            public void run() {
                try {
                    File f = new File(fullFilePath) ;
                    f.delete() ;
                    FileUtils.write(f, new StringBuffer(fileContent));
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
                Toast.makeText(FileEditActivity.this, "文件修改成功", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

}
