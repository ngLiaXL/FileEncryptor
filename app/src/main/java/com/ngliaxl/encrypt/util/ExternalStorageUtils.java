/**
 *
 */
package com.ngliaxl.encrypt.util;

import android.os.Environment;


import com.ngliaxl.encrypt.MainApp;

import java.io.File;

public class ExternalStorageUtils {

    static final String MEIDA_DRI = "encryptor";

    static boolean mExternalStorageAvailable = false;
    static boolean mExternalStorageWriteable = false;

    /**
     *
     */
    private ExternalStorageUtils() {
    }

    public static void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    public static File getAppExternalStorage() {
        updateExternalStorageState();
        if (mExternalStorageAvailable && mExternalStorageWriteable) {
            return Environment.getExternalStoragePublicDirectory(MEIDA_DRI);
        }

        File externalStorage = MainApp.getContext().getExternalFilesDir(MEIDA_DRI);
        if (externalStorage != null && externalStorage.isDirectory()) {
            return externalStorage;
        }
        return null;
    }

    public static String getAppExternalStorage(String fileName, String parentName) {
        File appExtraRootFile = new File(getAppExternalStorage() + File.separator + parentName);
        if (!appExtraRootFile.exists()) {
            if (!makeDir(appExtraRootFile)) {
                return null;
            }
        }
        return appExtraRootFile.getAbsolutePath() + File.separator + fileName;
    }

    public static String getAppPirvateStorage(String fileName, String parentName) {
        File appExtraRootFile = new File(MainApp.getContext().getFilesDir().getAbsolutePath()
                + File.separator + parentName);
        if (!appExtraRootFile.exists()) {
            if (!makeDir(appExtraRootFile)) {
                return null;
            }
        }
        return appExtraRootFile.getAbsolutePath() + File.separator + fileName;
    }

    public static String getAppPirvateDir(String fileType) {
        File appExtraRootFile = new File(MainApp.getContext().getFilesDir().getAbsolutePath()
                + File.separator + fileType);
        if (!appExtraRootFile.exists()) {
            if (!makeDir(appExtraRootFile)) {
                return null;
            }
        }
        return appExtraRootFile.getAbsolutePath() + File.separator;
    }

    // 路径太深不能安装
    public static String getApkExtraFilePath(String fileName) {
        File appExtraRootFile = new File(MainApp.getContext().getFilesDir() + File.separator);
        if (!appExtraRootFile.exists()) {
            if (!makeDir(appExtraRootFile)) {
                return null;
            }
        }
        return appExtraRootFile.getAbsolutePath() + File.separator + fileName;
    }

    static boolean makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        return dir.mkdir();
    }

}
