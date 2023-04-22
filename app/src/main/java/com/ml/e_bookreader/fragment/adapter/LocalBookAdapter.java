package com.ml.e_bookreader.fragment.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ml.e_bookreader.R;
import com.ml.e_bookreader.db.bean.BookBean;

import java.util.List;

/**
 * Date: 2023/3/2 19:16
 * Description: 书架书籍适配器
 */
public class LocalBookAdapter extends BaseQuickAdapter<BookBean, BaseViewHolder> {
    private Context context;

    public LocalBookAdapter(Context context, int layoutResId, @Nullable List<BookBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookBean bookBean) {

        holder.setBackgroundResource(R.id.book_cover_img, bookBean.isAdd() ?
                        R.drawable.icon_book_select :
                        R.drawable.icon_book)
                .setGone(R.id.book_encoder_txt, bookBean.isAdd())
                .setTextColor(R.id.book_name_txt, bookBean.isAdd() ?
                        ContextCompat.getColor(context, R.color.hint_color) :
                        ContextCompat.getColor(context, R.color.black))
                .setGone(R.id.is_import_txt, !bookBean.isAdd())
                .setGone(R.id.check_img, bookBean.isAdd())
                .setText(R.id.book_encoder_txt, bookBean.getFileType())
                .setText(R.id.book_name_txt, bookBean.getBookName())
                .setText(R.id.book_size_txt, "文件大小：" + bookBean.getBookSize())
                .setBackgroundResource(R.id.check_img, bookBean.isChecked() ?
                        R.drawable.ic_radio_checked :
                        R.drawable.ic_radio_unchecked);
    }
}
