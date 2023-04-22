package com.ml.e_bookreader.utils.media;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Author: 刘腾
 * Date: 2023/3/3 17:21
 * Description: 本地文件查询
 * Copyright © 爱士惟新能源
 */


public class LocalFileTool {
    @SuppressLint("CheckResult")
    public static void readFile(final String[] mimeType, Context context, final IReadCallBack iReadCallBack) {
        Observable.just(context).map((Function<Context, Object>) context1 -> {
            List<File> files = new ArrayList<>();
            Uri[] fileUri;
            fileUri = new Uri[]{MediaStore.Files.getContentUri("external")};
            String[] colums = new String[]{MediaStore.Files.FileColumns.DATA};
            //构造筛选语句
            StringBuilder selection = new StringBuilder();
            for (int i = 0; i < mimeType.length; i++) {
                if (i != 0) {
                    selection.append(" OR ");
                }
                selection.append(MediaStore.Files.FileColumns.MIME_TYPE).append(" LIKE '%").append(mimeType[i]).append("'");
            }
            //获取内容解析器对象
            ContentResolver resolver = context1.getContentResolver();
            //获取游标
            for (Uri uri : fileUri) {
                Cursor cursor = resolver.query(uri, colums, selection.toString(), null, null);
                if (cursor == null) {
                    return null;
                }//游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
                if (cursor.moveToLast()) {
                    do {
                        //输出文件的完整路径
                        String data = cursor.getString(0);
                        files.add(new File(data));
                    } while (cursor.moveToPrevious());
                }
                cursor.close();
            }
            return files;
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(iReadCallBack::callBack);
    }

    public interface IReadCallBack {
        void callBack(Object localPath);
    }
}
