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
import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/3 下午11:13
 * 描述:友盟 SDK 工具类
 */
public class UMAnalyticsUtil {
    /**
     * 是否已经初始化过
     */
    private static boolean sIsInit = false;

    private UMAnalyticsUtil() {
    }

    /**
     * 初始化友盟 SDK，在 Application 的 onCreate 方法里调用
     *
     * @param appKey    友盟的 AppKey
     * @param channelId 渠道号
     */
    public static void initSdk(String appKey, String channelId) {
        initSdk(new MobclickAgent.UMAnalyticsConfig(AppManager.getApp(), appKey, channelId));
    }

    /**
     * 初始化友盟 SDK，在 Application 的 onCreate 方法里调用
     *
     * @param analyticsConfig
     */
    public static void initSdk(MobclickAgent.UMAnalyticsConfig analyticsConfig) {
        sIsInit = true;

        MobclickAgent.startWithConfigure(analyticsConfig);
        MobclickAgent.setDebugMode(AppManager.getInstance().isBuildDebug());
        // 禁止默认的页面统计方式，这样将不会再自动统计Activity
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setSessionContinueMillis(AppManager.getInstance().isBuildDebug() ? 3000 : 30000);
    }

    /**
     * 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据
     */
    public static void onKillProcess() {
        if (sIsInit) {
            MobclickAgent.onKillProcess(AppManager.getApp());
        }
    }

    // ======================== 页面路径统计 START ========================

    /**
     * 在 MvcFragment 的 handleOnVisibilityChangedToUser 方法中调用
     *
     * @param fragment
     * @param isVisibleToUser
     */
    public static void onVisibilityChangedToUser(Fragment fragment, boolean isVisibleToUser) {
        if (sIsInit) {
            // 统计页面
            if (isVisibleToUser) {
                MobclickAgent.onPageStart(fragment.getClass().getSimpleName());
            } else {
                MobclickAgent.onPageEnd(fragment.getClass().getSimpleName());
            }
        }
    }

    /**
     * 在 ActivityLifecycleCallbacks 的 onActivityResumed 回调方法里调用
     *
     * @param activity
     */
    public static void onActivityResumed(Activity activity) {
        if (sIsInit) {
            // 统计页面
            if (AppManager.getInstance().isActivityNotContainFragment(activity)) {
                MobclickAgent.onPageStart(activity.getClass().getSimpleName());
            }
            // 统计时长
            MobclickAgent.onResume(activity);
        }
    }

    /**
     * 在 ActivityLifecycleCallbacks 的 onActivityPaused 回调方法里调用
     *
     * @param activity
     */
    public static void onActivityPaused(Activity activity) {
        if (sIsInit) {
            // 统计页面
            if (AppManager.getInstance().isActivityNotContainFragment(activity)) {
                // 保证 onPageEnd 在 onPause 之前调用,因为 onPause 中会保存信息
                MobclickAgent.onPageEnd(activity.getClass().getSimpleName());
            }
            // 统计时长
            MobclickAgent.onPause(activity);
        }
    }
    // ======================== 页面路径统计 START ========================
}