package com.ayla.hotelsaas.data.net;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.ssl.SSLSocketClient;
import com.ayla.hotelsaas.ui.LoginActivity;
import com.ayla.hotelsaas.utils.DateUtils;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.ToastUtil;
import com.ayla.hotelsaas.utils.ToastUtils;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @描述 Retrofit 帮助类
 * @创建 fanchunlei
 * @时间 2020/6/3
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
public class RetrofitHelper {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static volatile RetrofitHelper instance;

    /**
     * 用volatile修饰的变量，线程在每次使用变量的时候，都会读取变量修改后的最的值
     */
    private static volatile OkHttpClient sOkHttpClient;

    private static ApiService apiService;

    public static final String apiBaseUrl = Constance.BASE_URL;

    private static Retrofit retrofit;
    private Response mProceed;

    public static RetrofitHelper getInstance() {
        if (instance == null) {
            synchronized (RetrofitHelper.class) {
                if (instance == null) {
                    instance = new RetrofitHelper();
                }
            }
        }
        return instance;
    }

    private RetrofitHelper() {
        retrofit = new Retrofit.Builder()
                .client(getOkHttpClient())
                .addConverterFactory(CustomGsonConverterFactory.create())
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
            builder.sslSocketFactory(SSLSocketClient.getUnSafeSSLSocketFactory());
            /*
             * 在握手期间，如果URL的主机名和服务器的标识主机名不匹配，
             * 则验证机制可以回调此接口实现程序来确定是否应该允许此连接，
             * 如果回调内实现不恰当，默认接受所有域名，则有安全风险。
             * 参数：
             *  hostname-主机名
             *  session - 到主机的连接上使用的 SSLSession
             * 如果主机名是可接受，则返回true;
             * */
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    if ("youhostname".equals(hostname)) {
                        return true;
                    } else {
                        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                        return hv.verify(hostname, session);
                    }
                }
            });

            //HttpLoggingInterceptor打印网络日志的方法 默认日志拦截器普通版:OkHttp：
            //自定义拦截器，小写日志
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    System.out.println(message);
                    Log.d("okhttp", message);
                }
            });

            //添加请求头
            builder.addInterceptor(CommonParameterInterceptor);
            //登录失败 重新登录
            builder.addInterceptor(ReloginInterceptor);
            builder.connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS);
            builder.retryOnConnectionFailure(true);

            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);

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
            if (MyApplication.getInstance() != null) {
                final String sava_token = SharePreferenceUtils.getString(MyApplication.getInstance(), Constance.SP_Login_Token, null);
                if (sava_token != null) {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", sava_token).build();
                    return chain.proceed(request);
                }
            }
            return chain.proceed(chain.request());
        }
    };

    /**
     * 重新登录拦截器
     * 当code 为5003 或者为 5004 时重新登录
     */
    private Interceptor ReloginInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //原始接口请求
            Request originalRequest = chain.request();
            Request request = originalRequest.newBuilder()
//                    .addHeader("Content-type", "application/json; charset=utf-8")
                    .build();
            //原始接口结果
            Response originalResponse = chain.proceed(request);
            try {
                //获得请求body
                JSONObject json = getResponseBodyJson(originalResponse);
                if (null != json && (json.optInt("code") == 401)) {
                    originalResponse = sendRefreshToken(chain);
                } else if (null != json && (json.optInt("code") == 122002)) {
                    sendLoginReceiver();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return originalResponse;
        }

        /**
         *
         * 重新登录
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
            //body() 和 peekBody() 方法返回的都是一个 ResponseBody 对象，不同的是 body() 返回的当前 response 的 body，查看源码
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

    private Response sendRefreshToken(Interceptor.Chain chain) {
        String refresh_token = SharePreferenceUtils.getString(MyApplication.getInstance(), Constance.SP_Refresh_Token, null);
        JsonObject body = new JsonObject();
        body.addProperty("refreshToken", refresh_token);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        getApiService().refreshToken(new_body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new RxjavaObserver<User>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void _onNext(User data) {
                        MyApplication.getInstance().setUserEntity(data);
                        SharePreferenceUtils.saveString(MyApplication.getContext(), Constance.SP_Login_Token, data.getAuthToken());
                        SharePreferenceUtils.saveString(MyApplication.getContext(), Constance.SP_Refresh_Token, data.getRefreshToken());
                        if (!TextUtils.isEmpty(data.getAuthToken())) {
                            // 创建新的请求，并增加header
                            Request retryRequest = chain.request()
                                    .newBuilder()
                                    .header("Authorization", data.getAuthToken())
                                    .build();

                            // 再次发起请求
                            try {
                                mProceed = chain.proceed(retryRequest);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void _onError(String code, String msg) {
                    }
                });

        return mProceed;
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
        parameterMap.put("v", "1.0");
        return parameterMap;
    }
}
