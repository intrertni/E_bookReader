package com.ml.e_bookreader.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ml.e_bookreader.db.bean.ChapterBean;

import java.util.List;


/**
 * Date: 2023/3/1 14:20
 * Description: 章节数据库数据库Dao
 */
@Dao
public interface ChapterDao {

    /**
     * 根据bookId 查询章节是否被加载过
     */
    @Query("SELECT * FROM chapter_table WHERE bookId IN (:bookId)")
    List<ChapterBean> loadById(int bookId);

    /**
     * 插入章节数据
     *
     * @param chapterBeans
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ChapterBean> chapterBeans);

    @Delete
    void deleteAll(List<ChapterBean> chapterBeans);
}
