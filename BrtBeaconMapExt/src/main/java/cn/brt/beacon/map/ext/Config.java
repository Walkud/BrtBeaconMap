package cn.brt.beacon.map.ext;

import android.os.Environment;

import java.io.File;

/**
 * Created by Walkud on 2018/3/16 0016.
 */

public class Config {

    /**
     * 服务器地址
     */
    public static final String BASE_URL = "https://www.baidu.com";


    //重庆美源美源
    public static final String BUILDING_ID = "00230027";
    public static final String APP_KEY = "696bd5cb6f91467fba445f77909f5378";
    /**
     * 地图Beacon数据库路径
     */
    public static final String MAP_BEACON_DB_PATH = Environment.getExternalStorageDirectory() + File.separator + "TYData" + File.separator;

}
