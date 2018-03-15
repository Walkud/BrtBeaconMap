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
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYPoi;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 拾取POI
 * 1、添加图层
 * 2、根据POI选取事件回调，获取到POI列表，然后再画出POI形状
 * Created by Walkud on 2018/3/13 0013.
 */

public class PickupPoiActivity extends BaseMapViewActivity {

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

    private GraphicsLayer hintLayer;
    private Point mappoint;

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
            hintLayer = new GraphicsLayer();
            map.addLayer(hintLayer);
        }
    }

    @Override
    public void onClickAtPoint(TYMapView mapView, Point mappoint) {
        super.onClickAtPoint(mapView, mappoint);
        this.mappoint = mappoint;
    }

    @Override
    public void onPoiSelected(TYMapView mapView, List<TYPoi> poiList) {
        super.onPoiSelected(mapView, poiList);

        hintLayer.removeAll();
        for (TYPoi tyPoi : poiList) {
            //显示填充色
            hintLayer.addGraphic(new Graphic(tyPoi.getGeometry(), new SimpleFillSymbol(Color.argb(70, 0, 0, 255))));
            //显示边框
            hintLayer.addGraphic(new Graphic(tyPoi.getGeometry(), new SimpleLineSymbol(Color.RED, 2)));
            //显示点(Poi点击点作为显示点坐标)
            hintLayer.addGraphic(new Graphic(mappoint, new SimpleMarkerSymbol(Color.YELLOW, 5, SimpleMarkerSymbol.STYLE.CIRCLE)));

            //现实点(POI形状的中心点作为显示点坐标)
//            Point point;
//            if (tyPoi.getGeometry() instanceof Polygon) {
//                point = GeometryEngine.getLabelPointForPolygon((Polygon) tyPoi.getGeometry(), mapView.getSpatialReference());
//            } else {
//                point = (Point) tyPoi.getGeometry();
//            }
//            hintLayer.addGraphic(new Graphic(point, new SimpleMarkerSymbol(Color.YELLOW, 5, SimpleMarkerSymbol.STYLE.CIRCLE)));
        }

    }
}
