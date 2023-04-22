package com.ml.e_bookreader.db.bean;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Date: 2023/3/1 14:04
 * Description: 小说详细数据
 */
@Entity(tableName = "book_table")
public class BookBean implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int bookId;
    private String bookName;//书名
    private String bookUrl;//本地书籍地址
    private String fileType;//书籍类型（txt,ePub）
    private String fileEncoder;//文件编码
    private String bookCoverUrl;//封面图片url
    private String bookDesc;//简介
    private String bookAuthor;//作者
    private String updateDate;//更新时间
    private String bookSize;//书籍大小
    private String wordCount;
    private boolean isUpdate = true;//未阅读
    private String historyBookId;//上次关闭时的章节ID
    private int historyBookNum;//上次关闭时的章节数
    private int sortCode;//排序编码
    private int noReadNum;//未读章数量
    private int BookTotalNum;//总章节数
    private int lastReadPosition;//上次阅读到的章节的位置
    private String lastChapter;
    @Ignore
    private boolean isChecked; //是否选中
    @Ignore
    private boolean isAdd; //是否已经添加

    public String getBookSize() {
        return bookSize;
    }

    public void setBookSize(String bookSize) {
        this.bookSize = bookSize;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getFileEncoder() {
        return fileEncoder;
    }

    public void setFileEncoder(String fileEncoder) {
        this.fileEncoder = fileEncoder;
    }

    public String getBookCoverUrl() {
        return bookCoverUrl;
    }

    public void setBookCoverUrl(String bookCoverUrl) {
        this.bookCoverUrl = bookCoverUrl;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getWordCount() {
        return wordCount;
    }

    public void setWordCount(String wordCount) {
        this.wordCount = wordCount;
    }

    public String getHistoryBookId() {
        return historyBookId;
    }

    public void setHistoryBookId(String historyBookId) {
        this.historyBookId = historyBookId;
    }

    public int getHistoryBookNum() {
        return historyBookNum;
    }

    public void setHistoryBookNum(int historyBookNum) {
        this.historyBookNum = historyBookNum;
    }

    public int getSortCode() {
        return sortCode;
    }

    public void setSortCode(int sortCode) {
        this.sortCode = sortCode;
    }

    public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
    }

    public int getBookTotalNum() {
        return BookTotalNum;
    }

    public void setBookTotalNum(int bookTotalNum) {
        BookTotalNum = bookTotalNum;
    }

    public int getLastReadPosition() {
        return lastReadPosition;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public void setLastReadPosition(int lastReadPosition) {
        this.lastReadPosition = lastReadPosition;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }


}
