package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.ty.mapsdk.TYMapEnvironment;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.tiled.TYTiledLayer;
import com.ty.mapsdk.tiled.TYTiledManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 瓦片地图
 * 存在当前界面退出时崩溃Bug未解决
 * Created by Walkud on 2018/3/13 0013.
 */

public class TiledMapActivity extends BaseMapViewActivity {

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

    private TYTiledManager tiledManager;
    private long tyTiledId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        ButterKnife.bind(this);

        mapView = map;

        tiledManager = new TYTiledManager("00210025");//BUILDING_ID;
//        TYTiledManager tiledManager = new TYTiledManager("00230027");//BUILDING_ID;

//        initMapEnvironment();
        TYMapEnvironment.initMapEnvironment();
        //演示用，实际地图会由矢量地图调用。
        onFinishLoadingFloor(mapView, null);
    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        //瓦片地图需要矢量地图配合才可以拾取POI信息等，所以可以在矢量楼层切换这里，加载瓦片数据
        if (tyTiledId > 0) {
            mapView.removeLayer(mapView.getLayerByID(tyTiledId));
        }
        TYTiledLayer tiledLayer = new TYTiledLayer(tiledManager.tileInfoByFloor("1"));
        tyTiledId = mapView.addLayer(tiledLayer);
        mapView.setExtent(tiledLayer.getExtent());
    }
}
