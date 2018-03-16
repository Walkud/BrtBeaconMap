package cn.brt.beacon.map.ext.common.rx.transformer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


/**
 * 进度显示隐藏处理
 * 如果当用户再进度显示期间点击Back，隐藏进度的同时会放弃本次请求回调
 *
 * @param <T>
 */
public class ProgressTransformer<T> implements ObservableTransformer<T, T> {
    private Subject<Boolean> mSubject = PublishSubject.create();
    private ProgressDialog loadingDialog;

    public ProgressTransformer(Context ct) {
        init(ct, true);
    }


    public ProgressTransformer(Context ct, boolean isShowDialog) {
        init(ct, isShowDialog);
    }

    private void init(Context ct, boolean isShowDialog) {
        //是否显示Dialog,isShowDialog标记用于某些请求可不显示Dialog
        if (isShowDialog) {
            loadingDialog = new ProgressDialog(ct);
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mSubject.onNext(true);
                }
            });
        }
    }


    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream
                .takeUntil(mSubject.filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                }).take(1))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(
                        new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                show();
                            }
                        })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dismiss();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        dismiss();
                    }
                });
    }

    private void show() {
        if (loadingDialog != null) {
            loadingDialog.show();
        }
    }

    private void dismiss() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

}
