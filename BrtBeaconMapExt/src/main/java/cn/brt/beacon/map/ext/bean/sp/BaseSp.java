package cn.brt.beacon.map.ext.bean.sp;

import cn.brt.beacon.map.ext.common.utils.AppContextUtil;
import me.yokeyword.api.Spf;

/**
 * 常用的Sp数据对象
 * Created by Walkud on 2018/3/16 0016.
 */

@Spf
public class BaseSp {

    /**
     * 单例Spf_BaseSp
     *
     * @return
     */
    public static Spf_BaseSp get() {
        return BaseSpHolder.baseSp;
    }

    private static class BaseSpHolder {
        static final Spf_BaseSp baseSp = Spf_BaseSp.create(AppContextUtil.getInstance());
    }
}
