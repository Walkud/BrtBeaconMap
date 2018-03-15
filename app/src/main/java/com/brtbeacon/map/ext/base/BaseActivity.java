package com.brtbeacon.map.ext.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.brtbeacon.map.ext.R;

/**
 * 基类
 * Created by Administrator on 2018/3/7 0007.
 */

public class BaseActivity extends AppCompatActivity {

    protected String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
    }

    public void forward(Class cls) {
        Intent intent = new Intent(this, cls);
        forward(intent);
    }

    public void forward(Intent intent) {
        startActivity(intent);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示权限申请说明
     *
     * @param permission
     */
    public void showPremissionDialog(String permission) {
        new AlertDialog.Builder(this)
                .setMessage(permission)
                .setTitle(getString(R.string.string_help_text, permission))
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                })
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        forward(intent);
                    }
                })
                .create()
                .show();
    }
}
