package cn.brt.beacon.map.ext.common.utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.socks.library.KLog;

import static android.R.attr.duration;

/**
 * 解决多个toast显示时，出现的长时间才消失的问题
 */
public class MyToast {
    private static final String TAG = MyToast.class.getSimpleName();
    public static Context sContext;


    private static Toast toast;
    private static Handler handler = new Handler();

    private static Runnable run = new Runnable() {
        public void run() {
            toast.cancel();
        }
    };

    /**
     * 空检查
     */
    private static void check() {
        if (sContext == null) {
            sContext = AppContextUtil.getInstance().getApplicationContext();
            if (sContext == null) {
                throw new NullPointerException(
                        "Must initial call MyToast.register(Context context) in your " +
                                "<? " +
                                "extends Application class>");
            }
        }
    }

    /**
     * @param msg
     * @param longDuration
     */
    private static void toast(CharSequence msg, boolean longDuration) {
        if (msg == null || TextUtils.isEmpty(msg.toString())) {
            KLog.i(TAG, "");
            return;
        }
        handler.removeCallbacks(run);
        // handler的duration不能直接对应Toast的常量时长，在此针对Toast的常量相应定义时长
        if (null != toast) {
            toast.setText(msg);
        } else {
            toast = Toast.makeText(sContext, msg, longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        }
        handler.postDelayed(run, duration);
        toast.show();
    }

    /**
     * 弹出Toast
     *
     * @param resId 内容资源Id
     */
    public static void show(int resId) {
        check();
        show(sContext.getString(resId));
    }

    /**
     * 弹出Toast
     *
     * @param msg 内容
     */
    public static void show(CharSequence msg) {
        check();
        toast(msg, false);
    }

    /**
     * 弹出Toast长时间
     *
     * @param msg 内容
     */
    public static void showLong(CharSequence msg) {
        check();
        toast(msg, true);
    }

    /**
     * 弹出Toast
     *
     * @param resId 内容资源Id
     */
    public static void showLong(int resId) {
        check();
        showLong(sContext.getString(resId));
    }

}