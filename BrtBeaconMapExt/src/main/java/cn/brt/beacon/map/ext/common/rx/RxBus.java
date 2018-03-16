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


import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * 支持背压处理的RxBus
 */
public class RxBus {

    private final FlowableProcessor<Object> mBus;

    private RxBus() {
        // toSerialized method made bus thread safe
        mBus = PublishProcessor.create().toSerialized();
    }

    public static RxBus get() {
        return Holder.BUS;
    }

    public static void send(Object obj) {
        get().mBus.onNext(obj);
    }

    public static <T> Flowable<T> toObservable(Class<T> tClass) {
        return get().mBus.ofType(tClass);
    }

    public static <T> Flowable<T> toObservableMain(Class<T> tClass) {
        return get().mBus.ofType(tClass).observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<Object> toObservable() {
        return get().mBus;
    }

    public static boolean hasSubscribers() {
        return get().mBus.hasSubscribers();
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }
}