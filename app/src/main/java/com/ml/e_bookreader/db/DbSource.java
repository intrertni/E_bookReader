package com.ml.e_bookreader.db;

import com.ml.e_bookreader.App;
import com.ml.e_bookreader.db.bean.BookBean;
import com.ml.e_bookreader.db.bean.ChapterBean;
import com.ml.e_bookreader.db.dao.BookDao;
import com.ml.e_bookreader.db.dao.ChapterDao;

import java.util.List;

/**
 * Date: 2023/3/9 11:05
 * Description: 访问数据库
 */
public class DbSource {
    private final BookDao bookDao;
    private final ChapterDao chapterDao;
    /**
     * 实例
     * 类初始化时，不初始化这个对象，用的时候才创建实现了延时加载
     */
    private static volatile DbSource instance;

    public DbSource(BookDao bookDao, ChapterDao chapterDao) {
        this.bookDao = bookDao;
        this.chapterDao = chapterDao;
    }

    /**
     * 单例调用
     */
    public static DbSource getInstance() {
        if (instance == null) {
            synchronized (DbSource.class) {
                if (instance == null) {
                    instance = new DbSource(App.getDB().getBookDao(), App.getDB().getChapterDao());
                }
            }
        }
        return instance;
    }
    /*===============================================查询书籍信息======================================================*/

    /**
     * 查询书架所有小说
     */
    public List<BookBean> getAllBook() {
        return bookDao.getAllBook();
    }

    /**
     * 根据ID查询书架所有小说
     */
    public List<BookBean> loadAllByIds(List<Integer> bookIds) {
        return bookDao.loadAllByIds(bookIds);
    }

    public BookBean loadByUrl(String url) {
        return bookDao.loadByUrl(url);
    }

    /**
     * 批量插入
     */
    public void insertAll(List<BookBean> bookBeans) {
        bookDao.insertAll(bookBeans);
    }

    /**
     * 单条插入
     */
    public void insert(BookBean bookBean) {
        bookDao.insert(bookBean);

    }

    /**
     * 单条删除
     */
    public void delete(BookBean bookBean) {
        bookDao.delete(bookBean);
    }

    /**
     * 批量删除
     */
    public void deleteAll(List<BookBean> bookBeans) {
        bookDao.deleteAll(bookBeans);
    }

    /**
     * 单条更新
     */
    public void update(BookBean bookBean) {
        bookDao.update(bookBean);
    }


    /*===============================================查询章节信息======================================================*/

    /**
     * 根据id查询是否加载过
     *
     * @param bookId
     */
    public List<ChapterBean> loadChapterById(int bookId) {
        return chapterDao.loadById(bookId);
    }

    /**
     * 批量插入
     */
    public void insertChapterAll(List<ChapterBean> chapterBeans) {
        chapterDao.insertAll(chapterBeans);
    }

    /**
     * 批量删除
     */
    public void deleteChapterAll(List<ChapterBean> chapterBeans) {
        chapterDao.deleteAll(chapterBeans);
    }
}
