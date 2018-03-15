package com.brtbeacon.map.ext.utils;

import com.brtbeacon.map.ext.bean.MainItem;
import com.brtbeacon.map.ext.bean.MainSubItem;
import com.brtbeacon.map.ext.ui.ClassicMapActivity;
import com.brtbeacon.map.ext.ui.LayerControlActivity;
import com.brtbeacon.map.ext.ui.LocationActivity;
import com.brtbeacon.map.ext.ui.LocationSnapActivity;
import com.brtbeacon.map.ext.ui.MapCompassActivity;
import com.brtbeacon.map.ext.ui.MapCoordinateActivity;
import com.brtbeacon.map.ext.ui.MapGestureActivity;
import com.brtbeacon.map.ext.ui.MapInfoActivity;
import com.brtbeacon.map.ext.ui.MapLocalizationActivity;
import com.brtbeacon.map.ext.ui.MapOperationActivity;
import com.brtbeacon.map.ext.ui.MarkerFenceActivity;
import com.brtbeacon.map.ext.ui.MarkerImageTextActivity;
import com.brtbeacon.map.ext.ui.MarkerLineActivity;
import com.brtbeacon.map.ext.ui.MarkerShapeActivity;
import com.brtbeacon.map.ext.ui.PickupPoiActivity;
import com.brtbeacon.map.ext.ui.PoiDistanceSearchActivity;
import com.brtbeacon.map.ext.ui.PoiFacilitySearchActivity;
import com.brtbeacon.map.ext.ui.PoiNameSearchActivity;
import com.brtbeacon.map.ext.ui.PopWindowMapActivity;
import com.brtbeacon.map.ext.ui.RouteDistanceActivity;
import com.brtbeacon.map.ext.ui.RouteForbiddenActivity;
import com.brtbeacon.map.ext.ui.RouteNavigationActivity;
import com.brtbeacon.map.ext.ui.RouteOnlyActivity;
import com.brtbeacon.map.ext.ui.RoutePlanningActivity;
import com.brtbeacon.map.ext.ui.RoutePlanningTipActivity;
import com.brtbeacon.map.ext.ui.TiledMapActivity;

import java.util.ArrayList;
import java.util.List;

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
        subItems.add(new MainSubItem("基础地图", ClassicMapActivity.class));
        subItems.add(new MainSubItem("地图信息", MapInfoActivity.class));
        subItems.add(new MainSubItem("地图操作", MapOperationActivity.class));
        subItems.add(new MainSubItem("图层控制", LayerControlActivity.class));
        subItems.add(new MainSubItem("坐标转换", MapCoordinateActivity.class));
        subItems.add(new MainSubItem("地图本地化", MapLocalizationActivity.class));
        subItems.add(new MainSubItem("瓦片地图", TiledMapActivity.class));
        mainItems.add(new MainItem("显示地图", subItems));

        //地图事件
        subItems = new ArrayList<>();
        subItems.add(new MainSubItem("拾取POI", PickupPoiActivity.class));
        subItems.add(new MainSubItem("手势控制", MapGestureActivity.class));
        mainItems.add(new MainItem("地图事件", subItems));

        //地图控制
        subItems = new ArrayList<>();
        subItems.add(new MainSubItem("指北针", MapCompassActivity.class));
        mainItems.add(new MainItem("地图控制", subItems));

        //标注弹窗
        subItems = new ArrayList<>();
        subItems.add(new MainSubItem("图文点标注", MarkerImageTextActivity.class));
        subItems.add(new MainSubItem("线标注", MarkerLineActivity.class));
        subItems.add(new MainSubItem("形状标注", MarkerShapeActivity.class));
        subItems.add(new MainSubItem("展示弹窗", PopWindowMapActivity.class));
        subItems.add(new MainSubItem("围栏示例", MarkerFenceActivity.class));
        mainItems.add(new MainItem("标注弹窗", subItems));

        //POI搜索
        subItems = new ArrayList<>();
        subItems.add(new MainSubItem("名称搜索", PoiNameSearchActivity.class));
        subItems.add(new MainSubItem("设施搜索", PoiFacilitySearchActivity.class));
        subItems.add(new MainSubItem("距离搜索", PoiDistanceSearchActivity.class));
        mainItems.add(new MainItem("POI搜索", subItems));

        //路径规划
        subItems = new ArrayList<>();
        subItems.add(new MainSubItem("路径规划", RoutePlanningActivity.class));
        subItems.add(new MainSubItem("距离计算", RouteDistanceActivity.class));
        subItems.add(new MainSubItem("路径提示", RoutePlanningTipActivity.class));
        subItems.add(new MainSubItem("设施禁行", RouteForbiddenActivity.class));
        subItems.add(new MainSubItem("仅路径", RouteOnlyActivity.class));
        mainItems.add(new MainItem("路径规划", subItems));

        //导航
        subItems = new ArrayList<>();
        subItems.add(new MainSubItem("开始定位", LocationActivity.class));
        subItems.add(new MainSubItem("定位吸附", LocationSnapActivity.class));
        subItems.add(new MainSubItem("导航示例", RouteNavigationActivity.class));
        mainItems.add(new MainItem("导航", subItems));

        return mainItems;
    }
}
