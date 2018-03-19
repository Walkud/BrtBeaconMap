package cn.brt.beacon.map.ext;

import android.app.Application;

import com.socks.library.KLog;

import cn.brt.beacon.map.ext.common.utils.AppContextUtil;

/**
 * Created by Walkud on 2018/3/16 0016.
 */

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        AppContextUtil.init(this);
        KLog.init(true);

    }

    public static App getInstance() {
        return app;
    }
}
