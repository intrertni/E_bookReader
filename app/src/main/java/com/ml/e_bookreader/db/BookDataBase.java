package com.ml.e_bookreader.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ml.e_bookreader.db.dao.BookDao;
import com.ml.e_bookreader.db.bean.BookBean;
import com.ml.e_bookreader.db.bean.ChapterBean;
import com.ml.e_bookreader.db.dao.ChapterDao;

/**
 * Date: 2023/3/1 14:39
 * Description: 创建数据库和创建创建表
 */
@Database(entities = {BookBean.class, ChapterBean.class}, version = 1, exportSchema = false)
public abstract class BookDataBase extends RoomDatabase {
    //单例模式，返回db volatile 确保线程安全
    public static volatile BookDataBase INSTANCE;

    //用户操作DAO，必须暴露，用户进行增删改查
    public abstract BookDao getBookDao();

    public abstract ChapterDao getChapterDao();

    public static synchronized BookDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BookDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder
                                    (context.getApplicationContext(), BookDataBase.class, "ml_book.db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
