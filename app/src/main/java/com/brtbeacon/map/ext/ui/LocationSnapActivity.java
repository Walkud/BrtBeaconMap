package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.view.View;

import com.brtbeacon.map.ext.R;
import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.ty.locationengine.ble.TYLocationManager;
import com.ty.mapdata.TYLocalPoint;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYPictureMarkerSymbol;
import com.ty.mapsdk.TYRoutePart;
import com.ty.mapsdk.TYRouteResult;

import butterknife.OnClick;

/**
 * 定位吸附
 * Created by Walkud on 2018/3/15 0015.
 */

public class LocationSnapActivity extends LocationActivity {

    private int graphicID;
    private GraphicsLayer xfGraphicsLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnMoni.setText("吸附\n演示");
        btnMoni.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btn_moni, R.id.location_iv})
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_moni://吸附演示
                simulateLocation();
                break;
        }
    }

    @Override
    public void didUpdateImmediateLocation(TYLocationManager tyLocationManager, TYLocalPoint newLocalPoint) {
        snapToRoute(newLocalPoint);
    }


    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            xfGraphicsLayer = new GraphicsLayer();
            mapView.addLayer(xfGraphicsLayer);
        }
    }

    @Override
    public void setMapLocationSymbol() {
    }

    private void snapToRoute(TYLocalPoint lp) {

        //显示原始点
        if (graphicID != 0) {
            xfGraphicsLayer.updateGraphic(graphicID, new Point(lp.getX(), lp.getY()));
        } else {
            TYPictureMarkerSymbol pms = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.location_arrow));
            pms.setWidth(24);
            pms.setHeight(24);
            graphicID = xfGraphicsLayer.addGraphic(new Graphic(new Point(lp.getX(), lp.getY()), pms));
        }

        //如果点距离规划路径5米内，吸附到规划路线；否则吸附到地图路网。吸附仅是可选方案，根据实际情况参考是否使用。
        //是否正在路径规划中&& 当前点是否偏离航线
        if (isRouting && !mapView.getRouteResult().isDeviatingFromRoute(lp, 5)) {
            lp = mapView.getRouteResult().getNearestPointOnRoute(lp);
        } else {
            lp = mapView.routeManager().getNearestRoutePoint(lp);
        }
        mapView.showLocation(lp);
    }

    //产生一个随机地图点、或随机路线上的点
    private void simulateLocation() {
        Point random = isRouting ? getRandomPointAroudRoute() : getRandomPoint();
        snapToRoute(new TYLocalPoint(random.getX(), random.getY(), mapView.currentMapInfo.getFloorNumber()));
    }

    private Point getRandomPoint() {
        Point pt = new Point(mapView.currentMapInfo.getMapExtent().getXmin(), mapView.currentMapInfo.getMapExtent().getYmax());
        pt.setXY(pt.getX() + Math.random() * mapView.currentMapInfo.getMapSize().getX(), pt.getY() - Math.random() * mapView.currentMapInfo.getMapSize().getY());
        return pt;
    }

    private Point getRandomPointAroudRoute() {
        TYRouteResult routeResult = mapView.getRouteResult();
        Point pt = new Point();
        for (TYRoutePart part : routeResult.getRoutePartsOnFloor(mapView.currentMapInfo.getFloorNumber())) {
            pt = part.getRoute().getPoint((int) (Math.random() * part.getRoute().getPointCount()));
        }
        pt.setXY(pt.getX() + Math.random() * 10 - 5, pt.getY() + Math.random() * 10 - 5);
        return pt;
    }

}
