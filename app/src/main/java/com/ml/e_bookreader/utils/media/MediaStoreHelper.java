package com.ml.e_bookreader.utils.media;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.ml.e_bookreader.db.bean.BookBean;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Date: 2023/3/3 13:42
 * Description: 获取媒体库的数据
 */
public class MediaStoreHelper {
    /**
     * 获取媒体库中 txt epub类型的书籍文件
     */
    public static void getAllBookFile(FragmentActivity activity, MediaResultCallback resultCallback) {
        // 将文件的获取处理交给 LoaderManager。
        LoaderManager.getInstance(activity)
                .initLoader(LoaderCreator.ALL_BOOK_FILE, null, new MediaLoaderCallbacks(activity, resultCallback));
    }

    public interface MediaResultCallback {
        void onResultCallback(List<BookBean> bookBeans);
    }

    /**
     * Loader 回调处理
     */
    static class MediaLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        protected WeakReference<Context> mContext;
        protected MediaResultCallback mResultCallback;

        public MediaLoaderCallbacks(Context context, MediaResultCallback resultCallback) {
            mContext = new WeakReference<>(context);
            mResultCallback = resultCallback;
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return LoaderCreator.create(mContext.get(), id, args);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            LocalFileLoader localFileLoader = (LocalFileLoader) loader;
            localFileLoader.parseData(data, mResultCallback);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        }
    }
}
