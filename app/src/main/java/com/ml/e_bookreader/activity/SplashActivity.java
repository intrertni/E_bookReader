package com.ml.e_bookreader.activity;

import android.content.Intent;

import com.ml.e_bookreader.base.BaseVbActivity;
import com.ml.e_bookreader.databinding.ActivitySplashBinding;

/**
 * Date: 2023/3/2 16:46
 * Description: 闪屏页
 */
public class SplashActivity extends BaseVbActivity<ActivitySplashBinding> {

    @Override
    public void initView() {
        //跳过的点击事件
        binding.rlEnter.setOnClickListener(view -> jumpToMain());
        //开始倒计时
        binding.progress.startDownTime(3000, this::jumpToMain);
    }


    public void jumpToMain() {
        binding.progress.stopCountDown();
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
