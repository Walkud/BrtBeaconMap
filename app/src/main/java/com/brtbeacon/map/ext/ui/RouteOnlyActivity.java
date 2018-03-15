package com.brtbeacon.map.ext.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.brtbeacon.map.ext.Constants;
import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseActivity;
import com.ty.mapdata.TYBuilding;
import com.ty.mapdata.TYLocalPoint;
import com.ty.mapsdk.TYDirectionalHint;
import com.ty.mapsdk.TYDownloader;
import com.ty.mapsdk.TYMapInfo;
import com.ty.mapsdk.TYOfflineRouteManager;
import com.ty.mapsdk.TYRoutePart;
import com.ty.mapsdk.TYRouteResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 仅路径规划，不展示地图
 * Created by Walkud on 2018/3/15 0015.
 */

public class RouteOnlyActivity extends BaseActivity {

    @BindView(R.id.route_info_tv)
    TextView routeInfoTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_only);
        ButterKnife.bind(this);
        //仅使用数据进行路径规划，定位等，不使用地图模式
        //下载地图数据
        TYDownloader.loadMap(this, Constants.BUILDING_ID, Constants.APP_KEY, new TYDownloader.mapdataLoadListener() {
            @Override
            public void onMapCompetion(TYBuilding tyBuilding, List<TYMapInfo> list) {
                routePlan(tyBuilding, list);
            }

            @Override
            public void onError(Error error) {
                Log.e("msg", error.toString());
            }
        });
    }

    private void routePlan(TYBuilding tyBuilding, List<TYMapInfo> list) {
        TYOfflineRouteManager rm = new TYOfflineRouteManager(tyBuilding, list);
        rm.addRouteManagerListener(new TYOfflineRouteManager.TYOfflineRouteManagerListener() {
            /**
             * 路径规划成功
             * @param tyOfflineRouteManager
             * @param tyRouteResult
             */
            @Override
            public void didSolveRouteWithResult(TYOfflineRouteManager tyOfflineRouteManager, TYRouteResult tyRouteResult) {
                TYRoutePart part = tyRouteResult.getAllRouteParts().get(0);
                routeInfoTv.setText("路径规划成功");
                do {
                    Log.e(TAG, part.getMapInfo().getFloorName());
                    List<TYDirectionalHint> hints = tyRouteResult.getRouteDirectionalHint(part);
                    TYDirectionalHint hint = hints.get(0);
                    do {
                        Log.e(TAG, hint.getDirectionString());
                        hint = hint.getNextHint();
                    } while (hint != null);
                    part = part.getNextPart();
                } while (part != null);
            }

            /**
             * 路径规划失败
             * @param tyOfflineRouteManager
             * @param e
             */
            @Override
            public void didFailSolveRouteWithError(TYOfflineRouteManager tyOfflineRouteManager, Exception e) {
                e.printStackTrace();
                routeInfoTv.setText("路劲规划失败");
            }
        });
        int floorNumber = list.get(0).getFloorNumber();
        rm.requestRoute(new TYLocalPoint(1.1856168483452138E7, 3425145.1770455316, floorNumber),
                new TYLocalPoint(1.1856182739021417E7, 3425150.1406408553, floorNumber));
    }
}
