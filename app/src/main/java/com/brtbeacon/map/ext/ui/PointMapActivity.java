package com.brtbeacon.map.ext.ui;

import android.graphics.Color;
import android.os.Bundle;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.ty.mapsdk.TYMapView;

import java.util.HashMap;
import java.util.Map;

/**
 * 标注点地图
 * 1、初始化地图、并监听地图点击事件
 * 2、创建图层
 * 3、加载地图完成事件，将图层添加至地图View
 * 4、地图点击事件，根据当前点击坐标获取对应的标注点集合，如果存在标注点，则修改标注点Mark颜色，并刷新图层，
 * 否则创建标注点并添加至图层
 * <p>
 * Created by Walkud on 2018/3/8 0008.
 */

public class PointMapActivity extends BaseMapViewActivity {

    private GraphicsLayer pointLayer;

    private int count;//总点数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        initMapEnvironment();

        //创建绘图层
        pointLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
    }

    /**
     * 地图加载完成事件
     *
     * @param mapView
     * @param error
     */
    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            mapView.addLayer(pointLayer);
        }
    }


    /**
     * 地图点击事件
     *
     * @param mapView
     * @param mappoint
     */
    @Override
    public void onClickAtPoint(TYMapView mapView, Point mappoint) {
        super.onClickAtPoint(mapView, mappoint);

        Point screenPoint = mapView.toScreenPoint(mappoint);
        //获取屏幕点?dp范围内的Graphic
        int[] gids = pointLayer.getGraphicIDs((float) screenPoint.getX(), (float) screenPoint.getY(), 25);
        if (gids.length > 0) {
            for (int gid : gids) {
                Graphic graphic = pointLayer.getGraphic(gid);
                Symbol symbol = graphic.getSymbol();
                if (symbol instanceof SimpleMarkerSymbol) {
                    SimpleMarkerSymbol markerSymbol = (SimpleMarkerSymbol) symbol;
                    markerSymbol.setColor(Color.GREEN);
                    pointLayer.updateGraphic(gid, markerSymbol);
                }
            }

        } else {
            Map<String, Object> attr = new HashMap<String, Object>();
            attr.put("name", "这是第" + (++count) + "个点");
            MarkerSymbol markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 15, SimpleMarkerSymbol.STYLE.CIRCLE);
            pointLayer.addGraphic(new Graphic(mappoint, markerSymbol, attr));
        }
    }
}
