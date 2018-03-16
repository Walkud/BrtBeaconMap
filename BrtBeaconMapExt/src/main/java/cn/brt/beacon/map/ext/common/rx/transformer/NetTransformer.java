package cn.brt.beacon.map.ext.common.rx.transformer;

import com.socks.library.KLog;

import java.util.concurrent.TimeUnit;

import cn.brt.beacon.map.ext.common.rx.SchedulersCompat;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 网络求情Transformer
 * 错误处理，UI线程切换,请求时延迟200毫秒，可修改
 *
 * @param <T>
 */
public class NetTransformer<T> implements ObservableTransformer<T, T> {
    private static final String TAG = NetTransformer.class.getSimpleName();

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        final Observable hideObservable = upstream.hide();
        final Observable<T> mObservable = upstream
                .flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T t) throws Exception {
                        KLog.d(TAG, "NetTransformer ：" + t.toString());

                        return Observable.just(t);
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

        //延迟200毫秒，防止请求过快，导致UI进度框闪烁
        return Observable.timer(200, TimeUnit.MILLISECONDS).flatMap(new Function<Long, ObservableSource<T>>() {
            @Override
            public ObservableSource<T> apply(Long aLong) throws Exception {
                return mObservable.compose(SchedulersCompat.<T>applyIoSchedulers());
            }

        });
    }
}
