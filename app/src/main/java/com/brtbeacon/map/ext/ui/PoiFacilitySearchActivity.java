package com.brtbeacon.map.ext.ui;

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
import com.esri.core.map.Graphic;
import com.ty.mapsdk.PoiEntity;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYPictureMarkerSymbol;
import com.ty.mapsdk.TYSearchAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Poi设施搜索
 * Created by Walkud on 2018/3/14 0014.
 */

public class PoiFacilitySearchActivity extends BaseMapViewActivity {

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
        poiNameSv.setQuery("150013", false);
        poiNameSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                graphicsLayer.removeAll();
                if (s.length() > 0) {
                    showPoiByCID(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        graphicsLayer.removeAll();
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);
        }
    }


    private void showPoiByCID(String cid) {
        TYSearchAdapter searchAdapter = new TYSearchAdapter(mapView.building.getBuildingID(), 1.0);
        List<PoiEntity> poiEntities = searchAdapter.queryPoiByCategoryID(cid + "", mapView.currentMapInfo.getFloorNumber());
        graphicsLayer.removeAll();
        Graphic[] graphics = new Graphic[poiEntities.size()];
        int i = 0;
        for (PoiEntity entity : poiEntities) {
            if (entity.getFloorNumber() != mapView.currentMapInfo.getFloorNumber())
                return;
            Point point = new Point(entity.getLabelX(), entity.getLabelY());
            Graphic graphic = new Graphic(point, getGreenpinSymbol());
            graphics[i++] = graphic;
        }
        graphicsLayer.addGraphics(graphics);
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
}
