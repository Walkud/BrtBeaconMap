package cn.brt.beacon.map.ext.network.manager;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.brt.beacon.map.ext.BuildConfig;
import cn.brt.beacon.map.ext.Config;
import cn.brt.beacon.map.ext.common.utils.AppContextUtil;
import cn.brt.beacon.map.ext.common.utils.NetUtil;
import cn.brt.beacon.map.ext.common.utils.gson.GsonUtil;
import cn.brt.beacon.map.ext.network.service.ApiService;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 请求
 * Created by laucherish on 16/3/15.
 */
public class RetrofitManager {

    public static final String BASE_URL = Config.BASE_URL;
    //超时时间(秒)
    public static final int OUT_TIME_SECONDS = 60;
    //短缓存有效期为1分钟
    public static final int CACHE_STALE_SHORT = 60;
    //长缓存有效期为7天
    public static final int CACHE_STALE_LONG = 60 * 60 * 24 * 7;
    //缓存大小50Mb
    public static final int CACHE_SIZE = 1024 * 1024 * 50;
    //缓存事件控制Header
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    public static final String CACHE_CONTROL_CACHE = "public, only-if-cached, max-stale=" + CACHE_STALE_LONG;
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    public static final String CACHE_CONTROL_NETWORK = "max-age=0";
    private static RetrofitManager mRetrofitManager;
    private final ApiService mApiService;
    private final OkHttpClient mOkHttpClient;

    public static RetrofitManager builder() {
        if (mRetrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (mRetrofitManager == null) {
                    mRetrofitManager = new RetrofitManager();
                }
            }
        }
        return mRetrofitManager;
    }

    public static ApiService getApi() {
        return builder().getApiService();
    }

    private RetrofitManager() {
        mOkHttpClient = buildOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.buildGson()))//自定义Gson
                .build();
        mApiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        return mApiService;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    private Context getContext() {
        return AppContextUtil.getInstance().getApplicationContext();
    }

    private OkHttpClient buildOkHttpClient() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        // 指定缓存路径
        Cache cache = new Cache(new File(getContext().getCacheDir(), "HttpCache"),
                CACHE_SIZE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .cache(cache)
                //屏蔽掉缓存策略
//                .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                .addInterceptor(logInterceptor)
                .addInterceptor(mBodyInterceptor)
                .connectTimeout(OUT_TIME_SECONDS, TimeUnit.SECONDS)
                .readTimeout(OUT_TIME_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(OUT_TIME_SECONDS, TimeUnit.SECONDS);


        if (BuildConfig.INSIDE) {
            //测试环境添加调试神器,便于测试
            Stetho.initializeWithDefaults(AppContextUtil.getInstance());
            builder.addInterceptor(new StethoInterceptor());
        }

        return builder.build();
    }

    // 云端响应头拦截器，用于配置缓存策略
    private Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetUtil.isNetworkAvailable()) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response originalResponse = chain.proceed(request);
            String cacheControl = CACHE_CONTROL_CACHE;
            if (NetUtil.isNetworkAvailable()) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                cacheControl = request.cacheControl().toString();
            }
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma").build();
        }
    };

    //Body加密拦截器,用于处理参数加密
    private Interceptor mBodyInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            return chain.proceed(request);
//            if (!(request.body() instanceof MultipartBody) && "POST".equals(request.method())) {
//
//                Request.Builder builder = request.newBuilder();
//
//                String body;
//                Headers headers;
//                //判断Body类型，区别解析和加密Body方式
//                Map<String, String> bodyMap = getBody(request);
//                body = getBodyString(bodyMap);
//                AesHelper aesHelper = new AesHelper();
//                headers = aesHelper.buildHeader(bodyMap);
//
//                //加密Body，在Body前添加=号与后台统一规则
//                String encryptBody = "=" + aesHelper.encryptStr(body);
//
//                //构建加密后的Request
//                Request newRequest = builder.headers(headers).post(RequestBody.create(null, encryptBody)).build();
//                return handleResponse(chain.proceed(newRequest), body);
//            } else {
//                //文件上传不使用https协议
//                if (request.body() instanceof MultipartBody) {
//                    request = request.newBuilder().url(request.url().newBuilder().scheme("http").build()).build();
//                }
//                return chain.proceed(request);
//            }
        }
    };

    /**
     * 是否输入FromBody
     *
     * @param request
     * @return
     */
    private boolean isFromBody(Request request) {
        RequestBody requestBody = request.body();
        return (requestBody != null) && (requestBody instanceof FormBody);
    }


    /**
     * 获取Body Map
     *
     * @param request
     * @return
     */
    private Map<String, String> getBody(Request request) throws IOException {

        RequestBody requestBody = request.body();
        if (isFromBody(request)) {
            Map<String, String> map = new HashMap<>();
            FormBody formBody = (FormBody) requestBody;
            for (int i = 0; i < formBody.size(); i++) {
                String value = formBody.value(i);
                if (!TextUtils.isEmpty(value)) {
                    map.put(formBody.name(i), value);
                }
            }
            return map;
        }

        return getBodyToMap(request);
    }


    /**
     * 获取Body
     *
     * @param bodyMap
     * @return
     */
    private String getBodyString(Map<String, String> bodyMap) {
        return new JSONObject(bodyMap).toString();
    }

    /**
     * 获取Body String
     *
     * @param request
     * @return
     * @throws IOException
     */
    private Map<String, String> getBodyToMap(Request request) throws IOException {
        String body = "";
        RequestBody requestBody = request.body();

        if (requestBody != null) {
            Buffer sink = new Buffer();
            requestBody.writeTo(sink);
            body = sink.readUtf8();
        }

        return converFormBody(body);
    }

    /**
     * 转换Body参数
     *
     * @param body
     * @return
     */
    private static Map<String, String> converFormBody(String body) {
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject json = new JSONObject(body);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = json.getString(key);
                if (!TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 处理响应
     *
     * @param response
     * @return
     */
    private Response handleResponse(Response response, String body) {
        StringBuilder logSb = new StringBuilder();
        try {
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = Charset.forName("UTF-8");
            logSb.append("Url:");
            logSb.append(response.request().url().toString());
            logSb.append("\n");
            logSb.append(",Body:");
            logSb.append(body);
            logSb.append("\n");
            logSb.append(",Response:");
            logSb.append(buffer.clone().readString(charset));
        } catch (Exception e) {
            e.printStackTrace();
            logSb.append("handleResponse error!");
        }
        KLog.d(logSb.toString());
        return response;
    }

}
