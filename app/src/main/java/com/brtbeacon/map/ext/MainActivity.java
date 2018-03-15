package com.brtbeacon.map.ext;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.brtbeacon.map.ext.adapter.MainExpandableListAdapter;
import com.brtbeacon.map.ext.base.BaseActivity;
import com.brtbeacon.map.ext.bean.MainSubItem;
import com.brtbeacon.map.ext.utils.PermissionHelper;
import com.brtbeacon.map.ext.utils.TestDataUtil;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    static {
        System.loadLibrary("TYMapSDK");
        System.loadLibrary("TYLocationEngine");
    }

    @BindView(R.id.list_view)
    ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //请求外置存储写权限和粗略定位权限
        MPermissions.requestPermissions(this, PermissionHelper.WES_ACL_CODE, PermissionHelper.getWesAclPermission());
        initListView();
    }

    /**
     * 初始化列表
     */
    private void initListView() {
        final MainExpandableListAdapter adapter = new MainExpandableListAdapter(this, TestDataUtil.buildMainItems());
        listView.setAdapter(adapter);
        listView.setGroupIndicator(null);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                MainSubItem mainSubItem = adapter.getChild(i, i1);
                forward(mainSubItem.getCls());
                return true;
            }
        });
    }


    /**
     * 外置存储写权限和粗略定位权限获取成功
     */
    @PermissionGrant(PermissionHelper.WES_ACL_CODE)
    public void requestWesAclCodeSuccess() {
    }

    /**
     * 外置存储写权限和粗略定位权限获取失败
     */
    @PermissionDenied(PermissionHelper.WES_ACL_CODE)
    public void requestWesAclCodeFailed() {
        showPremissionDialog(getString(R.string.p_camera) + "、" + getString(R.string.p_location));
    }
}
