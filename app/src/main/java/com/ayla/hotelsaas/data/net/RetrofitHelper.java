package com.ayla.hotelsaas.data.net;

import android.content.Intent;
import android.util.Log;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.ui.LoginActivity;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private static volatile RetrofitHelper instance;

    /**
     * 用volatile修饰的变量，线程在每次使用变量的时候，都会读取变量修改后的最的值
     */
    private static volatile OkHttpClient sOkHttpClient;

    private static ApiService apiService;

    public static final String apiBaseUrl = Constance.BASE_URL;

    private static Retrofit retrofit;

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

            //HttpLoggingInterceptor打印网络日志的方法 默认日志拦截器普通版:OkHttp：
            //自定义拦截器，小写日志
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    if (MyApplication.getInstance() == null) {
                        System.out.println(message);
                    }
                    Log.d("okhttp", message);
                }
            });
            //添加请求头
            builder.addInterceptor(CommonParameterInterceptor);
            //登录失败 重新登录
            builder.addInterceptor(ReloginInterceptor);
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
                            .header("Authorization", sava_token).build();
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
            //原始接口结果
            Response originalResponse = chain.proceed(originalRequest);
            MediaType mediaType = originalResponse.body().contentType();
            String content = originalResponse.body().string();
            if (originalResponse.isSuccessful()) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int code = jsonObject.optInt("code");
                    if (code == 401 || code == 122002) {//token过期
                        final CountDownLatch countDownLatch = new CountDownLatch(1);
                        String refresh_token = SharePreferenceUtils.getString(MyApplication.getInstance(), Constance.SP_Refresh_Token, null);
                        final User[] newUser = {null};
                        Disposable subscribe = RequestModel.getInstance().refreshToken(refresh_token)
                                .doFinally(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        countDownLatch.countDown();
                                    }
                                })
                                .subscribe(new Consumer<BaseResult<User>>() {
                                    @Override
                                    public void accept(BaseResult<User> baseResult) throws Exception {
                                        newUser[0] = baseResult.data;
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.d("token_refresh", "accept: " + throwable);
                                    }
                                });
                        countDownLatch.await();
                        if (newUser[0] != null) {//token刷新成功
                            SharePreferenceUtils.saveString(MyApplication.getContext(), Constance.SP_Login_Token, newUser[0].getAuthToken());
                            SharePreferenceUtils.saveString(MyApplication.getContext(), Constance.SP_Refresh_Token, newUser[0].getRefreshToken());
                            return CommonParameterInterceptor.intercept(chain);
                        } else {//token刷新失败
                            sendLoginReceiver();
                        }
                    }
                } catch (Exception e) {
                    Log.e("token_refresh", "intercept: ", e);
                }
            }
            return originalResponse.newBuilder().body(ResponseBody.create(mediaType, content)).build();
        }

        /**
         *
         * 重新登录
         */
        private void sendLoginReceiver() {
            //跳转到首页
            Intent intent = new Intent(MyApplication.getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            MyApplication.getContext().startActivity(intent);
        }
    };
}
