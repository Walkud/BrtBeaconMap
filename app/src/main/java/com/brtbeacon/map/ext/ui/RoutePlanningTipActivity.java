package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.view.View;

import com.brtbeacon.map.ext.R;
import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.ty.mapdata.TYLocalPoint;
import com.ty.mapsdk.TYDirectionalHint;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYPictureMarkerSymbol;
import com.ty.mapsdk.TYRoutePart;

import java.util.List;

import butterknife.OnClick;

/**
 * 路径规划模拟导航提示
 * Created by Walkud on 2018/3/15 0015.
 */

public class RoutePlanningTipActivity extends RoutePlanningActivity {

    private int graphicID;
    private GraphicsLayer graphicsLayer;
    private int pointIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapInfoTv.setVisibility(View.VISIBLE);
        btnMoni.setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.btn_moni})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_moni://模拟导航
                if (isRouting) {
                    view.setEnabled(false);
                    mapView.setFloor(startPoint.getFloor() + "");
                    mapView.setScale(mapView.getXScaleFactor(3), true);
                    mapView.centerAt(new Point(startPoint.getX(), startPoint.getY()), false);
                    showHints(startPoint);
                } else {
                    showToast("请选择起点、终点");
                }
                break;
        }
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            //添加图层
            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);
        }
    }

    /**
     * 模拟导航，显示导航信息
     *
     * @param lp
     */
    private void showHints(TYLocalPoint lp) {

        //新建、更新指示图标位置
        if (graphicID != 0) {
            graphicsLayer.updateGraphic(graphicID, new Point(lp.getX(), lp.getY()));
        } else {
            TYPictureMarkerSymbol pms = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.location_arrow));
            pms.setWidth(50);
            pms.setHeight(50);
            graphicID = graphicsLayer.addGraphic(new Graphic(new Point(lp.getX(), lp.getY()), pms));
        }

        if (routeResult.distanceToRouteEnd(lp) < 0.5) {
            //已到达目的地
            btnMoni.setEnabled(true);
            return;
        }

        //以下代码仅用于模拟整条线路点位移动，实际场景可直接使用定位回调
        //取出本段路线上各个点
        TYRoutePart part = routeResult.getNearestRoutePart(lp);
        if (part == null) {
            return;
        }
        //取出当前路径规划线段
        Polyline line = part.getRoute();
        //获取第一个点
        Point pt = line.getPoint(pointIndex);
        pointIndex++;
        int floor = part.getMapInfo().getFloorNumber();
        //是否为本段结束点
        if (pt.equals(part.getLastPoint())) {
            pointIndex = 0;
            //是否为终段
            if (part.isLastPart()) {
                btnMoni.setEnabled(true);
            } else {
                //取下一段路线起点
                TYRoutePart nextPart = part.getNextPart();
                pt = nextPart.getFirstPoint();
                floor = nextPart.getMapInfo().getFloorNumber();
                if (floor != mapView.currentMapInfo.getFloorNumber()) {
                    mapView.setFloor(nextPart.getMapInfo());
                }
            }
        }
        TYLocalPoint localPoint = new TYLocalPoint(pt.getX(), pt.getY(), floor);
        animateUpdateGraphic(0, lp, localPoint);

        //获取当前方向
        List<TYDirectionalHint> hints = routeResult.getRouteDirectionalHint(part);
        TYDirectionalHint hint = routeResult.getDirectionalHintForLocationFromHints(localPoint, hints);
        if (hint != null) {
            //旋转屏幕
            mapView.setRotationAngle(hint.getCurrentAngle(), true);
            //设置路径规划信息
            mapView.showRouteHint(hint, false);
        }
    }

    /**
     * 显示当前导航信息
     *
     * @param lp
     */
    private void showCurrentHint(TYLocalPoint lp) {
        TYRoutePart part = routeResult.getNearestRoutePart(lp);
        if (part != null) {
            List<TYDirectionalHint> hints = routeResult.getRouteDirectionalHint(part);
            TYDirectionalHint hint = routeResult.getDirectionalHintForLocationFromHints(lp, hints);
            if (hint != null) {
                mapInfoTv.setText("方向：" + hint.getDirectionString() + hint.getRelativeDirection()
                        + "\n本段长度：" + String.format("%.2f", hint.getLength())
                        + "\n本段角度：" + String.format("%.2f", hint.getCurrentAngle())
                        + "\n剩余/全长：" + String.format("%.2f", routeResult.distanceToRouteEnd(lp))
                        + "/" + String.format("%.2f", routeResult.length));
            }
        }
    }


    /**
     * 模拟导航动画
     *
     * @param offset
     * @param lp1
     * @param lp2
     */
    private void animateUpdateGraphic(final double offset, final TYLocalPoint lp1, final TYLocalPoint lp2) {
        mapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                double distance = lp1.distanceWithPoint(lp2);//获取当前路径线段行走距离
                if (distance > 0 && offset < distance) {//当前路径线段未走完
                    //获取下步行走位置
                    Point tmp = getPointWithLengthAndOffset(lp1, lp2, offset / distance);
                    //设置为地图中心点
                    mapView.centerAt(tmp, false);
                    //显示当前路径规划模拟位置信息
                    showCurrentHint(new TYLocalPoint(tmp.getX(), tmp.getY(), lp1.getFloor()));
                    //更新图层
                    graphicsLayer.updateGraphic(graphicID, tmp);
                    //递归继续模拟当前路径线段
                    animateUpdateGraphic(offset + 0.1, lp1, lp2);
                } else {//当前路径线段已走完
                    //显示当前路径规划模拟位置信息
                    showCurrentHint(lp2);
                    //继续模拟下一段线路
                    showHints(lp2);
                }

            }
        }, 10);
    }

    /**
     * 获取下一步要行走的位置
     *
     * @param start
     * @param end
     * @param per
     * @return
     */
    private Point getPointWithLengthAndOffset(TYLocalPoint start, TYLocalPoint end, double per) {
        double scale = per;

        double x = start.getX() * (1 - scale) + end.getX() * scale;
        double y = start.getY() * (1 - scale) + end.getY() * scale;

        return new Point(x, y);
    }


}
