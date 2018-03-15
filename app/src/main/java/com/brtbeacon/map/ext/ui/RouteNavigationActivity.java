package com.brtbeacon.map.ext.ui;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.brtbeacon.map.ext.R;
import com.esri.android.map.event.OnPinchListener;
import com.ty.locationengine.ble.TYLocationManager;
import com.ty.mapdata.TYLocalPoint;
import com.ty.mapsdk.TYDirectionalHint;
import com.ty.mapsdk.TYMapView;
import com.ty.mapsdk.TYOfflineRouteManager;
import com.ty.mapsdk.TYPoi;
import com.ty.mapsdk.TYRoutePart;
import com.ty.mapsdk.TYRouteResult;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import butterknife.OnClick;

/**
 * 路径规划导航
 * Created by Walkud on 2018/3/15 0015.
 */

public class RouteNavigationActivity extends LocationActivity {

    //音频播放控件
    private MediaPlayer player;
    private int poiID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        northArrow.setVisibility(View.VISIBLE);
        mapInfoTv.setVisibility(View.VISIBLE);

        rotateNorthOnPinch();
    }

    @OnClick({R.id.location_iv})
    public void onClick(View view) {
        super.onClick(view);
    }

    private void rotateNorthOnPinch() {
        mapView.setOnPinchListener(new OnPinchListener() {
            @Override
            public void prePointersMove(float v, float v1, float v2, float v3, double v4) {

            }

            @Override
            public void postPointersMove(float v, float v1, float v2, float v3, double v4) {
                northArrow.post(new Runnable() {
                    @Override
                    public void run() {
                        double angle = mapView.getRotationAngle() + mapView.building.getInitAngle();
                        northArrow.setRotation((float) -angle);
                    }
                });
            }

            @Override
            public void prePointersDown(float v, float v1, float v2, float v3, double v4) {

            }

            @Override
            public void postPointersDown(float v, float v1, float v2, float v3, double v4) {
            }

            @Override
            public void prePointersUp(float v, float v1, float v2, float v3, double v4) {

            }

            @Override
            public void postPointersUp(float v, float v1, float v2, float v3, double v4) {

            }
        });
    }

    @Override
    public void mapViewDidLoad(TYMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error == null) {
            //设置指南针初始为地图北偏角
            northArrow.setRotation((float) -mapView.building.getInitAngle());
        }
    }

    @Override
    public void didUpdateLocation(TYLocationManager tyLocationManager, TYLocalPoint tyLocalPoint) {

        playAudioForPoi(tyLocalPoint);

        //自动切换到定位楼层
        if (tyLocalPoint.getFloor() != mapView.currentMapInfo.getFloorNumber()) {
            mapView.setFloor(tyLocalPoint.getFloor() + "");
        }
        mapView.showLocation(tyLocalPoint);
        mapView.showPassedAndRemainingRouteResultOnCurrentFloor(tyLocalPoint);

        if (!isRouting) return;

        //终点判断，示例5米以内到达终点
        if (mapView.getRouteResult().distanceToRouteEnd(tyLocalPoint) < 5) {
            mapView.resetRouteLayer();
            isRouting = false;
            startPoint = null;
            endPoint = null;
            showTextTips("已到达终点附近。");
            return;
        }

        //偏航判断，示例5米偏航
        if (mapView.getRouteResult().isDeviatingFromRoute(tyLocalPoint, 5)) {
            mapView.clearRouteLayer();
            startPoint = tyLocalPoint;
            mapView.routeManager().requestRoute(startPoint, endPoint);
            showTextTips("已偏航5米，重新规划路径。");
            return;
        }

        String tips = "前行";
        TYRoutePart part = mapView.getRouteResult().getNearestRoutePart(tyLocalPoint);
        if (part != null) {
            List<TYDirectionalHint> hints = mapView.getRouteResult().getRouteDirectionalHint(part);
            TYDirectionalHint hint = mapView.getRouteResult().getDirectionalHintForLocationFromHints(tyLocalPoint, hints);
            if (hint != null) {
//                mapView.showRouteHint(hint,false);
                tips = "剩余：" + String.format("%.2f", mapView.getRouteResult().distanceToRouteEnd(tyLocalPoint))
                        + "(" + String.format("%.2f", mapView.getRouteResult().length) + ")" + "\n" + hint.getDirectionString();
                mapView.setRotationAngle(hint.getCurrentAngle(), true);
                northArrow.setRotation((float) (-mapView.getRotationAngle() - mapView.building.getInitAngle()));
            } else {
                tips = "剩余：" + String.format("%.2f", mapView.getRouteResult().distanceToRouteEnd(tyLocalPoint))
                        + "(" + String.format("%.2f", mapView.getRouteResult().length) + ")" + "\n沿路前行";
            }
        }
        showTextTips(tips);
    }

    @Override
    public void didUpdateImmediateLocation(TYLocationManager tyLocationManager, TYLocalPoint newLocalPoint) {
    }

    @Override
    public void didSolveRouteWithResult(TYOfflineRouteManager tyOfflineRouteManager, TYRouteResult tyRouteResult) {
        super.didSolveRouteWithResult(tyOfflineRouteManager, tyRouteResult);

        String tips = "全程：" + String.format("%.2f", tyRouteResult.length) + "米" + "预计耗时：" + String.format("%.2f", tyRouteResult.length / 80) + "分钟";
        showTextTips(tips);
    }

    private void showTextTips(final String tips) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapInfoTv.setText(tips);
            }
        });
    }

    private void playAudioForPoi(TYLocalPoint lp) {
        TYPoi poi = mapView.extractRoomPoiOnCurrentFloor(lp.getX(), lp.getY());
        if (poi != null) {
            Log.e(TAG, poi.getPoiID());
            /*
            String poiID = poi.getPoiID();
            //和上次poiID相同，不切换保持播放
            if(!poiID.equals(lastPoiID)){
               player(poiID.mp3);
               lastPoiID = poiID;
            }
            */
            //此处仅做演示，随机播放提示语音，实际处理应如上播放poi.getPoiID()相对应的音频文件。
            int rand = new Random().nextInt(2);
            if (poiID != rand) {
                player(rand == 0 ? "changbai.mp3" : rand == 1 ? "taishan.mp3" : "farewell.mp3");
                poiID = rand;
            }
        }
    }

    /**
     * 播放音频文件
     *
     * @param path
     */
    private void player(String path) {
        AssetManager am = getAssets();
        try {
            AssetFileDescriptor afd = am.openFd(path);
            if (afd != null) {
                if (player != null) player.stop();
                player = new MediaPlayer();
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                player.prepare();
                player.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) player.stop();
        if (locationManager != null) {
            locationManager.removeLocationEngineListener(this);//移除定位引擎的回调监听
            locationManager.stopUpdateLocation();//停止定位引擎
            locationManager = null;
        }
    }


}
