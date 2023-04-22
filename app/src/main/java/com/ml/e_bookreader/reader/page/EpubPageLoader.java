package com.ml.e_bookreader.reader.page;

import android.text.TextUtils;

import com.ml.e_bookreader.db.DbSource;
import com.ml.e_bookreader.db.bean.BookBean;
import com.ml.e_bookreader.db.bean.ChapterBean;
import com.ml.e_bookreader.utils.FileUtils;
import com.ml.e_bookreader.utils.RxUtils;
import com.ml.e_bookreader.utils.Utils;

import net.sf.jazzlib.ZipFile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;

/**
 * Date: 2023/3/2
 */
public class EpubPageLoader extends PageLoader {

    //编码类型
    private Disposable mChapterDisp = null;
    private Book epubBook;
    //编码类型
    private Charset mCharset;

    public EpubPageLoader(PageView pageView, BookBean bookShelfBean) {
        super(pageView, bookShelfBean);
        mStatus = STATUS_PARING;
    }

    @Override
    public void refreshChapterList() {
        if (mCollBook == null) return;
        Single.create((SingleOnSubscribe<ChapterBean>) e -> {
                    File bookFile = new File(mCollBook.getBookUrl());
                    if (!bookFile.exists()) {
                        Utils.showToast("书籍源文件不存在");
                    }
                    epubBook = readBook(bookFile);
                    if (epubBook == null) {
                        Utils.showToast("文件解析失败");
                        return;
                    }
                    mCharset = Charset.forName(mCollBook.getFileEncoder());
                    if (TextUtils.isEmpty(mCollBook.getBookCoverUrl())) {
                        saveCover();
                    }
                    checkChapterList();
                    e.onSuccess(new ChapterBean());
                }).subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ChapterBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mChapterDisp = d;
                    }

                    @Override
                    public void onSuccess(ChapterBean value) {
                        mChapterDisp = null;
                        isChapterListPrepare = true;

                        // 提示目录加载完成
                        if (mPageChangeListener != null) {
                            mPageChangeListener.onCategoryFinish(mChapterList);
                        }

                        // 加载并显示当前章节
                        openChapter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        chapterError();
                    }
                });
    }

    @Override
    protected BufferedReader getChapterReader(ChapterBean chapter) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(getChapterContent(chapter).getBytes(mCharset));
        BufferedReader br = new BufferedReader(new InputStreamReader(bais, mCharset));
        return br;
    }

    @Override
    protected boolean hasChapterData(ChapterBean chapter) {
        return true;
    }


    public static Book readBook(File file) {
        try {
            EpubReader epubReader = new EpubReader();
            MediaType[] lazyTypes = {
                    MediatypeService.CSS,
                    MediatypeService.GIF,
                    MediatypeService.JPG,
                    MediatypeService.PNG,
                    MediatypeService.MP3,
                    MediatypeService.MP4};
            ZipFile zipFile = new ZipFile(file);
            return epubReader.readEpubLazy(zipFile, "utf-8", Arrays.asList(lazyTypes));
        } catch (Exception e) {
            return null;
        }
    }

    private void saveCover() throws IOException {
        byte[] data = epubBook.getCoverImage().getData();
        FileUtils.writeFile(data, FileUtils.getCachePath() + "/covers/", mCollBook.getBookName() + ".jpg");
        mCollBook.setBookCoverUrl(FileUtils.getCachePath() + "/covers/" + mCollBook.getBookName() + ".jpg");
        DbSource.getInstance().update(mCollBook);
    }


    private List<ChapterBean> loadChapters() {
        mChapterList = new ArrayList<>();
        List<TOCReference> refs = epubBook.getTableOfContents().getTocReferences();
        if (refs == null || refs.isEmpty()) {
            List<SpineReference> spineReferences = epubBook.getSpine().getSpineReferences();
            for (int i = 0, size = spineReferences.size(); i < size; i++) {
                Resource resource = spineReferences.get(i).getResource();
                String title = resource.getTitle();
                if (TextUtils.isEmpty(title)) {
                    try {
                        Document doc = Jsoup.parse(new String(resource.getData(), mCharset));
                        Elements elements = doc.getElementsByTag("title");
                        if (elements.size() > 0) {
                            title = elements.get(0).text();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ChapterBean bean = new ChapterBean();
                bean.setChapterNumber(i);
                bean.setBookId(mCollBook.getBookId());
                bean.setEncoding(resource.getHref());
                bean.setUnreadable(false);
                if (i == 0 && title.isEmpty()) {
                    bean.setChapterTitle("封面");
                } else {
                    bean.setChapterTitle(title);
                }
                bean.setChapterEnd(1);
                mChapterList.add(bean);
            }
        } else {
            parseMenu(refs, 0);
            for (int i = 0; i < mChapterList.size(); i++) {
                mChapterList.get(i).setChapterNumber(i);
                mChapterList.get(i).setChapterEnd(1);
            }
        }

        return mChapterList;
    }

    private void parseMenu(List<TOCReference> refs, int level) {
        if (refs == null) return;
        for (TOCReference ref : refs) {
            if (ref.getResource() != null) {
                ChapterBean chapter = new ChapterBean();
                chapter.setBookId(mCollBook.getBookId());
                chapter.setChapterTitle(ref.getTitle());
                chapter.setEncoding(ref.getCompleteHref());
                mChapterList.add(chapter);
            }
            if (ref.getChildren() != null && !ref.getChildren().isEmpty()) {
                parseMenu(ref.getChildren(), level + 1);
            }
        }
    }

    protected String getChapterContent(ChapterBean chapter) throws Exception {
        Resource resource = epubBook.getResources().getByHref(chapter.getEncoding());
        StringBuilder content = new StringBuilder();
        Document doc = Jsoup.parse(new String(resource.getData(), mCharset));
        Elements elements = doc.getAllElements();
        for (Element element : elements) {
            List<TextNode> contentEs = element.textNodes();
            for (int i = 0; i < contentEs.size(); i++) {
                String text = contentEs.get(i).text().trim();
                text = Utils.formatHtml(text);
                if (elements.size() > 1) {
                    if (text.length() > 0) {
                        if (content.length() > 0) {
                            content.append("\r\n");
                        }
                        content.append("\u3000\u3000").append(text);
                    }
                } else {
                    content.append(text);
                }
            }
        }
        return content.toString();
    }

    private void checkChapterList() {
        mChapterList = DbSource.getInstance().loadChapterById(mCollBook.getBookId());
        if (!mChapterList.isEmpty()) {
            Observable.just(mCollBook);
        } else {
            // 通过RxJava异步处理分章事件
            Single.create((SingleOnSubscribe<ChapterBean>) e -> {
                        loadChapters();
                        e.onSuccess(new ChapterBean());
                    }).compose(RxUtils::toSimpleSingle)
                    .subscribe(new SingleObserver<ChapterBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mChapterDisp = d;
                        }

                        @Override
                        public void onSuccess(ChapterBean value) {
                            mChapterDisp = null;
                            isChapterListPrepare = true;

                            // 提示目录加载完成
                            if (mPageChangeListener != null) {
                                mPageChangeListener.onCategoryFinish(mChapterList);
                            }
                            DbSource.getInstance().insertChapterAll(mChapterList);
                            // 加载并显示当前章节
                            openChapter();
                        }

                        @Override
                        public void onError(Throwable e) {
                            chapterError();
                        }
                    });
        }
    }


}
