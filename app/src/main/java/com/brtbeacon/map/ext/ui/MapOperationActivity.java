package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.esri.core.geometry.Point;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 地图操作
 * Created by Walkud on 2018/3/13 0013.
 */
public class MapOperationActivity extends BaseMapViewActivity {

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

    private String[] operations = {"设置最大最小比例尺", "显示最小比例尺", "旋转180°", "移动到中心点"};

    private boolean floorLoaded = false;//地图楼层是否加载完成

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_operation);
        ButterKnife.bind(this);
        initMap();
        gridView.setNumColumns(3);
        gridView.setAdapter(new ArrayAdapter<>(this, R.layout.cell_grid_text_item, operations));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0://设置最大最小比例尺

                        /*此属性设置必须在楼层加载完成时调用才有效，如果在onCreate中调用，地图没有绘制完成会抛出空指针异常,
                          如果在mapViewDidLoad调用也无效，因为暂无楼层信息
                          以下修改比例尺上下限（0.5~5倍，亦可自行计算：实际距离／屏幕距离）
                         */
                        if (floorLoaded) {
                            mapView.setMinScale(mapView.getXScaleFactor(0.5f));//最小缩放0.5倍
                            mapView.setMaxScale(mapView.getXScaleFactor(5f));//最大缩放5倍
                        } else {
                            showToast("等待楼层加载完成");
                        }
                        break;
                    case 1://显示最小比例尺
//                        mapView.setScale(mapView.getMinScale());//不带动画效果
                        //设置缩放比例为1倍于屏幕
//                        mapView.setScale(mapView.getXScaleFactor(1f), true);
                        mapView.setScale(mapView.getMinScale(), true);
                        break;
                    case 2://旋转180°

                        double targetAngle = mapView.getRotationAngle() - 180;
                        if (targetAngle >= 0) {
                            mapView.setRotationAngle(targetAngle);//不带动画效果
                            mapView.setRotationAngle(targetAngle, true);
                        } else {
                            mapView.setRotationAngle(mapView.getRotationAngle() + 180, true);
                        }
                        break;
                    case 3://移动到中心点
                        if (mapView.initialEnvelope != null) {
                            Point point = mapView.initialEnvelope.getCenter();
                            mapView.centerAt(point, true);
                        }
                        break;
                }
            }
        });

    }

    /**
     * 初始化地图
     */
    private void initMap() {
        initMapEnvironment();
    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        floorLoaded = true;
    }
}
