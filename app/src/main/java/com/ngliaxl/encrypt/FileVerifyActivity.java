package com.ngliaxl.encrypt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ngliaxl.encrypt.event.FileVerifyEvent;
import com.ngliaxl.encrypt.manage.FileViewActivity;
import com.ngliaxl.encrypt.manage.filter.CompositeFilter;
import com.ngliaxl.encrypt.manage.filter.HiddenFilter;
import com.ngliaxl.encrypt.util.FileTypeUtils;
import com.ngliaxl.encrypt.util.MD5;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * MD5文件完整性验证
 */
public class FileVerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mName1;
    private TextView mTvSub1;


    private TextView mName2;
    private TextView mTvSub2;

    private TextView mTvSignInfo;


    private String mFilePath1;
    private String mFilePath2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_verify);

        EventBus.getDefault().register(this);
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
            ab.setTitle("文件完整性验证");
            ab.setDisplayHomeAsUpEnabled(true);
        }

        View item1 = findViewById(R.id.include_file_item_1);
        View item2 = findViewById(R.id.include_file_item_2);
        mName1 = (TextView) item1.findViewById(R.id.item_file_title);
        mTvSub1 = (TextView) item1.findViewById(R.id.item_file_subtitle);

        mName2 = (TextView) item2.findViewById(R.id.item_file_title);
        mTvSub2 = (TextView) item2.findViewById(R.id.item_file_subtitle);
        mTvSignInfo = (TextView) findViewById(R.id.tv_sing_info);

        findViewById(R.id.btn_verify).setOnClickListener(mFileVerifyClickListener);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseFileEvent(FileVerifyEvent event) {
        File currentFile = new File(event.filePath);
        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(currentFile);

        if (mFilePath1 == null) {
            mFilePath1 = event.filePath;
            mName1.setText(currentFile.getName());
            mTvSub1.setText(fileType.getDescription());
        } else if (mFilePath2 == null) {
            mFilePath2 = event.filePath;
            mName2.setText(currentFile.getName());
            mTvSub2.setText(fileType.getDescription());
        }

        setItemVisibility();
    }


    private void setItemVisibility() {
        findViewById(R.id.include_file_item_1).setVisibility(mFilePath1 == null ? View.GONE :
                View.VISIBLE);
        findViewById(R.id.include_file_item_2).setVisibility(mFilePath2 == null ? View.GONE :
                View.VISIBLE);

        if(mFilePath1 == null || mFilePath2 == null){
            mTvSignInfo.setText("");
            mTvSignInfo.setVisibility(View.GONE);
        }else{
            mTvSignInfo.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_choose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.file_choose) {
            openFilePicker();
        } else if (menuItem.getItemId() == R.id.file_delete) {
            if (mFilePath2 != null) {
                mFilePath2 = null;
            } else if (mFilePath1 != null) {
                mFilePath1 = null;
            }
            setItemVisibility();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private void openFilePicker() {

        ArrayList<FileFilter> filters = new ArrayList<>();
        filters.add(new HiddenFilter());
        CompositeFilter filter = new CompositeFilter(filters);

        Intent intent = new Intent(this, FileManageActivity.class);
        intent.putExtra(FileManageActivity.ARG_FILTER, filter);
        intent.putExtra(FileManageActivity.ARG_CLOSEABLE, true);
        intent.putExtra(FileManageActivity.ARG_FROM_PAGE, "file_verify");

        startActivity(intent);
    }


    private View.OnClickListener mFileVerifyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickFileVerify(v) ;
        }
    };

    public void onClickFileVerify(View view) {
        if (mFilePath1 == null || mFilePath2 == null) {
            Toast.makeText(this, "请选择两个文件进行校验", Toast.LENGTH_LONG).show();
            return;
        }

        File f1 = new File(mFilePath1);
        File f2 = new File(mFilePath2);

        String file1Md5 = MD5.calculateMD5(f1);
        String file2Md5 = MD5.calculateMD5(f2);


        String info = String.format("文件%1$sMD5值为：%2$s\n\n,文件%3$sMD5值为：%4$s\n\n", f1.getName(),
                file1Md5,
                f2.getName(), file2Md5);

        runOnUiThreadText(info);

    }


    private void runOnUiThreadText(final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvSignInfo.setText(info);
            }
        });
    }

    private void startToFileViewActivity(String filePath) {
        if (filePath != null)
            startActivity(new Intent(this, FileViewActivity.class).
                    putExtra("extra_file_view_path", filePath)


            );
    }


    @Override
    public void onClick(View v) {
        String filePath = null;
        switch (v.getId()) {
            case R.id.include_file_item_1:
                filePath = mFilePath1;
                break;
            case R.id.include_file_item_2:
                filePath = mFilePath2;
                break;
        }
        startToFileViewActivity(filePath);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
