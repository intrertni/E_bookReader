package com.ml.e_bookreader.activity;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ml.e_bookreader.R;
import com.ml.e_bookreader.base.BaseVbActivity;
import com.ml.e_bookreader.databinding.ActivityLocalImportBinding;
import com.ml.e_bookreader.fragment.SmartImportFragment;
import com.ml.e_bookreader.fragment.StorageImportFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2023/3/2 20:27
 * Description: 本地书籍导入
 */
public class LocalImportActivity extends BaseVbActivity<ActivityLocalImportBinding> {
    private final String[] tabs = new String[]{"智能导入", "手动选择"};
    private final List<Fragment> fragmentList = new ArrayList<>();
    private SmartImportFragment smartImportFragment;
    private StorageImportFragment storageImportFragment;

    @Override
    public void initView() {
        //返回
        binding.backLay.setOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });
        //禁用预加载
        binding.viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        //初始化fragment
        smartImportFragment = new SmartImportFragment();
        storageImportFragment = new StorageImportFragment();
        fragmentList.add(smartImportFragment);
        fragmentList.add(storageImportFragment);
        bindData();
    }

    public void bindData() {
        //Adapter
        binding.viewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                //FragmentStateAdapter内部自己会管理已实例化的fragment对象。
                // 所以不需要考虑复用的问题
                return fragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });
        //viewPager 页面切换监听监听
        binding.viewPager.registerOnPageChangeCallback(changeCallback);
        //这里可以自定义TabView
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabIndicator, binding.viewPager, (tab, position) -> {
            //这里可以自定义TabView
            TextView tabView = new TextView(LocalImportActivity.this);
            int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_selected};
            states[1] = new int[]{};
            int[] colors = new int[]{
                    ContextCompat.getColor(this, R.color.white),
                    ContextCompat.getColor(this, R.color.white_95)};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            tabView.setGravity(Gravity.CENTER);
            tabView.setText(tabs[position]);
            tabView.setTextSize(16);
            tabView.setTextColor(colorStateList);
            tab.setCustomView(tabView);
        });
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
    }

    private final ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            //可以来设置选中时tab的大小
            int tabCount = binding.tabIndicator.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = binding.tabIndicator.getTabAt(i);
                if (tab != null) {
                    TextView tabView = (TextView) tab.getCustomView();
                    if (tabView != null) {
                        if (tab.getPosition() == position) {
                            tabView.setTypeface(Typeface.DEFAULT_BOLD);
                        } else {
                            tabView.setTypeface(Typeface.DEFAULT);
                        }
                    }
                }
            }
            if (position == 0) smartImportFragment.getData();
        }
    };

    @Override
    public void onBackPressed() {
        if (binding.viewPager.getCurrentItem() == 1) {
            storageImportFragment.backHistory(1);
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }
}
