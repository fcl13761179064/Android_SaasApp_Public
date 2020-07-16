package com.ayla.hotelsaas;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.data.net.RetrofitHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

public class ConsumerDemoTest {

    static final Map<String, String> HEADERS;

    static {

        HEADERS = new HashMap<>();
        HEADERS.put("Content-Type", "application/json");
    }

    @Before
    public void setUp() {
        Constance.BASE_URL = "http://localhost:9292";
    }

    @Rule
    public PactProviderRule mockProvider = new PactProviderRule("our_provider", "localhost", 9292, this);

    @Pact(provider = "our_provider", consumer = "our_consumer")
    public RequestResponsePact createFragment(PactDslWithProvider builder) throws UnsupportedEncodingException {
        BaseResult<User> result2 = new BaseResult<>();
        result2.code = "0";
        result2.error = "账号或密码错误";
        return builder
                .given("正确的用户密码")
                .uponReceiving("用户实例，当前简单，只包含token")
                .path("/login")
                .method("GET")
                .query("username=111&password=222")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody().stringType("token", "11111"))

                .given("错误的用户名或密码")
                .uponReceiving("code = 1001，表示：用户名或密码错误")
                .path("/login")
                .method("GET")
                .query("username=111&password=333")
                .willRespondWith()
                .status(400)
                .body(new PactDslJsonBody().numberValue("code", 1001).stringType("message", "用户名或密码错误"))
                .toPact();
    }

    @Test
    @PactVerification("our_provider")
    public void testLogin() {
        loginSuccess();
        loginFailed();
    }

    private void loginSuccess() {
        RetrofitHelper.getInstance()
                .getApiService()
                .login("111", "222")
                .subscribe();
    }

    private void loginFailed() {
        RetrofitHelper.getInstance()
                .getApiService()
                .login("111", "333")
                .subscribe();
    }
}
