package com.brtbeacon.map.ext.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 地图信息UI
 * Created by Walkud on 2018/3/12 0012.
 */

public class MapInfoActivity extends BaseMapViewActivity {

    @BindView(R.id.map)
    TYMapView map;
    @BindView(R.id.list_floor)
    ListView listFloor;
    @BindView(R.id.btn_floor)
    TextView btnFloor;
    @BindView(R.id.btn_floor_arrow)
    ImageView btnFloorArrow;
    @BindView(R.id.bottom_menu)
    RelativeLayout bottomMenu;
    @BindView(R.id.btn_zoomin)
    TextView btnZoomin;
    @BindView(R.id.btn_zoomout)
    TextView btnZoomout;
    @BindView(R.id.map_layout)
    RelativeLayout mapLayout;
    @BindView(R.id.map_info_tv)
    TextView mapInfoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_info);
        ButterKnife.bind(this);

        initMap();
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        initMapEnvironment();
        //设置地图背景(网格颜色, 线条颜色, 网格宽度, 线条宽度)
        mapView.setMapBackground(Color.WHITE, Color.LTGRAY, 20, 10);

    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        setMapInfoText();

        /*此属性设置必须在楼层加载完成时调用才有效，如果在onCreate中调用，地图没有绘制完成会抛出空指针异常,
          如果在mapViewDidLoad调用也无效，因为暂无楼层信息
          以下修改比例尺上下限（0.5~5倍，亦可自行计算：实际距离／屏幕距离）
         */
        mapView.setMinScale(mapView.getXScaleFactor(0.5f));//最小缩放0.5倍
        mapView.setMaxScale(mapView.getXScaleFactor(5f));//最大缩放5倍

        //楼层加载完成，设置缩放比例为1倍于屏幕
        mapView.setScale(mapView.getXScaleFactor(1f), true);

    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        Log.d(TAG, "地图加载完成");

    }

    @Override
    public void mapViewDidZoomed(TYMapView mapView) {
        super.mapViewDidZoomed(mapView);
        setMapInfoText();
    }

    /**
     * 设置地图信息
     */
    private void setMapInfoText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "地图建筑名称：" + mapView.building.getName() + "      " +
                        "地图建筑北偏角：" + mapView.building.getInitAngle() + "\n" +
                        "地图建筑经纬度 Lon：" + mapView.building.getLongitude() + "，Lat：" + mapView.building.getLatitude() + "\n" +
                        "地图建筑CityId：" + mapView.building.getCityID() + "，地图建筑地址：" + mapView.building.getAddress() + "\n" +
                        "地图建筑路径URL：" + mapView.building.getRouteURL() + "\n" +
                        "地图旋转角度：" + mapView.getRotationAngle() + "\n" +
                        "地图比例尺：" + mapView.getScale();

                mapInfoTv.setText(text);
            }
        });
    }
}
