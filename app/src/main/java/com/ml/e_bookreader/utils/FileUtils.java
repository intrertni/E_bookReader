package com.ml.e_bookreader.utils;

import android.os.Environment;
import android.util.Log;
import com.ml.e_bookreader.App;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Author: 刘腾
 * Date: 2023/3/3 15:52
 * Description: 文件工具类
 * Copyright © 爱士惟新能源
 */
public class FileUtils {
    public static final byte BLANK = 0x0a;
    /**
     * 计算文件大小
     *
     * @param size
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"b", "KB", "MB", "G", "T"};
        //计算单位的，原理是利用lg,公式是 lg(1024^n) = nlg(1024)，最后 nlg(1024)/lg(1024) = n。
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        //计算原理是，size/单位值。单位值指的是:比如说b = 1024,KB = 1024^2
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 获取文件编码
     *
     * @param file
     * @return
     */
    public static String getFileCharset(File file) {
        FileInputStream fis = null;
        FileInputStream temFis = null;
        FileOutputStream fos = null;
        File temFile = null;
        try {
            temFile = getFile(getCachePath() + File.separator + "tem.fy");
            fis = new FileInputStream(file);
            fos = new FileOutputStream(temFile);
            //用10kb作为试探
            byte[] bytes = new byte[1024 * 10];
            int len;
            if ((len = fis.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.flush();
            temFis = new FileInputStream(temFile);
            String encoding = UniversalDetector.detectCharset(temFis);
            if (encoding != null) {
                Log.d("encoding", encoding);
                return encoding;
            } else {
                return "UTF-8";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "UTF-8";
        } finally {
            close(fis, temFis, fos);
            if (temFile != null) {
                temFile.delete();
            }
        }
    }

    //获取文件
    public static synchronized File getFile(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                //创建父类文件夹
                getFolder(file.getParent());
                //创建文件
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    //获取文件夹
    public static void getFolder(String filePath) {
        File file = new File(filePath);
        //如果文件夹不存在，就创建它
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    //获取Cache文件夹
    public static String getCachePath() {
        if (isSdCardExist()) {
            return App.getContext()
                    .getExternalCacheDir()
                    .getAbsolutePath();
        } else {
            return App.getContext()
                    .getCacheDir()
                    .getAbsolutePath();
        }
    }

    //判断是否挂载了SD卡
    public static boolean isSdCardExist() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public static void close(Closeable... closeables){
        for (Closeable closeable : closeables){
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向手机写图片
     *
     * @param buffer
     * @param folder
     * @param fileName
     * @return
     */
    public static boolean writeFile(byte[] buffer, String folder,
                                    String fileName) {
        boolean writeSucc = false;

        File fileDir = new File(folder);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(folder,fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            out.flush();
            writeSucc = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writeSucc;
    }
}
