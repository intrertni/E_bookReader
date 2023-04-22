package com.ml.e_bookreader.fragment;

import android.media.MediaScannerConnection;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ml.e_bookreader.R;
import com.ml.e_bookreader.db.DbSource;
import com.ml.e_bookreader.fragment.adapter.LocalBookAdapter;
import com.ml.e_bookreader.base.BaseVbFragment;
import com.ml.e_bookreader.databinding.FragmentSmartImportBinding;
import com.ml.e_bookreader.db.bean.BookBean;
import com.ml.e_bookreader.utils.Utils;
import com.ml.e_bookreader.utils.media.MediaStoreHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Date: 2023/3/3 10:19
 * Description: 智能导入
 */
public class SmartImportFragment extends BaseVbFragment<FragmentSmartImportBinding> {
    private LocalBookAdapter bookAdapter;
    private boolean isAllChecked; //是否全部选中

    @Override
    protected void initView() {
        binding.bookList.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.bookList.addItemDecoration(new DividerItemDecoration(requireActivity(), 1));
        bookAdapter = new LocalBookAdapter(requireActivity(), R.layout.item_smart_layout, new ArrayList<>());
        //如果书架没有书籍，显示书架空页面
        bookAdapter.setEmptyView(getEmptyDataView());
        binding.bookList.setAdapter(bookAdapter);
        bookAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (bookAdapter.getItem(position).isAdd()) {
                return;
            }
            boolean isChecked = bookAdapter.getItem(position).isChecked();
            bookAdapter.getItem(position).setChecked(!isChecked);
            bookAdapter.notifyItemChanged(position);
            setSelectData();
        });
        //全选按钮点击事件
        binding.checkTxt.setOnClickListener(view -> {
            if (isAllChecked) {
                binding.checkTxt.setText("全选");
                isAllChecked = false;
            } else {
                binding.checkTxt.setText("取消全选");
                isAllChecked = true;
            }
            for (BookBean bookBean : bookAdapter.getData()) {
                bookBean.setChecked(isAllChecked);
            }
            bookAdapter.notifyDataSetChanged();
            binding.addBookTxt.setText(String.format("加入书架（%d)", isAllChecked ? getSelectCount() : 0));
        });
        //加入书架点击事件
        binding.addBookTxt.setOnClickListener(view -> {
            List<BookBean> selectBooks = bookAdapter.getData().stream().filter(bookBean -> bookBean.isChecked() && !bookBean.isAdd()).collect(Collectors.toList());
            if (selectBooks.size() > 0) {
                DbSource.getInstance().insertAll(selectBooks);
                Utils.showToast("书籍插入成功！");
                initSelectView();
                //添加完书架之后刷新当前页面数据
                getData();
            } else {
                Utils.showToast("请先选择书籍");
            }
        });
        getData();
    }

    /**
     * 设置选中数据，更新页面状态
     */
    private void setSelectData() {
        binding.addBookTxt.setText(String.format("加入书架（%d)", getSelectCount()));
        if (getUnSelectAndUnAddCount(bookAdapter.getData()) == 0) {
            binding.checkTxt.setText("取消全选");
            isAllChecked = true;
        } else {
            binding.checkTxt.setText("全选");
            isAllChecked = false;
        }
    }

    /**
     * 获取选中的并且未被添加的书籍数量
     *
     * @return
     */
    private int getSelectCount() {
        return (int) bookAdapter.getData().stream().filter(bookBean -> bookBean.isChecked() && !bookBean.isAdd()).count();
    }

    /**
     * 获取即没被选中又没有被添加的书籍数量
     *
     * @return
     */
    private int getUnSelectAndUnAddCount(List<BookBean> bookBeans) {
        return (int) bookBeans.stream().filter(bookBean -> !bookBean.isChecked() && !bookBean.isAdd()).count();
    }

    /**
     * 初始化选择状态
     */
    private void initSelectView() {
        if (getUnSelectAndUnAddCount(bookAdapter.getData()) == 0) {
            binding.addLay.setVisibility(View.GONE);
        } else {
            binding.addLay.setVisibility(View.VISIBLE);
            binding.checkTxt.setText("全选");
            binding.addBookTxt.setText("加入书架（0)");
        }
    }

    //查询本地书籍
    public void getData() {
        //更新媒体库
        try {
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()},
                    new String[]{"text/plain", "application/epub+zip"}, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //查询本地数据并更新数据
        MediaStoreHelper.getAllBookFile(requireActivity(), (data) -> {
            if (data.size() > 0 && getUnSelectAndUnAddCount(data) != 0) {
                binding.addLay.setVisibility(View.VISIBLE);
            } else {
                binding.addLay.setVisibility(View.GONE);
            }
            bookAdapter.setList(data);
        });
    }

    /**
     * 获取空数据布局
     *
     * @return
     */
    private View getEmptyDataView() {
        View view = getLayoutInflater().inflate(R.layout.empty_bookshelf_layout, binding.bookList, false);
        AppCompatTextView emptyTxt = view.findViewById(R.id.empty_txt);
        emptyTxt.setText("本地暂无书籍");
        return view;
    }

}
