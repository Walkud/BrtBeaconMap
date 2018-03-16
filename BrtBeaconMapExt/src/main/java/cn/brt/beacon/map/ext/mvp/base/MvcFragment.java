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

package cn.brt.beacon.map.ext.mvp.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socks.library.KLog;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import cn.bingoogolapple.swipebacklayout.BGAKeyboardUtil;
import cn.brt.beacon.map.ext.R;
import cn.brt.beacon.map.ext.common.utils.AppManager;
import cn.brt.beacon.map.ext.common.utils.MyToast;
import cn.brt.beacon.map.ext.common.utils.UMAnalyticsUtil;

/**
 * Created by Walkud on 17/3/2.
 */
public abstract class MvcFragment extends RxFragment {
    protected View mContentView;
    protected MvcActivity mActivity;
    protected boolean mIsLoadedData = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MvcActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 避免多次从xml中加载布局文件
        if (mContentView == null) {
            mContentView = LayoutInflater.from(getActivity()).inflate(getRootLayoutResID(), null);
            initBind(savedInstanceState, mContentView);
            initView(savedInstanceState, mContentView);
            setListener();
            processLogic(savedInstanceState);
        } else {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
        }
        return mContentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            handleOnVisibilityChangedToUser(isVisibleToUser);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(false);
        }
    }

    /**
     * 处理对用户是否可见
     *
     * @param isVisibleToUser
     */
    private void handleOnVisibilityChangedToUser(boolean isVisibleToUser) {
        UMAnalyticsUtil.onVisibilityChangedToUser(this, isVisibleToUser);

        if (isVisibleToUser) {
            // 对用户可见
            if (!mIsLoadedData) {
                KLog.d(this.getClass().getSimpleName() + " 懒加载一次");
                mIsLoadedData = true;
                onLazyLoadOnce();
            }
            KLog.d(this.getClass().getSimpleName() + " 对用户可见");
            onVisibleToUser();
        } else {
            // 对用户不可见
            KLog.d(this.getClass().getSimpleName() + " 对用户不可见");
            onInvisibleToUser();
        }
    }

    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法
     */
    protected void onLazyLoadOnce() {
    }

    /**
     * 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法
     */
    protected void onVisibleToUser() {
    }

    /**
     * 对用户不可见时触发该方法
     */
    protected void onInvisibleToUser() {
    }

    /**
     * 获取布局文件根视图
     *
     * @return
     */
    protected abstract
    @LayoutRes
    int getRootLayoutResID();

    /**
     * 初始化 Bind View ,用于ButterKnife注入登框架，在initView之前执行
     */
    protected void initBind(Bundle savedInstanceState, View rootView) {
    }

    /**
     * 初始化 View 控件
     */
    protected abstract void initView(Bundle savedInstanceState, View rootView);

    /**
     * 给 View 控件添加事件监听器
     */
    protected void setListener() {
    }

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected void processLogic(Bundle savedInstanceState) {
    }

    /**
     * 跳转到下一个 Activity，并且销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    public void forwardAndFinish(Class<?> cls) {
        forward(cls);
        mActivity.finish();
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    public void forward(Class<?> cls) {
        BGAKeyboardUtil.closeKeyboard(mActivity);
        startActivity(new Intent(mActivity, cls));
        mActivity.executeForwardAnim();
    }

    /**
     * 跳转到下一个 Activity，并且销毁当前 Activity
     *
     * @param intent
     */
    public void forwardAndFinish(Intent intent) {
        forward(intent);
        mActivity.finish();
    }

    public void forward(Intent intent) {
        BGAKeyboardUtil.closeKeyboard(mActivity);
        startActivity(intent);
        mActivity.executeForwardAnim();
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity 接收回调
     *
     * @param cls
     * @param requestCode
     */
    public void forward(Class<?> cls, int requestCode) {
        forward(new Intent(mActivity, cls), requestCode);
    }


    /**
     * 跳转到下一个 Activity，不销毁当前 Activity 接收回调
     *
     * @param intent
     * @param requestCode
     */
    public void forward(Intent intent, int requestCode) {
        BGAKeyboardUtil.closeKeyboard(mActivity);
        startActivityForResult(intent, requestCode);
        mActivity.executeForwardAnim();
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity
     */
    public void backward() {
        BGAKeyboardUtil.closeKeyboard(mActivity);
        mActivity.finish();
        mActivity.executeBackwardAnim();
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity（应用场景：欢迎、登录、注册这三个界面）
     *
     * @param cls 上一个 Activity 的 Class
     */
    public void backwardAndFinish(Class<?> cls) {
        BGAKeyboardUtil.closeKeyboard(mActivity);
        startActivity(new Intent(mActivity, cls));
        mActivity.executeBackwardAnim();
        mActivity.finish();
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT findViewById(@IdRes int id) {
        return mContentView.findViewById(id);
    }

    /**
     * 查找View
     *
     * @param view View
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(View view, @IdRes int id) {
        return (VT) view.findViewById(id);
    }

    /**
     * 绑定 Rxlifecycle Destroy生命周期销毁事件
     * 便于子类使用
     *
     * @param <T>
     * @return
     */
    public <T> LifecycleTransformer<T> bindToDestroyEvent() {
        return bindUntilEvent(FragmentEvent.DESTROY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().refWatcherWatchFragment(this);
    }

    /**
     * 显示toast
     *
     * @param msgId
     */
    public void showToast(int msgId) {
        MyToast.show(getString(msgId));
    }

    /**
     * 显示toast
     *
     * @param msg
     */
    public void showToast(final String msg) {
        MyToast.show(msg);
    }


    /**
     * 显示权限申请说明
     *
     * @param permission
     */
    public void showPremissionDialog(String permission) {
        new AlertDialog.Builder(mActivity)
                .setMessage(permission)
                .setTitle(getString(R.string.string_help_text, permission))
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                        forward(intent);
                    }
                })
                .create()
                .show();
    }

    /**
     * 隐藏View
     *
     * @param views
     */
    public void goneView(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 显示View
     *
     * @param views
     */
    public void visibleView(View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }
}