package com.brtbeacon.map.ext.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
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
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 地图手势
 * Created by Walkud on 2018/3/13 0013.
 */

public class MapGestureActivity extends BaseMapViewActivity {

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
    private String[] operations = {"启用/禁止双指旋转", "启用/禁止缩放", "启用/禁用所有"};
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
                    case 0://启用/禁止双指旋转
                        mapView.setAllowRotationByPinch(selectors[i]);
                        break;
                    case 1://启用/禁止缩放
                        setMapScale();
                        break;
                    case 2://启用/禁用所有手势
                        break;
                }
                ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
            }
        });

        TouchListener touchListener = new TouchListener(this, mapView);
        mapView.setOnTouchListener(touchListener);

    }

    /**
     * 地图触摸事件
     */
    public class TouchListener extends MapOnTouchListener {

        public TouchListener(Context context, MapView view) {
            super(context, view);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!selectors[2]) {//禁用所有手势
                //消费触摸事件，不调用父类onTouch
                return false;
            }
            return super.onTouch(v, event);
        }
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        //初始化地图环境
        initMapEnvironment();
    }

    /**
     * 设置地图缩放比例尺(开启/禁用缩放)
     */
    private void setMapScale() {
        double minScale = selectors[1] ? 0.1 : 1;
        double maxScale = selectors[1] ? 10 : 1;
        mapView.setMinScale(mapView.getXScaleFactor(minScale));//设置最小缩放比例尺
        mapView.setMaxScale(mapView.getXScaleFactor(maxScale));//设置最大缩放比例尺
    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        setMapScale();
    }
}
