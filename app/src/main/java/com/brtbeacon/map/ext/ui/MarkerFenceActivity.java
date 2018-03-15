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
import android.widget.Toast;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;
import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYPictureMarkerSymbol;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 标注围栏
 * Created by Walkud on 2018/3/14 0014.
 */

public class MarkerFenceActivity extends BaseMapViewActivity {

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
    @BindView(R.id.fence_status_tv)
    TextView fenceStatusTv;

    private Polygon polygon = new Polygon();
    private String[] operations = {"模拟移动"};
    private GraphicsLayer graphicsLayer;
    private int graphicID;
    private boolean isStart;//是否开始模拟移动

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_fence);
        ButterKnife.bind(this);

        initMapEnvironment();

        gridView.setNumColumns(3);
        gridView.setAdapter(new ArrayAdapter<>(this, R.layout.cell_grid_text_item, operations));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0://模拟移动
                        if (!isStart) {
                            isStart = true;
                            graphicsLayer.updateGraphic(graphicID, new Point(mapView.currentMapInfo.getMapExtent().getXmin(), mapView.currentMapInfo.getMapExtent().getYmax()));
                            movePoint(1);
                        }
                        break;
                }
            }
        });
    }

    /**
     * 模拟移动
     *
     * @param delta
     */
    private void movePoint(final double delta) {
        //获取图层上的图形
        Graphic graphic = graphicsLayer.getGraphic(graphicID);
        if (graphic == null) return;
        //判断移动前是否在图形内
        boolean isOldIn = GeometryEngine.contains(polygon, graphic.getGeometry(), mapView.getSpatialReference());
        //设置移动距离
        Point point = (Point) graphic.getGeometry();
        point.setX(point.getX() + delta);
        point.setY(point.getY() - delta);
        //更新图层
        graphicsLayer.updateGraphic(graphicID, point);

        //计算移动点与图形的距离
        double distance = GeometryEngine.distance(polygon, point, mapView.getSpatialReference());
        fenceStatusTv.setText(String.format("距离区域%.2f米", distance));
        //判断移动后是否在图形内
        boolean isNowIn = GeometryEngine.contains(polygon, point, mapView.getSpatialReference());
        //判断移动前后移动后状态是否发生变化
        if (isNowIn != isOldIn) {
            Toast.makeText(this, isNowIn ? "进入区域" : "离开区域", Toast.LENGTH_SHORT).show();
            if (isOldIn) {
                //离开区域后停止模拟移动
                isStart = false;
                return;
            }
        }
        //模拟重复移动
        mapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                movePoint(delta);
            }
        }, 100);
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            //获取地图的中心点
            TYMapInfo info = mapView.allMapInfo().get(0);
            Envelope envelope = new Envelope(info.getMapExtent().getXmin(), info.getMapExtent().getYmin(), info.getMapExtent().getXmax(), info.getMapExtent().getYmax());
            Point center = envelope.getCenter();
            //构建圆形围栏参数
            getCircle(center, envelope.getWidth() / 3.0, polygon);

            //创建图层
            graphicsLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
            //再图层上添加围栏图形
            graphicsLayer.addGraphic(new Graphic(polygon, new SimpleFillSymbol(Color.argb(80, 128, 128, 128), SimpleFillSymbol.STYLE.SOLID)));
            //添加图层至地图上
            mapView.addLayer(graphicsLayer);

            //创建模拟移动点
            TYPictureMarkerSymbol tyPictureMarkerSymbol = new TYPictureMarkerSymbol(getResources().getDrawable(R.drawable.location_arrow));
            tyPictureMarkerSymbol.setWidth(40);
            tyPictureMarkerSymbol.setHeight(40);
            Graphic moniMan = new Graphic(envelope.getUpperLeft(), tyPictureMarkerSymbol);
            //将模拟点添加至图层
            graphicID = graphicsLayer.addGraphic(moniMan);
        }
    }


    /**
     * 构建圆形围栏参数
     *
     * @param center
     * @param radius
     * @param circle
     */
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
