package com.brtbeacon.map.ext.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brtbeacon.map.ext.R;
import com.ty.mapdata.TYLocalPoint;
import com.ty.mapsdk.PoiEntity;
import com.ty.mapsdk.TYSearchAdapter;

import java.util.List;

/**
 * 路径规划禁行
 * Created by Walkud on 2018/3/15 0015.
 */

public class RouteForbiddenActivity extends RoutePlanningActivity {

    private String[] operations = {"扶梯可行/禁行", "电梯可行/禁行"};
    private boolean[] selectors = {true, true};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridView.setVisibility(View.VISIBLE);
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
                mapView.routeManager().removeForbiddenPoints();
                switch (i) {
                    case 0://扶梯可行/禁行
                        forbidden("150014");
                        break;
                    case 1://电梯可行/禁行
                        forbidden("150013");
                        break;
                }
                ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    /**
     * 设置禁行
     *
     * @param cid
     */
    private void forbidden(String cid) {
        //查询出禁行点
        TYSearchAdapter searchAdapter = new TYSearchAdapter(mapView.building.getBuildingID());
        List<PoiEntity> list = searchAdapter.queryPoiByCategoryID(cid);
        //路径管理添加禁行点
        for (PoiEntity pe : list) {
            mapView.routeManager().addForbiddenPoint(new TYLocalPoint(pe.getLabelX(), pe.getLabelY(), pe.getFloorNumber()));
        }
    }
}
