package com.brtbeacon.map.ext.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.brtbeacon.map.ext.Constants;
import com.brtbeacon.map.ext.R;
import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.TextSymbol;
import com.ty.locationengine.ble.TYBeacon;
import com.ty.locationengine.ble.TYLocationManager;
import com.ty.locationengine.ble.TYPublicBeacon;
import com.ty.mapdata.TYLocalPoint;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYPictureMarkerSymbol;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Beacon定位
 * Created by Walkud on 2018/3/15 0015.
 */

public class LocationActivity extends RoutePlanningActivity implements TYLocationManager.TYLocationManagerListener {

    protected TYLocationManager locationManager;
    protected TYPictureMarkerSymbol[] pics;
    protected GraphicsLayer graphicsLayer;
    protected boolean isShowLocation;

    private TYLocationManager.TYLocationManagerListener updateCheckListener = new TYLocationManager.TYLocationManagerListener() {
        @Override
        public void didRangedBeacons(TYLocationManager tyLocationManager, List<TYBeacon> list) {

        }

        @Override
        public void didRangedLocationBeacons(TYLocationManager tyLocationManager, List<TYPublicBeacon> list) {

        }

        @Override
        public void didUpdateLocation(TYLocationManager tyLocationManager, TYLocalPoint tyLocalPoint) {

        }

        @Override
        public void didUpdateImmediateLocation(TYLocationManager tyLocationManager, TYLocalPoint tyLocalPoint) {

        }

        @Override
        public void didFailUpdateLocation(TYLocationManager tyLocationManager, Error error) {

        }

        @Override
        public void didUpdateDeviceHeading(TYLocationManager tyLocationManager, double v) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        locationIv.setVisibility(View.VISIBLE);

        locationManager = new TYLocationManager(this, Constants.BUILDING_ID, Constants.APP_KEY);
        locationManager.addLocationEngineListener(this);

        //按需控制定位设备个数
        locationManager.setLimitBeaconNumber(true);
        locationManager.setMaxBeaconNumberForProcessing(5);

        //控制所有信号很弱<-80,定位失败
        locationManager.setRssiThreshold(-80);

        //是否启用热力数据
        locationManager.enableHeatData(true);


        //定位动画图片组
        Integer[] picids = new Integer[]{
                R.drawable.l0,
                R.drawable.l1,
                R.drawable.l2,
                R.drawable.l3,
                R.drawable.l4,
                R.drawable.l5,
                R.drawable.l6,
                R.drawable.l7};
        pics = new TYPictureMarkerSymbol[8];
        for (int i = 0; i < 8; i++) {
            TYPictureMarkerSymbol pms = new TYPictureMarkerSymbol(getResources().getDrawable(picids[i]));
            pms.setHeight(44);
            pms.setWidth(44);
            pics[i] = pms;
        }
    }

    @OnClick({R.id.location_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.location_iv:
                if (isShowLocation) {//停止定位
                    isShowLocation = false;
                    locationManager.stopUpdateLocation();
                    showToast("停止定位");
                    graphicsLayer.removeAll();
                    locationIv.setImageResource(R.drawable.btn_locate_gray);
                } else {//开始定位
                    isShowLocation = true;
                    locationManager.startUpdateLocation();
                    showToast("开始定位，需配置定位设备。查看README.md。");
                    locationIv.setImageResource(R.drawable.btn_locate);
                }
                break;
        }
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {

            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);

            setMapLocationSymbol();
        }
    }

    /**
     * 设置地图定位图标
     */
    public void setMapLocationSymbol() {
        TYPictureMarkerSymbol pms = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.location_arrow));
        pms.setWidth(40);
        pms.setHeight(40);
        mapView.setLocationSymbol(pms);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //定位必须停止
        if (locationManager != null)
            locationManager.stopUpdateLocation();
    }


    /**
     * Beacon扫描结果事件回调，返回符合扫描参数的所有Beacon
     *
     * @param tyLocationManager
     * @param list
     */
    @Override
    public void didRangedBeacons(TYLocationManager tyLocationManager, List<TYBeacon> list) {

    }

    /**
     * 定位Beacon扫描结果事件回调，返回符合扫描参数的定位Beacon，定位Beacon包含坐标信息。此方法可用于辅助巡检，以及基于定位beacon的相关触发事件。
     *
     * @param tyLocationManager
     * @param beacons
     */
    @Override
    public void didRangedLocationBeacons(TYLocationManager tyLocationManager, List<TYPublicBeacon> beacons) {
        if (graphicsLayer == null) {
            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);
        } else {
            graphicsLayer.removeAll();
        }
        Graphic[] graphics = new Graphic[beacons.size()];
        int i = 0;
        for (TYPublicBeacon pb : beacons) {
            if (pb.getLocation().getFloor() == mapView.currentMapInfo.getFloorNumber()) {
                Graphic g = new Graphic(new Point(pb.getLocation().getX(), pb.getLocation().getY()), new TextSymbol(13, pb.getMinor() + "," + pb.getRssi() + "", Color.RED));
                graphics[i] = g;
            }
            i++;
        }
        graphicsLayer.addGraphics(graphics);
    }

    /**
     * 位置更新事件回调，位置更新并返回新的位置结果。
     * 与[TYLocationManager:didUpdateImmediationLocation:]方法相近，此方法回调结果融合手机设备传感器信息，稳定性较好，适合用于步行场景下
     *
     * @param tyLocationManager
     * @param tyLocalPoint
     */
    @Override
    public void didUpdateLocation(TYLocationManager tyLocationManager, TYLocalPoint tyLocalPoint) {

    }

    /**
     * 位置更新事件回调，位置更新并返回新的位置结果。
     * 与[TYLocationManager:didUpdateLocatin:]方法相近，此方法回调结果未融合手机传感器信息，灵敏度较高，适合用于行车场景下或传感器无效场景
     *
     * @param tyLocationManager
     * @param newLocalPoint
     */
    @Override
    public void didUpdateImmediateLocation(TYLocationManager tyLocationManager, TYLocalPoint newLocalPoint) {
        if (newLocalPoint.getFloor() != mapView.currentMapInfo.getFloorNumber()) {
            mapView.setFloor(TYMapInfo.searchMapInfoFromArray(mapView.allMapInfo(), newLocalPoint.getFloor()));
            return;
        }
//        if (picIndex == 0) animateLocationSymbol();
        mapView.showLocation(newLocalPoint);
    }

    /**
     * 定位失败，定位数据失败
     *
     * @param tyLocationManager
     * @param error
     */
    @Override
    public void didFailUpdateLocation(TYLocationManager tyLocationManager, Error error) {
        error.printStackTrace();
        Log.d(TAG, "定位失败");
    }

    /**
     * 如方位有误，尝试打开手机校准指南针或提醒用户直接转8字。
     * 设备方向改变事件回调。结合地图MapMode可以处理地图自动旋转，或箭头方向功能。
     *
     * @param tyLocationManager
     * @param newHeading
     */
    @Override
    public void didUpdateDeviceHeading(TYLocationManager tyLocationManager, double newHeading) {
        Log.i(TAG, "地图初始北偏角：" + mapView.building.getInitAngle() + "；当前设备北偏角：" + newHeading);
        mapView.processDeviceRotation(newHeading);
    }
}
