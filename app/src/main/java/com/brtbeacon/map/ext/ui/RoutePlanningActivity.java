package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.esri.android.map.Callout;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.ty.mapdata.TYLocalPoint;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYOfflineRouteManager;
import com.ty.mapsdk.TYPictureMarkerSymbol;
import com.ty.mapsdk.TYPoi;
import com.ty.mapsdk.TYRoutePart;
import com.ty.mapsdk.TYRouteResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 路径规划
 * 1、初始化地图
 * 2、设置路径规划起点、终点、楼层切换图标
 * 3、设置路径规划监听，楼层切换判断是否显示当前楼层路径规划
 * 4、根据点击位置设置起点或终点坐标信息
 * 5、请求路径规划
 * 5、回调路径规划信息并设置规划信息至地图
 * Created by Walkud on 2018/3/14 0014.
 */

public class RoutePlanningActivity extends BaseMapViewActivity implements TYOfflineRouteManager.TYOfflineRouteManagerListener {


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
    @BindView(R.id.btn_moni)
    TextView btnMoni;
    @BindView(R.id.grid_view)
    GridView gridView;
    @BindView(R.id.location_iv)
    ImageView locationIv;
    @BindView(R.id.north_arrow)
    ImageView northArrow;

    protected Callout mapCallout;//弹窗
    protected TYLocalPoint startPoint;//起点
    protected TYLocalPoint endPoint;//终点
    protected TYRouteResult routeResult;
    protected boolean isRouteManagerReady, isRouting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_planning_tip);
        ButterKnife.bind(this);

        //初始化地图环境
        initMapEnvironment();
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            mapCallout = mapView.getCallout();
            //初始化起点、终点、楼层切换点图标样式
            initSymbols();
            //设置路径规划监听
            mapView.routeManager().addRouteManagerListener(this);
            //路径规划准备完成
            isRouteManagerReady = true;
        }
    }

    /**
     * 初始化起点、终点、楼层切换点图标样式
     */
    protected void initSymbols() {
        TYPictureMarkerSymbol startSymbol = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.start));
        startSymbol.setWidth(34);
        startSymbol.setHeight(43);
        startSymbol.setOffsetX(0);
        startSymbol.setOffsetY(22);
        mapView.setStartSymbol(startSymbol);

        TYPictureMarkerSymbol endSymbol = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.end));
        endSymbol.setWidth(34);
        endSymbol.setHeight(43);
        endSymbol.setOffsetX(0);
        endSymbol.setOffsetY(22);
        mapView.setEndSymbol(endSymbol);

        TYPictureMarkerSymbol switchSymbol = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.nav_exit));
        switchSymbol.setWidth(37);
        switchSymbol.setHeight(37);
        mapView.setSwitchSymbol(switchSymbol);
    }

    @Override
    public void onClickAtPoint(TYMapView mapView, Point mappoint) {
        super.onClickAtPoint(mapView, mappoint);
        //获取当前点击点的Poi
        TYPoi poi = mapView.extractRoomPoiOnCurrentFloor(mappoint.getX(), mappoint.getY());
        if (poi == null) {
            //点击地图外提示
            showToast("请选择地图范围内的区域");
            return;
        }
        Point centerPt;
        //获取当前图形的中心点
        Geometry geom = poi.getGeometry();
        if (geom instanceof Polygon) {
            //获取多边形中心点
            centerPt = GeometryEngine.getLabelPointForPolygon((Polygon) geom, mapView.getSpatialReference());
        } else {
            centerPt = (Point) geom;
        }

        if (centerPt == null) {
            return;
        }

        //显示弹窗
        String title = (poi.getName() != null) ? poi.getName() : "未知道路";
        title += "x:" + centerPt.getX() + "\ny:" + centerPt.getY();
        mapCallout.setStyle(R.xml.callout_style);
        mapCallout.setMaxWidthDp(300);
        mapCallout.setMaxHeightDp(300);
        mapCallout.setContent(loadCalloutView(title, centerPt));
        mapCallout.show(mappoint);
    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        if (isRouting) {
            mapView.showRouteResultOnCurrentFloor();
        }
    }

    /**
     * 起点、终点设置弹框
     *
     * @param title
     * @param pt
     * @return
     */
    protected View loadCalloutView(final String title, final Point pt) {
        View view = getLayoutInflater().inflate(R.layout.layout_pop_window, null);
        TextView text = view.findViewById(R.id.message);
        TextView cancel = view.findViewById(R.id.cancel_btn);
        TextView confirm = view.findViewById(R.id.confirm_btn);

        text.setText(title);
        cancel.setText("起点");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapCallout.hide();
                setStartPoint(pt);
            }
        });
        confirm.setText("终点");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapCallout.hide();
                setEndPoint(pt);
            }
        });
        return view;
    }

    /**
     * 设置起点
     *
     * @param currentPoint
     */
    protected void setStartPoint(Point currentPoint) {
        startPoint = new TYLocalPoint(currentPoint.getX(), currentPoint.getY(),
                mapView.getCurrentMapInfo().getFloorNumber());
        mapView.showRouteStartSymbolOnCurrentFloor(startPoint);
        requestRoute();
    }

    /**
     * 设置终点
     *
     * @param currentPoint
     */
    protected void setEndPoint(Point currentPoint) {
        endPoint = new TYLocalPoint(currentPoint.getX(), currentPoint.getY(),
                mapView.getCurrentMapInfo().getFloorNumber());
        mapView.showRouteEndSymbolOnCurrentFloor(endPoint);
        requestRoute();
    }

    /**
     * 请求导航
     */
    protected void requestRoute() {
        if (!isRouteManagerReady) {
            showToast("路径管理器未初始化完成！");
            return;
        }

        if (startPoint == null || endPoint == null) {
            showToast("需要两个点请求路径！");
            return;
        }

        mapView.resetRouteLayer();
        mapView.routeManager().requestRoute(startPoint, endPoint);
    }

    /**
     * 路径规划成功
     *
     * @param tyOfflineRouteManager
     * @param tyRouteResult
     */
    @Override
    public void didSolveRouteWithResult(TYOfflineRouteManager tyOfflineRouteManager, TYRouteResult tyRouteResult) {
        this.routeResult = tyRouteResult;
        isRouting = true;
        mapView.setRouteResult(tyRouteResult);
        mapView.setRouteStart(startPoint);
        mapView.setRouteEnd(endPoint);

        mapView.showRouteResultOnCurrentFloor();

        List<TYRoutePart> routePartArray = tyRouteResult.getRoutePartsOnFloor(mapView.getCurrentMapInfo().getFloorNumber());
        if (routePartArray != null && routePartArray.size() > 0) {
            TYRoutePart currentRoutePart = routePartArray.get(0);
            Envelope env = new Envelope();
            currentRoutePart.getRoute().queryEnvelope(env);
            mapView.setExtent(env, 200, true);
        }
    }


    /**
     * 路径规划错误
     *
     * @param tyOfflineRouteManager
     * @param e
     */
    @Override
    public void didFailSolveRouteWithError(TYOfflineRouteManager tyOfflineRouteManager, Exception e) {
        showToast("路径规划失败");
        mapView.resetRouteLayer();
        isRouting = false;
        startPoint = null;
        endPoint = null;
    }

}
