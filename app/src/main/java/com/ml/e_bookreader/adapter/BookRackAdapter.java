package com.ml.e_bookreader.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ml.e_bookreader.R;
import com.ml.e_bookreader.db.bean.BookBean;
import com.ml.e_bookreader.utils.Utils;

import java.util.List;

/**
 * Date: 2023/3/2 19:16
 * Description: 书架书籍适配器
 */
public class BookRackAdapter extends BaseQuickAdapter<BookBean, BaseViewHolder> {
    private Context context;
    private boolean isEdit;

    public BookRackAdapter(Context context, int layoutResId, @Nullable List<BookBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookBean bookBean) {
        Glide.with(context).asBitmap()
                .load(bookBean.getBookCoverUrl())
                .placeholder(R.drawable.default_book_bg)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(Utils.dip2px(context, 5))))
                .into((ImageView) holder.getView(R.id.cover_img));

        if (TextUtils.isEmpty(bookBean.getBookCoverUrl())) {
            holder.setText(R.id.cover_name_txt, bookBean.getBookName());
        }
        holder.setText(R.id.book_name_txt, bookBean.getBookName())
                .setVisible(R.id.check_img, isEdit)
                .setBackgroundResource(R.id.check_img, bookBean.isChecked() ?
                        R.drawable.ic_radio_checked_white : R.drawable.ic_radio_unchecked_white);
    }
}
