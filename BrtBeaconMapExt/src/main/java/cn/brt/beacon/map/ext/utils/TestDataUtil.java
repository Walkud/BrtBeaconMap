package cn.brt.beacon.map.ext.utils;


import java.util.ArrayList;
import java.util.List;

import cn.brt.beacon.map.ext.bean.data.MainItem;
import cn.brt.beacon.map.ext.bean.data.MainSubItem;
import cn.brt.beacon.map.ext.mvp.ui.activity.BeaconLoadActivity;

/**
 * 测试数据构建工具类
 * Created by Walkud on 2018/3/12 0012.
 */

public class TestDataUtil {

    /**
     * 构建功能列表
     *
     * @return
     */
    public static List<MainItem> buildMainItems() {
        List<MainItem> mainItems = new ArrayList<>();

        //显示地图
        List<MainSubItem> subItems = new ArrayList<>();
        subItems.add(new MainSubItem("Beacon加载显示", BeaconLoadActivity.class));
        mainItems.add(new MainItem("显示地图", subItems));

        return mainItems;
    }
}
