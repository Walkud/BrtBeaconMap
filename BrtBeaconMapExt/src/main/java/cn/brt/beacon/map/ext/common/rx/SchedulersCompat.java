package cn.brt.beacon.map.ext.common.rx;


import android.content.Context;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import cn.brt.beacon.map.ext.common.rx.transformer.NetTransformer;
import cn.brt.beacon.map.ext.common.rx.transformer.ProgressTransformer;
import cn.brt.beacon.map.ext.common.rx.transformer.SmartRefreshTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Rxjava2 避免打断链式结构：使用.compose( )操作符 http://www.jianshu.com/p/e9e03194199e
 * Created by Walkud on 2018/3/9.
 */
public class SchedulersCompat {

    private static final ObservableTransformer computationTransformer =
            new ObservableTransformer() {
                @Override
                public ObservableSource apply(Observable upstream) {
                    return upstream.subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread());
                }
            };

    private static final ObservableTransformer ioTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

    };

    private static final ObservableTransformer newTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer trampolineTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream.subscribeOn(Schedulers.trampoline())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final ObservableTransformer executorTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return upstream.subscribeOn(Schedulers.from(ExecutorManager.eventExecutor))
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * Don't break the chain: use RxJava's compose() operator
     */
    public static <T> ObservableTransformer<T, T> applyComputationSchedulers() {
        return (ObservableTransformer<T, T>) computationTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyIoSchedulers() {

        return (ObservableTransformer<T, T>) ioTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyNewSchedulers() {

        return (ObservableTransformer<T, T>) newTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyTrampolineSchedulers() {

        return (ObservableTransformer<T, T>) trampolineTransformer;
    }

    public static <T> ObservableTransformer<T, T> applyExecutorSchedulers() {

        return (ObservableTransformer<T, T>) executorTransformer;
    }

    /**
     * 网络请求ObservableTransformer
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> applyNetSchedulers() {
        return new NetTransformer<>();
    }

    /**
     * 进度事务
     *
     * @param context
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> progressSchedulers(Context context, Object obj) {
        if (obj instanceof SmartRefreshLayout) {
            return new SmartRefreshTransformer<>((SmartRefreshLayout) obj);
        }
        return new ProgressTransformer<>(context);
    }
}
