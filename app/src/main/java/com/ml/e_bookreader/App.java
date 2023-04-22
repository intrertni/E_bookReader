package com.ml.e_bookreader;

import android.app.Application;
import android.content.Context;

import com.ml.e_bookreader.db.BookDataBase;

/**
 * Date: 2023/3/1 16:17
 * Description: application
 */
public class App extends Application {
    private static BookDataBase dataBase;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        dataBase = BookDataBase.getInstance(this);
    }

    public static BookDataBase getDB() {
        return dataBase;
    }

    public static Context getContext() {
        return context;
    }
}
