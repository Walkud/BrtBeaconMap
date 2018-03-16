package cn.brt.beacon.map.ext.common.rx.transformer;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;


/**
 * 无处理
 *
 * @param <T>
 */
public class EmptyTransformer<T> implements ObservableTransformer<T, T> {

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream;
    }
}
