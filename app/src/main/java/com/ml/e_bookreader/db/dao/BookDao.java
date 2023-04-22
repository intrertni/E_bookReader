package com.ml.e_bookreader.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ml.e_bookreader.db.bean.BookBean;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;


/**
 * Date: 2023/3/1 14:20
 * Description: 数据库Dao
 */
@Dao
public interface BookDao {

    /**
     * 查询书架所有小说
     */
    @Query("SELECT * FROM book_table")
    List<BookBean> getAllBook();

    /**
     * 根据bookId 批量查询小说
     */
    @Query("SELECT * FROM book_table WHERE bookId IN (:bookIds)")
    List<BookBean> loadAllByIds(List<Integer> bookIds);

    /**
     * 根据小说地址查询小说
     */
    @Query("SELECT * FROM book_table WHERE bookUrl=:bookUrl")
    BookBean loadByUrl(String bookUrl);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BookBean> bookBeans);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BookBean bookBean);

    @Delete
    void delete(BookBean bookBean);

    @Delete
    void deleteAll(List<BookBean> bookBeans);

    @Update
    void update(BookBean bookBean);
}
