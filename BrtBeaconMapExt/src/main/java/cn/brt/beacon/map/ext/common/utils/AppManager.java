/**
 * Copyright 2016 bingoogolapple
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.brt.beacon.map.ext.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.socks.library.KLog;

import java.util.Iterator;
import java.util.Stack;

import cn.brt.beacon.map.ext.App;
import cn.brt.beacon.map.ext.R;
import cn.brt.beacon.map.ext.common.rx.RxBus;
import cn.brt.beacon.map.ext.common.rx.RxEvent;

import static android.content.Context.ACTIVITY_SERVICE;

public class AppManager implements Application.ActivityLifecycleCallbacks {
    private static final AppManager sInstance;
    private static final App sApp;

    private int mActivityStartedCount = 0;
    private long mLastPressBackKeyTime;
    private Stack<Activity> mActivityStack = new Stack<>();

    private boolean mIsBuildDebug;
    private Delegate mDelegate;

    static {
        sApp = App.getInstance();
        sInstance = new AppManager();
        sApp.registerActivityLifecycleCallbacks(sInstance);
    }

    private AppManager() {

        //注册监听网络状况
        sApp.registerReceiver(new BroadcastReceiver() {

            private long onReceiveTime = 0L;

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    try {
                        boolean isAvailable = NetUtil.isNetworkAvailable();
                        KLog.d("网络变化：" + isAvailable);
                    } catch (Exception e) {
                    }
                }
            }
        }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * 必须在 Application 的 onCreate 方法中调用
     *
     * @param buildType 构建类型，传入 BuildConfig.BUILD_TYPE
     * @param delegate  代理
     */
    public void init(String buildType, Delegate delegate) {
        mIsBuildDebug = "debug".equals(buildType);
        mDelegate = delegate;

        UMAnalyticsUtil.initSdk(getUemngAppKey(), getChannel());
    }

    public static AppManager getInstance() {
        return sInstance;
    }

    public static App getApp() {
        return sApp;
    }

    /**
     * 是否构建的是 debug
     *
     * @return
     */
    public boolean isBuildDebug() {
        return mIsBuildDebug;
    }

    /**
     * LeakCanary 监控 Fragment 的内存泄露
     *
     * @param fragment
     */
    public void refWatcherWatchFragment(Fragment fragment) {
        if (mDelegate != null) {
            mDelegate.refWatcherWatchFragment(fragment);
        }
    }

    /**
     * Activity 中是否不包含 Fragment。用于处理友盟页面统计，避免重复统计 Activity 和 Fragment
     *
     * @param activity
     * @return
     */
    public boolean isActivityNotContainFragment(Activity activity) {
        if (mDelegate != null) {
            return !mDelegate.isActivityContainFragment(activity);
        }
        // 默认返回 true，表示该 Activity 中不包含 Fragment
        return true;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityStack.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (mActivityStartedCount == 0) {
            RxBus.send(new RxEvent.AppEnterForegroundEvent());
        }
        mActivityStartedCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        UMAnalyticsUtil.onActivityResumed(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        UMAnalyticsUtil.onActivityPaused(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityStartedCount--;
        if (mActivityStartedCount == 0) {
            RxBus.send(new RxEvent.AppEnterBackgroundEvent());
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityStack.remove(activity);
    }

    /**
     * Activity栈是否是空的
     *
     * @return
     */
    public boolean isActivityStackEmpty() {
        return mActivityStack.isEmpty();
    }

    /**
     * Activity栈中Activity的个数
     *
     * @return
     */
    public int getActivityStackSize() {
        return mActivityStack.size();
    }

    /**
     * 获取当前栈顶Activity
     *
     * @return
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (!mActivityStack.empty()) {
            activity = mActivityStack.lastElement();
        }
        return activity;
    }

    /**
     * 移除指定Activity
     *
     * @param activity
     */
    public void popOneActivity(Activity activity) {
        if (activity == null || mActivityStack.isEmpty()) {
            return;
        }
        if (!activity.isFinishing()) {
            activity.finish();
        }
        mActivityStack.remove(activity);
    }

    /**
     * 应用场景：支付完后，关闭 MainActivity 之外的其他页面
     *
     * @param activityClass
     */
    public void popOthersActivity(Class<Activity> activityClass) {
        if (activityClass == null || mActivityStack.isEmpty()) {
            return;
        }

        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (!activity.getClass().equals(activityClass)) {
                activity.finish();
                iterator.remove();
            }
        }
    }

    /**
     * 双击后 全退出应用程序
     */
    public void exitWithDoubleClick() {
        if (System.currentTimeMillis() - mLastPressBackKeyTime <= 2000) {
            exit();
        } else {
            mLastPressBackKeyTime = System.currentTimeMillis();
            MyToast.show(R.string.toast_exit_tip);
        }
    }

    /**
     * 关闭所有Activity
     */
    public void finishAllActivity() {
        try {
            while (true) {
                Activity activity = currentActivity();
                if (activity == null) {
                    break;
                }
                popOneActivity(activity);
            }
        } catch (Exception e) {
            KLog.e("关闭所有Activity错误");
        }
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        try {
            finishAllActivity();

//            // 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据
//            UMAnalyticsUtil.onKillProcess();
//
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
        } catch (Exception e) {
            KLog.e("退出错误");
        }
    }

    /**
     * 应用是否在后台
     *
     * @return
     */
    public boolean isBackStage() {
        return mActivityStartedCount == 0;
    }

    /**
     * 应用是否在前台
     *
     * @return
     */
    public boolean isFrontStage() {
        return mActivityStartedCount > 0;
    }

    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName() {
        try {
            return sApp.getPackageManager().getPackageInfo(sApp.getPackageName(), 0).applicationInfo.loadLabel(sApp.getPackageManager()).toString();
        } catch (Exception e) {
            // 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
            return "";
        }
    }

    /**
     * 获取当前版本名称
     *
     * @return
     */
    public static String getCurrentVersionName() {
        try {
            return sApp.getPackageManager().getPackageInfo(sApp.getPackageName(), 0).versionName;
        } catch (Exception e) {
            // 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
            return "";
        }
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    public static int getCurrentVersionCode() {
        try {
            return sApp.getPackageManager().getPackageInfo(sApp.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            // 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
            return 0;
        }
    }

    /**
     * 获取Umeng Appkey
     *
     * @return
     */
    private static String getUemngAppKey() {
        try {
            ApplicationInfo appInfo = sApp.getPackageManager().getApplicationInfo(sApp.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_APPKEY");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取渠道号
     *
     * @return
     */
    private static String getChannel() {
        try {
            ApplicationInfo appInfo = sApp.getPackageManager().getApplicationInfo(sApp.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isInOtherProcess(Context context) {
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        Iterator iterator = am.getRunningAppProcesses().iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (iterator.next());
            try {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;
                }
            } catch (Exception e) {
                Log.d(AppManager.class.getSimpleName(), "Error>> :" + e.toString());
            }
        }
        return processName == null || !processName.equalsIgnoreCase(context.getPackageName());
    }

    public interface Delegate {

        /**
         * LeakCanary 监控 Fragment 的内存泄露
         *
         * @param fragment
         */
        void refWatcherWatchFragment(Fragment fragment);

        /**
         * Activity 中是否包含 Fragment。用于处理友盟页面统计，避免重复统计 Activity 和 Fragment
         *
         * @param activity
         * @return
         */
        boolean isActivityContainFragment(Activity activity);
    }
}
