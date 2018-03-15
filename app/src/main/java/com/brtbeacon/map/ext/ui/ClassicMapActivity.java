package com.brtbeacon.map.ext.ui;

import android.os.Bundle;

import com.brtbeacon.map.ext.R;
import com.brtbeacon.map.ext.base.BaseMapViewActivity;

/**
 * 经典Map
 * Created by Administrator on 2018/3/7 0007.
 */

public class ClassicMapActivity extends BaseMapViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        initMapEnvironment();
    }

}
