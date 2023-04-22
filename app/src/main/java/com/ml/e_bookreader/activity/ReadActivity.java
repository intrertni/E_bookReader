package com.ml.e_bookreader.activity;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ml.e_bookreader.R;
import com.ml.e_bookreader.adapter.ChapterAdapter;
import com.ml.e_bookreader.base.BaseVbActivity;
import com.ml.e_bookreader.databinding.ActivityReadBinding;
import com.ml.e_bookreader.db.DbSource;
import com.ml.e_bookreader.db.bean.BookBean;
import com.ml.e_bookreader.db.bean.ChapterBean;
import com.ml.e_bookreader.dialog.ReadSettingDialog;
import com.ml.e_bookreader.reader.ReadSettingManager;
import com.ml.e_bookreader.reader.page.PageLoader;
import com.ml.e_bookreader.reader.page.PageView;
import com.ml.e_bookreader.utils.BrightnessUtils;
import com.ml.e_bookreader.utils.ScreenUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends BaseVbActivity<ActivityReadBinding> {
    public static final String EXTRA_COLL_BOOK = "extra_coll_book";
    // 注册 Brightness 的 uri
    private final Uri BRIGHTNESS_MODE_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private final Uri BRIGHTNESS_ADJ_URI =
            Settings.System.getUriFor("screen_auto_brightness_adj");
    //页面加载器
    private PageLoader mPageLoader;
    //底部动画
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;
    //书籍实体
    private BookBean bookBean;
    //设置控制器Dialog
    private ReadSettingDialog mSettingDialog;

    private String bookUrl;

    private ChapterAdapter chapterAdapter;

    private boolean isNightMode = false;
    private boolean isRegistered = false;

    public static void startActivity(Context context, String bookUrl) {
        context.startActivity(new Intent(context, ReadActivity.class)
                .putExtra(EXTRA_COLL_BOOK, bookUrl));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, intentFilter);
        isNightMode = ReadSettingManager.getInstance().isNightMode();
        bookUrl = getIntent().getStringExtra(EXTRA_COLL_BOOK);
        bookBean = DbSource.getInstance().loadByUrl(bookUrl);
        initWidget();
    }

    // 接收电池信息和时间更新的广播
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                mPageLoader.updateBattery(level);
            }
            // 监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                mPageLoader.updateTime();
            }
        }
    };

    // 亮度调节监听
    // 由于亮度调节没有 Broadcast 而是直接修改 ContentProvider 的。所以需要创建一个 Observer 来监听 ContentProvider 的变化情况。
    private final ContentObserver mBrightObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);
            // 判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mSettingDialog.isBrightFollowSystem()) return;
            // 如果系统亮度改变，则修改当前 Activity 亮度
            if (BRIGHTNESS_URI.equals(uri) && !BrightnessUtils.isAutoBrightness(ReadActivity.this)) {
                //亮度模式为手动模式 值改变
                BrightnessUtils.setBrightness(ReadActivity.this, BrightnessUtils.getScreenBrightness(ReadActivity.this));
            } else if (BRIGHTNESS_ADJ_URI.equals(uri) && BrightnessUtils.isAutoBrightness(ReadActivity.this)) {
                //亮度模式为自动模式 值改变
                BrightnessUtils.setDefaultBrightness(ReadActivity.this);
            }
        }
    };

    // 注册亮度观察者
    private void registerBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    final ContentResolver cr = getContentResolver();
                    cr.unregisterContentObserver(mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver);
                    isRegistered = true;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    //解注册
    private void unregisterBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (isRegistered) {
                    getContentResolver().unregisterContentObserver(mBrightObserver);
                    isRegistered = false;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * 初始化个个部件
     */
    private void initWidget() {
        //获取页面加载器
        mPageLoader = binding.readPvPage.getPageLoader(bookBean);
        //禁止滑动展示DrawerLayout
        binding.readDlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //侧边打开后，返回键能够起作用
        binding.readDlSlide.setFocusableInTouchMode(false);
        mSettingDialog = new ReadSettingDialog(this, mPageLoader);
        try {
            mPageLoader.refreshChapterList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initAdapter();
        toggleNightMode();
        setBrightness();
        initBottomMenu();
        initListener();
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        binding.chapterList.setLayoutManager(new LinearLayoutManager(this));
        chapterAdapter = new ChapterAdapter(R.layout.item_chapter_laytout, new ArrayList<>());
        binding.chapterList.setAdapter(chapterAdapter);
    }

    /**
     * 初始化夜间按钮
     */
    private void toggleNightMode() {
        if (isNightMode) {
            binding.readTvNightMode.setText("日间");
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_morning);
            binding.readTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        } else {
            binding.readTvNightMode.setText("夜间");
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_read_menu_night);
            binding.readTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    /**
     * 设置亮度
     */
    private void setBrightness() {
        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto()) {
            BrightnessUtils.setDefaultBrightness(this);
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().getBrightness());
        }
    }

    /**
     * 初始化底部弹出框
     */
    private void initBottomMenu() {
        //判断是否全屏
        if (ReadSettingManager.getInstance().isFullScreen()) {
            //还需要设置mBottomMenu的底部高度
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.readLlBottomMenu.getLayoutParams();
            params.bottomMargin = ScreenUtils.getNavigationBarHeight();
            binding.readLlBottomMenu.setLayoutParams(params);
        } else {
            //设置mBottomMenu的底部距离
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.readLlBottomMenu.getLayoutParams();
            params.bottomMargin = 0;
            binding.readLlBottomMenu.setLayoutParams(params);
        }
    }

    /**
     * 各种点击监听
     */
    private void initListener() {
        mPageLoader.setOnPageChangeListener(
                new PageLoader.OnPageChangeListener() {
                    @Override
                    public void onChapterChange(int pos) {
                        chapterAdapter.setCurrentSelected(pos);
                    }

                    @Override
                    public void requestChapters(List<ChapterBean> requestChapters) {
                    }

                    @Override
                    public void onCategoryFinish(List<ChapterBean> chapters) {
                        chapterAdapter.setList(chapters);
                    }

                    @Override
                    public void onPageCountChange(int count) {
                        binding.readSbChapterProgress.setMax(Math.max(0, count - 1));
                        binding.readSbChapterProgress.setProgress(0);
                        // 如果处于错误状态，那么就冻结使用
                        binding.readSbChapterProgress.setEnabled(mPageLoader.getPageStatus() != PageLoader.STATUS_LOADING
                                && mPageLoader.getPageStatus() != PageLoader.STATUS_ERROR);
                    }

                    @Override
                    public void onPageChange(int pos) {
                        binding.readSbChapterProgress.post(
                                () -> binding.readSbChapterProgress.setProgress(pos)
                        );
                    }
                }
        );

        binding.readSbChapterProgress.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //进行切换
                        int pagePos = binding.readSbChapterProgress.getProgress();
                        if (pagePos != mPageLoader.getPagePos()) {
                            mPageLoader.skipToPage(pagePos);
                        }
                    }
                }
        );

        binding.readPvPage.setTouchListener(new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return !hideReadMenu();
            }

            @Override
            public void center() {
                toggleMenu();
            }

            @Override
            public void prePage() {
            }

            @Override
            public void nextPage() {
            }

            @Override
            public void cancel() {
            }
        });

        chapterAdapter.setOnItemClickListener((adapter, view, position) -> {
            binding.readDlSlide.closeDrawer(GravityCompat.START);
            mPageLoader.skipToChapter(position);
        });

        //目录
        binding.readTvCategory.setOnClickListener((v) -> {
            //移动到指定位置
            if (chapterAdapter.getItemCount() > 0) {
                binding.chapterList.smoothScrollToPosition(mPageLoader.getChapterPos());
            }
            //切换菜单
            toggleMenu();
            //打开侧滑动栏
            binding.readDlSlide.openDrawer(GravityCompat.START);
        });

        //设置
        binding.readTvSetting.setOnClickListener((v) -> {
            toggleMenu();
            mSettingDialog.show();
        });

        //上一章
        binding.readTvPreChapter.setOnClickListener((v) -> {
            if (mPageLoader.skipPreChapter()) {
                chapterAdapter.setCurrentSelected(mPageLoader.getChapterPos());
            }
        });
        //下一章
        binding.readTvNextChapter.setOnClickListener((v) -> {
            if (mPageLoader.skipNextChapter()) {
                chapterAdapter.setCurrentSelected(mPageLoader.getChapterPos());
            }
        });
        //夜间模式切换
        binding.readTvNightMode.setOnClickListener((v) -> {
            isNightMode = !isNightMode;
            mPageLoader.setNightMode(isNightMode);
            toggleNightMode();
        });
    }

    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private boolean hideReadMenu() {
        if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return true;
        }
        return false;
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private void toggleMenu() {
        initMenuAnim();
        if (binding.readLlBottomMenu.getVisibility() == View.VISIBLE) {
            //关闭
            binding.readLlBottomMenu.startAnimation(mBottomOutAnim);
            binding.readLlBottomMenu.setVisibility(GONE);
        } else {
            binding.readLlBottomMenu.setVisibility(View.VISIBLE);
            binding.readLlBottomMenu.startAnimation(mBottomInAnim);
        }
    }

    //初始化菜单动画
    private void initMenuAnim() {
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        //退出的速度要快
        mBottomOutAnim.setDuration(200);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBrightObserver();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPageLoader.saveRecord();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterBrightObserver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mPageLoader.closeBook();
        mPageLoader = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isVolumeTurnPage = ReadSettingManager
                .getInstance().isVolumeTurnPage();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (isVolumeTurnPage) {
                    return mPageLoader.skipToPrePage();
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (isVolumeTurnPage) {
                    return mPageLoader.skipToNextPage();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (binding.readLlBottomMenu.getVisibility() == View.VISIBLE) {
            // 非全屏下才收缩，全屏下直接退出
            if (!ReadSettingManager.getInstance().isFullScreen()) {
                toggleMenu();
            }
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
        } else if (binding.readDlSlide.isDrawerOpen(GravityCompat.START)) {
            binding.readDlSlide.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

}
