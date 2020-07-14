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
 * @创建 丁军伟
 * @时间 2017/8/3
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
            builder.addInterceptor(CommonParameterInterceptor);
            //登录失败 重新登录
            builder.addInterceptor(ReloginInterceptor);
            builder.connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS);
            builder.retryOnConnectionFailure(true);
            sOkHttpClient = builder.build();
        }
        return sOkHttpClient;
    }

    /**
     * Okhttp3 拦截器
     * 添加公共请求头参数
     */
    private static Interceptor CommonParameterInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (request.body() instanceof FormBody) {
                FormBody oldFormBody = (FormBody) request.body();
                Map<String, String> parameterMap = getRequestCommonParameter();
                //登录了加上参数
                if (null != MyApplication.getInstance().getUserEntity()) {
                    parameterMap.put("token", MyApplication.getInstance().getUserEntity().getToken());
                    parameterMap.put("userId", MyApplication.getInstance().getUserEntity().getUserId());
                }

                //获取参数
                for (int i = 0; i < oldFormBody.size(); i++) {
                    //参数内容不为null  URLDecoder对中文进行解码
                    if (!TextUtils.isEmpty(oldFormBody.encodedValue(i))) {
//                        parameterMap.put(oldFormBody.encodedName(i), URLDecoder.decode(oldFormBody.encodedValue(i), "UTF-8"));
                        parameterMap.put(oldFormBody.encodedName(i), Uri.decode(oldFormBody.encodedValue(i)));
                    }
                }
                String signMD5 = MD5.getMD5Str(SignUtils.addsignParams(parameterMap) + RequestModel.APP_SECRET).toUpperCase();
                parameterMap.put("signmethod", "MD5");
                parameterMap.put("sign", signMD5);

                //打印全部参数
//                String sign2 = SignUtils.addsignParams(parameterMap);
//                Log.d("OkHttp", sign2);
                //打印全部url的方法
                String url = request.url().toString().endsWith("?") ? request.url().toString() : request.url().toString() + "?";
//                String urlString = url + URLEncoder.encode(sign2, "UTF-8");
//                Log.d("OkHttp", "URL = " + urlString);

//                Request.Builder newBuilder = request.newBuilder().url(urlString).post(new FormBody.Builder().build());

                //只打印参数方法
                Request.Builder newBuilder = request.newBuilder().url(url).post(new FormBody.Builder().build());
                FormBody.Builder newFormBody = new FormBody.Builder();
                for (Map.Entry<String, String> paramEntry : parameterMap.entrySet()) {
                    newFormBody.addEncoded(paramEntry.getKey(), paramEntry.getValue());
                }
                newBuilder.post(newFormBody.build());
                return chain.proceed(newBuilder.build());
            }
            return chain.proceed(request);
        }
    };

    /**
     * 重新登录拦截器
     * 当code 为5003 或者为 5004 时重新登录
     */
    private static Interceptor ReloginInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //原始接口请求
            Request originalRequest = chain.request();
            Request request = originalRequest.newBuilder()
//                    .addHeader("Content-type", "application/json; charset=utf-8")
                    .build();
            //原始接口结果
            Response originalResponse = chain.proceed(request);
            //未登录直接返回
            if (!Constance.UserIsLogin) {
                return originalResponse;
            }
            try {
                //获得请求body
                JSONObject json = getResponseBodyJson(originalResponse);

                if (null != json
                        && (json.optString("code").equals("5003")
                        || json.optString("code").equals("5004"))) {
                    sendLoginReceiver();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return originalResponse;
        }

        /**
         * 登录失败跳转界面
         * 跳转到登录界面
         */
        private void sendLoginReceiver() {
            MyApplication.getInstance().setUserEntity(null);
            //跳转到首页
            Intent intent = new Intent(MyApplication.getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            MyApplication.getContext().startActivity(intent);
        }

        private JSONObject getResponseBodyJson(Response response) throws IOException, JSONException {
            if (null == response) {
                return null;
            }
            //获得请求body  不能直接用response.body()
            ResponseBody originalBody = response.peekBody(1024 * 1024);
            long originalLength = originalBody.contentLength();
            if (!HttpHeaders.hasBody(response)) {
                return null;
            } else if (bodyEncoded(response.headers())) {
                return null;
            } else {
                BufferedSource source = originalBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;

                MediaType contentType = originalBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (originalLength != 0) {
                    String result = buffer.clone().readString(charset);
                    return new JSONObject(result);
                }
                return null;
            }
        }

        private boolean bodyEncoded(Headers headers) {
            String contentEncoding = headers.get("Content-Encoding");
            return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
        }
    };

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
