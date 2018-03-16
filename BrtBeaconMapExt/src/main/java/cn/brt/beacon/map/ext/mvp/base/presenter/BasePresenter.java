package cn.brt.beacon.map.ext.mvp.base.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import cn.brt.beacon.map.ext.mvp.base.MvpFragment;


/**
 * Presenter基类
 * Created by Walkud on 17/2/28.
 */
public abstract class BasePresenter<M, V> {

    protected Context mContext;
    protected M mModel;
    protected V mView;
    protected LifecycleProvider mLifecycleProvider;

    public BasePresenter() {
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    public M getModel() {
        return this.mModel;
    }

    public void setModel(M model) {
        this.mModel = model;
    }

    public V getView() {
        return this.mView;
    }

    public void setView(V view) {
        this.mView = view;
    }

    public void setLifecycleProvider(LifecycleProvider lifecycleProvider) {
        this.mLifecycleProvider = lifecycleProvider;
    }

    public LifecycleProvider getLifecycleProvider() {
        return mLifecycleProvider;
    }

    public final String getString(@StringRes int resId) {
        return mContext.getString(resId);
    }

    public final String getString(@StringRes int resId, Object... formatArgs) {
        return mContext.getString(resId, formatArgs);
    }

    public <VT> LifecycleTransformer<VT> bindUntilEvent(@NonNull ActivityEvent event) {
        return mLifecycleProvider.bindUntilEvent(event);
    }

    public <VT> LifecycleTransformer<VT> bindFragmentUntilEvent(@NonNull FragmentEvent event) {
        return mLifecycleProvider.bindUntilEvent(event);
    }

    public <VT> LifecycleTransformer<VT> bindToDestroyEvent() {
        if (mView instanceof MvpFragment) {
            return bindFragmentUntilEvent(FragmentEvent.DESTROY);
        }
        return bindUntilEvent(ActivityEvent.DESTROY);
    }

    public <VT> LifecycleTransformer<VT> bindToStopEvent() {
        if (mView instanceof MvpFragment) {
            return bindFragmentUntilEvent(FragmentEvent.STOP);
        }
        return bindUntilEvent(ActivityEvent.STOP);
    }

    public <VT> LifecycleTransformer<VT> bindToLifecycle() {
        return mLifecycleProvider.bindToLifecycle();
    }
}
