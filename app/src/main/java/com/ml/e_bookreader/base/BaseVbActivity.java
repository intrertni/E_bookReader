package com.ml.e_bookreader.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Date: 2023/3/1 16:45
 * Description: Activity基类
 */
public abstract class BaseVbActivity<VB extends ViewBinding> extends AppCompatActivity {
    public VB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            // 获取真实泛型
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            Class<VB> viewBindingClass = (Class<VB>) types[0];
            try {
                // 绑定viewBinding
                Method method = viewBindingClass.getMethod("inflate", LayoutInflater.class);
                binding = (VB) method.invoke(null, getLayoutInflater());
            } catch (Exception e) {
                e.printStackTrace();
            }
            setContentView(binding.getRoot());
        }
        initView();
    }

    /**
     * 初始化视图
     */
    public abstract void initView();

}
