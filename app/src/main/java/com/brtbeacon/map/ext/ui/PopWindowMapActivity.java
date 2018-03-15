package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.esri.android.map.Callout;
import com.esri.core.geometry.Point;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYPoi;

import java.util.List;

/**
 * 弹窗地图
 * 1、初始化地图，监听地图事件
 * 2、获取地图CallOut组建
 * 3、poi选择事件，如果已显示弹窗，则先关闭弹窗，再获取出对应的poi信息重设弹窗信息，显示弹窗
 * Created by Walkud on 2018/3/8 0008.
 */

public class PopWindowMapActivity extends BaseMapViewActivity {

    private Callout callout;//弹窗组件
    private Point mappoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        initMapEnvironment();
        callout = mapView.getCallout();
    }

    @Override
    public void onClickAtPoint(TYMapView mapView, Point mappoint) {
        super.onClickAtPoint(mapView, mappoint);
        Log.d(TAG, "onClickAtPoint" + mappoint);
        this.mappoint = mappoint;
        //关闭弹窗
        if (callout != null && callout.isShowing()) {
            callout.hide();
        }

    }

    /**
     * 构建弹框自定义View
     *
     * @param msg
     * @return
     */
    private View buildPopWindowView(final String msg) {
        View view = getLayoutInflater().inflate(R.layout.layout_pop_window, null);
        TextView text = view.findViewById(R.id.message);
        TextView cancel = view.findViewById(R.id.cancel_btn);
        TextView confirm = view.findViewById(R.id.confirm_btn);

        text.setText(msg);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callout.hide();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(msg);
                callout.hide();
            }
        });

        return view;

    }

    @Override
    public void onPoiSelected(TYMapView mapView, List<TYPoi> poiList) {
        super.onPoiSelected(mapView, poiList);
        Log.d(TAG, "onPoiSelected Size：" + poiList.size());

        if (!poiList.isEmpty()) {
            //取出第一个poi
            TYPoi tyPoi = poiList.get(0);

            //已Poi位置作为显示标准
//            if (tyPoi.getGeometry() instanceof Polygon) {
//                //多边形
//                Polygon polygon = (Polygon) tyPoi.getGeometry();
//                mappoint = GeometryEngine.getLabelPointForPolygon(polygon,
//                        TYMapEnvironment.defaultSpatialReference());
//            } else {
//                mappoint = (Point) tyPoi.getGeometry();
//            }

            Log.d(TAG, "Point:" + mappoint.toString());

            String name = tyPoi.getName() == null ? "未知" : tyPoi.getName();
            String msg = "名称：" + name + "\n"
                    + "类别：" + tyPoi.getCategoryID();

            //注意这里是设置Dp
            callout.setMaxWidthDp(300);
            callout.setMaxHeightDp(300);
            //设置样式
            callout.setStyle(R.xml.callout_style);
            //设置自定义View
            callout.setContent(buildPopWindowView(msg));
            callout.show(mappoint);
        }
    }

}
