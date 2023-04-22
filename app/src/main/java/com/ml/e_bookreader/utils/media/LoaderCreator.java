package com.ml.e_bookreader.utils.media;

import android.content.Context;
import android.os.Bundle;

import androidx.loader.content.CursorLoader;

/**
 * Author: 刘腾
 * Date: 2023/3/3 13:44
 * Description: 创建Loader
 * Copyright © 爱士惟新能源
 */
public class LoaderCreator {
    public static final int ALL_BOOK_FILE = 1;

    public static CursorLoader create(Context context, int id, Bundle args) {
        LocalFileLoader loader = null;
        if (id == ALL_BOOK_FILE) {
            loader = new LocalFileLoader(context);
        }
        if (loader != null) {
            return loader;
        }
        throw new IllegalArgumentException("The id of Loader is invalid!");
    }
}
