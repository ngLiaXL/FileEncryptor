package com.ngliaxl.encrypt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ngliaxl.encrypt.event.FileEncryptEvent;
import com.ngliaxl.encrypt.event.FileSignEvent;
import com.ngliaxl.encrypt.event.FileVerifyEvent;
import com.ngliaxl.encrypt.manage.FileViewActivity;
import com.ngliaxl.encrypt.manage.filter.CompositeFilter;
import com.ngliaxl.encrypt.manage.filter.HiddenFilter;
import com.ngliaxl.encrypt.util.Base64Utils;
import com.ngliaxl.encrypt.util.FileTypeUtils;
import com.ngliaxl.encrypt.util.FileUtils;
import com.ngliaxl.encrypt.util.RSAUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 * 文件数字签名
 */
public class FileSignActivity extends AppCompatActivity {


    private TextView mTvName;
    private TextView mTvSubTitle;
    private Button mBtnEncrypt;
    private Button mBtnDecrypt;


    private View mFileLayout;
    private String mCurrentFilePath;

    private ProgressDialog mLoadingDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sign);

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
            ab.setTitle("文件数字证书");
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mTvName = (TextView) findViewById(R.id.item_file_title);
        mTvSubTitle = (TextView) findViewById(R.id.item_file_subtitle);
        mFileLayout = findViewById(R.id.ll_file_item);
        mFileLayout.setVisibility(View.GONE);

        mBtnEncrypt = (Button) findViewById(R.id.btn_encrypt);
        mBtnDecrypt = (Button) findViewById(R.id.btn_decrypt);

        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setMessage("正在处理....");
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
            mCurrentFilePath = null;
            mFileLayout.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChooseFileEvent(FileSignEvent event) {
        File currentFile = new File(event.filePath);
        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(currentFile);
        mTvName.setText(currentFile.getName());
        mTvSubTitle.setText(fileType.getDescription());
        mCurrentFilePath = event.filePath;
        mFileLayout.setVisibility(View.VISIBLE);


    }


    private void openFilePicker() {

        ArrayList<FileFilter> filters = new ArrayList<>();
        filters.add(new HiddenFilter());
        CompositeFilter filter = new CompositeFilter(filters);

        Intent intent = new Intent(this, FileManageActivity.class);
        intent.putExtra(FileManageActivity.ARG_FILTER, filter);
        intent.putExtra(FileManageActivity.ARG_CLOSEABLE, true);
        intent.putExtra(FileManageActivity.ARG_FROM_PAGE, "file_sign");

        startActivity(intent);
    }


    public void onClickEncryptFile(View view) {
        if (mCurrentFilePath == null) {
            Toast.makeText(this, "请先选择文件", Toast.LENGTH_LONG).show();
            return;
        }

        mLoadingDialog.show();

        new Thread("encryptFileThread") {
            @Override
            public void run() {
                File currentFile = new File(mCurrentFilePath);
                try {
                    StringBuilder beforeEncrypt = FileUtils.readFile(currentFile.getAbsolutePath(), "UTF-8");
                    InputStream inPublic = FileSignActivity.this.getResources().getAssets().open
                            ("rsa_public_key.pem");

                    PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
                    byte[] encryptByte = RSAUtils.encryptData(beforeEncrypt.toString().getBytes("UTF-8"),
                            publicKey);
                    String afterEncrypt = Base64Utils.encode(encryptByte);

                    FileUtils.deleteFile(mCurrentFilePath);
                    FileUtils.write(currentFile, new StringBuffer(afterEncrypt));

                    runOnUiThreadToast("签名成功");

                } catch (Exception e) {
                    runOnUiThreadToast("签名失败，请检查是否已经签名");
                }finally {
                    mLoadingDialog.dismiss();
                }
            }
        }.start();


    }


    public void onClickDecrypeFile(View view) {

        if (mCurrentFilePath == null) {
            Toast.makeText(this, "请先选择文件", Toast.LENGTH_LONG).show();
            return;
        }

        mLoadingDialog.show();
        new Thread("decryptFileThread") {
            @Override
            public void run() {
                File currentFile = new File(mCurrentFilePath);
                try {
                    InputStream inPrivate = getResources().getAssets().open("pkcs8_rsa_private_key.pem");
                    PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);

                    StringBuilder beforeDencrypt = FileUtils.readFile(currentFile.getAbsolutePath
                            (), "UTF-8");

                    // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
                    byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(beforeDencrypt.toString())
                            , privateKey);
                    FileUtils.deleteFile(currentFile.getAbsolutePath());
                    FileUtils.write(currentFile, new StringBuffer(new String(decryptByte, "UTF-8")));

                    runOnUiThreadToast("解签成功");

                } catch (Exception e) {
                    runOnUiThreadToast("解签失败 : " + e.toString());
                }finally {
                    mLoadingDialog.dismiss();
                }
            }
        }.start();


    }



    private void runOnUiThreadToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileSignActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    public void onClickFileView(View view) {
        startActivity(new Intent(this, FileViewActivity.class).
                putExtra("extra_file_view_path", mCurrentFilePath)


        );
    }
}
