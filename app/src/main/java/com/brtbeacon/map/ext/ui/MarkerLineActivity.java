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
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.ty.mapsdk.TYMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 线条标注
 * Created by Walkud on 2018/3/14 0014.
 */

public class MarkerLineActivity extends BaseMapViewActivity {

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
            //添加图层
            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);
        }
    }

    @Override
    public void onClickAtPoint(TYMapView mapView, Point mappoint) {
        super.onClickAtPoint(mapView, mappoint);


        if (prevPoint == null) {
            SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
            graphicsLayer.addGraphic(new Graphic(mappoint, simpleMarkerSymbol));
        } else {

            Line line = new Line();
            line.setStart(prevPoint);
            line.setEnd(mappoint);
            Polyline polyline = new Polyline();
            polyline.addSegment(line, true);

            SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(Color.GREEN, 5, SimpleLineSymbol.STYLE.SOLID);

            graphicsLayer.addGraphic(new Graphic(polyline, simpleLineSymbol));

            showToast("当前线段长度:" + Double.toString(Math.round(line.calculateLength2D())) + "米");
        }

        prevPoint = mappoint;
    }
}
