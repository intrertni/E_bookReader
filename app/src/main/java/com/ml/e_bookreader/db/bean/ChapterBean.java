package com.ml.e_bookreader.db.bean;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Date: 2023/3/1 14:04
 * Description: 章节
 */
@Entity(tableName = "chapter_table")
public class ChapterBean {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int bookId;//章节所属书的ID
    private int chapterNumber;//章节序号
    private String chapterTitle;//章节标题
    private String encoding;//字符编码
    private String chapterContent;//章节正文
    //章节内容在文章中的起始位置(本地)
    private long chapterStart;
    //章节内容在文章中的终止位置(本地)
    private long chapterEnd;
    private boolean unreadable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getChapterContent() {
        return chapterContent;
    }

    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }

    public long getChapterStart() {
        return chapterStart;
    }

    public void setChapterStart(long chapterStart) {
        this.chapterStart = chapterStart;
    }

    public long getChapterEnd() {
        return chapterEnd;
    }

    public void setChapterEnd(long chapterEnd) {
        this.chapterEnd = chapterEnd;
    }

    public boolean isUnreadable() {
        return unreadable;
    }

    public void setUnreadable(boolean unreadable) {
        this.unreadable = unreadable;
    }
}
