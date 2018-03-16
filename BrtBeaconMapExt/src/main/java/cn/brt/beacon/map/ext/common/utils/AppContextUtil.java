package cn.brt.beacon.map.ext.common.utils;

import android.content.Context;
import android.content.res.Resources;

public class AppContextUtil {
    private static Context sContext;

    private AppContextUtil() {

    }

    public static void init(Context context) {
        sContext = context;
    }

    public static Context getInstance() {
        if (sContext == null) {
            throw new NullPointerException("the context is null,please init AppContextUtil in Application first.");
        }
        return sContext;
    }

    public static Resources getResources() {
        return getInstance().getResources();
    }
}
