package cn.brt.beacon.map.ext.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Tank on 2017-2-24.
 * Description:网络请求工具类
 */
public class NetUtil {
    //网络请求的超时时长
    public static final int TIME_OUT = 8000;

    public static boolean isWifiConnected() {
        NetworkInfo networkInfo = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    public static boolean isNetworkAvailable() {
        NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        return networkInfo == null ? false : networkInfo.isAvailable();
    }

    private static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) AppContextUtil.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
