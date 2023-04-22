package com.ml.e_bookreader.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ml.e_bookreader.R;
import com.ml.e_bookreader.base.BaseVbFragment;
import com.ml.e_bookreader.databinding.FragmentStorageImportBinding;
import com.ml.e_bookreader.db.DbSource;
import com.ml.e_bookreader.db.bean.BookBean;
import com.ml.e_bookreader.fragment.adapter.StorageAdapter;
import com.ml.e_bookreader.utils.FileComparator;
import com.ml.e_bookreader.utils.FileUtils;
import com.ml.e_bookreader.utils.SimpleFileFilter;
import com.ml.e_bookreader.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 2023/3/3 10:19
 * Description: 智能导入
 */
public class StorageImportFragment extends BaseVbFragment<FragmentStorageImportBinding> {
    private StorageAdapter storageAdapter;
    private final List<File> backFiles = new ArrayList<>(); //保存层级数据
    private File topFile;

    @Override
    protected void initView() {
        initAdapter();
        topFile = Environment.getExternalStorageDirectory();
        backFiles.add(topFile);
        toggleFileTree(topFile);
        binding.backTxt.setOnClickListener(view -> backHistory(0));
    }

    @SuppressLint("SetTextI18n")
    private void toggleFileTree(File file) {
        if (file != null) {
            //路径名
            binding.fileCategoryTvPath.setText("存储卡：" + file.getPath());
            //获取数据
            File[] files = file.listFiles(new SimpleFileFilter());
            //转换成List
            List<File> rootFiles = Arrays.asList(files);
            //排序
            rootFiles.sort(new FileComparator());
            //加入
            storageAdapter.setList(rootFiles);
        }
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        binding.folderList.setLayoutManager(new LinearLayoutManager(requireActivity()));
        storageAdapter = new StorageAdapter(R.layout.item_storage_layout, new ArrayList<>());
        storageAdapter.setEmptyView(getEmptyDataView());
        binding.folderList.addItemDecoration(new DividerItemDecoration(requireActivity(), 1));
        binding.folderList.setAdapter(storageAdapter);
        storageAdapter.setOnItemClickListener((adapter, view, position) -> {
            File file = storageAdapter.getItem(position);
            if (file.isDirectory()) {//如果点击的是文件夹
                backFiles.add(file);
                toggleFileTree(file);
            } else {
                if (DbSource.getInstance().loadByUrl(file.getAbsolutePath()) == null) {
                    addDialog(file, position);
                }
            }
        });
    }

    /**
     * 获取空数据布局
     *
     * @return
     */
    private View getEmptyDataView() {
        View view = getLayoutInflater().inflate(R.layout.empty_bookshelf_layout, binding.folderList, false);
        AppCompatTextView emptyTxt = view.findViewById(R.id.empty_txt);
        emptyTxt.setVisibility(View.GONE);
        return view;
    }

    /**
     * 是否加入书架弹框提示
     *
     * @param file
     * @param position
     */
    private void addDialog(File file, int position) {
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_add_tip_layout, null, true);
        AlertDialog dialog = new AlertDialog.Builder(requireActivity(), R.style.Dialog_Width_85).setView(view).create();
        AppCompatTextView cancelTxt = view.findViewById(R.id.cancel_txt);
        AppCompatTextView commitTxt = view.findViewById(R.id.commit_txt);
        AppCompatTextView tipTxt = view.findViewById(R.id.tip_txt);
        tipTxt.setText(String.format("是否把%s添加至书架？", file.getName()));
        cancelTxt.setOnClickListener(v -> dialog.dismiss());
        commitTxt.setOnClickListener(view1 -> {
            BookBean bookBean = new BookBean();
            String fileName = file.getName();
            String[] names = fileName.split("\\.");
            bookBean.setBookName(names[0]);
            bookBean.setFileType(names[1]);
            bookBean.setBookUrl(file.getAbsolutePath());
            bookBean.setFileEncoder(FileUtils.getFileCharset(file));
            bookBean.setBookSize(FileUtils.getFileSize(file.length()));
            DbSource.getInstance().insert(bookBean);
            Utils.showToast("书籍插入成功！");
            dialog.dismiss();
            storageAdapter.notifyItemChanged(position);
        });
        //设置点击弹窗外界面 弹窗是否自动消失
        dialog.setCancelable(false);
        //弹窗背景
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    public void backHistory(int type) {
        //拿到上一级的file，然后设置数据，在移除当前file，为下一次返回做准备
        if (backFiles.isEmpty()) {
            return;
        }
        backFiles.remove(backFiles.size() - 1);
        if (backFiles.size() > 0) {
            toggleFileTree(backFiles.get(backFiles.size() - 1));
        } else {
            if (type == 0) {
                backFiles.add(topFile);
            } else {
                requireActivity().setResult(Activity.RESULT_OK);
                requireActivity().finish();
            }
        }
    }
}
