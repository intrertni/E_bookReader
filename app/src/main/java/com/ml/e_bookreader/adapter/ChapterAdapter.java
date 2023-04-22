package com.ml.e_bookreader.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ml.e_bookreader.R;
import com.ml.e_bookreader.db.bean.ChapterBean;

import java.util.List;

public class ChapterAdapter extends BaseQuickAdapter<ChapterBean, BaseViewHolder> {
    private int currentSelected = 0;

    public ChapterAdapter(int layoutResId, @Nullable List<ChapterBean> data) {
        super(layoutResId, data);
    }

    public void setCurrentSelected(int currentSelected) {
        this.currentSelected = currentSelected;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, ChapterBean chapterBean) {
        AppCompatTextView chapterTxt = holder.getView(R.id.tv_chapter);
        chapterTxt.setTextColor(getItemPosition(chapterBean) == currentSelected ?
                ContextCompat.getColor(getContext(), R.color.blue) :
                ContextCompat.getColor(getContext(), R.color.black));
        chapterTxt.setText(chapterBean.getChapterTitle());
        chapterTxt.setSelected(getItemPosition(chapterBean) == currentSelected);
    }
}
