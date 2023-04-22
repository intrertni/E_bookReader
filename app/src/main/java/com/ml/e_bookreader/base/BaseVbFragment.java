package com.ml.e_bookreader.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Date: 2023/3/3 9:50
 * Description: Fragment基类
 */
public abstract class BaseVbFragment<VB extends ViewBinding> extends Fragment {
    private static final String TAG = "BaseFragment";
    public VB binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    /**
     * 初始化视图
     */
    protected abstract void initView();



    /**
     * 初始化viewBinding和viewModel
     *
     * @param inflater
     * @param parent       父容器
     * @param attachToRoot
     */
    private void init(LayoutInflater inflater, ViewGroup parent, boolean attachToRoot) {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            // 获取真实泛型
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            Class<VB> viewBindingClass = (Class<VB>) types[0];
            try {
                // 绑定viewBinding
                Method method = viewBindingClass.getMethod("inflate", LayoutInflater.class,
                        ViewGroup.class, boolean.class);
                binding = (VB) method.invoke(null, inflater, parent, attachToRoot);
            } catch (Exception e) {
                Log.e(TAG, "找不到inflater方法:" + e);
            }
        }
    }

}
