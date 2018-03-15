package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.esri.core.geometry.Point;
import com.ty.mapsdk.TYMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 地图坐标转换
 * Created by Walkud on 2018/3/13 0013.
 */
public class MapCoordinateActivity extends BaseMapViewActivity {

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
        initMapEnvironment();
    }

    @Override
    public void onClickAtPoint(TYMapView mapView, Point mappoint) {
        super.onClickAtPoint(mapView, mappoint);

        Point screenPoint = mapView.toScreenPoint(mappoint);//地图点击事件坐标转换为屏幕坐标
        //地图点击事件坐标与转换屏幕转换后的坐标是一致的
        Point mapPoint = mapView.toMapPoint(screenPoint);//屏幕坐标转换为地图坐标

        mapInfoTv.setText("屏幕坐标 X：" + screenPoint.getX() + "，Y：" + screenPoint.getY() + "\n" +
                "地图坐标 X：" + mapPoint.getX() + "，Y：" + mapPoint.getY() + "\n" +
                "点击事件坐标：" + mappoint.getX() + "，Y：" + mappoint.getY());

    }
}
