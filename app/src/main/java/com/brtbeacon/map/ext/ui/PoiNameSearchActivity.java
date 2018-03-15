package com.brtbeacon.map.ext.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
 * POI名称搜索
 * Created by Walkud on 2018/3/14 0014.
 */

public class PoiNameSearchActivity extends BaseMapViewActivity {

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

    private List<PoiEntity> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_search);
        ButterKnife.bind(this);

        initMapEnvironment();

        poiNameSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                graphicsLayer.removeAll();
                if (s.length() > 0) {
                    showPoiByName(s);
                }
                return false;
            }
        });
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            graphicsLayer = new GraphicsLayer();
            graphicsLayer.setSelectionColor(Color.GREEN);//设置选中后的颜色
            graphicsLayer.setSelectionColorWidth(2);//设置选中后的高亮宽度
            mapView.addLayer(graphicsLayer);
        }
    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        graphicsLayer.removeAll();
    }

    @Override
    public void onClickAtPoint(TYMapView mapView, Point mappoint) {
        super.onClickAtPoint(mapView, mappoint);

        //转换坐标
        Point screenPoint = mapView.toScreenPoint(mappoint);
        //获取当前坐标下所有图形id
        int[] grahicIds = graphicsLayer.getGraphicIDs((float) screenPoint.getX(), (float) screenPoint.getY(), 5);

        //设置是否选中
        if (grahicIds.length > 0) {
            boolean isSelected = !graphicsLayer.isGraphicSelected(grahicIds[0]);
            graphicsLayer.setSelectedGraphics(grahicIds, isSelected);
        }

        //更新图标
//        for (int graphicID : grahicUids) {
//            graphicsLayer.updateGraphic(graphicID, getGreenpinSymbol());
//        }
    }

    /**
     * 显示Poi点
     *
     * @param name
     */
    private void showPoiByName(String name) {
        //创建Poi搜索Adapter
        TYSearchAdapter searchAdapter = new TYSearchAdapter(mapView.building.getBuildingID(), 0.5);
        //在数据库中查询Poi
        searchList = searchAdapter.queryPoi(name, mapView.currentMapInfo.getFloorNumber());
        for (PoiEntity entity : searchList) {
            //创建Poi坐标点
            Point point = new Point(entity.getLabelX(), entity.getLabelY());
            //在坐标点上添加图标
            Graphic graphic = new Graphic(point, getRedpinSymbol());
            //将图标添加至图层
            graphicsLayer.addGraphic(graphic);

            Log.e(TAG, entity.toString());
            //文字添加方式
//            TYTextSymbol textSymbol = new TYTextSymbol(this,15, entity.getName(), Color.BLACK);
//            textSymbol.setOffsetX(-5);
//            textSymbol.setOffsetY(-5);
//            Graphic txtGraphic = new Graphic(point, textSymbol);
//            poiLayer.addGraphic(txtGraphic);
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

    //红色图标
    private TYPictureMarkerSymbol getRedpinSymbol() {
        TYPictureMarkerSymbol symbol = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.red_pushpin));
        symbol.setWidth(20);
        symbol.setHeight(20);
        symbol.setOffsetX(5);
        symbol.setOffsetY(10);
        return symbol;
    }
}
