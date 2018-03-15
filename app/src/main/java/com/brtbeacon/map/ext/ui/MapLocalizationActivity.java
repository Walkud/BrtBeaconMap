package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.util.Log;
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
import com.ty.mapsdk.TYMapEnvironment;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 地图本地化
 * Created by Walkud on 2018/3/13 0013.
 */

public class MapLocalizationActivity extends BaseMapViewActivity {

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

    private String[] operations = {"地图本地化切换"};

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
                    case 0://地图本地化切换
                        //方式一
//                        if (TYMapEnvironment.getMapLanguage() == TYMapLanguage.TYSimplifiedChinese) {
//                            TYMapEnvironment.setMapLanguage(TYMapLanguage.TYEnglish);
//                        } else {
//                            TYMapEnvironment.setMapLanguage(TYMapLanguage.TYSimplifiedChinese);
//                        }

                        //方式二(自定义语言切换,需配置values-语种/strings.xml)
                        if (TYMapEnvironment.getMapCustomLocale() == null || TYMapEnvironment.getMapCustomLocale().equals("")) {
                            TYMapEnvironment.setMapCustomLanguage("en");
                        } else {
                            TYMapEnvironment.setMapCustomLanguage("");
                        }

                        //重新加载地图View,不会出发mapViewDidLoad及onFinishLoadingFloor回调
                        mapView.reloadMapView();
                        break;
                }
            }
        });
    }


    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        Log.d(TAG, "楼层加载完成");
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        Log.d(TAG, "地图初始化完成");
    }
}
