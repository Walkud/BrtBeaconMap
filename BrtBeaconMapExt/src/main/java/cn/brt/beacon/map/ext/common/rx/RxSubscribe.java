package cn.brt.beacon.map.ext.common.rx;

import android.os.Looper;

import com.socks.library.KLog;

import org.reactivestreams.Subscriber;

/**
 * 二次封装Subscriber
 * Created by Walkud on 2017-05-04.
 */
public abstract class RxSubscribe<T> implements Subscriber<T> {
    public static final String TAG = RxSubscribe.class.getSimpleName();

    @Override
    public void onComplete() {
        KLog.d(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        KLog.e(TAG, "isUiThread:" + Looper.getMainLooper().equals(Looper.myLooper()));
        KLog.e(TAG, e);
    }

    @Override
    public void onNext(T t) {
        call(t);
    }

    public abstract void call(T t);
}
