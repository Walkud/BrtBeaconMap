package com.brtbeacon.map.ext.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.ty.mapsdk.TYMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 形状标注
 * Created by Walkud on 2018/3/14 0014.
 */

public class MarkerShapeActivity extends BaseMapViewActivity {

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

    private GraphicsLayer graphicsLayer;
    private Point prevPoint;
    private Polygon polygon = new Polygon();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        ButterKnife.bind(this);
        initMapEnvironment();
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);
        }
    }

    @Override
    public void onClickAtPoint(TYMapView mapView, Point mappoint) {
        super.onClickAtPoint(mapView, mappoint);

        if (prevPoint == null) {
            //单点时，至添加点标注
            SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
            graphicsLayer.addGraphic(new Graphic(mappoint, simpleMarkerSymbol));
        } else {
            //线段及形状
            //1、设置创建线段
            Line line = new Line();
            line.setStart(prevPoint);
            line.setEnd(mappoint);
            //创建线段及形状样式参数
            SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(Color.GREEN, 8, SimpleLineSymbol.STYLE.SOLID);
            SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.argb(60, 128, 128, 128));
            simpleFillSymbol.setOutline(simpleLineSymbol);

            //添加形状参数,bStartNewPath为关键参数
            polygon.addSegment(line, false);
            //移除图层中所有形状
            graphicsLayer.removeAll();
            //添加形状至图层
            graphicsLayer.addGraphic(new Graphic(polygon, simpleFillSymbol));

            //计算形状面积
            String area = getAreaString(polygon.calculateArea2D());
            showToast("当前面积为：" + area);
        }

        prevPoint = mappoint;
    }


    private String getAreaString(double dValue) {
        long area = Math.abs(Math.round(dValue));
        String sArea;
        // 顺时针绘制多边形，面积为正，逆时针绘制，则面积为负
        if (area >= 1000000) {
            double dArea = area / 1000000.0;
            sArea = Double.toString(dArea) + " 平方公里";
        } else {
            sArea = Double.toString(area) + " 平方米";
        }

        return sArea;
    }
}
