package cn.brt.beacon.map.ext.mvp.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import cn.bingoogolapple.swipebacklayout.BGAKeyboardUtil;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import cn.brt.beacon.map.ext.R;
import cn.brt.beacon.map.ext.network.manager.RetrofitManager;
import cn.brt.beacon.map.ext.network.service.ApiService;
import cn.brt.beacon.map.ext.common.utils.MyToast;


/**
 * Created by Walkud on 17/2/28.
 */
public class MvcActivity extends RxAppCompatActivity implements BGASwipeBackHelper.Delegate {
    protected BGASwipeBackHelper mSwipeBackHelper;
    protected ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);
        super.onCreate(savedInstanceState);
        setScreenOrientation();
        apiService = RetrofitManager.getApi();
    }

    /**
     * 吐司
     */
    public void showToast(String text) {
        MyToast.show(text);
    }

    /**
     * 吐司
     *
     * @param resId
     */
    public void showToast(int resId) {
        MyToast.show(resId);
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        mSwipeBackHelper.backward();
    }

    /**
     * 设置方向
     *
     * @return
     */
    protected void setScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

    /**
     * 执行跳转到下一个Activity的动画
     */
    public void executeForwardAnim() {
        mSwipeBackHelper.executeForwardAnim();
    }

    /**
     * 执行回到到上一个Activity的动画
     */
    public void executeBackwardAnim() {
        mSwipeBackHelper.executeBackwardAnim();
    }

    /**
     * 跳转到下一个 Activity，并且销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    public void forwardAndFinish(Class<?> cls) {
        mSwipeBackHelper.forwardAndFinish(cls);
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    public void forward(Class<?> cls) {
        mSwipeBackHelper.forward(cls);
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity
     *
     * @param cls         下一个 Activity 的 Class
     * @param requestCode 请求码
     */
    public void forward(Class<?> cls, int requestCode) {
        mSwipeBackHelper.forward(cls, requestCode);
    }

    /**
     * 跳转到下一个 Activity，销毁当前 Activity
     *
     * @param intent 下一个 Activity 的意图对象
     */
    public void forwardAndFinish(Intent intent) {
        mSwipeBackHelper.forwardAndFinish(intent);
    }

    /**
     * 跳转到下一个 Activity,不销毁当前 Activity
     *
     * @param intent 下一个 Activity 的意图对象
     */
    public void forward(Intent intent) {
        mSwipeBackHelper.forward(intent);
    }

    /**
     * 跳转到下一个 Activity,不销毁当前 Activity
     *
     * @param intent      下一个 Activity 的意图对象
     * @param requestCode 请求码
     */
    public void forward(Intent intent, int requestCode) {
        mSwipeBackHelper.forward(intent, requestCode);
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity
     */
    public void backward() {
        mSwipeBackHelper.backward();
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity（应用场景：欢迎、登录、注册这三个界面）
     *
     * @param cls 上一个 Activity 的 Class
     */
    public void backwardAndFinish(Class<?> cls) {
        mSwipeBackHelper.backwardAndFinish(cls);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        BGAKeyboardUtil.handleAutoCloseKeyboard(isAutoCloseKeyboard(), getCurrentFocus(), ev, this);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 点击非 EditText 时，是否自动关闭键盘
     *
     * @return
     */
    protected boolean isAutoCloseKeyboard() {
        return true;
    }

    /**
     * 获取RxJava生命周期
     *
     * @return
     */
    protected LifecycleProvider getLifecycleProvider() {
        return this;
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

//    /**
//     * 显示导航返回按钮
//     */
//    protected void showNavBackEnabled() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.btn_nav_back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navBackClick(v);
//            }
//        });
//    }

//    /**
//     * 导航返回按钮点击事件
//     * 子类可复写该事件，默认为finish
//     *
//     * @param v
//     */
//    protected void navBackClick(View v) {
//        backward();
//    }
}
