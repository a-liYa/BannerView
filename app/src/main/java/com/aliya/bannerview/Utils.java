package com.aliya.bannerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 工具类
 *
 * @author a_liYa
 * @date 2018/9/3 17:05.
 */
public class Utils {

    public static View inflate(int layout, ViewGroup root, boolean attachToRoot) {
        return LayoutInflater.from(root.getContext()).inflate(layout, root, attachToRoot);
    }

}
