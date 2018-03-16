package cn.brt.beacon.map.ext;

import android.app.Application;

import com.socks.library.KLog;

import java.io.File;

import cn.brt.beacon.map.ext.common.utils.AppContextUtil;
import cn.brt.beacon.map.ext.db.DBHandler;
import cn.brt.beacon.map.ext.utils.AssetsCopyTOSDcard;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = "db/00230027_Beacon.db";
                String toPath = Config.MAP_BEACON_DB_PATH + File.separator + "00230027_Beacon.db";
                if (!new File(toPath).exists()) {
                    AssetsCopyTOSDcard assetsCopyTOSDcard = new AssetsCopyTOSDcard(getApplicationContext());
                    assetsCopyTOSDcard.AssetToSD(path, toPath);
                }
                DBHandler.getInstance().initDb(toPath);
            }
        }).start();


    }

    public static App getInstance() {
        return app;
    }
}
