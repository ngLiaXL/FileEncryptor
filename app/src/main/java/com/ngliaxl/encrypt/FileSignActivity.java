package com.ngliaxl.encrypt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ngliaxl.encrypt.event.FileVerifyEvent;
import com.ngliaxl.encrypt.manage.filter.CompositeFilter;
import com.ngliaxl.encrypt.manage.filter.HiddenFilter;
import com.ngliaxl.encrypt.util.FileTypeUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * 文件数字签名
 */
public class FileSignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_verify);

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
            ab.setTitle("文件数字签名");
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseFileEvent(FileVerifyEvent event) {
        File currentFile = new File(event.filePath);
        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(currentFile);

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

}
