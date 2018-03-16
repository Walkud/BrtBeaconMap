package cn.brt.beacon.map.ext.common.rx.transformer;

import android.os.Looper;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.socks.library.KLog;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


/**
 * 下拉刷新显示隐藏处理
 *
 * @param <T>
 */
public class SmartRefreshTransformer<T> implements ObservableTransformer<T, T> {
    private static final String TAG = SmartRefreshTransformer.class.getSimpleName();
    private SmartRefreshLayout smartRefreshLayout;

    public SmartRefreshTransformer(SmartRefreshLayout smartRefreshLayout) {
        this.smartRefreshLayout = smartRefreshLayout;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {

        return upstream
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        finish();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        finish();
                    }
                })
                .doOnNext(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {

                    }
                });
    }

    /**
     * 结束刷新及加载更多动画
     */
    private void finish() {
        KLog.d(TAG, "finish isUiThread:" + Looper.getMainLooper().equals(Looper.myLooper()));
        if (smartRefreshLayout != null) {
            smartRefreshLayout.finishRefresh(0);
            smartRefreshLayout.finishLoadmore(0);
        }
    }

}
