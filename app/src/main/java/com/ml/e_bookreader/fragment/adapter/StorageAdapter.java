package com.ml.e_bookreader.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ml.e_bookreader.R;
import com.ml.e_bookreader.db.DbSource;
import com.ml.e_bookreader.utils.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Date: 2023/3/7 19:38
 * Description: 本地文件夹列表
 */
public class StorageAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    public StorageAdapter(int layoutResId, @Nullable List<File> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, File file) {
        holder.setBackgroundResource(R.id.file_icon_img, file.isDirectory() ? R.drawable.icon_folder : R.drawable.icon_book)
                .setText(R.id.file_name_txt, file.getName())
                .setTextColor(R.id.file_name_txt, ContextCompat.getColor(getContext(), R.color.black))
                .setText(R.id.desc_txt, file.isDirectory() ? file.list().length + "项" : FileUtils.getFileSize(file.length()));
        if (DbSource.getInstance().loadByUrl(file.getAbsolutePath()) != null) {
            holder.setVisible(R.id.is_import_txt, true)
                    .setBackgroundResource(R.id.file_icon_img, R.drawable.icon_book_select)
                    .setTextColor(R.id.file_name_txt, ContextCompat.getColor(getContext(), R.color.hint_color));
        }
    }

}
