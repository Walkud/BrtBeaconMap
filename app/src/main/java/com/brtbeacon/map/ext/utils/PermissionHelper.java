package com.brtbeacon.map.ext.utils;

import android.Manifest;

/**
 * 权限请求Code
 * Created by Walkud on 2018/3/8 0008.
 */

public class PermissionHelper {

    public static final int WRITE_EXTERNAL_STORAGE_CODE = 0x1 << 1;//外置存储写权限
    public static final int ACCESS_COARSE_LOCATION_CODE = 0x2 << 2;//粗略定位权限

    public static final int WES_ACL_CODE = WRITE_EXTERNAL_STORAGE_CODE | ACCESS_COARSE_LOCATION_CODE;//外置存储写权限和粗略定位权限Code


    /**
     * 获取粗略定位及外置存储写权限标识
     *
     * @return
     */
    public static String[] getWesAclPermission() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.ACCESS_COARSE_LOCATION};
    }

}