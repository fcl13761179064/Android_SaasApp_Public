package com.ayla.hotelsaas.data.net;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.ui.LoginActivity;
import com.ayla.hotelsaas.utils.DateUtils;
import com.ayla.hotelsaas.utils.MD5;
import com.ayla.hotelsaas.utils.SignUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @描述 Retrofit 帮助类
 * @创建 fanchunlei
 * @时间 2020/8/3
 */

/**
 * 动态切换BaseUrl 有四种方案
 * 1.创建配置一样只是BaseUrl 不一样的 Retrofit对象
 * <p>
 * 2 @GET
 * Call<List<Client>> getClientList(@Url String anEmptyString);
 * 给每个接口传全路径作为参数
 * <p>
 * 3 拦截器方案 进行替换
 */
public class RetrofitDebugHelper {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * 设缓存有效期为三天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 3;

    private static volatile RetrofitDebugHelper instance;

    /**
     * 用volatile修饰的变量，线程在每次使用变量的时候，都会读取变量修改后的最的值
     */
    private static volatile OkHttpClient sOkHttpClient;

    private static ApiService apiService;

    private static final String apiBaseUrl = Constance.BASE_URL_BATA;
    private static Retrofit retrofit;

    public static RetrofitDebugHelper getInstance() {
        if (instance == null) {
            synchronized (RetrofitDebugHelper.class) {
                if (instance == null) {
                    instance = new RetrofitDebugHelper();
                }
            }
        }
        return instance;
    }

    private RetrofitDebugHelper() {
        retrofit = new Retrofit.Builder()
                .client(getOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(apiBaseUrl)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        if (apiService == null) {
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    /**
     * 获取 OkHttpClient * * @return OkHttpClient
     */
    private OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            //https
            SSLSocketFactory sslSocketFactory = HttpsUtils.getSslSocketFactory(null, null, null);
            builder.sslSocketFactory(sslSocketFactory);
            //设置HTTPS
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
            //添加请求头
            //登录失败 重新登录
            builder.connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS);
            builder.retryOnConnectionFailure(true);
            sOkHttpClient = builder.build();
        }
        return sOkHttpClient;
    }


    /**
     * 添加公共参数头
     * appkey   OpenApi.APP_key
     * format   json
     * apptype   drama
     * timestamp   yyyy-MM-dd HH:mm:ss
     * v        1.0
     */
    private static Map<String, String> getRequestCommonParameter() {
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("appkey", RequestModel.APP_key);
        parameterMap.put("format", "json");
        parameterMap.put("apptype", "drama");
        parameterMap.put("timestamp", DateUtils.DateToString(new Date(), DateUtils.DateStyle.YYYY_MM_DD_HH_MM_SS));
//        parameterMap.put("timestamp", "2017-08-21 18:02:00");
        parameterMap.put("v", "1.0");
        return parameterMap;
    }
}
