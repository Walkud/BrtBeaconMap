package cn.brt.beacon.map.ext.mvp.base;

import android.os.Bundle;

import cn.brt.beacon.map.ext.mvp.base.presenter.BasePresenter;
import cn.brt.beacon.map.ext.common.utils.ReflectionUtils;


/**
 * Created by Walkud on 17/2/28.
 */

public class MvpActivity<P extends BasePresenter> extends MvcActivity {
    protected P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = ReflectionUtils.getSuperClassGenricType(this, 0);
        if (mPresenter != null) {
            mPresenter.setLifecycleProvider(this);
            mPresenter.setView(this);
            mPresenter.setModel(ReflectionUtils.getSuperClassGenricType(mPresenter, 0));
            mPresenter.setContext(this);
        }

    }
}
