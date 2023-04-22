package com.ml.e_bookreader.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ml.e_bookreader.R;
import com.ml.e_bookreader.adapter.BookRackAdapter;
import com.ml.e_bookreader.base.BaseVbActivity;
import com.ml.e_bookreader.databinding.ActivityMainBinding;
import com.ml.e_bookreader.db.DbSource;
import com.ml.e_bookreader.db.bean.BookBean;
import com.ml.e_bookreader.db.bean.ChapterBean;
import com.ml.e_bookreader.utils.Utils;
import com.permissionx.guolindev.PermissionX;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends BaseVbActivity<ActivityMainBinding> {
    private BookRackAdapter rackAdapter;
    private List<BookBean> bookBeans = new ArrayList<>();
    private boolean isEdit;
    private boolean isAllChecked; //是否全部选中

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    getData();
                }
            });

    @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
    @Override
    public void initView() {
        initAdapter();
        getData();

        // 本地导入
        binding.localImg.setOnClickListener(view12 -> jumpToLocal());
        //管理书架
        binding.editImg.setOnClickListener(view -> {
            rackAdapter.setEdit(!isEdit);
            rackAdapter.notifyDataSetChanged();
            isEdit = !isEdit;
            binding.removeLay.setVisibility(isEdit ? View.VISIBLE : View.GONE);
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
            for (BookBean bookBean : rackAdapter.getData()) {
                bookBean.setChecked(isAllChecked);
            }
            rackAdapter.notifyDataSetChanged();
            binding.removeBookTxt.setText(String.format("移出书架（%d)", isAllChecked ? getSelectCount() : 0));
        });

        //移除书架点击事件
        binding.removeBookTxt.setOnClickListener(view -> {
            List<BookBean> selectBooks = rackAdapter.getData().stream().filter(BookBean::isChecked).collect(Collectors.toList());
            if (selectBooks.size() > 0) {
                DbSource.getInstance().deleteAll(selectBooks);
                for (BookBean bookBean : selectBooks) {
                    List<ChapterBean> chapterBeans = DbSource.getInstance().loadChapterById(bookBean.getBookId());
                    DbSource.getInstance().deleteChapterAll(chapterBeans);
                }
                Utils.showToast("书籍移出书架成功！");
                getData();
            } else {
                Utils.showToast("请先选择书籍！");
            }
        });
    }

    //获取数据
    private void getData() {
        //获取已加入书架的所有书籍
        bookBeans = DbSource.getInstance().getAllBook();
        //如果书架没有书籍，那么隐藏编辑按钮
        if (bookBeans == null || bookBeans.size() == 0) {
            binding.editImg.setVisibility(View.GONE);
        } else {
            binding.editImg.setVisibility(View.VISIBLE);
        }
        binding.removeLay.setVisibility(View.GONE);
        isEdit = false;
        rackAdapter.setEdit(false);
        rackAdapter.setList(bookBeans);
    }

    //初始化adapter
    private void initAdapter() {
        binding.bookList.setLayoutManager(new GridLayoutManager(this, 3));
        rackAdapter = new BookRackAdapter(this, R.layout.item_book_layout, bookBeans);
        //如果书架没有书籍，显示书架空页面
        rackAdapter.setEmptyView(getEmptyDataView());
        binding.bookList.setAdapter(rackAdapter);
        rackAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (isEdit) { //如果是编辑模式
                boolean isChecked = rackAdapter.getItem(position).isChecked();
                rackAdapter.getItem(position).setChecked(!isChecked);
                rackAdapter.notifyItemChanged(position);
                setSelectData();
            } else {
                ReadActivity.startActivity(this, rackAdapter.getItem(position).getBookUrl());
            }
        });
    }

    /**
     * 设置选中数据，更新页面状态
     */
    @SuppressLint("DefaultLocale")
    private void setSelectData() {
        binding.removeBookTxt.setText(String.format("移出书架（%d)", getSelectCount()));
        if (getSelectCount() == bookBeans.size()) {
            binding.checkTxt.setText("取消全选");
            isAllChecked = true;
        } else {
            binding.checkTxt.setText("全选");
            isAllChecked = false;
        }
    }

    /**
     * 获取选中的书籍数量
     *
     * @return
     */
    private int getSelectCount() {
        return (int) rackAdapter.getData().stream().filter(BookBean::isChecked).count();
    }


    /**
     * 获取空数据布局
     *
     * @return
     */
    private View getEmptyDataView() {
        View view = getLayoutInflater().inflate(R.layout.empty_bookshelf_layout, binding.bookList, false);
        view.setOnClickListener(view1 -> jumpToLocal());
        return view;
    }

    /**
     * 跳转本地书籍导入页面
     */
    private void jumpToLocal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PermissionX.init(this).permissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    .onExplainRequestReason((scope, deniedList) ->
                            scope.showRequestReasonDialog(deniedList, "需要您同意以下权限才能正常使用", "设置", "拒绝"))
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            launcher.launch(new Intent(MainActivity.this, LocalImportActivity.class));
                        } else {
                            Utils.showToast("没有储存权限，无法添加本地书籍！");
                        }
                    });
        } else {
            PermissionX.init(this).permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE).request((allGranted, grantedList, deniedList) -> {
                if (allGranted) {
                    launcher.launch(new Intent(MainActivity.this, LocalImportActivity.class));
                } else {
                    Utils.showToast("没有储存权限，无法添加本地书籍！");
                }
            });
        }

    }

}