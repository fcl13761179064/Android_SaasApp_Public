package com.ayla.hotelsaas.data.net;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.LoginActivity;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.blankj.utilcode.util.LogUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Future;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private static ApiService apiService;

    private RetrofitHelper() {
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Constance.getBaseUrl())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    /**
     * 获取 OkHttpClient * * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(CommonParameterInterceptor);//添加请求头
        builder.addInterceptor(ReloginInterceptor);//登录失败 重新登录
        LogUtils.getConfig().setLog2FileSwitch(false).setBorderSwitch(false).setLogHeadSwitch(false);
        builder.addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.dTag("okhttp", message);
            }
        }).setLevel(Constance.isNetworkDebug() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BODY));
        builder.retryOnConnectionFailure(true);

//        builder.sslSocketFactory(SSLUtil.getSslSocketFactory().sSLSocketFactory, SSLUtil.getSslSocketFactory().trustManager)
//                .hostnameVerifier(SSLUtil.UnSafeHostnameVerifier);

        return builder.build();
    }

    /**
     * Okhttp3 拦截器
     * 添加公共请求头参数
     */
    private static Interceptor CommonParameterInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder requestBuilder = chain.request().newBuilder();
            requestBuilder.header("serviceId", "3");
            if (MyApplication.getInstance() != null) {
                final String save_token = SharePreferenceUtils.getString(MyApplication.getInstance(), Constance.SP_Login_Token, null);
                if (save_token != null) {
                    requestBuilder.header("Authorization", save_token).build();
                }
            }
            return chain.proceed(requestBuilder.build());
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
            //原始接口结果
            Response originalResponse = chain.proceed(originalRequest);

            MediaType mediaType = originalResponse.body().contentType();
            String content = originalResponse.body().string();
            originalResponse.body().close();
            if (originalResponse.isSuccessful()) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");

                    if (code == 122001 || code == 121002) {
                        synchronized (RetrofitHelper.class) {
                            if (code == 122001) {//authToken过期
                                {
                                    String authorization = originalRequest.header("Authorization");
                                    String savedToken = SharePreferenceUtils.getString(MyApplication.getContext(), Constance.SP_Login_Token, null);
                                    if (!TextUtils.isEmpty(savedToken) && !TextUtils.equals(authorization, savedToken)) {//如果本地的token不为空，并且和请求中的token不一致，说明有更新本地token
                                        return CommonParameterInterceptor.intercept(chain);
                                    }
                                }

                                Future<String[]> tokenRefreshFuture;
                                String refresh_token = SharePreferenceUtils.getString(MyApplication.getInstance(), Constance.SP_Refresh_Token, null);
                                if (!TextUtils.isEmpty(refresh_token)) {
                                    tokenRefreshFuture = RequestModel.getInstance()
                                            .refreshToken(refresh_token)
                                            .map(new Function<User, String[]>() {
                                                @Override
                                                public String[] apply(@NonNull User user) throws Exception {
                                                    return new String[]{user.getAuthToken(), user.getRefreshToken()};
                                                }
                                            })
                                            .toFuture();
                                } else {
                                    tokenRefreshFuture = Observable.just(new String[0]).toFuture();
                                }

                                String[] tokens = null;
                                try {
                                    tokens = tokenRefreshFuture.get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (tokens == null || tokens.length == 0) {//刷新token失败
                                    jump2Main();
                                } else {//token刷新成功
                                    SharePreferenceUtils.saveString(MyApplication.getContext(), Constance.SP_Login_Token, tokens[0]);
                                    SharePreferenceUtils.saveString(MyApplication.getContext(), Constance.SP_Refresh_Token, tokens[1]);
                                    return CommonParameterInterceptor.intercept(chain);
                                }
                            } else if (code == 121002) {//authToken不正确
                                jump2Main();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("token_refresh", "intercept: ", e);
                }
            }
            return originalResponse.newBuilder().body(ResponseBody.create(mediaType, content)).build();
        }
    };

    private static void jump2Main() {
        SharePreferenceUtils.remove(MyApplication.getContext(), Constance.SP_Login_Token);
        SharePreferenceUtils.remove(MyApplication.getContext(), Constance.SP_Refresh_Token);
        //跳转到首页
        Intent intent = new Intent(MyApplication.getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MyApplication.getContext().startActivity(intent);
    }
}
