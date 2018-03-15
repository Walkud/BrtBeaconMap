package com.brtbeacon.map.ext.ui;

import android.view.View;

import com.ty.mapsdk.TYOfflineRouteManager;
import com.ty.mapsdk.TYRoutePart;
import com.ty.mapsdk.TYRouteResult;

/**
 * 路径规划距离
 * Created by Walkud on 2018/3/14 0014.
 */

public class RouteDistanceActivity extends RoutePlanningActivity {

    @Override
    public void didSolveRouteWithResult(TYOfflineRouteManager tyOfflineRouteManager, TYRouteResult tyRouteResult) {
        super.didSolveRouteWithResult(tyOfflineRouteManager, tyRouteResult);
        double distance = tyRouteResult.distanceToRouteEnd(startPoint);
        int numOfPart = tyRouteResult.getAllRouteParts().size();
        String partDistance = "各段长度：";
        for (TYRoutePart part : tyRouteResult.getAllRouteParts()) {
            partDistance += String.format("%.2f", part.getRoute().calculateLength2D()) + "米\n";
        }
        //普通人每分钟走80米
        mapInfoTv.setText("全程：" + String.format("%.2f", distance) + "米\n共" + numOfPart + "段\n" + partDistance + "预计耗时：" + String.format("%.2f", distance / 80) + "分钟");
        mapInfoTv.setVisibility(View.VISIBLE);
    }
}
