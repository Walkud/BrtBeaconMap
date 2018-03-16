package cn.brt.beacon.map.ext.mvp.ui.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.socks.library.KLog;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYMapView;

import java.util.List;

import cn.brt.beacon.map.ext.R;
import cn.brt.beacon.map.ext.bean.data.BecaonEntry;
import cn.brt.beacon.map.ext.db.DBHandler;

/**
 * 楼层Beacon点加载
 * Created by Walkud on 2018/3/16 0016.
 */

public class BeaconLoadActivity extends BaseMapViewActivity {

    private List<BecaonEntry> becaonEntries;
    private GraphicsLayer graphicsLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_load);

        initMapEnvironment();
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            //添加同步旋转图层
            graphicsLayer = new GraphicsLayer();
            mapView.addLayer(graphicsLayer);
        }
    }

    @Override
    public void onFinishLoadingFloor(TYMapView mapView, TYMapInfo mapInfo) {
        super.onFinishLoadingFloor(mapView, mapInfo);
        becaonEntries = DBHandler.getInstance().findBecaonList(mapInfo.getFloorNumber());

        KLog.d("size:" + becaonEntries.size());
        graphicsLayer.removeAll();
        for (BecaonEntry becaonEntry : becaonEntries) {
            SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(Color.BLUE, 8, SimpleMarkerSymbol.STYLE.CIRCLE);
            Point point = new Point(becaonEntry.getX(), becaonEntry.getY());
            graphicsLayer.addGraphic(new Graphic(point, simpleMarkerSymbol));
        }
    }

    @Override
    public void onClickAtPoint(TYMapView mapView, Point mappoint) {
        super.onClickAtPoint(mapView, mappoint);
        //转换坐标
        Point screenPoint = mapView.toScreenPoint(mappoint);
        int[] ids = graphicsLayer.getGraphicIDs((float) screenPoint.getX(), (float) screenPoint.getY(), 5);
        for (int id : ids) {
            //改变颜色
            Graphic graphic = graphicsLayer.getGraphic(id);
            Symbol symbol = graphic.getSymbol();
            if (symbol instanceof SimpleMarkerSymbol) {
                SimpleMarkerSymbol simpleMarkerSymbol = (SimpleMarkerSymbol) symbol;
                simpleMarkerSymbol.setColor(Color.GREEN);
            }
            graphicsLayer.updateGraphic(id, symbol);
        }
    }
}
