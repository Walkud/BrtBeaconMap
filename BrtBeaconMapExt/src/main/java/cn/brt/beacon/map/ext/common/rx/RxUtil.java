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

package cn.brt.beacon.map.ext.common.rx;


import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class RxUtil {
    private RxUtil() {
    }

    public static <T> Observable<T> runInUIThread(T t) {
        return Observable.just(t).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> runInUIThreadDelay(T t, long delayMillis) {
        return Observable.just(t).delaySubscription(delayMillis, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> runInUIThreadDelay(T t, long delayMillis, LifecycleProvider lifecycleProvider) {
        return Observable.just(t).delaySubscription(delayMillis, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).compose(lifecycleProvider.bindToLifecycle());
    }

    public static Observable<Void> runInUIThread() {
        return runInUIThread(null);
    }

    public static Observable<Void> runInUIThreadDelay(long delayMillis) {
        return runInUIThreadDelay(null, delayMillis);
    }

    public static Observable<Void> runInUIThreadDelay(long delayMillis, LifecycleProvider lifecycleProvider) {
        return runInUIThreadDelay(null, delayMillis, lifecycleProvider);
    }

    public static <T> Observable<T> runInIoThread(T t) {
        return Observable.just(t).observeOn(Schedulers.io());
    }

    public static Observable<Void> runInIoThread() {
        return runInIoThread(null);
    }

    public static <T> Observable<T> runInIoThreadDelay(T t, long delayMillis) {
        return Observable.just(t).delaySubscription(delayMillis, TimeUnit.MILLISECONDS, Schedulers.io());
    }

    public static Observable<Void> runInIoThreadDelay(long delayMillis) {
        return runInIoThreadDelay(null, delayMillis);
    }

    /**
     * As of RxJava 1.1.7, Observable.fromCallable is still @Beta, so just in case...
     */
    public static <T> Observable<T> fromCallable(final Callable<T> callable) {
        return Observable.defer(new Callable<Observable<T>>() {

            @Override
            public Observable<T> call() {
                T result;
                try {
                    result = callable.call();
                } catch (Exception e) {
                    return Observable.error(e);
                }
                return Observable.just(result);
            }
        });
    }
}