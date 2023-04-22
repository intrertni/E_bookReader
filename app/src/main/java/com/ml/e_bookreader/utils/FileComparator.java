package com.ml.e_bookreader.utils;

import java.io.File;
import java.util.Comparator;

/**
 * Date: 2023/3/7 19:14
 * Description: 排序
 */
public class FileComparator implements Comparator<File> {
    @Override
    public int compare(File file1, File file2) {
        if (file1.isDirectory() && file2.isFile()) {
            return -1;
        }
        if (file2.isDirectory() && file1.isFile()) {
            return 1;
        }
        return file1.getName().compareToIgnoreCase(file2.getName());
    }
}
