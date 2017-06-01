package com.ngliaxl.encrypt;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ngliaxl.encrypt.event.FileEncryptEvent;
import com.ngliaxl.encrypt.event.FileSignEvent;
import com.ngliaxl.encrypt.event.FileVerifyEvent;
import com.ngliaxl.encrypt.manage.DirectoryFragment;
import com.ngliaxl.encrypt.manage.FileCreateActivity;
import com.ngliaxl.encrypt.manage.FileEditActivity;
import com.ngliaxl.encrypt.manage.filter.CompositeFilter;
import com.ngliaxl.encrypt.manage.filter.PatternFilter;
import com.ngliaxl.encrypt.util.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;


public class FileManageActivity extends AppCompatActivity implements DirectoryFragment.FileClickListener {

    public static final String ARG_FROM_PAGE = "arg_from_page" ;

    public static final String ARG_START_PATH = "arg_start_path";
    public static final String ARG_CURRENT_PATH = "arg_current_path";

    public static final String ARG_FILTER = "arg_filter";
    public static final String ARG_CLOSEABLE = "arg_closeable";
    public static final String ARG_TITLE = "arg_title";

    public static final String STATE_START_PATH = "state_start_path";
    private static final String STATE_CURRENT_PATH = "state_current_path";

    public static final String RESULT_FILE_PATH = "result_file_path";
    private static final int HANDLE_CLICK_DELAY = 150;

    private Toolbar mToolbar;
    private String mStartPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String mCurrentPath = mStartPath;
    private CharSequence mTitle;

    private Boolean mCloseable;

    private CompositeFilter mFilter;

    private String mFromPage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);

        initArguments();
        initViews();
        initToolbar();
        initBackStackState();

        if (savedInstanceState != null) {
            mStartPath = savedInstanceState.getString(STATE_START_PATH);
            mCurrentPath = savedInstanceState.getString(STATE_CURRENT_PATH);
        } else {
            initFragment();
        }
    }

    @SuppressWarnings("unchecked")
    private void initArguments() {
        if (getIntent().hasExtra(ARG_FILTER)) {
            Serializable filter = getIntent().getSerializableExtra(ARG_FILTER);

            if (filter instanceof Pattern) {
                ArrayList<FileFilter> filters = new ArrayList<>();
                filters.add(new PatternFilter((Pattern) filter, false));
                mFilter = new CompositeFilter(filters);
            } else {
                mFilter = (CompositeFilter) filter;
            }
        }

        if (getIntent().hasExtra(ARG_TITLE)) {
            mTitle = getIntent().getCharSequenceExtra(ARG_TITLE);
        }

        if (getIntent().hasExtra(ARG_START_PATH)) {
            mStartPath = getIntent().getStringExtra(ARG_START_PATH);
            mCurrentPath = mStartPath;
        }

        if (getIntent().hasExtra(ARG_CLOSEABLE)) {
            mCloseable = getIntent().getBooleanExtra(ARG_CLOSEABLE, true);
        }

        if (getIntent().hasExtra(ARG_CURRENT_PATH)) {
            String currentPath = getIntent().getStringExtra(ARG_CURRENT_PATH);

            if (currentPath.startsWith(mStartPath)) {
                mCurrentPath = currentPath;
            }
        }

        if(getIntent().hasExtra(ARG_FROM_PAGE)){
            mFromPage = getIntent().getStringExtra(ARG_FROM_PAGE) ;
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        // Show back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Truncate start of path
        try {
            Field f;
            if (TextUtils.isEmpty(mTitle)) {
                f = mToolbar.getClass().getDeclaredField("mTitleTextView");
            } else {
                f = mToolbar.getClass().getDeclaredField("mSubtitleTextView");
            }

            f.setAccessible(true);
            TextView textView = (TextView) f.get(mToolbar);
            textView.setEllipsize(TextUtils.TruncateAt.START);
        } catch (Exception ignored) {
        }

        if (!TextUtils.isEmpty(mTitle)) {
            setTitle(mTitle);
        }
        updateTitle();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(
                        mCurrentPath, mFilter))
                .addToBackStack(null)
                .commit();
    }

    private void initBackStackState() {
        String pathToAdd = mCurrentPath;
        ArrayList<String> separatedPaths = new ArrayList<>();

        while (!pathToAdd.equals(mStartPath)) {
            pathToAdd = FileUtils.cutLastSegmentOfPath(pathToAdd);
            separatedPaths.add(pathToAdd);
        }

        Collections.reverse(separatedPaths);

        for (String path : separatedPaths) {
            addFragmentToBackStack(path);
        }
    }

    private void updateTitle() {
        if (getSupportActionBar() != null) {
            String titlePath = mCurrentPath.isEmpty() ? "/" : mCurrentPath;
            if (TextUtils.isEmpty(mTitle)) {
                getSupportActionBar().setTitle(titlePath);
            } else {
                getSupportActionBar().setSubtitle(titlePath);
            }
        }
    }

    private void addFragmentToBackStack(String path) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(
                        path, mFilter))
                .addToBackStack(null)
                .commit();
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_mgr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (menuItem.getItemId() == R.id.file_create) {
            startActivity(new Intent(FileManageActivity.this, FileCreateActivity.class)
            .putExtra("extra_current_path",mCurrentPath));
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        if (!mCurrentPath.equals(mStartPath)) {
            fm.popBackStack();
            mCurrentPath = FileUtils.cutLastSegmentOfPath(mCurrentPath);
            updateTitle();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_PATH, mCurrentPath);
        outState.putString(STATE_START_PATH, mStartPath);
    }

    @Override
    public void onFileClicked(final File clickedFile) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleFileClicked(clickedFile);
            }
        }, HANDLE_CLICK_DELAY);
    }

    private void handleFileClicked(final File clickedFile) {
        if (clickedFile.isDirectory()) {
            mCurrentPath = clickedFile.getPath();
            // If the user wanna go to the emulated directory, he will be taken to the
            // corresponding user emulated folder.
            if (mCurrentPath.equals("/storage/emulated"))
                mCurrentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            addFragmentToBackStack(mCurrentPath);
            updateTitle();
        } else {
            setResultAndFinish(clickedFile.getPath());
        }
    }

    private void setResultAndFinish(String filePath) {
        /*Intent data = new Intent();
        data.putExtra(RESULT_FILE_PATH, filePath);
        setResult(RESULT_OK, data);
        finish();*/


        if (filePath.endsWith(".txt")) {

            // 文件管理
            if("file_mgr".equals(mFromPage)){
                startActivity(new Intent(this, FileEditActivity.class).putExtra("extra_file_path",
                        filePath)
                );
            }

            // 加密解密
            else if("file_encrypt".equals(mFromPage)){
                EventBus.getDefault().post(new FileEncryptEvent(filePath));
                finish();
            }

            // MD5
            else if("file_verify".equals(mFromPage)){
                EventBus.getDefault().post(new FileVerifyEvent(filePath));
                finish();
            }

            // 签名
            else if("file_sign".equals(mFromPage)){
                EventBus.getDefault().post(new FileSignEvent(filePath));
                finish();
            }

        }
    }
}
