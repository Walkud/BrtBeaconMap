package com.brtbeacon.map.ext.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.ty.mapsdk.PoiEntity;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYPictureMarkerSymbol;
import com.ty.mapsdk.TYSearchAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Poi距离搜索
 * Created by Walkud on 2018/3/14 0014.
 */

public class PoiDistanceSearchActivity extends BaseMapViewActivity {

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
    @BindView(R.id.poi_name_sv)
    SearchView poiNameSv;

    private GraphicsLayer graphicsLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_search);
        ButterKnife.bind(this);

        initMapEnvironment();
        poiNameSv.setQuery("10", false);
        poiNameSv.setQueryHint("请输入搜索半径距离(米)");
        poiNameSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                graphicsLayer.removeAll();
                if (s.length() > 0) {
                    showPoiByDistance(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    /**
     * 显示Poi距离
     *
     * @param s
     */
    private void showPoiByDistance(String s) {
        graphicsLayer.removeAll();
        Polygon polygon = new Polygon();
        double radius = Double.parseDouble(s);
        getCircle(mapView.getCenter(), radius, polygon);
        graphicsLayer.addGraphic(new Graphic(polygon, new SimpleFillSymbol(Color.argb(120, 255, 50, 50))));

        TYSearchAdapter searchAdapter = new TYSearchAdapter(mapView.building.getBuildingID(), 1.0);

        List<PoiEntity> searchList = searchAdapter.queryPoiByRadius(mapView.getCenter(), radius, mapView.getCurrentMapInfo().getFloorNumber());
        for (PoiEntity entity : searchList) {
            Point point = new Point(entity.getLabelX(), entity.getLabelY());
            Graphic graphic = new Graphic(point, getGreenpinSymbol());
            graphicsLayer.addGraphic(graphic);

//            TYTextSymbol textSymbol = new TYTextSymbol(this,15,entity.getName(), Color.RED);
//            textSymbol.setOffsetX(-5);
//            textSymbol.setOffsetY(-5);
//            Graphic txtGraphic = new Graphic(point,textSymbol);
//            graphicsLayer.addGraphic(txtGraphic);
        }
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);
        }
    }


    //绿色图标
    private TYPictureMarkerSymbol getGreenpinSymbol() {
        TYPictureMarkerSymbol symbol = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.green_pushpin));
        symbol.setWidth(20);
        symbol.setHeight(20);
        symbol.setOffsetX(5);
        symbol.setOffsetY(10);
        return symbol;
    }

    //	画圆形
    private void getCircle(Point center, double radius, Polygon circle) {
        circle.setEmpty();
        Point[] points = getPoints(center, radius);
        circle.startPath(points[0]);
        for (int i = 1; i < points.length; i++)
            circle.lineTo(points[i]);
    }

    private Point[] getPoints(Point center, double radius) {
        Point[] points = new Point[50];
        double sin;
        double cos;
        double x;
        double y;
        for (double i = 0; i < 50; i++) {
            sin = Math.sin(Math.PI * 2 * i / 50);
            cos = Math.cos(Math.PI * 2 * i / 50);
            x = center.getX() + radius * sin;
            y = center.getY() + radius * cos;
            points[(int) i] = new Point(x, y);
        }
        return points;
    }
}
