package com.ml.e_bookreader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ml.e_bookreader.R;
import com.ml.e_bookreader.adapter.SettingAdapter;
import com.ml.e_bookreader.databinding.DialogReadSettingBinding;
import com.ml.e_bookreader.reader.ReadSettingManager;
import com.ml.e_bookreader.reader.page.PageLoader;
import com.ml.e_bookreader.reader.page.PageMode;
import com.ml.e_bookreader.reader.page.PageStyle;
import com.ml.e_bookreader.utils.BrightnessUtils;
import com.ml.e_bookreader.utils.ScreenUtils;
import com.ml.e_bookreader.utils.Utils;

import org.slf4j.helpers.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by newbiechen on 17-5-18.
 */

public class ReadSettingDialog extends Dialog {
    private static final String TAG = "ReadSettingDialog";
    private static final int DEFAULT_TEXT_SIZE = 16;
    private DialogReadSettingBinding binding;
    /************************************/
    private SettingAdapter settingAdapter;
    private ReadSettingManager mSettingManager;
    private PageLoader mPageLoader;
    private Activity mActivity;

    private PageMode mPageMode;
    private PageStyle mPageStyle;

    private int mBrightness;
    private int mTextSize;

    private boolean isBrightnessAuto;
    private boolean isTextDefault;


    public ReadSettingDialog(@NonNull Activity activity, PageLoader mPageLoader) {
        super(activity, R.style.ReadSettingDialog);
        mActivity = activity;
        this.mPageLoader = mPageLoader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogReadSettingBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());
        setUpWindow();
        initData();
        initWidget();
        initClick();
    }

    //设置Dialog显示的位置
    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = Utils.dip2px(mActivity, 260);
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();
        isBrightnessAuto = mSettingManager.isBrightnessAuto();
        mBrightness = mSettingManager.getBrightness();
        mTextSize = mSettingManager.getTextSize();
        isTextDefault = mSettingManager.isDefaultTextSize();
        mPageMode = mSettingManager.getPageMode();
        mPageStyle = mSettingManager.getPageStyle();
    }

    private void initWidget() {
        binding.sbBrightness.setProgress(mBrightness);
        binding.tvFont.setText(mTextSize + "");
        binding.cbBrightnessAuto.setChecked(isBrightnessAuto);
        binding.cbFontDefault.setChecked(isTextDefault);
        initPageMode();
        setUpAdapter();
    }

    private void setUpAdapter() {
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(mActivity, R.color.nb_read_bg_1));
        colors.add(ContextCompat.getColor(mActivity, R.color.nb_read_bg_2));
        colors.add(ContextCompat.getColor(mActivity, R.color.nb_read_bg_3));
        colors.add(ContextCompat.getColor(mActivity, R.color.nb_read_bg_4));
        colors.add(ContextCompat.getColor(mActivity, R.color.nb_read_bg_5));
        settingAdapter = new SettingAdapter(R.layout.item_read_bg, colors);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvBgList.setLayoutManager(manager);
        binding.rvBgList.setAdapter(settingAdapter);
        settingAdapter.setOnItemClickListener((adapter, view, position) -> {
                    settingAdapter.setCurrentChecked(position);
                    mPageLoader.setPageStyle(PageStyle.values()[position]);
                }
        );
    }

    private void initPageMode() {
        switch (mPageMode) {
            case SIMULATION:
                binding.rbSimulation.setChecked(true);
                break;
            case COVER:
                binding.rbCover.setChecked(true);
                break;
            case SLIDE:
                binding.rbSlide.setChecked(true);
                break;
            case NONE:
                binding.rbNone.setChecked(true);
                break;
            case SCROLL:
                binding.rbScroll.setChecked(true);
                break;
        }
    }

    private Drawable getDrawable(int drawRes) {
        return ContextCompat.getDrawable(getContext(), drawRes);
    }

    private void initClick() {
        //亮度调节
        binding.ivBrightnessMinus.setOnClickListener((v) -> {
                    if (binding.cbBrightnessAuto.isChecked()) {
                        binding.cbBrightnessAuto.setChecked(false);
                    }
                    int progress = binding.sbBrightness.getProgress() - 1;
                    if (progress < 0) return;
                    binding.sbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                }
        );
        binding.ivBrightnessPlus.setOnClickListener((v) -> {
                    if (binding.cbBrightnessAuto.isChecked()) {
                        binding.cbBrightnessAuto.setChecked(false);
                    }
                    int progress = binding.sbBrightness.getProgress() + 1;
                    if (progress > binding.sbBrightness.getMax()) return;
                    binding.sbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                    //设置进度
                    ReadSettingManager.getInstance().setBrightness(progress);
                }
        );

        binding.sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (binding.cbBrightnessAuto.isChecked()) {
                    binding.cbBrightnessAuto.setChecked(false);
                }
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(mActivity, progress);
                //存储亮度的进度条
                ReadSettingManager.getInstance().setBrightness(progress);
            }
        });

        binding.cbBrightnessAuto.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        //获取屏幕的亮度
                        BrightnessUtils.setBrightness(mActivity, BrightnessUtils.getScreenBrightness(mActivity));
                    } else {
                        //获取进度条的亮度
                        BrightnessUtils.setBrightness(mActivity, binding.sbBrightness.getProgress());
                    }
                    ReadSettingManager.getInstance().setAutoBrightness(isChecked);
                }
        );

        //字体大小调节
        binding.tvFontMinus.setOnClickListener((v) -> {
                    if (binding.cbFontDefault.isChecked()) {
                        binding.cbFontDefault.setChecked(false);
                    }
                    int fontSize = Integer.parseInt(binding.tvFont.getText().toString()) - 1;
                    if (fontSize < 0) return;
                    binding.tvFont.setText(fontSize + "");
                    mPageLoader.setTextSize(fontSize);
                }
        );

        binding.tvFontPlus.setOnClickListener((v) -> {
                    if (binding.cbFontDefault.isChecked()) {
                        binding.cbFontDefault.setChecked(false);
                    }
                    int fontSize = Integer.parseInt(binding.tvFont.getText().toString()) + 1;
                    binding.tvFont.setText(fontSize + "");
                    mPageLoader.setTextSize(fontSize);
                }
        );
        //选中默认字体
        binding.cbFontDefault.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        int fontSize = ScreenUtils.dpToPx(DEFAULT_TEXT_SIZE);
                        binding.tvFont.setText(fontSize + "");
                        mPageLoader.setTextSize(fontSize);
                    }
                }
        );

        //Page Mode 切换
        binding.rgPageMode.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    PageMode pageMode;
                    if (checkedId == R.id.rb_cover) {
                        pageMode = PageMode.COVER;
                    } else if (checkedId == R.id.rb_slide) {
                        pageMode = PageMode.SLIDE;
                    } else if (checkedId == R.id.rb_scroll) {
                        pageMode = PageMode.SCROLL;
                    } else if (checkedId == R.id.rb_none) {
                        pageMode = PageMode.NONE;
                    } else {
                        pageMode = PageMode.SIMULATION;
                    }
                    mPageLoader.setPageMode(pageMode);
                }
        );

    }

    public boolean isBrightFollowSystem() {
        if (binding != null) {
            return binding.cbBrightnessAuto.isChecked();
        } else {
            return false;
        }

    }
}
