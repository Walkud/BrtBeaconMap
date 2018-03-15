package com.brtbeacon.map.ext.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 图层控制
 * Created by Walkud on 2018/3/13 0013.
 */

public class LayerControlActivity extends BaseMapViewActivity {

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

    private String[] operations = {"文字优先设施切换", "显示/隐藏文字层", "显示/隐藏设施层"};
    private boolean[] selectors = {true, true, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layer_control);
        ButterKnife.bind(this);
        initMap();
        gridView.setNumColumns(3);
        gridView.setAdapter(new ArrayAdapter<String>(this, R.layout.cell_grid_text_item, operations) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(selectors[position] ? Color.GREEN : Color.RED);
                return textView;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectors[i] = !selectors[i];
                switch (i) {
                    case 0://文字优先设施
                        mapView.setFacilityPriority(selectors[i]);//设置设施优先级
                        showToast("缩放地图，让文字和图标碰撞试试");
                        break;
                    case 1://显示/隐藏文字层
                        //获取文字图层并设置是否可见
                        mapView.getLabelGroupLayer().getLayer(1).setVisible(selectors[i]);
                        break;
                    case 2://隐藏设施层
                        //获取设置图层并设置是否可见
                        mapView.getLabelGroupLayer().getLayer(0).setVisible(selectors[i]);
                        break;
                }
                ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
            }
        });

    }

    /**
     * 初始化地图
     */
    private void initMap() {
        //初始化地图环境
        initMapEnvironment();
    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        mapView.setMinScale(mapView.getXScaleFactor(0.1));//设置最小缩放比例尺
        mapView.setMaxScale(mapView.getXScaleFactor(10));//设置最大缩放比例尺
    }
}
