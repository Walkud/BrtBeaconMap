package com.brtbeacon.map.ext.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.esri.android.map.GraphicsLayer;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYPictureMarkerSymbol;
import com.ty.mapsdk.TYTextSymbol;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 图文点标注
 * Created by Walkud on 2018/3/14 0014.
 */

public class MarkerImageTextActivity extends BaseMapViewActivity {


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
    @BindView(R.id.grid_view)
    GridView gridView;
    private String[] operations = {"添加图标", "添加文本", "添加点"};

    private GraphicsLayer hintLayer;
    private GraphicsLayer rotateLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_operation);
        ButterKnife.bind(this);

        initMapEnvironment();
        gridView.setNumColumns(3);
        gridView.setAdapter(new ArrayAdapter<>(this, R.layout.cell_grid_text_item, operations));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0://添加图标
                        TYPictureMarkerSymbol symbol = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.red_pushpin));
                        symbol.setWidth(24);
                        symbol.setHeight(24);
                        symbol.setOffsetX(6);
                        symbol.setOffsetY(12);
                        hintLayer.addGraphic(new Graphic(map.getCenter(), symbol));
                        break;
                    case 1://添加文本
                        TYTextSymbol textSymbol = new TYTextSymbol(MarkerImageTextActivity.this, 15, "这是一段蓝色文字", Color.BLUE);
                        textSymbol.setOffsetY(-20);
                        hintLayer.addGraphic(new Graphic(map.getCenter(), textSymbol));

                        textSymbol = new TYTextSymbol(MarkerImageTextActivity.this, 15, "这是一段红色不随地图旋转文字", Color.RED);
                        textSymbol.setOffsetY(20);
                        rotateLayer.addGraphic(new Graphic(map.getCenter(), textSymbol));
                        break;
                    case 2://添加点
                        SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(Color.BLUE, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
                        hintLayer.addGraphic(new Graphic(map.getCenter(), simpleMarkerSymbol));
                        break;
                }
            }
        });
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            //添加同步旋转图层
            hintLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            mapView.addLayer(hintLayer);

            //GraphicsLayer.RenderingMode.DYNAMIC 地图旋转时，内容保持不动,默认行为
            rotateLayer = new GraphicsLayer();
            mapView.addLayer(rotateLayer);
        }
    }
}
