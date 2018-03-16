package cn.brt.beacon.map.ext.common.utils.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 自定义Gson工具类
 * Created by Walkud on 16/3/17.
 */
public class GsonUtil {

    private static class GsonHolder {
        static final Gson instance = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")//序列化日志格式
                .setExclusionStrategies(new AppExclusionStrategy())//自定义排除策略
                .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
                .serializeNulls()//序列化空字段
                .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                .registerTypeAdapter(long.class, new LongDefault0Adapter())
                .registerTypeAdapter(String.class, new StringDefaultAdapter())
                .create();
    }

    private GsonUtil() {
    }

    /**
     * 构建配置Gson
     *
     * @return
     */
    public static Gson buildGson() {
        return GsonHolder.instance;
    }

    /**
     * 自定义排除策略
     */
    private static class AppExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

        /**
         * 排除ActiveAndroid Model字段
         * 排除Wexclus注解字段
         *
         * @param f
         * @return
         */
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(WExclus.class) != null;
        }
    }

    public static <E> E parse(String s) {
        return buildGson().fromJson(s, new TypeToken<E>() {
        }.getType());
    }
}
