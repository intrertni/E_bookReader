package com.ml.e_bookreader.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ml.e_bookreader.R;

import java.util.List;

public class SettingAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
    private int currentChecked;

    public void setCurrentChecked(int currentChecked) {
        this.currentChecked = currentChecked;
        notifyDataSetChanged();
    }

    public SettingAdapter(int layoutResId, @Nullable List<Integer> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Integer color) {
        holder.setBackgroundColor(R.id.read_bg_view, color)
                .setVisible(R.id.read_bg_iv_checked, currentChecked == getItemPosition(color));

    }
}
