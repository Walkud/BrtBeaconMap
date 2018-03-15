package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.esri.android.map.event.OnPinchListener;
import com.ty.mapsdk.TYMapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 地图指北针
 * 设置地图角度后，点击复位无法监听地图已旋转角度事件，导致无法修正指北针角度
 * Created by Walkud on 2018/3/13 0013.
 */

public class MapCompassActivity extends BaseMapViewActivity {

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
    @BindView(R.id.north_arrow)
    ImageView northArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_compass);
        ButterKnife.bind(this);

        initMapEnvironment();
    }

    @OnClick({R.id.north_arrow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.north_arrow://复位
                mapView.setRotationAngle(mapView.building.getInitAngle());
                break;
        }
    }

    @Override
    public void mapViewDidLoad(final TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            //初始化完成，设置建筑的初始角度
            northArrow.setRotation((float) mapView.building.getInitAngle());
            mapView.setOnPinchListener(new OnPinchListener() {
                @Override
                public void prePointersMove(float v, float v1, float v2, float v3, double v4) {
                }

                @Override
                public void postPointersMove(float v, float v1, float v2, float v3, double v4) {
                    northArrow.post(new Runnable() {
                        @Override
                        public void run() {
                            double angle = mapView.getRotationAngle() + mapView.building.getInitAngle();
                            northArrow.setRotation((float) -angle);
                        }
                    });
                }

                @Override
                public void prePointersDown(float v, float v1, float v2, float v3, double v4) {
                }

                @Override
                public void postPointersDown(float v, float v1, float v2, float v3, double v4) {
                }

                @Override
                public void prePointersUp(float v, float v1, float v2, float v3, double v4) {
                }

                @Override
                public void postPointersUp(float v, float v1, float v2, float v3, double v4) {
                }
            });
            //#TODO 监听地图旋转事件，未找到
        }
    }
}
