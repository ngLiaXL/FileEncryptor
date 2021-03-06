package com.ngliaxl.encrypt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.ngliaxl.encrypt.manage.filter.CompositeFilter;
import com.ngliaxl.encrypt.manage.filter.HiddenFilter;

import java.io.FileFilter;
import java.util.ArrayList;


/**
 * 文件管理：文件建立、文件删除、修改等。
 * 加密解密：根据RSA算法，加密解密用户文件。
 * 文件完整性验证与文件数字签名。
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("文件加密");
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setSubtitle("张元昊(13002617)");
        }
    }


    /**
     * 文件管理
     *
     * @param view
     */
    public void onClickFileMgr(View view) {
        openFilePicker("file_mgr");
    }

    private void openFilePicker(String from) {
        ArrayList<FileFilter> filters = new ArrayList<>();
        filters.add(new HiddenFilter());
        CompositeFilter filter = new CompositeFilter(filters);

        Intent intent = new Intent(this, FileManageActivity.class);
        intent.putExtra(FileManageActivity.ARG_FILTER, filter);
        intent.putExtra(FileManageActivity.ARG_CLOSEABLE, true);
        intent.putExtra(FileManageActivity.ARG_FROM_PAGE, from);

        startActivity(intent);
    }


    /**
     * 加密解密
     *
     * @param view
     */
    public void onClickFileEncrypt(View view) {
        startActivity(new Intent(this, FileEncryptActivity.class));
    }

    /**
     * 文件验证
     *
     * @param view
     */
    public void onClickFileVerify(View view) {
        startActivity(new Intent(this, FileVerifyActivity.class));
    }
    /**
     * 数字证书
     * @param view
     */
    public void onClickFileSign(View view) {
        startActivity(new Intent(this, FileSignActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_installer, menu);
        return super.onCreateOptionsMenu(menu);

    }


}
