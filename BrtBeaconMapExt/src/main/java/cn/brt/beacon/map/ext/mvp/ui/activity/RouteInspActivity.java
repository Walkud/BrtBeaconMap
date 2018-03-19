package cn.brt.beacon.map.ext.mvp.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.socks.library.KLog;
import com.ty.locationengine.ble.TYBeacon;
import com.ty.locationengine.ble.TYLocationManager;
import com.ty.locationengine.ble.TYPublicBeacon;
import com.ty.mapdata.TYLocalPoint;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.brt.beacon.map.ext.Config;
import cn.brt.beacon.map.ext.R;
import cn.brt.beacon.map.ext.bean.data.BeaconEntry;
import cn.brt.beacon.map.ext.db.DBHandler;

/**
 * 巡检
 * Created by Walkud on 2018/3/19 0019.
 */
public class RouteInspActivity extends BaseMapViewActivity {

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
    @BindView(R.id.route_insp_btn)
    TextView routeInspBtn;
    @BindView(R.id.beacon_info_tv)
    TextView beaconInfoTv;

    private List<BeaconEntry> beaconEntries;//当前楼层beacon总数
    private List<BeaconEntry> routeInspBeacons;//已巡检beacon个数
    private List<BeaconEntry> updateBeacons;//已更新beacon个数
    private Map<Integer, BeaconEntry> hashMap = new HashMap<>();
    private Map<BeaconEntry, Integer> beaconMap = new HashMap<>();
    private GraphicsLayer graphicsLayer;
    private boolean isShowLocation;
    private TYLocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_insp);
        ButterKnife.bind(this);

        initMapEnvironment();

        locationManager = new TYLocationManager(this, Config.BUILDING_ID, Config.APP_KEY);
        locationManager.addLocationEngineListener(locationManagerListener);

        //按需控制定位设备个数
        locationManager.setLimitBeaconNumber(true);
        locationManager.setMaxBeaconNumberForProcessing(5);

        //控制所有信号很弱<-80,定位失败
        locationManager.setRssiThreshold(-80);

        //是否启用热力数据
        locationManager.enableHeatData(true);

    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {

            //添加同步旋转图层
            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);
        }
    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        beaconEntries = DBHandler.getInstance().findBecaonList(mapInfo.getFloorNumber());
        routeInspBeacons = new ArrayList<>();
        updateBeacons = new ArrayList<>();
        KLog.d("size:" + beaconEntries.size());
        graphicsLayer.removeAll();
        for (BeaconEntry beaconEntry : beaconEntries) {
            SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(Color.BLUE, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
            Point point = new Point(beaconEntry.getX(), beaconEntry.getY());
            int uid = graphicsLayer.addGraphic(new Graphic(point, simpleMarkerSymbol));
            hashMap.put(uid, beaconEntry);
            beaconMap.put(beaconEntry, uid);
        }
    }

    /**
     * 定位、蓝牙Beacon监听
     */
    private TYLocationManager.TYLocationManagerListener locationManagerListener = new TYLocationManager.TYLocationManagerListener() {
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
        public void didRangedLocationBeacons(final TYLocationManager tyLocationManager, final List<TYPublicBeacon> beacons) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateData(tyLocationManager, beacons);
                }
            });
        }

        public void updateData(TYLocationManager tyLocationManager, List<TYPublicBeacon> beacons) {
            for (TYPublicBeacon beacon : beacons) {
                Point screenPoint = mapView.toScreenPoint(new Point((float) beacon.getLocation().getX(), (float) beacon.getLocation().getY()));
                int[] uids = graphicsLayer.getGraphicIDs((float) screenPoint.getX(), (float) screenPoint.getY(), 2);
                for (int uid : uids) {
                    Graphic graphic = graphicsLayer.getGraphic(uid);
                    SimpleMarkerSymbol symbol = (SimpleMarkerSymbol) graphic.getSymbol();
                    if (symbol.getColor() != Color.GREEN) {
                        symbol.setColor(Color.GREEN);
                        graphicsLayer.updateGraphic(uid, symbol);

                    }

                    if (uids.length == 1) {
                        BeaconEntry be = hashMap.get(uids[0]);
                        if (!updateBeacons.contains(be)) {
                            //更新数据
                            be.setFloor(beacon.getLocation().getFloor());
                            be.setUuid(beacon.getUUID());
                            be.setMajor((short) beacon.getMajor());
                            be.setMinor((short) beacon.getMinor());
                            be.setX(beacon.getLocation().getX());
                            be.setY(beacon.getLocation().getY());
                            boolean isUpdate = DBHandler.getInstance().updateBeaconEntity(be);
                            if (isUpdate) {
                                updateBeacons.add(be);
                            }
                        }

                        if (!routeInspBeacons.contains(be)) {
                            //设置是否巡检
                            routeInspBeacons.add(be);
                        }
                    }
                }


//                //这里应该通过UUID && Major && Minor来判断是否为同一个Beacon
//                for (BeaconEntry beaconEntry : beaconEntries) {
//                    if (beaconEntry.getUuid().equalsIgnoreCase(beacon.getUUID())
//                            && beaconEntry.getMajor() == beacon.getMajor()
//                            && beaconEntry.getMinor() == beacon.getMinor()) {
//                        int uid = beaconMap.get(beaconEntry);
//                        Graphic graphic = graphicsLayer.getGraphic(uid);
//                        SimpleMarkerSymbol symbol = (SimpleMarkerSymbol) graphic.getSymbol();
//                        if (symbol.getColor() != Color.GREEN) {
//                            symbol.setColor(Color.GREEN);
//                            graphicsLayer.updateGraphic(uid, symbol);
//                        }
//
//                        if (!routeInspBeacons.contains(beaconEntry)) {
//                            //设置是否巡检
//                            routeInspBeacons.add(beaconEntry);
//                        }
//                    }
//
//                }

            }
            setRouteInspText();
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
            KLog.d("定位失败");
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
            KLog.d("地图初始北偏角：" + mapView.building.getInitAngle() + "；当前设备北偏角：" + newHeading);
            mapView.processDeviceRotation(newHeading);
        }
    };

    @OnClick({R.id.route_insp_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.route_insp_btn://巡检
                if (isShowLocation) {//停止定位
                    isShowLocation = false;
                    locationManager.stopUpdateLocation();
                    resetGraphicsLayer();
                    routeInspBtn.setText("巡检");
                    showToast("停止定位");
                } else {//开始定位
                    isShowLocation = true;
                    locationManager.startUpdateLocation();
                    routeInspBtn.setText("巡检中");
                    showToast("开始巡检");
                }
                break;
        }
    }

    private void resetGraphicsLayer() {
        for (int uid : graphicsLayer.getGraphicIDs()) {
            Graphic graphic = graphicsLayer.getGraphic(uid);
            SimpleMarkerSymbol symbol = (SimpleMarkerSymbol) graphic.getSymbol();
            if (symbol.getColor() == Color.GREEN) {
                symbol.setColor(Color.BLUE);
                graphicsLayer.updateGraphic(uid, symbol);
            }
        }

        routeInspBeacons.clear();
        setRouteInspText();
    }

    private void setRouteInspText() {
        StringBuilder sb = new StringBuilder();
        sb.append("已巡检:" + routeInspBeacons.size() + "/" + beaconEntries.size());
        sb.append("\n");
        sb.append("已更新：" + updateBeacons.size() + "/" + beaconEntries.size());
        beaconInfoTv.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //定位必须停止
        if (locationManager != null)
            locationManager.stopUpdateLocation();
    }
}
