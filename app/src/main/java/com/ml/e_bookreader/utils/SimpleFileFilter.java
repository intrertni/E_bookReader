package com.ml.e_bookreader.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * Date: 2023/3/7 19:11
 * Description: 筛选器
 */
public class SimpleFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        if (pathname.getName().startsWith(".")) {
            return false;
        }
        //文件夹内部数量为0
        if (pathname.isDirectory() && (pathname.list() == null || pathname.list().length == 0)) {
            return false;
        }

        //文件内容为空,或者不以txt为开头
        return pathname.isDirectory() ||
                (pathname.length() != 0
                        && (pathname.getName().toLowerCase().endsWith(".txt")
                        || pathname.getName().toLowerCase().endsWith(".epub")));
    }
}
